package com.grimore.util;

import com.grimore.exception.validation.BadRequestException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleCodeParser {

    private static final Pattern SCHEDULE_PATTERN = Pattern.compile("([2-6])([MTN])(\\d+)");

    /**
     * Verifica se dois códigos de horário têm algum conflito
     */
    public static boolean hasConflict(String scheduleCode1, String scheduleCode2) {
        if (scheduleCode1 == null || scheduleCode2 == null) {
            return false;
        }

        Set<String> slots1 = extractSlots(scheduleCode1);
        Set<String> slots2 = extractSlots(scheduleCode2);

        return !Collections.disjoint(slots1, slots2);
    }

    /**
     * Extrai todos os slots individuais de um código de horário
     * Ex: "2M34" -> ["2M3", "2M4"]
     * Ex: "35T234" -> ["3T2", "3T3", "3T4", "5T2", "5T3", "5T4"]
     */
    private static Set<String> extractSlots(String scheduleCode) {
        Set<String> slots = new HashSet<>();
        Matcher matcher = SCHEDULE_PATTERN.matcher(scheduleCode.toUpperCase().trim());

        while (matcher.find()) {
            String day = matcher.group(1);
            String shift = matcher.group(2);
            String blocks = matcher.group(3);

            validateScheduleComponent(day, shift, blocks);

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

    private static void validateScheduleComponent(String day, String shift, String blocks) {
        int dayNum = Integer.parseInt(day);
        if (dayNum < 2 || dayNum > 6) {
            throw new BadRequestException("Dia da semana inválido: " + day + " (deve ser entre 2 e 6)");
        }

        if (!shift.matches("[MTN]")) {
            throw new BadRequestException("Turno inválido: " + shift + " (deve ser M, T ou N)");
        }

        if (blocks.isEmpty()) {
            throw new BadRequestException("Nenhum bloco de horário especificado");
        }
    }

    private static void validateBlock(char shift, int block) {
        if (shift == 'N') {
            if (block < 1 || block > 4) {
                throw new BadRequestException("Bloco noturno inválido: " + block + " (deve ser entre 1 e 4)");
            }
        } else {
            if (block < 1 || block > 6) {
                throw new BadRequestException("Bloco de horário inválido: " + block + " (deve ser entre 1 e 6)");
            }
        }
    }
}