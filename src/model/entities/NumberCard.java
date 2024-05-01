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
        return color + " - " + number;
    }

    @Override
    public boolean match(String[] parts) {
        Color inputColor = Color.fromString(parts[0]);
        int inputNumber = Integer.parseInt(parts[1]);

        return color.equals(inputColor) && number == inputNumber;
    }
}
