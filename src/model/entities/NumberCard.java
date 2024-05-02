package model.entities;

import model.enums.CardType;
import model.enums.Color;

import java.util.Objects;

public class NumberCard extends Card {
    private final int value;

    public NumberCard(Color color, int value) {
        super(color, CardType.NUMBER);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return color + " - " + value;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        NumberCard card = (NumberCard) obj;
        return color == card.getColor() && value == card.getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, value);
    }
}
