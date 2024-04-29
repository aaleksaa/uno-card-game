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

    @Override
    public String toString() {
        return cards.toString();
    }

    public Card getCard(String cardString) {
        String[] parts = cardString.split("_");

        Card target = null;

        for (Card card : cards) {
            if (card instanceof NumberCard) {
                NumberCard nc = (NumberCard) card;

                if (nc.getColor().getColor().equals(parts[0]) && nc.getNumber() == Integer.parseInt(parts[1]))
                    return nc;

            } else if (card instanceof ActionCard) {
                ActionCard ac = (ActionCard) card;

                if (ac.getColor().getColor().equals(parts[0]) && ac.getAction().getAction().equals(parts[1]))
                    return ac;
            } else  {
                WildCard wc = (WildCard) card;
            }
        }

        return null;
    }
}
