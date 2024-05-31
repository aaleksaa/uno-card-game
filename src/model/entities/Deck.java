package model.entities;

import model.enums.CardType;
import model.enums.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards = new ArrayList<>();

    public Deck() {
        init();
        Collections.shuffle(cards);
    }

    private void init() {
//        addWildCards();

        addActionCards(Color.RED);
        addActionCards(Color.YELLOW);
        addActionCards(Color.BLUE);
        addActionCards(Color.GREEN);

        addNumberCards(Color.RED);
        addNumberCards(Color.YELLOW);
        addNumberCards(Color.BLUE);
        addNumberCards(Color.GREEN);
    }

    public void addCardToDeck(Card card) {
        cards.add(card);
    }

    public Card dealCard() {
        return cards.remove(0);
    }

    private void addWildCards() {
        for (int i = 0; i < 4; i++) {
            addCardToDeck(new WildCard(CardType.CHANGE_COLOR));
            addCardToDeck(new WildCard(CardType.DRAW_FOUR));
        }
    }

    private void addActionCards(Color color) {
        for (int i = 0; i < 2; i++) {
//            addCardToDeck(new ActionCard(color, CardType.REVERSE));
            addCardToDeck(new ActionCard(color, CardType.SKIP));
            addCardToDeck(new ActionCard(color, CardType.DRAW_TWO));
        }
    }

    private void addNumberCards(Color color) {
        for (int i = 0; i < 10; i++) {
            if (i == 0)
                addCardToDeck(new NumberCard(color, i));
            else {
                addCardToDeck(new NumberCard(color, i));
                addCardToDeck(new NumberCard(color, i));
            }
        }
    }

    @Override
    public String toString() {
        return cards.toString();
    }
}
