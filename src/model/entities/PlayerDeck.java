package model.entities;

import model.enums.Color;

import java.util.ArrayList;
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

    public boolean isEmpty() {
        return numberOfCards == 0;
    }

    public void removeCard(Card card) {
        cards.remove(card);
        numberOfCards--;
    }

    public void addCard(Card card) {
        cards.add(card);
        numberOfCards++;
    }

    public String getCardsString() {
        StringBuilder sb = new StringBuilder();

        for (Card card : cards)
            sb.append(card).append(" ");

        return sb.toString();
    }

    public String availableCards(Card currentCard, Color currentColor, boolean colorChanged) {
        StringBuilder sb = new StringBuilder();

        if (!colorChanged) {
            for (Card card : cards)
                if (Card.compareCards(card, currentCard))
                    sb.append(card).append(" ");
        } else {
            for (Card card : cards)
                if (card.getColor() == currentColor || card instanceof WildCard)
                    sb.append(card).append(" ");
        }

        return sb.toString().isEmpty() ? "NO_CARDS" : "UNBLOCK " + sb;
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
