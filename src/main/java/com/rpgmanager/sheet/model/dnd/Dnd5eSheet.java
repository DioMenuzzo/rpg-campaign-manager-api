package com.rpgmanager.sheet.model.dnd;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rpgmanager.campaign.GameSystem;
import com.rpgmanager.sheet.model.CharacterSheetData;
import com.rpgmanager.sheet.model.Item;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Ficha de D&D 5ª edição. */
public record Dnd5eSheet(
        @NotBlank @Size(max = 60) String species,
        @NotEmpty @Size(max = 6) List<@Valid ClassLevel> classes,
        @Size(max = 60) String background,
        @Size(max = 30) String alignment,
        @Min(0) Integer experiencePoints,
        @NotNull @Valid AbilityScores abilities,
        @NotNull @Valid HitPoints hitPoints,
        @NotNull @Min(0) @Max(40) Integer armorClass,
        @Min(0) @Max(200) Integer speed,
        Set<Ability> savingThrowProficiencies,
        @Size(max = 18) List<@Valid SkillProficiency> skills,
        @Size(max = 9) List<@Valid SpellSlot> spellSlots,
        @Size(max = 300) List<@Valid Spell> spells,
        @Size(max = 300) List<@Valid Item> inventory,
        @Size(max = 100) List<@NotBlank @Size(max = 200) String> features,
        @Size(max = 5000) String notes
) implements CharacterSheetData {

    @Override
    public GameSystem system() {
        return GameSystem.DND_5E;
    }

    /** Nível total do personagem (soma das classes). */
    public int totalLevel() {
        return classes == null ? 0 : classes.stream()
                .mapToInt(c -> c.level() == null ? 0 : c.level())
                .sum();
    }

    @JsonIgnore
    @AssertTrue(message = "A soma dos níveis das classes deve ficar entre 1 e 20")
    public boolean isTotalLevelValid() {
        int total = totalLevel();
        return classes == null || (total >= 1 && total <= 20);
    }

    /** Bônus de proficiência: +2 nos níveis 1–4, +3 nos 5–8, ... até +6 nos 17–20. */
    public int proficiencyBonus() {
        return 2 + (Math.max(totalLevel(), 1) - 1) / 4;
    }

    @Override
    public Map<String, Object> computeDerived() {
        int proficiency = proficiencyBonus();

        Map<Ability, Integer> modifiers = new EnumMap<>(Ability.class);
        Map<Ability, Integer> savingThrows = new EnumMap<>(Ability.class);
        for (Ability ability : Ability.values()) {
            int modifier = abilities.modifier(ability);
            modifiers.put(ability, modifier);
            boolean proficient = savingThrowProficiencies != null && savingThrowProficiencies.contains(ability);
            savingThrows.put(ability, modifier + (proficient ? proficiency : 0));
        }

        Map<DndSkill, Integer> skillBonuses = new EnumMap<>(DndSkill.class);
        for (DndSkill skill : DndSkill.values()) {
            skillBonuses.put(skill, modifiers.get(skill.ability()) + proficiency * proficiencyMultiplier(skill));
        }

        Map<String, Object> derived = new LinkedHashMap<>();
        derived.put("totalLevel", totalLevel());
        derived.put("proficiencyBonus", proficiency);
        derived.put("abilityModifiers", modifiers);
        derived.put("savingThrows", savingThrows);
        derived.put("skills", skillBonuses);
        derived.put("initiative", modifiers.get(Ability.DEX));
        derived.put("passivePerception", 10 + skillBonuses.get(DndSkill.PERCEPTION));
        return derived;
    }

    private int proficiencyMultiplier(DndSkill skill) {
        if (skills == null) {
            return 0;
        }
        return skills.stream()
                .filter(s -> s.skill() == skill)
                .mapToInt(s -> s.effectiveLevel().multiplier())
                .max()
                .orElse(0);
    }
}
