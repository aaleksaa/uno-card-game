package model.lobby_uno;

import client_server.Server;
import client_server.ClientThread;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The Lobby class represents a game lobby where players can join and start a game.
 * It manages the players, the lobby settings, and the game state.
 */
public class Lobby {
    private final Server server;
    private ClientThread admin;
    private final String lobbyName;
    private final Set<ClientThread> players;
    private boolean privateLobby;
    private boolean gameStarted;
    private Uno uno;

    /**
     * Constructs a Lobby with the specified server, admin, and lobby name.
     * The admin is automatically added to the players list.
     *
     * @param server    the server hosting the lobby
     * @param admin     the admin of the lobby
     * @param lobbyName the name of the lobby
     */
    public Lobby(Server server, ClientThread admin, String lobbyName) {
        this.server = server;
        this.admin = admin;
        this.lobbyName = lobbyName;
        this.players = Collections.synchronizedSet(new HashSet<>());
        this.players.add(admin);
    }

    /**
     * Starts the game by initializing a new Uno game with the players in the lobby.
     * Broadcasts the game start to all players and sends the initial cards to each player.
     */
    public void start() {
        uno = new Uno(server, this, players);
        players.forEach(player -> player.sendResponse("CARDS CARDS " + player.getDeck()));
        gameStarted = true;
        server.broadcastInGame(this, "BLOCK");
        uno.getPlayerOnMove().sendResponse(uno.getPlayerOnMove().getDeck().getPlayableCards(uno.getCurrentCard(), uno.getCurrentColor(), false));
    }

    /**
     * Returns the name of the lobby.
     *
     * @return the name of the lobby
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * Returns the set of players in the lobby.
     *
     * @return the set of players in the lobby
     */
    public Set<ClientThread> getPlayers() {
        return players;
    }

    /**
     * Returns the admin of the lobby.
     *
     * @return the admin of the lobby
     */
    public ClientThread getAdmin() {
        return admin;
    }

    /**
     * Checks if the lobby is private.
     *
     * @return true if the lobby is private, false otherwise
     */
    public boolean isPrivateLobby() {
        return privateLobby;
    }

    /**
     * Checks if the game has started.
     *
     * @return true if the game has started, false otherwise
     */
    public boolean isGameStarted() {
        return gameStarted;
    }

    /**
     * Returns the current Uno game instance.
     *
     * @return the current Uno game instance
     */
    public Uno getUno() {
        return uno;
    }

    /**
     * Sets the lobby to private or public.
     *
     * @param privateLobby true to set the lobby to private, false to set it to public
     */
    public void setPrivateLobby(boolean privateLobby) {
        this.privateLobby = privateLobby;
    }

    /**
     * Sets a new admin for the lobby.
     * The new admin is chosen as the next player in the set.
     */
    public void setNewAdmin() {
        admin = players.iterator().next();
    }

    /**
     * Checks if a specific player is in the lobby.
     *
     * @param player the player to check
     * @return true if the player is in the lobby, false otherwise
     */
    public boolean isPlayerInLobby(ClientThread player) {
        return players.contains(player);
    }

    /**
     * Adds a player to the lobby.
     *
     * @param user the player to add
     */
    public void addPlayer(ClientThread user) {
        players.add(user);
    }

    /**
     * Removes a player from the lobby.
     *
     * @param user the player to remove
     */
    public void removePlayer(ClientThread user) {
        players.remove(user);
    }

    /**
     * Checks if the lobby is empty.
     *
     * @return true if the lobby is empty, false otherwise
     */
    public boolean isEmpty() {
        return players.isEmpty();
    }

    /**
     * Checks if there are not enough players to start the game.
     *
     * @return true if there is only one player in the lobby, false otherwise
     */
    public boolean notEnoughPlayers() {
        return players.size() == 1;
    }

    /**
     * Checks if all players in the lobby are ready.
     *
     * @return true if all players are ready, false otherwise
     */
    public boolean arePlayersReady() {
        return players.stream().allMatch(ClientThread::isReady);
    }

    /**
     * Sets all players in the lobby to in-game status.
     */
    public void setPlayersInGame(boolean inGame) {
        players.forEach(player -> player.setInGame(inGame));
    }

    /**
     * Returns a string representation of the lobby, which is the lobby name.
     *
     * @return the lobby name
     */
    @Override
    public String toString() {
        return lobbyName;
    }
}
