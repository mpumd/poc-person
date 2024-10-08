package com.mpumd.poc.person.context.command;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class PersonRegistrationCommand {
    private String firstName;
    private String lastName;
    private ZonedDateTime birthDate;
    private Gender gender;
    private String birthPlace;
    private Nationality nationality;

    private PersonRegistrationCommand() {
    }

    public static FirstNameStep builder() {
        return new Builder(new PersonRegistrationCommand());
    }

    private static class Builder implements FirstNameStep, LastNameStep, BirthDateStep, GenderStep, BirthPlaceStep, BuildStep, NationalityStep {
        private final PersonRegistrationCommand cmd;

        private Builder(PersonRegistrationCommand cmd) {
            this.cmd = cmd;
        }

        @Override
        public LastNameStep firstName(String firstName) {
            cmd.firstName = firstName;
            return this;
        }

        @Override
        public BirthDateStep lastName(String lastName) {
            cmd.lastName = lastName;
            return this;
        }

        @Override
        public BirthPlaceStep birthDate(ZonedDateTime birthDate) {
            cmd.birthDate = birthDate;
            return this;
        }

        @Override
        public GenderStep birthPlace(String birthPlace) {
            cmd.birthPlace = birthPlace;
            return this;
        }

        @Override
        public NationalityStep gender(Gender gender) {
            cmd.gender = gender;
            return this;
        }

        @Override
        public BuildStep nationality(Nationality nationality) {
            cmd.nationality = nationality;
            return this;
        }

        @Override
        public PersonRegistrationCommand build() {
            return cmd;
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


