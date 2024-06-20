package model.deck;

import client_server.ClientThread;
import model.card.ActionCard;
import model.card.Card;
import model.card.NumberCard;
import model.card.WildCard;
import model.enums.CardType;
import model.enums.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import static model.enums.Color.*;

/**
 * The UnoDeck class represents the deck of cards used in the Uno game.
 * It extends the Deck class and includes methods to initialize the deck,
 * shuffle the cards, and deal cards to players.
 */
public class UnoDeck extends Deck {
    private static final int WILD_CARD_PER_TYPE = 4;
    private static final int ACTION_CARD_PER_TYPE = 2;
    private static final int NUMBER_CARD_MAX = 9;
    private static final int NUMBER_CARDS_PLAYER = 7;
    private static final Color[] colors = {BLUE, GREEN, YELLOW, RED};

    /**
     * Constructs a new UnoDeck and initializes it with all Uno cards.
     */
    public UnoDeck() {
        super();
        init();
    }

    /**
     * Initializes the Uno deck with wild cards, action cards, and number cards.
     * Shuffles the deck after adding all cards.
     */
    private void init() {
        addWildCards();
        for (Color color : colors) {
            addActionCards(color);
            addNumberCards(color);
        }
        Collections.shuffle(cards);
    }

    /**
     * Deals cards to players in game.
     *
     * @param players the list of players to deal cards to
     */
    public void setInitialDeckToPlayers(Deque<ClientThread> players) {
        for (ClientThread player : players) {
            List<Card> playerDeck = new ArrayList<>();

            for (int i = 0; i < NUMBER_CARDS_PLAYER; i++)
                playerDeck.add(dealCard());

            player.setDeck(new PlayerDeck(playerDeck));
        }
    }

    /**
     * Adds wild cards (Change Color and Draw Four) to the Uno deck.
     */
    private void addWildCards() {
        for (int i = 0; i < WILD_CARD_PER_TYPE; i++) {
            addCardToDeck(new WildCard(Color.WILD, CardType.CHANGE_COLOR));
            addCardToDeck(new WildCard(Color.WILD, CardType.DRAW_FOUR));
        }
    }

    /**
     * Adds action cards (Reverse, Skip, Draw Two) of a specific color to the Uno deck.
     *
     * @param color the color of action cards to add
     */
    private void addActionCards(Color color) {
        for (int i = 0; i < ACTION_CARD_PER_TYPE; i++) {
            addCardToDeck(new ActionCard(color, CardType.REVERSE));
            addCardToDeck(new ActionCard(color, CardType.SKIP));
            addCardToDeck(new ActionCard(color, CardType.DRAW_TWO));
        }
    }

    /**
     * Adds number cards (0 to 9) of a specific color to the Uno deck.
     *
     * @param color the color of number cards to add
     */
    private void addNumberCards(Color color) {
        addCardToDeck(new NumberCard(color, 0));
        for (int i = 1; i <= NUMBER_CARD_MAX; i++) {
            addCardToDeck(new NumberCard(color, i));
            addCardToDeck(new NumberCard(color, i));
        }
    }
}
