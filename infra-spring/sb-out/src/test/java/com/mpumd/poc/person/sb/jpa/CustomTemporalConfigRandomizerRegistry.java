package com.mpumd.poc.person.sb.jpa;

import org.jeasy.random.api.Randomizer;
import org.jeasy.random.randomizers.registry.TimeRandomizerRegistry;
import org.jeasy.random.randomizers.time.LocalDateTimeRandomizer;
import org.jeasy.random.randomizers.time.ZonedDateTimeRandomizer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * This randomizer configuration is usefull to avoid some temporal useless data like millisecondes or
 * name of timezone. After relaod form db, assertJ will find some differents who there are none.
 * /!\ Database can loose data, depend of how you define your time fields.
 */
class CustomTemporalConfigRandomizerRegistry extends TimeRandomizerRegistry {
    @Override
    public Randomizer<?> getRandomizer(Class<?> type) {
        if (type.equals(ZonedDateTime.class)) {
            return new ZonedDateTimeRandomizer() {
                @Override
                public ZonedDateTime getRandomValue() {
                    // erase the name of timezone at the end of the string date
                    return super.getRandomValue().withZoneSameInstant(ZoneOffset.ofHours(2));
                }
            };
        } else if (type.equals(LocalDateTime.class)) {
            return new LocalDateTimeRandomizer() {
                @Override
                public LocalDateTime getRandomValue() {
                    // erase milliseconds by truncate at seconds.
                    return super.getRandomValue().truncatedTo(ChronoUnit.SECONDS);
                }
            };
        }
        return super.getRandomizer(type);
    }
}