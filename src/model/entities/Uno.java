package model.entities;

import client.UserThread;
import server.Lobby;
import server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Uno {
    private Server server;
    private Lobby lobby;
    private Deck deck;
    private Card currentCard;
    private Set<UserThread> players;

    public Uno(Server server, Lobby lobby, Set<UserThread> players) {
        this.server = server;
        this.lobby = lobby;
        this.deck = new Deck();
        this.players = players;
        dealCards();
        this.currentCard = deck.dealCard();
    }

    public Deck getDeck() {
        return deck;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public String getCurrentStatus() {
        StringBuilder sb = new StringBuilder();

        sb.append("---------------------------------------------\n");
        sb.append("Current card: ").append(currentCard).append("\n");
        sb.append("Number of cards\n");

        for (UserThread player : players)
            sb.append(player.getUsername()).append(" ").append(player.getDeck().getNumberOfCards()).append("\n");

        sb.append("-----------------------------------------------");

        return sb.toString();
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
