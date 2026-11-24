package com.rpgmanager.sheet.model.ordem;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** AGI, FOR, INT, PRE e VIG. */
public record OrdemAttributes(
        @NotNull @Min(0) @Max(5) Integer agility,
        @NotNull @Min(0) @Max(5) Integer strength,
        @NotNull @Min(0) @Max(5) Integer intellect,
        @NotNull @Min(0) @Max(5) Integer presence,
        @NotNull @Min(0) @Max(5) Integer vigor
) {
}
