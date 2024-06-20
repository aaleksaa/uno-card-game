package model.card;

import model.enums.CardType;
import model.enums.Color;

import java.util.Objects;

/**
 * The WildCard class represents a "Change color" and "Draw four" cards in Uno card game.
 * It extends the Card class and includes specific behavior for wild cards.
 */
public class WildCard extends Card {
    /**
     * Constructs a WildCard with the specified color and card type.
     *
     * @param color the color of the card
     * @param cardType the type of the card
     */
    public WildCard(Color color, CardType cardType) {
        super(color, cardType);
    }

    /**
     * Compares this card to the specified object for equality.
     * Two WildCards are considered equal if they have the same color and card type.
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

        WildCard card = (WildCard) obj;
        return color == card.getColor() && cardType == card.getCardType();
    }


    /**
     * Returns a hash code value for the card.
     * The hash code is based on the card's color and type.
     *
     * @return a hash code value for this card
     */
    @Override
    public int hashCode() {
        return Objects.hash(color, cardType);
    }
}
