package model.entities;

import model.enums.Color;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerDeck {
    private final List<Card> cards;

    public PlayerDeck(List<Card> cards) {
        this.cards = cards;
    }

    public List<Card> getCards() {
        return cards;
    }

    public int getNumberOfCards() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    @Override
    public String toString() {
        return printCards(cards);
    }

    private String printCards(List<Card> cards) {
        return cards.stream().map(Card::toString).collect(Collectors.joining(" "));
    }

    public String availableCards(Card currentCard, Color currentColor, boolean colorChanged) {
        List<Card> availableCards;

        if (!colorChanged)
            availableCards = cards.stream().filter(card -> Card.compareCards(card, currentCard)).toList();
        else
            availableCards = cards.stream().filter(card -> card.getColor() == currentColor || card instanceof WildCard).toList();

        return availableCards.isEmpty() ? "NO_CARDS" : "UNBLOCK " + printCards(availableCards);
    }


    public Card getCardFromDeck(String cardString) {
        Card inputCard = Card.fromString(cardString);

        System.out.println(inputCard);

        return cards.stream().filter(card -> card.equals(inputCard)).findFirst().orElse(null);
    }
}
