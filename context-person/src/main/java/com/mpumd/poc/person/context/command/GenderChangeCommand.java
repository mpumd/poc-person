package com.mpumd.poc.person.context.command;

import com.mpumd.poc.person.context.aggregat.Gender;

import java.time.LocalDateTime;

import static com.mpumd.poc.person.context.utils.ObjectsUtils.notNull;

public record GenderChangeCommand(
        Gender gender,
        LocalDateTime changeDate) {

    public GenderChangeCommand {
        gender = notNull(gender, "gender must not be null");
        changeDate = notNull(changeDate, "changeDate must not be null");
    }
}
