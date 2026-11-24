package com.rpgmanager.sheet.model.dnd;

/** Proficiente soma o bônus de proficiência uma vez; especialização (expertise) soma duas vezes. */
public enum ProficiencyLevel {
    PROFICIENT(1),
    EXPERTISE(2);

    private final int multiplier;

    ProficiencyLevel(int multiplier) {
        this.multiplier = multiplier;
    }

    public int multiplier() {
        return multiplier;
    }
}
