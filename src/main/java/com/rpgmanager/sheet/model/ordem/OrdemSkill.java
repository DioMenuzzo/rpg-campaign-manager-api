package com.rpgmanager.sheet.model.ordem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Perícia treinada. {@code detail} serve para perícias com especialização, ex.: PROFISSAO "Jornalista".
 * {@code otherBonus} soma bônus de itens ou poderes.
 */
public record OrdemSkill(
        @NotNull OrdemSkillName skill,
        @NotNull TrainingDegree training,
        @Size(max = 60) String detail,
        Integer otherBonus
) {
    public int totalBonus() {
        return training.bonus() + (otherBonus != null ? otherBonus : 0);
    }
}
