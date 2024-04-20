package server;

import client.UserThread;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Lobby {
    private Server server;
    private UserThread admin;
    private String lobbyName;
    private Set<UserThread> players;
    private boolean privateLobby;

    public Lobby(Server server, UserThread admin, String lobbyName) {
        this.server = server;
        this.admin = admin;
        this.lobbyName = lobbyName;
        this.players = Collections.synchronizedSet(new HashSet<>());
        players.add(admin);
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public UserThread getAdmin() {
        return admin;
    }

    public void addPlayer(UserThread user) {
        players.add(user);
    }

    public boolean isPrivateLobby() {
        return privateLobby;
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
