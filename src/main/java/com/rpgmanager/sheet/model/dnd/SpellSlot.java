package com.rpgmanager.sheet.model.dnd;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Espaços de magia de um círculo: quantos existem e quantos já foram gastos. */
public record SpellSlot(
        @NotNull @Min(1) @Max(9) Integer level,
        @NotNull @Min(0) @Max(10) Integer total,
        @NotNull @Min(0) @Max(10) Integer used
) {
    @JsonIgnore
    @AssertTrue(message = "Espaços usados não podem passar do total")
    public boolean isUsedWithinTotal() {
        return total == null || used == null || used <= total;
    }
}
