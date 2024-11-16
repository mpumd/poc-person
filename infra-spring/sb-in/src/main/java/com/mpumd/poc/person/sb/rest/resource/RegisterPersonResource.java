package com.mpumd.poc.person.sb.rest.resource;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

@Schema(description = "Payload for creating a person resource")
public record RegisterPersonResource(

        @Schema(description= "First name of the person", example = "John")
        String firstName,

        @Schema(description = "Last name of the person", example = "Rambo")
        String lastName,

        @Schema(description = "Gender of the person", example = "Male")
        String gender,

        @Schema(description = "Birthdate of the person in ISO 8601", example = "1947-07-06T00:00:00+01:00")
        ZonedDateTime birthDate,

        @Schema(description = "Birthplace of the person", example = "Bowie, Arizona, USA")
        String birthPlace,

        @Schema(description = "Nationality of the person", example = "American")
        String nationality
) {}
