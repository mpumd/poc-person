package com.mpumd.poc.person.sb.rest.resource;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Payload for change the sex of a person")
public record GenderChangeResource(
        @Schema(example = "male, female")
        String gender,

        @Schema(example = "1980-02-15T02:37:00")
        @JsonFormat(without = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        LocalDateTime changeDate
) {
}
