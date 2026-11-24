package com.rpgmanager.sheet.model.dnd;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record HitPoints(
        @NotNull @Min(1) Integer max,
        @NotNull @Min(0) Integer current,
        @Min(0) Integer temporary
) {
    @JsonIgnore
    @AssertTrue(message = "PV atuais não podem ser maiores que o máximo")
    public boolean isCurrentWithinMax() {
        return max == null || current == null || current <= max;
    }
}
