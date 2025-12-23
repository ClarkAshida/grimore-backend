package com.grimore.util;

import com.grimore.enums.WorkloadHours;
import com.grimore.exception.validation.BadRequestException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilitário centralizado para parsing e análise de códigos de horário UFRN.
 *
 * Agora suporta:
 * - múltiplos dias no mesmo segmento (ex: 246N12, 35T12)
 * - múltiplos segmentos na mesma string (ex: "246N12 7N12" ou "35M56 4T34")
 */
@Slf4j
public class ScheduleCodeParser {

    /**
     * Captura:
     *  - grupo de dias: 1..7 repetidos (ex: 246, 35, 7)
     *  - turno: M/T/N (e aceitamos V e normalizamos para T)
     *  - blocos: sequência de dígitos (ex: 12, 3456)
     *
     * Ex: "246N12 7N12" -> matches:
     *  - daysGroup=246 shift=N blocks=12
     *  - daysGroup=7   shift=N blocks=12
     */
    private static final Pattern SEGMENT_PATTERN =
            Pattern.compile("([1-7]+)\\s*([MTNV])\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    private ScheduleCodeParser() {}

    // ==================== API pública ====================

    public static boolean hasConflict(String scheduleCode1, String scheduleCode2) {
        if (scheduleCode1 == null || scheduleCode2 == null) {
            return false;
        }

        try {
            Set<String> slots1 = extractSlots(scheduleCode1);
            Set<String> slots2 = extractSlots(scheduleCode2);

            boolean conflict = !Collections.disjoint(slots1, slots2);

            if (conflict) {
                log.debug("Schedule conflict detected between '{}' and '{}'", scheduleCode1, scheduleCode2);
            }

            return conflict;
        } catch (BadRequestException e) {
            log.warn("Invalid schedule code during conflict check: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Infere carga horária por quantidade de dias únicos no scheduleCode inteiro.
     * - 1 dia = 30h
     * - 2 dias = 60h
     * - 3+ dias = 90h
     */
    public static WorkloadHours inferWorkloadFromScheduleCode(String scheduleCode) {
        if (scheduleCode == null || scheduleCode.isBlank()) {
            log.warn("Schedule code is null or empty, defaulting to 30h");
            return WorkloadHours.H30;
        }

        try {
            int daysPerWeek = countUniqueDays(scheduleCode);
            WorkloadHours workload = mapDaysToWorkload(daysPerWeek);

            log.debug("Inferred workload for schedule '{}': {} days/week = {}",
                    scheduleCode, daysPerWeek, workload);

            return workload;
        } catch (Exception e) {
            log.error("Error inferring workload from schedule code '{}': {}",
                    scheduleCode, e.getMessage());
            return WorkloadHours.H30;
        }
    }

    /**
     * Conta quantos dias únicos existem em TODOS os segmentos.
     * Ex: "246N12 7N12" -> 4 dias
     * Ex: "35T12" -> 2 dias
     */
    public static int countUniqueDays(String scheduleCode) {
        if (scheduleCode == null || scheduleCode.isBlank()) {
            return 1;
        }

        String normalized = normalize(scheduleCode);

        Set<String> uniqueDays = new HashSet<>();
        Matcher matcher = SEGMENT_PATTERN.matcher(normalized);

        while (matcher.find()) {
            String daysGroup = matcher.group(1); // ex: "246"
            for (char d : daysGroup.toCharArray()) {
                validateDayChar(d);
                uniqueDays.add(String.valueOf(d));
            }
        }

        return Math.max(uniqueDays.size(), 1);
    }

    /**
     * Extrai slots individuais (dia+turno+bloco) de TODOS os segmentos.
     *
     * Ex: "246N12 7N12" ->
     * 2N1,2N2,4N1,4N2,6N1,6N2,7N1,7N2
     */
    public static Set<String> extractSlots(String scheduleCode) {
        if (scheduleCode == null || scheduleCode.isBlank()) {
            throw new BadRequestException("Código de horário inválido: vazio");
        }

        String normalized = normalize(scheduleCode);

        Set<String> slots = new HashSet<>();
        Matcher matcher = SEGMENT_PATTERN.matcher(normalized);

        while (matcher.find()) {
            String daysGroup = matcher.group(1);
            char shift = normalizeShift(matcher.group(2).charAt(0)); // V->T
            String blocks = matcher.group(3);

            validateSegment(daysGroup, shift, blocks);

            // expande dias
            for (char dayChar : daysGroup.toCharArray()) {
                validateDayChar(dayChar);
                String day = String.valueOf(dayChar);

                // expande blocos
                for (char blockChar : blocks.toCharArray()) {
                    if (!Character.isDigit(blockChar)) {
                        throw new BadRequestException("Bloco inválido no código de horário: " + blockChar);
                    }
                    int block = Character.getNumericValue(blockChar);
                    validateBlock(shift, block);
                    slots.add(day + shift + blockChar);
                }
            }
        }

        if (slots.isEmpty()) {
            throw new BadRequestException("Código de horário inválido: " + scheduleCode);
        }

        return slots;
    }

    /**
     * Parse estruturado (união dos componentes em todos os segmentos).
     */
    public static ScheduleInfo parseScheduleCode(String scheduleCode) {
        if (scheduleCode == null || scheduleCode.isBlank()) {
            throw new BadRequestException("Código de horário não pode ser vazio");
        }

        String normalized = normalize(scheduleCode);

        Set<String> days = new HashSet<>();
        Set<String> shifts = new HashSet<>();
        Set<String> blocks = new HashSet<>();
        Set<String> slots = new HashSet<>();

        Matcher matcher = SEGMENT_PATTERN.matcher(normalized);

        while (matcher.find()) {
            String daysGroup = matcher.group(1);
            char shift = normalizeShift(matcher.group(2).charAt(0)); // V->T
            String blockGroup = matcher.group(3);

            validateSegment(daysGroup, shift, blockGroup);

            shifts.add(String.valueOf(shift));

            for (char dayChar : daysGroup.toCharArray()) {
                validateDayChar(dayChar);
                String day = String.valueOf(dayChar);
                days.add(day);

                for (char blockChar : blockGroup.toCharArray()) {
                    int block = Character.getNumericValue(blockChar);
                    validateBlock(shift, block);
                    blocks.add(String.valueOf(blockChar));
                    slots.add(day + shift + blockChar);
                }
            }
        }

        if (slots.isEmpty()) {
            throw new BadRequestException("Código de horário inválido: " + scheduleCode);
        }

        return new ScheduleInfo(
                normalized,
                days,
                shifts,
                blocks,
                slots,
                days.size()
        );
    }

    public static boolean isValidScheduleCode(String scheduleCode) {
        if (scheduleCode == null || scheduleCode.isBlank()) {
            return false;
        }

        try {
            extractSlots(scheduleCode);
            return true;
        } catch (BadRequestException e) {
            return false;
        }
    }

    // ==================== Validações e helpers ====================

    /**
     * Normaliza:
     * - trim
     * - uppercase
     * - substitui separadores comuns por espaço
     * - colapsa múltiplos espaços
     * - V -> T
     */
    private static String normalize(String scheduleCode) {
        String s = scheduleCode.trim().toUpperCase();
        s = s.replace('\u00A0', ' ');                  // NBSP
        s = s.replaceAll("[,;|/]+", " ");              // separadores tolerados
        s = s.replaceAll("\\s+", " ");                 // colapsa espaços
        s = s.replace('V', 'T');                       // vespertino -> tarde
        return s;
    }

    private static char normalizeShift(char shift) {
        char s = Character.toUpperCase(shift);
        return (s == 'V') ? 'T' : s;
    }

    private static void validateSegment(String daysGroup, char shift, String blocks) {
        if (daysGroup == null || daysGroup.isBlank()) {
            throw new BadRequestException("Dias não informados no código de horário");
        }
        if (shift != 'M' && shift != 'T' && shift != 'N') {
            throw new BadRequestException("Turno inválido: " + shift + " (deve ser M/T/N)");
        }
        if (blocks == null || blocks.isBlank()) {
            throw new BadRequestException("Nenhum bloco de horário especificado");
        }
    }

    private static void validateDayChar(char dayChar) {
        if (dayChar < '1' || dayChar > '7') {
            throw new BadRequestException(
                    "Dia da semana inválido no código de horário: " + dayChar + " (deve ser entre 1 e 7)"
            );
        }
    }

    private static void validateBlock(char shift, int block) {
        if (shift == 'N') {
            if (block < 1 || block > 4) {
                throw new BadRequestException(
                        "Bloco noturno inválido: " + block + " (deve ser entre 1 e 4)"
                );
            }
        } else {
            if (block < 1 || block > 6) {
                throw new BadRequestException(
                        "Bloco de horário inválido: " + block + " (deve ser entre 1 e 6)"
                );
            }
        }
    }

    private static WorkloadHours mapDaysToWorkload(int daysPerWeek) {
        return switch (daysPerWeek) {
            case 1 -> WorkloadHours.H30;
            case 2 -> WorkloadHours.H60;
            default -> WorkloadHours.H90; // 3 ou mais dias = 90h
        };
    }

    // ==================== Records ====================

    public record ScheduleInfo(
            String originalCode,
            Set<String> days,
            Set<String> shifts,
            Set<String> blocks,
            Set<String> slots,
            int daysPerWeek
    ) {
        public String getDaysDescription() {
            return String.join(", ", days.stream()
                    .map(ScheduleCodeParser::dayToDescription)
                    .sorted()
                    .toList());
        }

        public String getShiftsDescription() {
            return String.join(", ", shifts.stream()
                    .map(ScheduleCodeParser::shiftToDescription)
                    .sorted()
                    .toList());
        }
    }

    // ==================== Descrições ====================

    private static String dayToDescription(String day) {
        return switch (day) {
            case "1" -> "Domingo";
            case "2" -> "Segunda";
            case "3" -> "Terça";
            case "4" -> "Quarta";
            case "5" -> "Quinta";
            case "6" -> "Sexta";
            case "7" -> "Sábado";
            default -> "Dia " + day;
        };
    }

    private static String shiftToDescription(String shift) {
        return switch (shift) {
            case "M" -> "Manhã";
            case "T" -> "Tarde";
            case "N" -> "Noite";
            default -> shift;
        };
    }
}
