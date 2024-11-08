package com.mpumd.poc.person.context.command;

import com.mpumd.poc.person.context.aggregat.Gender;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Optional.ofNullable;

public record ChangeSexCommand(UUID id, Gender gender, LocalDateTime changeDate) {

    public ChangeSexCommand(UUID id, Gender gender, LocalDateTime changeDate) {
        this.id = ofNullable(id).orElseThrow(() -> new IllegalArgumentException("id is marked non-null but is null"));
        this.gender = ofNullable(gender).orElseThrow(() -> new IllegalArgumentException("gender is marked non-null but is null"));
        this.changeDate = ofNullable(changeDate).orElseThrow(() -> new IllegalArgumentException("changeDate is marked non-null but is null"));
    }
}
