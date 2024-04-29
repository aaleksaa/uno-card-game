package model.entities;

import client.UserThread;
import model.enums.Color;
import server.Lobby;
import server.Server;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Uno {
    private Server server;
    private Lobby lobby;
    private Deck deck;
    private Set<UserThread> players;
    private Queue<UserThread> queue = new ConcurrentLinkedQueue<>();
    private Card currentCard;
    private Color currentColor;
    private UserThread playerOnMove;

    public Uno(Server server, Lobby lobby, Set<UserThread> players) {
        this.server = server;
        this.lobby = lobby;
        this.deck = new Deck();
        this.players = players;
        dealCards();
        this.queue.addAll(players);
        do {
            this.currentCard = deck.dealCard();
        } while (!(currentCard instanceof NumberCard));
        this.currentColor = currentCard.getColor();
        this.playerOnMove = queue.peek();
    }

    public Deck getDeck() {
        return deck;
    }

    public synchronized void playMove(String move) {
        UserThread currPlayer = queue.poll();

        Card card = currPlayer.getDeck().getCard(move);
        currPlayer.getDeck().decrementNumberOfCards();
        currentCard = card;
        currentColor = card.getColor();
        currPlayer.getDeck().removeCard(card);

        queue.add(currPlayer);
        playerOnMove = queue.peek();

        server.broadcastInGame(lobby, getCurrentStatus());
        send();
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public String getCurrentStatus() {
        StringBuilder sb = new StringBuilder();

        sb.append("---------------------------------------------\n");
        sb.append("Current card: ").append(currentCard).append("\n");
        sb.append("Current color: ").append(currentColor).append("\n");
        sb.append("Player on the move: ").append(playerOnMove).append("\n");
        sb.append("Number of cards\n");

        for (UserThread player : players) {
            sb.append(player.getUsername()).append(" ").append(player.getDeck().getNumberOfCards()).append("\n");
        }

        sb.append("-----------------------------------------------");

        return sb.toString();
    }

    public void send() {
        for (UserThread player : players)
            player.sendMessage(player.getDeck().toString());
    }

    public void dealCards() {
        for (UserThread player : players) {
            List<Card> cards = new ArrayList<>();

            for (int i = 0; i < 7; i++) {
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
