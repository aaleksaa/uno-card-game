package model.deck;

import model.card.Card;
import model.card.WildCard;
import model.enums.Color;

import java.util.List;

/**
 * The PlayerDeck class represents a player's deck in a card game.
 * It extends the Deck class and provides additional functionality specific to a player's deck.
 */
public class PlayerDeck extends Deck {
    /**
     * Constructs a PlayerDeck with the given list of cards.
     *
     * @param cards the list of cards in the player's deck
     */
    public PlayerDeck(List<Card> cards) {
        super(cards);
    }

    /**
     * Retrieves a string representation of the playable cards based on the current card and color.
     *
     * @param currentCard the current card in play
     * @param currentColor the current color in play
     * @param colorChanged flag indicating if the color has changed
     * @return a string representation of the playable cards or "NO_CARDS" if no cards are playable
     */
    public String getPlayableCards(Card currentCard, Color currentColor, boolean colorChanged) {
        List<Card> availableCards;

        if (!colorChanged)
            availableCards = cards.stream().filter(card -> Card.compareCards(card, currentCard)).toList();
        else
            availableCards = cards.stream().filter(card -> card.getColor() == currentColor || card instanceof WildCard).toList();

        return availableCards.isEmpty() ? "NO_CARDS" : "UNBLOCK UNBLOCK " + Card.printCards(availableCards);
    }

    /**
     * Retrieves a card from the deck based on its string representation.
     *
     * @param cardString the string representation of the card to retrieve
     * @return the Card object if found, or null if not found
     */
    public Card getCardFromDeck(String cardString) {
        Card inputCard = Card.fromString(cardString);

        return cards.stream().filter(card -> card.equals(inputCard)).findFirst().orElse(null);
    }
}
