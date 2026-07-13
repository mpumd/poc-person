


package com.mpumd.poc.person.application.command;

import com.mpumd.poc.person.context.aggregat.Gender;

import java.time.LocalDateTime;


public record GenderChangeCommand(
        Gender gender,
        LocalDateTime changeDate) {
}
