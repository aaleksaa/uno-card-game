package model.entities;

import java.util.List;

public class PlayerDeck {
    private List<Card> cards;
    private int numberOfCards;

    public PlayerDeck(List<Card> cards) {
        this.cards = cards;
        this.numberOfCards = cards.size();
    }

    public List<Card> getCards() {
        return cards;
    }

    public int getNumberOfCards() {
        return numberOfCards;
    }

    public void removeCard(Card card) {
        cards.remove(card);
        numberOfCards--;
    }

    public void addCard(Card card) {
        cards.add(card);
        numberOfCards++;
    }

    @Override
    public String toString() {
        return cards.toString();
    }

    public Card getCard(String cardString) {
        Card inputCard = Card.fromString(cardString);

        for (Card card : cards)
            if (card.equals(inputCard))
                return card;
        return null;
    }
}
