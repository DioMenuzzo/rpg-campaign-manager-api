package com.rpgmanager.sheet;

import com.rpgmanager.sheet.model.ResourcePool;
import com.rpgmanager.sheet.model.tresdt.Characteristics;
import com.rpgmanager.sheet.model.tresdt.TresDTAlphaSheet;
import com.rpgmanager.sheet.model.tresdt.Trait;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TresDTAlphaSheetTest {

    @Test
    void computesAttackDefenseAndBasePoints() {
        // F0 H3 R2 A1 PdF1
        Map<String, Object> derived = sheet(new Characteristics(0, 3, 2, 1, 1), null, List.of(), List.of())
                .computeDerived();

        assertThat(derived.get("meleeAttack")).isEqualTo(3);
        assertThat(derived.get("rangedAttack")).isEqualTo(4);
        assertThat(derived.get("defense")).isEqualTo(4);
        assertThat(derived.get("baseHitPoints")).isEqualTo(10);
        assertThat(derived.get("baseMagicPoints")).isEqualTo(10);
        assertThat(derived).doesNotContainKey("pointsRemaining");
    }

    @Test
    void pointsSpentCountsAdvantagesAndRefundsDisadvantages() {
        TresDTAlphaSheet sheet = sheet(new Characteristics(2, 2, 2, 1, 1), 12,
                List.of(new Trait("Aceleração", 1, null), new Trait("Ataque Múltiplo", 1, null)),
                List.of(new Trait("Código de Honra", 1, null)));

        // 8 de Características + 2 de vantagens − 1 de desvantagem
        assertThat(sheet.pointsSpent()).isEqualTo(9);
        assertThat(sheet.computeDerived().get("pointsRemaining")).isEqualTo(3);
    }

    private static TresDTAlphaSheet sheet(Characteristics c, Integer totalPoints,
                                          List<Trait> advantages, List<Trait> disadvantages) {
        return new TresDTAlphaSheet("Humano", null, totalPoints, 0, c,
                new ResourcePool(10, 10), new ResourcePool(10, 10),
                advantages, disadvantages, List.of(), List.of(), List.of(), null);
    }
}
