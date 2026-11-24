package com.rpgmanager.sheet.model.dnd;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Magia conhecida. {@code level} 0 = truque (cantrip). */
public record Spell(
        @NotBlank @Size(max = 80) String name,
        @NotNull @Min(0) @Max(9) Integer level,
        Boolean prepared
) {
}
