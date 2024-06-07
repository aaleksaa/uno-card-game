package model.entities;

import client_server.UserThread;
import model.enums.CardType;
import model.enums.Color;
import client_server.Server;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Uno {
    private final Server server;
    private final Lobby lobby;
    private final UnoDeck deck;
    private final Deque<UserThread> queue;
    private Card currentCard;
    private Color currentColor;
    private UserThread playerOnMove;
    private boolean directionChanged;
    private boolean colorChanged;
    private final List<UserThread> spectators;

    public Uno(Server server, Lobby lobby, Set<UserThread> players) {
        this.server = server;
        this.lobby = lobby;
        this.deck = new UnoDeck();
        this.queue = new ConcurrentLinkedDeque<>();
        this.queue.addAll(players);
        deck.dealCards(queue);
        initCurrentCard();
        this.currentColor = currentCard.getColor();
        this.playerOnMove = getNextPlayer();
        this.spectators = new ArrayList<>();
        sendGameInfo();
    }

    private void initCurrentCard() {
        while (true) {
            Card card = deck.dealCard();

            if (card instanceof NumberCard) {
                currentCard = card;
                break;
            }

            deck.addCardToDeck(card);
        }
    }

    public void removePlayer(UserThread player) {
        if (queue.contains(player))
            queue.remove(player);
        else
            spectators.remove(player);
    }

    public void returnCards(List<Card> cards) {
        deck.addCardsToDeck(cards);
    }

    public List<UserThread> getSpectators() {
        return spectators;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public UserThread getPlayerOnMove() {
        return playerOnMove;
    }

    public boolean isColorChanged() {
        return colorChanged;
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    private void changeDirection() {
        directionChanged = !directionChanged;
    }

    private boolean isFinished() {
        return queue.size() == 1;
    }

    private UserThread getCurrentPlayer() {
        return !directionChanged ? queue.pollFirst() : queue.pollLast();
    }

    private UserThread getNextPlayer() {
        return !directionChanged ? queue.peekFirst() : queue.peekLast();
    }

    public synchronized void playMove(String move) {
        UserThread currPlayer = getCurrentPlayer();
        Card card = currPlayer.getDeck().getCardFromDeck(move);

        if (card instanceof NumberCard)
            playNumberCard(currPlayer, card);
        else if (card instanceof ActionCard)
            playActionCard(currPlayer, card);
        else
            playWildCard(currPlayer, card);

        deck.addCardToDeck(card);

        if (!isFinished())
            continueGame();
        else
            endGame();
    }

    private void continueGame() {
        sendGameInfo();
        server.broadcastInGame(lobby, "BLOCK");

        if (!isColorChanged())
            playerOnMove.sendResponse(playerOnMove.getDeck().availableCards(currentCard, currentColor, colorChanged));
    }

    private void endGame() {
        spectators.add(queue.poll());
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < spectators.size(); i++)
            sb.append(i + 1).append(" - ").append(spectators.get(i)).append("  ");

        server.broadcastInGame(lobby, "REMOVE LOBBY " + lobby.getLobbyName());
        server.broadcastInGame(lobby, "FINISH FINISH " + sb);
        server.removeLobby(lobby);
    }

    private void playNumberCard(UserThread player, Card card) {
        handleCardPlay(player, card);
        addToQueue(player);
        playerOnMove = getNextPlayer();
    }

    private void handleCardPlay(UserThread player, Card card) {
        player.getDeck().removeCard(card);
        currentCard = card;
        currentColor = card.getColor();
        colorChanged = false;
    }

    private void skipNextPlayer(UserThread player) {
        addToQueue(player);
        UserThread skippedPlayer = getCurrentPlayer();
        addToQueue(skippedPlayer);
        playerOnMove = getNextPlayer();
    }

    private void reverseDirection(UserThread player) {
        if (queue.size() == 1)
            skipNextPlayer(player);
        else {
            changeDirection();
            addToQueue(player);
            playerOnMove = getNextPlayer();
        }
    }

    private void playActionCard(UserThread player, Card card) {
        handleCardPlay(player, card);

        switch (card.getCardType()) {
            case DRAW_TWO:
                addToQueue(player);
                playerOnMove = getNextPlayer();
                playerOnMove.sendResponse("CARDS CARDS " + drawCards(playerOnMove, 2));
                break;
            case SKIP:
                skipNextPlayer(player);
                break;
            case REVERSE:
                reverseDirection(player);
                break;
        }
    }

    private void playWildCard(UserThread player, Card card) {
        handleCardPlay(player, card);
        playerOnMove.sendResponse("CHANGE");
        colorChanged = true;

        addToQueue(player);
        playerOnMove = getNextPlayer();

        if (card.getCardType() == CardType.DRAW_FOUR)
            playerOnMove.sendResponse("CARDS CARDS " + drawCards(playerOnMove, 4));
    }

    private void addToQueue(UserThread player) {
        if (!player.getDeck().isEmpty()) {
            if (!directionChanged)
                queue.addLast(player);
            else
                queue.addFirst(player);
        }
        else
            spectators.add(player);
    }

    private void sendGameInfo() {
        StringBuilder sb = new StringBuilder();
        for (UserThread player : queue)
            sb.append(player.getUsername()).append(" - ").append(player.getDeck().getNumberOfCards()).append(" ");

        String currentCardMessage = "CURRENT " + currentCard;
        String currentPlayerMessage = "GAME_INFO CURR_PLAYER Current player " + playerOnMove;
        String cardsNumberMessage = "GAME_INFO CARDS_NUM " + sb;

        server.broadcastInGame(lobby, currentCardMessage);
        server.broadcastInGame(lobby, currentPlayerMessage);
        server.broadcastInGame(lobby, cardsNumberMessage);

        if (!spectators.isEmpty()) {
            server.broadcastToSpectators(lobby, currentCardMessage);
            server.broadcastToSpectators(lobby, currentPlayerMessage);
            server.broadcastToSpectators(lobby, cardsNumberMessage);
        }
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

    public void draw() {
        Card card = deck.dealCard();
        playerOnMove.getDeck().addCard(card);
        playerOnMove.sendResponse("CARDS CARDS " + card);
        playerOnMove.sendResponse("BLOCK");
        playerOnMove.sendResponse(playerOnMove.getDeck().availableCards(currentCard, currentColor, colorChanged));
        sendGameInfo();
    }
}
