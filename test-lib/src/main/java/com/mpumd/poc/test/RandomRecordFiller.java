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

        for (int i = 0; i < fields.length; i++) {
            RecordComponent field = fields[i];
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();

            if (replacementFields.containsKey(fieldName)) {
                args[i] = convertToType(replacementFields.get(fieldName), fieldType);
            } else {
                args[i] = generateRandomValue(fieldType);
            }
        }

        Class<?>[] parameterTypes = new Class<?>[fields.length];
        for (int i = 0; i < fields.length; i++) {
            parameterTypes[i] = fields[i].getType();
        }
        Constructor<T> constructor = recordClass.getDeclaredConstructor(parameterTypes);

        return constructor.newInstance(args);
    }

    private static Object generateRandomValue(Class<?> type) {
        if (type == int.class || type == Integer.class) {
            return random.nextInt(Integer.MAX_VALUE);
        } else if (type == long.class || type == Long.class) {
            return random.nextLong(Long.MAX_VALUE);
        } else if (type == double.class || type == Double.class) {
            return random.nextDouble(Double.MAX_VALUE);
        } else if (type == boolean.class || type == Boolean.class) {
            return random.nextBoolean();
        } else if (type == UUID.class) {
            return UUID.randomUUID();
        } else if (type == String.class) {
            return generateRandomString(30);
        } else if (TemporalAccessor.class.isAssignableFrom(type)) {
            return generateRandomDate(type);
        } else if (type.isEnum()) {
            Enum<?>[] enumConstants = (Enum<?>[]) type.getEnumConstants();
            return enumConstants[random.nextInt(enumConstants.length)];
        }
        throw new UnsupportedOperationException("Type not supported : " + type);
    }

    private static Object convertToType(Object value, Class<?> type) {
        if (type.isInstance(value)) {
            return value;
        }
        if (value instanceof String) {
            String stringValue = (String) value;
            if (type == int.class || type == Integer.class) {
                return Integer.parseInt(stringValue);
            } else if (type == long.class || type == Long.class) {
                return Long.parseLong(stringValue);
            } else if (type == double.class || type == Double.class) {
                return Double.parseDouble(stringValue);
            } else if (type == boolean.class || type == Boolean.class) {
                return Boolean.parseBoolean(stringValue);
            } else if (type.isEnum()) {
                return Enum.valueOf((Class<Enum>) type, stringValue);
            } else if (LocalDate.class.isAssignableFrom(type)) {
                return LocalDate.parse(stringValue);
            } else if (LocalDateTime.class.isAssignableFrom(type)) {
                return LocalDateTime.parse(stringValue);
            } else if (ZonedDateTime.class.isAssignableFrom(type)) {
                return ZonedDateTime.parse(stringValue);
            } else if (Instant.class.isAssignableFrom(type)) {
                return Instant.parse(stringValue);
            }
        }
        throw new UnsupportedOperationException("Type not supported from " + value.getClass() + " to " + type);
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