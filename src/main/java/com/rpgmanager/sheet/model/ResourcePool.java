package com.rpgmanager.sheet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Recurso com valor atual e máximo: PV, PE, PM, Sanidade... Comum a vários sistemas. */
public record ResourcePool(
        @NotNull @Min(0) Integer max,
        @NotNull @Min(0) Integer current
) {
    @JsonIgnore
    @AssertTrue(message = "O valor atual não pode ser maior que o máximo")
    public boolean isCurrentWithinMax() {
        return max == null || current == null || current <= max;
    }
}
