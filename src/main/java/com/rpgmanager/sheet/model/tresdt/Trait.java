package com.rpgmanager.sheet.model.tresdt;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Vantagem ou desvantagem. {@code points} é sempre positivo: numa vantagem é o custo,
 * numa desvantagem é quanto ela devolve.
 */
public record Trait(
        @NotBlank @Size(max = 80) String name,
        @Min(0) @Max(20) Integer points,
        @Size(max = 500) String notes
) {
    public int pointsOrZero() {
        return points != null ? points : 0;
    }
}
