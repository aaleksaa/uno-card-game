package model.entities;

import client_server.Server;
import client_server.UserThread;
import model.entities.Uno;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Lobby {
    private Server server;
    private UserThread admin;
    private String lobbyName;
    private Set<UserThread> players;
    private boolean privateLobby;
    private boolean gameStarted;
    private Uno uno;

    public Lobby(Server server, UserThread admin, String lobbyName) {
        this.server = server;
        this.admin = admin;
        this.lobbyName = lobbyName;
        this.players = Collections.synchronizedSet(new HashSet<>());
        players.add(admin);
    }

    public void start() {
        uno = new Uno(server, this, players);
        players.forEach(player -> player.sendResponse("CARDS CARDS " + player.getDeck()));
        gameStarted = true;
        server.broadcastInGame(this, "BLOCK");
        uno.getPlayerOnMove().sendResponse(uno.getPlayerOnMove().getDeck().availableCards(uno.getCurrentCard(), uno.getCurrentColor(), false));
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public Set<UserThread> getPlayers() {
        return players;
    }

    public UserThread getAdmin() {
        return admin;
    }

    public boolean isPrivateLobby() {
        return privateLobby;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public Uno getUno() {
        return uno;
    }

    public void setPrivateLobby(boolean privateLobby) {
        this.privateLobby = privateLobby;
    }

    public void setNewAdmin() {
        admin = players.iterator().next();
    }

    public boolean isPlayerInLobby(UserThread player) {
        return players.contains(player);
    }

    public void addPlayer(UserThread user) {
        players.add(user);
    }

    public void removePlayer(UserThread user) {
        players.remove(user);
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public boolean notEnoughPlayers() {
        return players.size() == 1;
    }

    public boolean arePlayersReady() {
        return players.stream().allMatch(UserThread::isReady);
    }

    public void setPlayersInGame() {
        players.forEach(player -> player.setInGame(true));
    }

    @Override
    public String toString() {
        return lobbyName;
    }
}
