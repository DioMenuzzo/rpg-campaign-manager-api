package com.rpgmanager.sheet.model.dnd;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AbilityScores(
        @NotNull @Min(1) @Max(30) Integer strength,
        @NotNull @Min(1) @Max(30) Integer dexterity,
        @NotNull @Min(1) @Max(30) Integer constitution,
        @NotNull @Min(1) @Max(30) Integer intelligence,
        @NotNull @Min(1) @Max(30) Integer wisdom,
        @NotNull @Min(1) @Max(30) Integer charisma
) {
    public int score(Ability ability) {
        return switch (ability) {
            case STR -> strength;
            case DEX -> dexterity;
            case CON -> constitution;
            case INT -> intelligence;
            case WIS -> wisdom;
            case CHA -> charisma;
        };
    }

    /** Modificador = (valor − 10) / 2, arredondado para baixo. */
    public int modifier(Ability ability) {
        return Math.floorDiv(score(ability) - 10, 2);
    }
}
