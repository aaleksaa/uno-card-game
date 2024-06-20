package model.card;

import model.enums.CardType;
import model.enums.Color;

import java.util.Objects;

/**
 * The NumberCard class represents a card with a numeric value.
 * It extends the Card class and includes specific behavior for number cards.
 */
public class NumberCard extends Card {
    private final int value;

    /**
     * Constructs a NumberCard with the specified color and value.
     *
     * @param color the color of the card
     * @param value the numeric value of the card
     */
    public NumberCard(Color color, int value) {
        super(color, CardType.NUMBER);
        this.value = value;
    }

    /**
     * Returns the numeric value of the card.
     *
     * @return the numeric value of the card
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns a string representation of the NumberCard.
     * The format is "color-value".
     *
     * @return a string representation of the card
     */
    @Override
    public String toString() {
        return color + "-" + value;
    }

    /**
     * Compares this card to the specified object for equality.
     * Two NumberCards are considered equal if they have the same color and value.
     *
     * @param obj the object to compare with
     * @return true if this card is equal to the specified object, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        NumberCard card = (NumberCard) obj;
        return color == card.getColor() && value == card.getValue();
    }

    /**
     * Returns a hash code value for the card.
     * The hash code is based on the card's color and value.
     *
     * @return a hash code value for this card
     */
    @Override
    public int hashCode() {
        return Objects.hash(color, value);
    }
}
