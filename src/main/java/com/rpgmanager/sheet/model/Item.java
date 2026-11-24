package com.rpgmanager.sheet.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Item de inventário, comum a todos os sistemas. {@code quantity} omitido equivale a 1. */
public record Item(
        @NotBlank @Size(max = 120) String name,
        @Min(1) @Max(9999) Integer quantity,
        @Size(max = 500) String notes
) {
}
