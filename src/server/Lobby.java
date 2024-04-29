package server;

import client.UserThread;
import model.entities.Uno;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Lobby {
    private Server server;
    private UserThread admin;
    private String lobbyName;
    private Set<UserThread> players;
    private boolean privateLobby;
    private Uno uno;

    public Lobby(Server server, UserThread admin, String lobbyName) {
        this.server = server;
        this.admin = admin;
        this.lobbyName = lobbyName;
        this.players = Collections.synchronizedSet(new HashSet<>());
        players.add(admin);
    }

    public synchronized void start() {
        uno = new Uno(server, this, players);

        server.broadcastInGame(this, uno.getCurrentStatus());
        for (UserThread player : players)
            player.sendMessage(player.getDeck().toString());
    }

    public Uno getUno() {
        return uno;
    }

    public void setNewAdmin() {
        Iterator<UserThread> iter = players.iterator();

        admin = iter.next();
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public UserThread getAdmin() {
        return admin;
    }

    public Set<UserThread> getPlayers() {
        return players;
    }

    public void addPlayer(UserThread user) {
        players.add(user);
    }

    public void removePlayer(UserThread user) {
        players.remove(user);
    }

    public boolean isPrivateLobby() {
        return privateLobby;
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

    public void setInGamePlayers() {
        players.forEach(player -> player.setInGame(true));
    }

    public void setPrivateLobby(boolean privateLobby) {
        this.privateLobby = privateLobby;
    }

    private String privateLobbyInfo() {
        return "Private - " + lobbyName;
   }

    private String publicLobbyInfo() {
        return "Public - " + lobbyName + " Players: " + players;
   }

    @Override
    public String toString() {
        return isPrivateLobby() ? privateLobbyInfo() : publicLobbyInfo();
    }
}
