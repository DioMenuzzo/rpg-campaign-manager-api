package com.rpgmanager.sheet.model.dnd;

import jakarta.validation.constraints.NotNull;

/** Perícia em que o personagem é treinado. {@code level} omitido equivale a PROFICIENT. */
public record SkillProficiency(
        @NotNull DndSkill skill,
        ProficiencyLevel level
) {
    public ProficiencyLevel effectiveLevel() {
        return level != null ? level : ProficiencyLevel.PROFICIENT;
    }
}
