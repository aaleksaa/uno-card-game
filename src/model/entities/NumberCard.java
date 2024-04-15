package model.entities;

import model.enums.Action;
import model.enums.Color;

public class NumberCard extends Card {
    private final int number;

    public NumberCard(Color color, int number) {
        super(color);
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "[" + color + " - " + number + "]";
    }
}
