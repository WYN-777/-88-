package com.kxw.ta.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public final class TextUtil {
    private TextUtil() {
    }

    public static List<String> parseCsvLike(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return Arrays.stream(text.split("[,;\\n]"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .toList();
    }

    public static String joinList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return String.join(", ", values);
    }

    public static int parsePositiveInt(String value, int fallback) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : fallback;
        } catch (RuntimeException ex) {
            return fallback;
        }
    }

    public static int requiredInt(String value, String message) {
        try {
            return Integer.parseInt(required(value, message));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(message);
        }
    }

    public static String required(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    public static String optional(String value) {
        return value == null ? "" : value.trim();
    }

    public static String choice(String value, Set<String> allowed, String message) {
        String trimmed = required(value, message);
        if (!allowed.contains(trimmed)) {
            throw new IllegalArgumentException(message);
        }
        return trimmed;
    }

    public static int boundedInt(int value, int min, int max, String message) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static LocalDate parseDate(String value, String message) {
        try {
            return LocalDate.parse(required(value, message));
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(message);
        }
    }
}
