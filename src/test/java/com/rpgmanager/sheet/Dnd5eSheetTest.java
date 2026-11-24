package com.rpgmanager.sheet;

import com.rpgmanager.sheet.model.dnd.Ability;
import com.rpgmanager.sheet.model.dnd.AbilityScores;
import com.rpgmanager.sheet.model.dnd.ClassLevel;
import com.rpgmanager.sheet.model.dnd.Dnd5eSheet;
import com.rpgmanager.sheet.model.dnd.DndSkill;
import com.rpgmanager.sheet.model.dnd.HitPoints;
import com.rpgmanager.sheet.model.dnd.ProficiencyLevel;
import com.rpgmanager.sheet.model.dnd.SkillProficiency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class Dnd5eSheetTest {

    @ParameterizedTest(name = "nível {0} → bônus de proficiência +{1}")
    @CsvSource({"1,2", "4,2", "5,3", "8,3", "9,4", "13,5", "17,6", "20,6"})
    void proficiencyBonusFollowsLevelTable(int level, int expectedBonus) {
        assertThat(sheet(List.of(new ClassLevel("Fighter", null, level)), List.of()).proficiencyBonus())
                .isEqualTo(expectedBonus);
    }

    @Test
    void multiclassLevelsAreSummed() {
        Dnd5eSheet sheet = sheet(List.of(
                new ClassLevel("Fighter", "Champion", 3),
                new ClassLevel("Wizard", null, 2)), List.of());

        assertThat(sheet.totalLevel()).isEqualTo(5);
        assertThat(sheet.proficiencyBonus()).isEqualTo(3);
    }

    @Test
    void abilityModifiersRoundDown() {
        AbilityScores scores = new AbilityScores(8, 16, 14, 10, 13, 12);

        assertThat(scores.modifier(Ability.STR)).isEqualTo(-1);
        assertThat(scores.modifier(Ability.DEX)).isEqualTo(3);
        assertThat(scores.modifier(Ability.WIS)).isEqualTo(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    void derivedValuesApplyProficiencyAndExpertise() {
        Dnd5eSheet sheet = sheet(List.of(new ClassLevel("Rogue", "Thief", 5)), List.of(
                new SkillProficiency(DndSkill.STEALTH, ProficiencyLevel.EXPERTISE),
                new SkillProficiency(DndSkill.PERCEPTION, null)));

        Map<String, Object> derived = sheet.computeDerived();
        Map<DndSkill, Integer> skills = (Map<DndSkill, Integer>) derived.get("skills");
        Map<Ability, Integer> saves = (Map<Ability, Integer>) derived.get("savingThrows");

        assertThat(skills.get(DndSkill.STEALTH)).isEqualTo(3 + 2 * 3);   // DEX +3, expertise
        assertThat(skills.get(DndSkill.PERCEPTION)).isEqualTo(1 + 3);    // WIS +1, proficiente
        assertThat(skills.get(DndSkill.ATHLETICS)).isEqualTo(-1);        // STR -1, sem treino
        assertThat(saves.get(Ability.DEX)).isEqualTo(3 + 3);
        assertThat(saves.get(Ability.STR)).isEqualTo(-1);
        assertThat(derived.get("passivePerception")).isEqualTo(14);
        assertThat(derived.get("initiative")).isEqualTo(3);
    }

    private static Dnd5eSheet sheet(List<ClassLevel> classes, List<SkillProficiency> skills) {
        return new Dnd5eSheet("Halfling", classes, "Criminal", "CN", 0,
                new AbilityScores(8, 16, 14, 10, 13, 12),
                new HitPoints(33, 33, 0), 15, 25,
                Set.of(Ability.DEX, Ability.INT), skills,
                List.of(), List.of(), List.of(), List.of(), null);
    }
}
