package com.rpgmanager.sheet.model.ordem;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record Ritual(
        @NotBlank @Size(max = 80) String name,
        @NotNull Element element,
        @NotNull @Min(1) @Max(4) Integer circle,
        @Size(max = 1000) String notes
) {
}
