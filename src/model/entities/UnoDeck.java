package model.entities;

import client_server.UserThread;
import model.enums.CardType;
import model.enums.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import static model.enums.Color.*;

public class UnoDeck {
    private final List<Card> cards;
    private static final int WILD_CARD_PER_TYPE = 4;
    private static final int ACTION_CARD_PER_TYPE = 2;
    private static final int NUMBER_CARD_MAX = 9;
    private static final int NUMBER_CARDS_PLAYER = 7;
    private static final Color[] colors = {BLUE, GREEN, YELLOW, RED};

    public UnoDeck() {
        this.cards = new ArrayList<>();
        init();
    }

    private void init() {
        addWildCards();
        for (Color color : colors) {
            addActionCards(color);
            addNumberCards(color);
        }
        Collections.shuffle(cards);
    }

    public void addCardToDeck(Card card) {
        cards.add(card);
    }

    public void addCardsToDeck(List<Card> playerDeck) {
        cards.addAll(playerDeck);
    }

    public Card dealCard() {
        return cards.remove(0);
    }

    public void dealCards(Deque<UserThread> players) {
        for (UserThread player : players) {
            List<Card> playerDeck = new ArrayList<>();

            for (int i = 0; i < NUMBER_CARDS_PLAYER; i++)
                playerDeck.add(dealCard());

            player.setDeck(new PlayerDeck(playerDeck));
        }
    }

    private void addWildCards() {
        for (int i = 0; i < WILD_CARD_PER_TYPE; i++) {
            addCardToDeck(new WildCard(Color.WILD, CardType.CHANGE_COLOR));
            addCardToDeck(new WildCard(Color.WILD, CardType.DRAW_FOUR));
        }
    }

    private void addActionCards(Color color) {
        for (int i = 0; i < ACTION_CARD_PER_TYPE; i++) {
            addCardToDeck(new ActionCard(color, CardType.REVERSE));
            addCardToDeck(new ActionCard(color, CardType.SKIP));
            addCardToDeck(new ActionCard(color, CardType.DRAW_TWO));
        }
    }

    private void addNumberCards(Color color) {
        addCardToDeck(new NumberCard(color, 0));
        for (int i = 1; i <= NUMBER_CARD_MAX; i++) {
            addCardToDeck(new NumberCard(color, i));
            addCardToDeck(new NumberCard(color, i));
        }
    }

    public List<Card> getCards() {
        return cards;
    }

    @Override
    public String toString() {
        return cards.toString();
    }
}
