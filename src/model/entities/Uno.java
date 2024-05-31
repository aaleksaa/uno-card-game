package model.entities;

import client.UserThread;
import model.enums.CardType;
import model.enums.Color;
import model.enums.Direction;
import server.Lobby;
import server.Server;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Uno {
    private Server server;
    private Lobby lobby;
    private Deck deck;
    private Queue<UserThread> queue = new ConcurrentLinkedQueue<>();
    private Deque<UserThread> test = new ConcurrentLinkedDeque<>();
    private Card currentCard;
    private Color currentColor;
    private UserThread playerOnMove;
    private Map<String, Integer> rankings = new TreeMap<>();
    private Set<UserThread> players;
    private Direction direction;
    private boolean colorChanged;

    public static final int NUMBER_OF_CARDS = 7;

    public Uno(Server server, Lobby lobby, Set<UserThread> players) {
        this.server = server;
        this.lobby = lobby;
        this.deck = new Deck();
        this.players = players;
        this.queue.addAll(players);
        dealCards();
        do {
            this.currentCard = deck.dealCard();
        } while (!(currentCard instanceof NumberCard));
        this.currentColor = currentCard.getColor();
        this.playerOnMove = queue.peek();
        this.direction = Direction.CLOCKWISE;
        server.broadcastInGame(lobby, "CURRENT " + currentCard);
        server.broadcastInGame(lobby, "STATUS " + getCurrentStatus());
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    private void changeDirection() {
        if (direction == Direction.CLOCKWISE)
            direction = Direction.COUNTER_CLOCKWISE;
        else
            direction = Direction.CLOCKWISE;
    }

    public Deck getDeck() {
        return deck;
    }

    public UserThread getPlayerOnMove() {
        return playerOnMove;
    }


    public synchronized void playMove(String move) {
        UserThread currPlayer = queue.poll();
        Card card = currPlayer.getDeck().getCard(move);

        if (card instanceof NumberCard)
            playNumberCard(currPlayer, card);
        else if (card instanceof ActionCard)
            playActionCard(currPlayer, card);
        else
            playWildCard(currPlayer, card);


        server.broadcastInGame(lobby, "STATUS " + getCurrentStatus());
        server.broadcastInGame(lobby, "CURRENT " + currentCard);
        server.broadcastInGame(lobby, "BLOCK");

        if (!colorChanged)
            playerOnMove.sendMessage("UNBLOCK " + playerOnMove.getDeck().availableCards(currentCard, currentColor, colorChanged));
    }
    public boolean isColorChanged() {
        return colorChanged;
    }

    private void playNumberCard(UserThread player, Card card) {
        player.getDeck().removeCard(card);
        currentCard = card;
        currentColor = card.getColor();
        colorChanged = false;


        addToQueue(player);
        playerOnMove = queue.peek();
    }

    private void playActionCard(UserThread player, Card card) {
        player.getDeck().removeCard(card);
        currentCard = card;
        currentColor = card.getColor();
        colorChanged = false;


        addToQueue(player);

        if (card.getCardType() == CardType.DRAW_TWO) {
            playerOnMove = queue.peek();
            playerOnMove.sendMessage("DRAW " + drawCards(playerOnMove, 2));
        } else if (card.getCardType() == CardType.SKIP) {
            UserThread skippedPlayer = queue.poll();
            queue.add(skippedPlayer);
            playerOnMove = queue.peek();
        }
    }

    private void playWildCard(UserThread player, Card card) {
        player.getDeck().removeCard(card);
        currentCard = card;
        playerOnMove.sendMessage("CHANGE");
        colorChanged = true;

        addToQueue(player);

        playerOnMove = queue.peek();
        if (card.getCardType() == CardType.DRAW_FOUR)
            playerOnMove.sendMessage("DRAW " + drawCards(playerOnMove, 4));
    }

    private void addToQueue(UserThread player) {
        if (!player.getDeck().isEmpty())
            queue.add(player);
        else
            rankings.put(player.getUsername(), rankings.size() + 1);
    }


    public void setCurrentColor(Color color) {
        this.currentColor = color;
        server.broadcastInGame(lobby, "STATUS " + getCurrentStatus() + " Color is " + currentColor);
    }


    private String drawCards(UserThread player, int amount) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < amount; i++) {
            Card card = deck.dealCard();
            player.getDeck().addCard(card);
            sb.append(card).append(" ");
        }

        return sb.toString();
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public String getCurrentStatus() {
        StringBuilder sb = new StringBuilder();

        sb.append("Player on the move: ").append(playerOnMove).append(" ");
        sb.append("Number of cards ");

        for (UserThread player : queue)
            sb.append(player.getUsername()).append(" ").append(player.getDeck().getNumberOfCards()).append(", ");


        return sb.toString();
    }


    public void dealCards() {
        for (UserThread player : queue) {
            List<Card> cards = new ArrayList<>();

            for (int i = 0; i < NUMBER_OF_CARDS; i++) {
                cards.add(deck.dealCard());
                deck.removeCardFromDeck();
            }

            player.setDeck(new PlayerDeck(cards));
        }
    }

    public void setCurrentCard(Card currentCard) {
        this.currentCard = currentCard;
    }
}
