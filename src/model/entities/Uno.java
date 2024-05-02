package model.entities;

import client.UserThread;
import model.enums.CardType;
import model.enums.Color;
import server.Lobby;
import server.Server;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Uno {
    private Server server;
    private Lobby lobby;
    private Deck deck;
    private Queue<UserThread> queue = new ConcurrentLinkedQueue<>();
    private Card currentCard;
    private Color currentColor;
    private UserThread playerOnMove;
    //private Set<UserThread> players;

    public Uno(Server server, Lobby lobby, Set<UserThread> players) {
        this.server = server;
        this.lobby = lobby;
        this.deck = new Deck();
        //this.players = players;
        this.queue.addAll(players);
        dealCards();
        do {
            this.currentCard = deck.dealCard();
        } while (!(currentCard instanceof NumberCard));
        this.currentColor = currentCard.getColor();
        this.playerOnMove = queue.peek();
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

        server.broadcastInGame(lobby, currPlayer + " played " + card);
        server.broadcastInGame(lobby, getCurrentStatus());
        send();
    }

    private void playNumberCard(UserThread player, Card card) {
        player.getDeck().removeCard(card);
        currentCard = card;
        currentColor = card.getColor();

        queue.add(player);
        playerOnMove = queue.peek();
    }

    private void playActionCard(UserThread player, Card card) {
        player.getDeck().removeCard(card);
        currentCard = card;
        currentColor = card.getColor();

        queue.add(player);

        if (card.getCardType() == CardType.DRAW_TWO) {
            playerOnMove = queue.peek();
            drawCards(playerOnMove, 2);
        } else if (card.getCardType() == CardType.SKIP) {
            UserThread skippedPlayer = queue.poll();
            queue.add(skippedPlayer);
            playerOnMove = queue.peek();
        }
    }

    private void playWildCard(UserThread player, Card card) {
        player.getDeck().removeCard(card);
        currentCard = card;
        currentColor = getRandomColor();

        queue.add(player);

        if (card.getCardType() == CardType.DRAW_FOUR) {
            playerOnMove = queue.peek();
            drawCards(playerOnMove, 4);
        }
    }

    private Color getRandomColor() {
        Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW};

        return colors[new Random().nextInt(colors.length)];
    }


    private void drawCards(UserThread player, int amount) {
        for (int i = 0; i < amount; i++)
            player.getDeck().addCard(deck.dealCard());
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public String getCurrentStatus() {
        StringBuilder sb = new StringBuilder();

        sb.append("---------------------------------------------\n");
        sb.append("Current card: ").append(currentCard).append("  ");
        sb.append("Current color: ").append(currentColor).append("  ");
        sb.append("Player on the move: ").append(playerOnMove).append("  ");
        sb.append("Next player: ").append(queue.peek()).append("  ");
        sb.append("Number of cards: ");

        for (UserThread player : queue)
            sb.append(player.getUsername()).append(" ").append(player.getDeck().getNumberOfCards()).append(", ");


        sb.append("\n-----------------------------------------------");

        return sb.toString();
    }

    public void send() {
        for (UserThread player : queue)
            player.sendMessage(player.getDeck().toString());
    }

    public void dealCards() {
        for (UserThread player : queue) {
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
