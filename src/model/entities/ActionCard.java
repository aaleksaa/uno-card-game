package model.entities;

import model.enums.CardType;
import model.enums.Color;

import java.util.Objects;

public class ActionCard extends Card {
    public ActionCard(Color color, CardType cardType) {
        super(color, cardType);
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

        ActionCard card = (ActionCard) obj;
        return color == card.getColor() && cardType == card.getCardType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, cardType);
    }
}
