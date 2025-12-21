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
 * Responsabilidades:
 * - Validar formato de scheduleCode
 * - Detectar conflitos de horário
 * - Inferir carga horária baseada em dias/semana
 * - Extrair componentes do código (dias, turnos, blocos)
 */
@Slf4j
public class ScheduleCodeParser {

    private static final Pattern SCHEDULE_PATTERN = Pattern.compile("([2-6])([MTN])(\\d+)");

    // Pattern alternativo para validação mais flexível (aceita dia 1 e 7 para sábado/domingo)
    private static final Pattern FLEXIBLE_PATTERN = Pattern.compile("([1-7])([MVN])(\\d+)");

    /**
     * Verifica se dois códigos de horário têm algum conflito (mesmo dia, turno e bloco).
     *
     * @param scheduleCode1 Primeiro código de horário
     * @param scheduleCode2 Segundo código de horário
     * @return true se houver conflito, false caso contrário
     */
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
     * Infere a carga horária de uma disciplina baseado no código de horário UFRN.
     *
     * Regra: Conta quantos dias diferentes a disciplina ocorre por semana.
     * - 1 dia/semana = 30h
     * - 2 dias/semana = 60h
     * - 3+ dias/semana = 90h
     *
     * @param scheduleCode Código de horário no formato UFRN (ex: 246N12, 35N12, 3N34)
     * @return WorkloadHours inferida, ou H30 como padrão se não for possível inferir
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
     * Conta quantos dias únicos por semana a disciplina ocorre.
     *
     * @param scheduleCode Código de horário (ex: 246N12 = 3 dias, 35N12 = 2 dias)
     * @return Número de dias únicos por semana
     */
    public static int countUniqueDays(String scheduleCode) {
        if (scheduleCode == null || scheduleCode.isBlank()) {
            return 1;
        }

        Set<String> uniqueDays = new HashSet<>();
        Matcher matcher = SCHEDULE_PATTERN.matcher(scheduleCode.toUpperCase().trim());

        while (matcher.find()) {
            String day = matcher.group(1);
            uniqueDays.add(day);
        }

        return Math.max(uniqueDays.size(), 1); // Mínimo 1 dia
    }

    /**
     * Extrai todos os slots individuais de um código de horário.
     * Cada slot representa uma combinação única de dia + turno + bloco.
     *
     * Ex: "2M34" -> ["2M3", "2M4"]
     * Ex: "35T234" -> ["3T2", "3T3", "3T4", "5T2", "5T3", "5T4"]
     * Ex: "246N12" -> ["2N1", "2N2", "4N1", "4N2", "6N1", "6N2"]
     *
     * @param scheduleCode Código de horário
     * @return Set de slots individuais
     * @throws BadRequestException se o código for inválido
     */
    public static Set<String> extractSlots(String scheduleCode) {
        Set<String> slots = new HashSet<>();
        Matcher matcher = SCHEDULE_PATTERN.matcher(scheduleCode.toUpperCase().trim());

        while (matcher.find()) {
            String day = matcher.group(1);
            String shift = matcher.group(2);
            String blocks = matcher.group(3);

            validateScheduleComponent(day, shift, blocks);

            // Expande cada bloco em um slot individual
            for (char block : blocks.toCharArray()) {
                validateBlock(shift.charAt(0), Character.getNumericValue(block));
                slots.add(day + shift + block);
            }
        }

        if (slots.isEmpty()) {
            throw new BadRequestException("Código de horário inválido: " + scheduleCode);
        }

        return slots;
    }

    /**
     * Valida e extrai informações estruturadas do código de horário.
     * Útil para debugging, logs e validações mais complexas.
     *
     * @param scheduleCode Código de horário
     * @return ScheduleInfo com componentes parseados
     * @throws BadRequestException se o código for inválido
     */
    public static ScheduleInfo parseScheduleCode(String scheduleCode) {
        if (scheduleCode == null || scheduleCode.isBlank()) {
            throw new BadRequestException("Código de horário não pode ser vazio");
        }

        Set<String> days = new HashSet<>();
        Set<String> shifts = new HashSet<>();
        Set<String> blocks = new HashSet<>();
        Set<String> slots = new HashSet<>();

        Matcher matcher = SCHEDULE_PATTERN.matcher(scheduleCode.toUpperCase().trim());

        while (matcher.find()) {
            String day = matcher.group(1);
            String shift = matcher.group(2);
            String blockGroup = matcher.group(3);

            validateScheduleComponent(day, shift, blockGroup);

            days.add(day);
            shifts.add(shift);

            for (char block : blockGroup.toCharArray()) {
                validateBlock(shift.charAt(0), Character.getNumericValue(block));
                blocks.add(String.valueOf(block));
                slots.add(day + shift + block);
            }
        }

        if (slots.isEmpty()) {
            throw new BadRequestException("Código de horário inválido: " + scheduleCode);
        }

        return new ScheduleInfo(
                scheduleCode,
                days,
                shifts,
                blocks,
                slots,
                days.size()
        );
    }

    /**
     * Valida se um código de horário está no formato correto.
     *
     * @param scheduleCode Código a validar
     * @return true se válido, false caso contrário
     */
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

    // ==================== Métodos Privados ====================

    private static void validateScheduleComponent(String day, String shift, String blocks) {
        int dayNum = Integer.parseInt(day);
        if (dayNum < 2 || dayNum > 6) {
            throw new BadRequestException(
                    "Dia da semana inválido: " + day + " (deve ser entre 2-Segunda e 6-Sexta)"
            );
        }

        if (!shift.matches("[MTN]")) {
            throw new BadRequestException(
                    "Turno inválido: " + shift + " (deve ser M-Manhã, T-Tarde ou N-Noite)"
            );
        }

        if (blocks.isEmpty()) {
            throw new BadRequestException("Nenhum bloco de horário especificado");
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

    /**
     * Informações estruturadas extraídas de um código de horário.
     */
    public record ScheduleInfo(
            String originalCode,
            Set<String> days,           // Ex: ["2", "4", "6"]
            Set<String> shifts,         // Ex: ["N"]
            Set<String> blocks,         // Ex: ["1", "2"]
            Set<String> slots,          // Ex: ["2N1", "2N2", "4N1", "4N2", "6N1", "6N2"]
            int daysPerWeek            // Ex: 3
    ) {
        public String getDaysDescription() {
            return String.join(", ", days.stream()
                    .map(ScheduleCodeParser::dayToDescription)
                    .toList());
        }

        public String getShiftsDescription() {
            return String.join(", ", shifts.stream()
                    .map(ScheduleCodeParser::shiftToDescription)
                    .toList());
        }
    }

    // ==================== Helpers para Descrição ====================

    private static String dayToDescription(String day) {
        return switch (day) {
            case "2" -> "Segunda";
            case "3" -> "Terça";
            case "4" -> "Quarta";
            case "5" -> "Quinta";
            case "6" -> "Sexta";
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