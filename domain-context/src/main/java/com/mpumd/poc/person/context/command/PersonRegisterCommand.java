package com.mpumd.poc.person.context.command;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PersonRegisterCommand {
    private String firstName;
    private String lastName;
    private LocalDateTime birthDate;
    private Gender gender;
    private String birthPlace;
    private Nationality nationality;

    private PersonRegisterCommand() {
    }

    public static FirstNameStep builder() {
        return new Builder(new PersonRegisterCommand());
    }

    private static class Builder implements FirstNameStep, LastNameStep, BirthDateStep, GenderStep, BirthPlaceStep, BuildStep, NationalityStep {
        private final PersonRegisterCommand cmd;

        private Builder(PersonRegisterCommand cmd) {
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
        public BirthPlaceStep birthDate(LocalDateTime birthDate) {
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
        public PersonRegisterCommand build() {
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
        BirthPlaceStep birthDate(LocalDateTime birthDate);
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
        PersonRegisterCommand build();
    }
}


