package model.deck;

import model.card.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * The Deck class represents a deck of cards.
 * It provides methods for managing the cards in the deck,
 * such as adding, removing, and dealing cards.
 */
public class Deck {
    protected List<Card> cards;

    /**
     * Constructs an empty deck of cards.
     */
    public Deck() {
        this.cards = new ArrayList<>();
    }

    /**
     * Constructs a deck of cards with the given list of cards.
     *
     * @param cards the initial list of cards
     */
    public Deck(List<Card> cards) {
        this.cards = cards;
    }

    /**
     * Returns the list of cards in the deck.
     *
     * @return the list of cards
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * Checks if the deck is empty.
     *
     * @return True if the deck is empty, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Returns the number of cards in the deck.
     *
     * @return the number of cards
     */
    public int getNumberOfCards() {
        return cards.size();
    }

    /**
     * Adds a card to the deck.
     *
     * @param card the card to be added
     */
    public void addCardToDeck(Card card) {
        cards.add(card);
    }

    /**
     * Adds a list of cards to the deck.
     *
     * @param deck the list of cards to be added
     */
    public void addCardsToDeck(List<Card> deck) {
        cards.addAll(deck);
    }

    /**
     * Deals (removes and returns) the top card from the deck.
     *
     * @return the top card from the deck
     */
    public Card dealCard() {
        return cards.remove(0);
    }

    /**
     * Removes a specific card from the deck.
     *
     * @param card the card to be removed
     */
    public void removeCard(Card card) {
        cards.remove(card);
    }

    /**
     * Returns a string representation of the deck.
     *
     * @return a string representation of the deck
     */
    @Override
    public String toString() {
        return Card.printCards(cards);
    }
}
