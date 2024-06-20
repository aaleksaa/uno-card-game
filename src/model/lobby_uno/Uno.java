package model.lobby_uno;

import client_server.ClientThread;
import model.card.ActionCard;
import model.card.Card;
import model.card.NumberCard;
import model.deck.UnoDeck;
import model.enums.CardType;
import model.enums.Color;
import client_server.Server;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * The Uno class represents the core logic of the Uno game.
 * It manages the game state, players, and deck of cards.
 */
public class Uno {
    private final Server server;
    private final Lobby lobby;
    private final UnoDeck deck;
    private final Deque<ClientThread> queue;
    private Card currentCard;
    private Color currentColor;
    private ClientThread playerOnMove;
    private boolean directionChanged;
    private boolean colorChanged;
    private final List<ClientThread> spectators;

    /**
     * Constructs a new Uno game with the specified server, lobby, and players.
     *
     * @param server  the server managing the game
     * @param lobby   the lobby for the game
     * @param players the set of players participating in the game
     */
    public Uno(Server server, Lobby lobby, Set<ClientThread> players) {
        this.server = server;
        this.lobby = lobby;
        this.deck = new UnoDeck();
        
        this.queue = new ConcurrentLinkedDeque<>();
        this.queue.addAll(players);
        deck.setInitialDeckToPlayers(queue);
        
        initCurrentCard();
        this.currentColor = currentCard.getColor();
        this.playerOnMove = getNextPlayer();
        this.spectators = new ArrayList<>();
        sendGameInfo();
    }

    /**
     * Initializes the current card at the start of the game.
     */
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

    /**
     * Returns the current color in play.
     *
     * @return the current color
     */
    public Color getCurrentColor() {
        return currentColor;
    }

    /**
     * Returns the current card in play.
     *
     * @return the current card
     */
    public Card getCurrentCard() {
        return currentCard;
    }

    /**
     * Returns the current player on move.
     *
     * @return the current player on move
     */
    public ClientThread getPlayerOnMove() {
        return playerOnMove;
    }

    /**
     * Checks if the color has changed.
     *
     * @return true if the color has changed, false otherwise
     */
    public boolean isColorChanged() {
        return colorChanged;
    }

    /**
     * Checks if the game is finished.
     *
     * @return true if the game is finished, false otherwise
     */
    private boolean isFinished() {
        return queue.size() == 1;
    }

    /**
     * Returns the queue of players.
     *
     * @return the queue of players
     */
    public Deque<ClientThread> getQueue() {
        return queue;
    }

    /**
     * Gets the list of spectators.
     *
     * @return the list of spectators
     */
    public List<ClientThread> getSpectators() {
        return spectators;
    }

    /**
     * Returns cards from a disconnected player back to the deck.
     *
     * @param cards the list of cards to return
     */
    public void returnCardsFromDisconnectedPlayer(List<Card> cards) {
        deck.addCardsToDeck(cards);
    }

    /**
     * Gets the current player based on the game direction.
     *
     * @return the current player
     */
    private ClientThread getCurrentPlayer() {
        return !directionChanged ? queue.pollFirst() : queue.pollLast();
    }

    /**
     * Gets the next player based on the game direction.
     *
     * @return the next player
     */
    public ClientThread getNextPlayer() {
        return !directionChanged ? queue.peekFirst() : queue.peekLast();
    }

    /**
     * Sets the current color in play.
     *
     * @param color the new color
     */
    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    /**
     * Sets the current player on move.
     *
     * @param playerOnMove the player on move
     */
    public void setPlayerOnMove(ClientThread playerOnMove) {
        this.playerOnMove = playerOnMove;
    }

    /**
     * Changes the direction of the game.
     */
    private void changeDirection() {
        directionChanged = !directionChanged;
    }

    /**
     * Removes a player from the game.
     *
     * @param player the player to remove
     */
    public void removePlayer(ClientThread player) {
        if (queue.contains(player))
            queue.remove(player);
        else
            spectators.remove(player);
    }

    /**
     * Plays a move with the specified card string.
     *
     * @param givenCard the string representation of the card
     */
    public synchronized void playMove(String givenCard) {
        ClientThread currPlayer = getCurrentPlayer();
        Card card = currPlayer.getDeck().getCardFromDeck(givenCard);

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

    /**
     * Handles the card play logic.
     *
     * @param player the player playing the card
     * @param card   the card being played
     */
    private void handleCardPlay(ClientThread player, Card card) {
        player.getDeck().removeCard(card);
        currentCard = card;
        currentColor = card.getColor();
    }

    /**
     * Plays a number card.
     *
     * @param player the player playing the card
     * @param card   the number card being played
     */
    private void playNumberCard(ClientThread player, Card card) {
        handleCardPlay(player, card);
        colorChanged = false;
        addToQueue(player);
        playerOnMove = getNextPlayer();
    }

    /**
     * Skips the next player.
     *
     * @param player the player playing the skip card
     */
    private void skipNextPlayer(ClientThread player) {
        addToQueue(player);
        ClientThread skippedPlayer = getCurrentPlayer();
        addToQueue(skippedPlayer);
        playerOnMove = getNextPlayer();
    }

    /**
     * Reverses the direction of the game.
     *
     * @param player the player playing the reverse card
     */
    private void reverseDirection(ClientThread player) {
        if (queue.size() == 1)
            skipNextPlayer(player);
        else {
            changeDirection();
            addToQueue(player);
            playerOnMove = getNextPlayer();
        }
    }

    /**
     * Plays an action card.
     *
     * @param player the player playing the card
     * @param card   the action card being played
     */
    private void playActionCard(ClientThread player, Card card) {
        handleCardPlay(player, card);
        colorChanged = false;

        switch (card.getCardType()) {
            case DRAW_TWO:
                addToQueue(player);
                playerOnMove = getNextPlayer();
                playerOnMove.sendResponse("CARDS CARDS " + drawCardsForNextPlayer(playerOnMove, 2));
                break;
            case SKIP:
                skipNextPlayer(player);
                break;
            case REVERSE:
                reverseDirection(player);
                break;
        }
    }

    /**
     * Plays a wild card.
     *
     * @param player the player playing the card
     * @param card   the wild card being played
     */
    private void playWildCard(ClientThread player, Card card) {
        handleCardPlay(player, card);
        playerOnMove.sendResponse("CHANGE");
        colorChanged = true;

        addToQueue(player);
        playerOnMove = getNextPlayer();

        if (card.getCardType() == CardType.DRAW_FOUR)
            playerOnMove.sendResponse("CARDS CARDS " + drawCardsForNextPlayer(playerOnMove, 4));
    }

    /**
     * Continues the game by sending game info to players.
     */
    public void continueGame() {
        sendGameInfo();
        server.broadcastInGame(lobby, "BLOCK");

        if (!isColorChanged())
            playerOnMove.sendResponse(playerOnMove.getDeck().getPlayableCards(currentCard, currentColor, colorChanged));
    }

    /**
     * Ends the game and broadcasts the results.
     */
    public void endGame() {
        spectators.add(queue.poll());
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < spectators.size(); i++)
            sb.append(i + 1).append(" - ").append(spectators.get(i)).append("  ");

        server.broadcastInGame(lobby, "REMOVE LOBBY " + lobby.getLobbyName());
        server.broadcastInGame(lobby, "FINISH FINISH " + sb);
        server.removeLobby(lobby);
    }

    /**
     * Adds a player to the queue based on the game direction.
     *
     * @param player the player to add
     */
    private void addToQueue(ClientThread player) {
        if (!player.getDeck().isEmpty()) {
            if (!directionChanged)
                queue.addLast(player);
            else
                queue.addFirst(player);
        }
        else
            spectators.add(player);
    }

    /**
     * Sends game information to all players and spectators.
     */
    private void sendGameInfo() {
        StringBuilder sb = new StringBuilder();
        for (ClientThread player : queue)
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

    /**
     * Draws cards for the next player as a result of the "Draw 2" or "Draw 4" card.
     *
     * @param player the player drawing cards
     * @param amount the number of cards to draw
     * @return a string representation of the drawn cards
     */
    private String drawCardsForNextPlayer(ClientThread player, int amount) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < amount; i++) {
            Card card = deck.dealCard();
            player.getDeck().addCardToDeck(card);
            sb.append(card).append(" ");
        }

        return sb.toString();
    }

    /**
     * Draws a card for the current player if they have no playable cards.
     */
    public void drawCardIfNoPlayable() {
        Card card = deck.dealCard();
        playerOnMove.getDeck().addCardToDeck(card);
        playerOnMove.sendResponse("CARDS CARDS " + card);
        playerOnMove.sendResponse("BLOCK");
        playerOnMove.sendResponse(playerOnMove.getDeck().getPlayableCards(currentCard, currentColor, colorChanged));
        sendGameInfo();
    }
}
