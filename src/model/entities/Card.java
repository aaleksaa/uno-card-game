package model.entities;

import model.enums.CardType;
import model.enums.Color;

public abstract class Card {
    protected Color color;
    protected CardType cardType;

    public Card(Color color, CardType cardType) {
        this.color = color;
        this.cardType = cardType;
    }

    public Color getColor() {
        return color;
    }

    public CardType getCardType() {
        return cardType;
    }

    public static Card fromString(String input) {
        String[] parts = input.split("-");
        Color inputColor = Color.fromString(parts[0]);
        CardType inputType = CardType.fromString(parts[1]);

        if (inputType == null)
            return new NumberCard(inputColor, Integer.parseInt(parts[1]));
        else if (inputType == CardType.DRAW_FOUR || inputType == CardType.CHANGE_COLOR)
            return new WildCard(inputType);
        else
            return new ActionCard(inputColor, inputType);
    }

    public static boolean compareCards(Card card1, Card card2) {
        if (card1.getColor() == card2.getColor())
            return true;
        else if (card1 instanceof WildCard)
            return true;
        else if (card1 instanceof NumberCard numberCard1 && card2 instanceof NumberCard numberCard2)
            return numberCard1.getValue() == numberCard2.getValue();
        else if (card1 instanceof ActionCard actionCard1 && card2 instanceof ActionCard actionCard2)
            return actionCard1.getCardType() == actionCard2.getCardType();
        else
            return false;
    }
}
