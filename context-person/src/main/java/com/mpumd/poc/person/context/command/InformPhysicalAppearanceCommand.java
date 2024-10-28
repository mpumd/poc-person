package com.mpumd.poc.person.context.command;

import com.mpumd.poc.person.context.aggregat.EyesColor;
import com.mpumd.poc.person.context.aggregat.HairColor;
import lombok.Getter;

@Getter
public class InformPhysicalAppearanceCommand {
    private short size;
    private short weight;
    private EyesColor eyesColor;
    private HairColor hairColor;

    private InformPhysicalAppearanceCommand() {
    }

    public static InformPhysicalAppearanceCommand.SizeStep builder() {
        return new InformPhysicalAppearanceCommand.Builder(new InformPhysicalAppearanceCommand());
    }

    private static class Builder implements SizeStep, WeightStep, EyesColorStep, HairColorStep, BuildStep {
        private final InformPhysicalAppearanceCommand cmd;

        private Builder(InformPhysicalAppearanceCommand cmd) {
            this.cmd = cmd;
        }

        @Override
        public WeightStep size(short size) {
            cmd.size = size;
            return this;
        }

        @Override
        public EyesColorStep weight(short weight) {
            cmd.weight = weight;
            return this;
        }

        @Override
        public HairColorStep eyesColor(EyesColor eyesColor) {
            cmd.eyesColor = eyesColor;
            return this;
        }

        @Override
        public BuildStep hairColor(HairColor hairColor) {
            this.cmd.hairColor = hairColor;
            return this;
        }

        @Override
        public InformPhysicalAppearanceCommand build() {
            return cmd;
        }
    }

    public interface SizeStep {
        WeightStep size(short size);
    }

    public interface WeightStep {
        EyesColorStep weight(short weight);
    }

    public interface EyesColorStep {
        HairColorStep eyesColor(EyesColor eyesColor);
    }
    public interface HairColorStep {
        BuildStep hairColor(HairColor hairColor);
    }

    public interface BuildStep {
        InformPhysicalAppearanceCommand build();
    }
}
