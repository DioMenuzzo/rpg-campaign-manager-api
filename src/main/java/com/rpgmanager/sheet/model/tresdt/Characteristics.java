package com.rpgmanager.sheet.model.tresdt;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * As cinco Características do 3D&T Alpha:
 * Força (F), Habilidade (H), Resistência (R), Armadura (A) e Poder de Fogo (PdF).
 */
public record Characteristics(
        @NotNull @Min(0) @Max(20) Integer strength,
        @NotNull @Min(0) @Max(20) Integer skill,
        @NotNull @Min(0) @Max(20) Integer resistance,
        @NotNull @Min(0) @Max(20) Integer armor,
        @NotNull @Min(0) @Max(20) Integer firepower
) {
    public int total() {
        return strength + skill + resistance + armor + firepower;
    }
}
