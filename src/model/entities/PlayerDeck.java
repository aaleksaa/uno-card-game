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

    @Override
    public String toString() {
        return cards.toString();
    }
}
