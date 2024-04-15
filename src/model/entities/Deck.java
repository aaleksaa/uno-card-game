package model.entities;

import model.enums.Action;
import model.enums.Color;
import model.enums.SpecialAction;

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
        addSpecialCards();

        addActionCards(Color.RED);
        addActionCards(Color.YELLOW);
        addActionCards(Color.BLUE);
        addActionCards(Color.GREEN);

        addNumberCards(Color.RED);
        addNumberCards(Color.YELLOW);
        addNumberCards(Color.BLUE);
        addNumberCards(Color.GREEN);
    }

    private void addCardToDeck(Card card) {
        cards.add(card);
    }

    private void removeCardFromDeck(Card card) {
        cards.remove(card);
    }

    private void addSpecialCards() {
        for (int i = 0; i < 4; i++) {
            addCardToDeck(new SpecialCard(SpecialAction.DRAW_FOUR));
            addCardToDeck(new SpecialCard(SpecialAction.CHANGE_COLOR));
        }
    }

    private void addActionCards(Color color) {
        for (int i = 0; i < 2; i++) {
            addCardToDeck(new ActionCard(color, Action.REVERSE));
            addCardToDeck(new ActionCard(color, Action.SKIP));
            addCardToDeck(new ActionCard(color, Action.DRAW_TWO));
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
