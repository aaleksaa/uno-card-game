package model.entities;

import model.enums.Action;
import model.enums.Color;
import model.enums.WildAction;

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
        addWildCard();

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

    public void removeCardFromDeck() {
        cards.remove(0);
    }

    public Card dealCard() {
        return cards.get(0);
    }

    private void addWildCard() {
        for (int i = 0; i < 4; i++) {
            addCardToDeck(new WildCard(WildAction.DRAW_FOUR));
            addCardToDeck(new WildCard(WildAction.CHANGE_COLOR));
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
