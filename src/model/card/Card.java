package model.card;

import model.enums.CardType;
import model.enums.Color;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The abstract class Card represents a generic card with a color and a type.
 * This class serves as a base for different types of cards in a Uno card game.
 */
public abstract class Card {
    protected Color color;
    protected CardType cardType;

    /**
     * Constructs a Card with the specified color and card type.
     *
     * @param color the color of the card
     * @param cardType the type of the card
     */
    public Card(Color color, CardType cardType) {
        this.color = color;
        this.cardType = cardType;
    }

    /**
     * Returns the color of the card.
     *
     * @return the color of the card
     */
    public Color getColor() {
        return color;
    }

    /**
     * Returns the type of the card.
     *
     * @return the type of the card
     */
    public CardType getCardType() {
        return cardType;
    }

    /**
     * Returns a string representation of the Card.
     * The format is "color-cardType".
     *
     * @return a string representation of the card
     */
    @Override
    public String toString() {
        return color + "-" + cardType;
    }

    /**
     * Creates a Card instance from a string representation.
     * The string should be in the format "color-type".
     *
     * @param input the string representation of the card
     * @return a Card instance based on the input string
     */
    public static Card fromString(String input) {
        String[] parts = input.split("-");
        Color inputColor = Color.fromString(parts[0]);
        CardType inputType = CardType.fromString(parts[1]);

        if (inputType == null)
            return new NumberCard(inputColor, Integer.parseInt(parts[1]));
        else if (inputType == CardType.DRAW_FOUR || inputType == CardType.CHANGE_COLOR)
            return new WildCard(inputColor, inputType);
        else
            return new ActionCard(inputColor, inputType);
    }

    /**
     * Compares two cards to determine if they can be played on each other.
     *
     * @param card1 the first card to compare
     * @param card2 the second card to compare
     * @return true if the cards can be played on each other, false otherwise
     */
    public static boolean compareCards(Card card1, Card card2) {
        if (card1.getColor() == card2.getColor())
            return true;
        else if (card1 instanceof WildCard)
            return true;
        else if (card1 instanceof NumberCard numberCard1 && card2 instanceof NumberCard numberCard2)
            return numberCard1.getValue() == numberCard2.getValue();
        else if (card1 instanceof ActionCard actionCard1 && card2 instanceof ActionCard actionCard2)
            return actionCard1.getCardType() == actionCard2.getCardType();
        return false;
    }

    /**
     * Returns a string representation of the list of cards.
     *
     * @return a string representation of the list of cards
     */
    public static String printCards(List<Card> cards) {
        return cards.stream().map(Card::toString).collect(Collectors.joining(" "));
    }
}
