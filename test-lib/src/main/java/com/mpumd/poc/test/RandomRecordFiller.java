package com.mpumd.poc.test;

import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class RandomRecordFiller {
    private static final Random random = new Random();


    public static <T extends Record> T fillRandomly(Class<T> recordClass) {
        return fillRandomly(recordClass, Map.of());
    }

    @SneakyThrows
    public static <T extends Record> T fillRandomly(Class<T> recordClass, Map<String, Object> replacementFields) {
        RecordComponent[] fields = recordClass.getRecordComponents();
        Object[] args = new Object[fields.length];
        Class<?>[] parameterTypes = new Class<?>[fields.length];

        for (int i = 0; i < fields.length; i++) {
            RecordComponent field = fields[i];
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();

            args[i] = replacementFields.containsKey(fieldName)
                    ? replacementFields.get(fieldName)
                    : generateRandomValue(fieldType);

            parameterTypes[i] = fields[i].getType();
        }

        Constructor<T> constructor = recordClass.getDeclaredConstructor(parameterTypes);
        return constructor.newInstance(args);
    }

    static Object generateRandomValue(Class<?> type) {
        return switch (type) {
            case Class<?> c when c == int.class || c == Integer.class ->
                    random.nextInt(Integer.MAX_VALUE);
            case Class<?> c when c == long.class || c == Long.class ->
                    random.nextLong(Long.MAX_VALUE);
            case Class<?> c when c == double.class || c == Double.class ->
                    random.nextDouble(Double.MAX_VALUE);
            case Class<?> c when c == boolean.class || c == Boolean.class ->
                    random.nextBoolean();
            case Class<?> c when c == UUID.class ->
                    UUID.randomUUID();
            case Class<?> c when c == String.class ->
                    generateRandomString(30);
            case Class<?> c when TemporalAccessor.class.isAssignableFrom(c) ->
                    generateRandomDate(c);
            case Class<?> c when c.isEnum() -> {
                Enum<?>[] enumConstants = (Enum<?>[]) c.getEnumConstants();
                yield enumConstants[random.nextInt(enumConstants.length)];
            }
            default -> throw new UnsupportedOperationException("Type not supported : " + type);
        };
    }

    private static TemporalAccessor generateRandomDate(Class<?> type) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime randomDate = now.minus(random.nextLong(365 * 10), ChronoUnit.DAYS)
                .plus(random.nextLong(24), ChronoUnit.HOURS)
                .plus(random.nextLong(60), ChronoUnit.MINUTES);

        if (type == LocalDate.class) {
            return randomDate.toLocalDate();
        } else if (type == LocalDateTime.class) {
            return randomDate;
        } else if (type == ZonedDateTime.class) {
            return randomDate.atZone(ZoneId.systemDefault());
        } else if (type == Instant.class) {
            return randomDate.toInstant(ZoneOffset.UTC);
        }

        throw new UnsupportedOperationException("Type non supported : " + type);
    }

    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int codePoint;
            do {
                // Génère un code point Unicode aléatoire entre 0x20 et 0x10FFFF
                codePoint = random.nextInt(0x20, 0x10FFFF + 1);
            } while (!isValidPrintableCodePoint(codePoint));

            sb.appendCodePoint(codePoint);
        }
        return sb.toString();
    }

    private static boolean isValidPrintableCodePoint(int codePoint) {
        return Character.isValidCodePoint(codePoint) &&
                !Character.isISOControl(codePoint) &&
                !Character.isWhitespace(codePoint) &&
                Character.isDefined(codePoint) &&
                (Character.isLetterOrDigit(codePoint) ||
                        Character.isIdeographic(codePoint) ||
                        isPunctuation(codePoint) ||
                        isSymbol(codePoint));
    }

    private static boolean isPunctuation(int codePoint) {
        int type = Character.getType(codePoint);
        return type == Character.CONNECTOR_PUNCTUATION ||
                type == Character.DASH_PUNCTUATION ||
                type == Character.START_PUNCTUATION ||
                type == Character.END_PUNCTUATION ||
                type == Character.INITIAL_QUOTE_PUNCTUATION ||
                type == Character.FINAL_QUOTE_PUNCTUATION ||
                type == Character.OTHER_PUNCTUATION;
    }

    private static boolean isSymbol(int codePoint) {
        int type = Character.getType(codePoint);
        return type == Character.MATH_SYMBOL ||
                type == Character.CURRENCY_SYMBOL ||
                type == Character.MODIFIER_SYMBOL ||
                type == Character.OTHER_SYMBOL;
    }
}