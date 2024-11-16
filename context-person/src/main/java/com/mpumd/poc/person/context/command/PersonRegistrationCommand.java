package com.mpumd.poc.person.context.command;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.util.Optional.ofNullable;

@Getter
public class PersonRegistrationCommand {
    private String firstName;
    private String lastName;
    private ZonedDateTime birthDate;
    private Gender gender;
    private String birthPlace;
    private Nationality nationality;

    private PersonRegistrationCommand(Builder builder) {
        this.firstName = ofNullable(builder.firstName)
                .filter(s -> !s.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("firstName must not be empty"));
        this.lastName = ofNullable(builder.lastName)
                .filter(s -> !s.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("lastName must not be empty"));
        this.birthDate = ofNullable(builder.birthDate)
                .orElseThrow(() -> new IllegalArgumentException("birthDate must not be null"));
        this.birthPlace = ofNullable(builder.birthPlace)
                .filter(s -> !s.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("birthPlace must not be empty"));
        this.gender = ofNullable(builder.gender)
                .orElseThrow(() -> new IllegalArgumentException("gender must not be null"));
        this.nationality = ofNullable(builder.nationality)
                .orElseThrow(() -> new IllegalArgumentException("nationality must not be null"));
    }

    public static FirstNameStep builder() {
        return new Builder();
    }

    static class Builder implements FirstNameStep, LastNameStep, BirthDateStep, GenderStep, BirthPlaceStep, BuildStep, NationalityStep {
        private String firstName;
        private String lastName;
        private ZonedDateTime birthDate;
        private Gender gender;
        private String birthPlace;
        private Nationality nationality;

        private Builder() {
        }

        @Override
        public LastNameStep firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        @Override
        public BirthDateStep lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        @Override
        public BirthPlaceStep birthDate(ZonedDateTime birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        @Override
        public GenderStep birthPlace(String birthPlace) {
            this.birthPlace = birthPlace;
            return this;
        }

        @Override
        public NationalityStep gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        @Override
        public BuildStep nationality(Nationality nationality) {
            this.nationality = nationality;
            return this;
        }

        @Override
        public PersonRegistrationCommand build() {
            return new PersonRegistrationCommand(this);
        }
    }

    public interface FirstNameStep {
        LastNameStep firstName(String firstName);
    }

    public interface LastNameStep {
        BirthDateStep lastName(String lastName);
    }

    public interface BirthDateStep {
        BirthPlaceStep birthDate(ZonedDateTime birthDate);
    }

    public interface BirthPlaceStep {
        GenderStep birthPlace(String birthPlace);
    }

    public interface GenderStep {
        NationalityStep gender(Gender gender);
    }

    public interface NationalityStep {
        BuildStep nationality(Nationality nationality);
    }

    public interface BuildStep {
        PersonRegistrationCommand build();
    }
}