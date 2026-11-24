package com.rpgmanager.sheet.model.ordem;

/** Grau de treinamento numa perícia e o bônus que ele concede. */
public enum TrainingDegree {
    DESTREINADO(0),
    TREINADO(5),
    VETERANO(10),
    EXPERT(15);

    private final int bonus;

    TrainingDegree(int bonus) {
        this.bonus = bonus;
    }

    public int bonus() {
        return bonus;
    }
}
