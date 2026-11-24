package com.rpgmanager.sheet.model.ordem;

import com.rpgmanager.campaign.GameSystem;
import com.rpgmanager.sheet.model.CharacterSheetData;
import com.rpgmanager.sheet.model.Item;
import com.rpgmanager.sheet.model.ResourcePool;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ficha de Ordem Paranormal. {@code nex} aceita 0 para personagens civis que ainda não tiveram
 * contato com o paranormal. {@code characterClass} é opcional pelo mesmo motivo.
 */
public record OrdemParanormalSheet(
        OrdemClass characterClass,
        @Size(max = 60) String origin,
        @Size(max = 60) String trail,
        @NotNull @Min(0) @Max(99) Integer nex,
        @NotNull @Valid OrdemAttributes attributes,
        @NotNull @Valid ResourcePool hitPoints,
        @Valid ResourcePool effortPoints,
        @NotNull @Valid ResourcePool sanity,
        @Min(0) @Max(60) Integer defense,
        @Size(max = 28) List<@Valid OrdemSkill> skills,
        @Size(max = 100) List<@Valid Ritual> rituals,
        @Size(max = 100) List<@Size(max = 200) String> powers,
        @Size(max = 300) List<@Valid Item> inventory,
        @Size(max = 5000) String notes
) implements CharacterSheetData {

    @Override
    public GameSystem system() {
        return GameSystem.ORDEM_PARANORMAL;
    }

    @Override
    public Map<String, Object> computeDerived() {
        Map<String, Integer> skillBonuses = new LinkedHashMap<>();
        if (skills != null) {
            for (OrdemSkill skill : skills) {
                String key = skill.detail() == null || skill.detail().isBlank()
                        ? skill.skill().name()
                        : skill.skill().name() + " (" + skill.detail() + ")";
                skillBonuses.put(key, skill.totalBonus());
            }
        }

        Map<Element, Long> ritualsByElement = new EnumMap<>(Element.class);
        if (rituals != null) {
            for (Ritual ritual : rituals) {
                ritualsByElement.merge(ritual.element(), 1L, Long::sum);
            }
        }

        Map<String, Object> derived = new LinkedHashMap<>();
        derived.put("trainedSkillBonuses", skillBonuses);
        derived.put("ritualsByElement", ritualsByElement);
        derived.put("sanityPercent", percent(sanity));
        derived.put("hitPointsPercent", percent(hitPoints));
        return derived;
    }

    private static int percent(ResourcePool pool) {
        if (pool == null || pool.max() == null || pool.max() == 0) {
            return 0;
        }
        return Math.round(100f * pool.current() / pool.max());
    }
}
