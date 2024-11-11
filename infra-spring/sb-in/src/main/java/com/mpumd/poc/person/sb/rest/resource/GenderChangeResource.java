package com.mpumd.poc.person.sb.rest.resource;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Payload for change the sex of a person")
public record GenderChangeResource(
        @Schema(example = "male, female")
        String gender,

        @Schema(example = "1980-02-15T02:37:00")
        LocalDateTime changeDate
) {}
