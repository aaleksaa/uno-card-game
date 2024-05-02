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
}
