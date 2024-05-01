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

    public void incrementNumberOfCards(int number) {
        numberOfCards += number;
    }

    public void decrementNumberOfCards() {
        numberOfCards--;
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    @Override
    public String toString() {
        return cards.toString();
    }

    public Card getCard(String cardString) {
        String[] parts = cardString.split("-");

        for (Card card : cards)
            if (card.match(parts))
                return card;
        return null;
    }
}
