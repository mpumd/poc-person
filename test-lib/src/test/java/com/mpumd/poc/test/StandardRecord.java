package com.mpumd.poc.test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

import static java.lang.reflect.AccessFlag.Location;

record StandardRecord(
        Integer integer,
        Long fLong,
        Double fDouble,
        UUID uuid,
        Boolean fBoolean,
        String string,
        Location enumLocation,
        //
        LocalDate localDate,
        LocalDateTime localDateTime,
        ZonedDateTime zonedDateTime,
        Instant instant
) {
}
