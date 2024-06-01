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
    private Direction direction;
    private boolean colorChanged;
    private List<UserThread> spectators = new ArrayList<>();

    public static final int NUMBER_OF_CARDS = 7;

    public Uno(Server server, Lobby lobby, Set<UserThread> players) {
        this.server = server;
        this.lobby = lobby;
        this.deck = new Deck();
        this.queue.addAll(players);
        this.test.addAll(players);
        dealCards();
        initCurrentCard();
        this.currentColor = currentCard.getColor();
        this.playerOnMove = queue.peek();
        this.direction = Direction.CLOCKWISE;
        sendGameInfo();
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

    private void initCurrentCard() {
        do {
            currentCard = deck.dealCard();
        } while (!(currentCard instanceof NumberCard));
    }


    private boolean isFinished() {
        return queue.size() == 1;
    }

    private UserThread getCurrentPlayer() {
        if (direction == Direction.CLOCKWISE)
            return test.pollFirst();
        return test.pollLast();
    }

    private UserThread playerOnMove() {
        if (direction == Direction.CLOCKWISE)
            return test.peekFirst();
        return null;
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

        deck.addCardToDeck(card);

        if (!isFinished()) {
            sendGameInfo();
            server.broadcastInGame(lobby, "BLOCK");

            if (!colorChanged)
                playerOnMove.sendMessage(playerOnMove.getDeck().availableCards(currentCard, currentColor, colorChanged));
        } else {
            spectators.add(queue.poll());
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < spectators.size(); i++)
                sb.append(i + 1).append(" - ").append(spectators.get(i)).append("  ");


            server.broadcastInGame(lobby, "REMOVE LOBBY " + lobby.getLobbyName());
            server.removeLobby(lobby);
            server.broadcastInGame(lobby, "FINISH " + sb);
        }
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

    private void sendGameInfo() {
        StringBuilder sb = new StringBuilder();
        for (UserThread player : queue)
            sb.append(player.getUsername()).append(" - ").append(player.getDeck().getNumberOfCards()).append(" ");

        server.broadcastInGame(lobby, "CURRENT " + currentCard);
        server.broadcastInGame(lobby, "GAME_INFO CURR_PLAYER Current player " + playerOnMove);
        server.broadcastInGame(lobby, "GAME_INFO CARDS_NUM " + sb);
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
            spectators.add(player);
    }


    public void setCurrentColor(Color color) {
        this.currentColor = color;
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

    public void dealCards() {
        for (UserThread player : queue) {
            List<Card> cards = new ArrayList<>();

            for (int i = 0; i < NUMBER_OF_CARDS; i++)
                cards.add(deck.dealCard());

            player.setDeck(new PlayerDeck(cards));
        }
    }

    public void draw() {
        Card card = deck.dealCard();
        playerOnMove.getDeck().addCard(card);
        playerOnMove.sendMessage("DRAW " + card);
        playerOnMove.sendMessage("BLOCK");
        playerOnMove.sendMessage(playerOnMove.getDeck().availableCards(currentCard, currentColor, colorChanged));
        sendGameInfo();
    }

    public void setCurrentCard(Card currentCard) {
        this.currentCard = currentCard;
    }
}
