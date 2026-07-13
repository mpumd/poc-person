


package com.mpumd.poc.person.application.command;

import com.mpumd.poc.person.context.aggregat.Gender;

import java.time.LocalDateTime;
import java.util.UUID;


public record GenderChangeCommand(
        UUID id,
        Gender gender,
        LocalDateTime changeDate) {
}
