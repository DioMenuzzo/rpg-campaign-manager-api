package com.rpgmanager.sheet.model.dnd;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Uma classe e seu nível. Personagens multiclasse têm mais de uma entrada. */
public record ClassLevel(
        @NotBlank @Size(max = 60) String name,
        @Size(max = 60) String subclass,
        @NotNull @Min(1) @Max(20) Integer level
) {
}
