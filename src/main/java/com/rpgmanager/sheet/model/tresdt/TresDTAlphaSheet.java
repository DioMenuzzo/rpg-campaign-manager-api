package com.rpgmanager.sheet.model.tresdt;

import com.rpgmanager.campaign.GameSystem;
import com.rpgmanager.sheet.model.CharacterSheetData;
import com.rpgmanager.sheet.model.Item;
import com.rpgmanager.sheet.model.ResourcePool;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ficha de 3D&T Alpha.
 *
 * <p>{@code hitPoints} e {@code magicPoints} guardam os valores reais do personagem, que podem passar da
 * base (R×5) com vantagens como Pontos de Vida Extras. Por isso a base é só calculada, não imposta.</p>
 */
public record TresDTAlphaSheet(
        @Size(max = 60) String archetype,
        @Size(max = 60) String kit,
        @Min(0) Integer totalPoints,
        @Min(0) Integer experiencePoints,
        @NotNull @Valid Characteristics characteristics,
        @NotNull @Valid ResourcePool hitPoints,
        @NotNull @Valid ResourcePool magicPoints,
        @Size(max = 50) List<@Valid Trait> advantages,
        @Size(max = 50) List<@Valid Trait> disadvantages,
        @Size(max = 30) List<@Size(max = 80) String> skills,
        @Size(max = 100) List<@Size(max = 80) String> spells,
        @Size(max = 300) List<@Valid Item> inventory,
        @Size(max = 5000) String notes
) implements CharacterSheetData {

    @Override
    public GameSystem system() {
        return GameSystem.TRES_D_T_ALPHA;
    }

    /** Pontos gastos: Características + vantagens − desvantagens. */
    public int pointsSpent() {
        return characteristics.total() + sum(advantages) - sum(disadvantages);
    }

    @Override
    public Map<String, Object> computeDerived() {
        Characteristics c = characteristics;
        Map<String, Object> derived = new LinkedHashMap<>();
        // Valores fixos; na mesa soma-se 1d6 a cada um
        derived.put("meleeAttack", c.skill() + c.strength());     // FA corpo a corpo = H + F + 1d
        derived.put("rangedAttack", c.skill() + c.firepower());   // FA à distância   = H + PdF + 1d
        derived.put("defense", c.skill() + c.armor());            // FD               = H + A + 1d
        derived.put("baseHitPoints", c.resistance() * 5);
        derived.put("baseMagicPoints", c.resistance() * 5);
        derived.put("pointsSpent", pointsSpent());
        if (totalPoints != null) {
            derived.put("pointsRemaining", totalPoints - pointsSpent());
        }
        return derived;
    }

    private static int sum(List<Trait> traits) {
        return traits == null ? 0 : traits.stream().mapToInt(Trait::pointsOrZero).sum();
    }
}
