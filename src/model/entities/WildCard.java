package model.entities;

import model.enums.CardType;
import model.enums.Color;

import java.util.Objects;

public class WildCard extends Card {
    public WildCard(CardType cardType) {
        super(Color.WILD, cardType);
    }

    @Override
    public String toString() {
        return color + " - " + cardType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        WildCard card = (WildCard) obj;
        return color == card.getColor() && cardType == card.getCardType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, cardType);
    }
}
