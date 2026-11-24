package com.rpgmanager.sheet.model.dnd;

/** As 18 perícias do D&D 5e e o atributo que cada uma usa. */
public enum DndSkill {
    ACROBATICS(Ability.DEX),
    ANIMAL_HANDLING(Ability.WIS),
    ARCANA(Ability.INT),
    ATHLETICS(Ability.STR),
    DECEPTION(Ability.CHA),
    HISTORY(Ability.INT),
    INSIGHT(Ability.WIS),
    INTIMIDATION(Ability.CHA),
    INVESTIGATION(Ability.INT),
    MEDICINE(Ability.WIS),
    NATURE(Ability.INT),
    PERCEPTION(Ability.WIS),
    PERFORMANCE(Ability.CHA),
    PERSUASION(Ability.CHA),
    RELIGION(Ability.INT),
    SLEIGHT_OF_HAND(Ability.DEX),
    STEALTH(Ability.DEX),
    SURVIVAL(Ability.WIS);

    private final Ability ability;

    DndSkill(Ability ability) {
        this.ability = ability;
    }

    public Ability ability() {
        return ability;
    }
}
