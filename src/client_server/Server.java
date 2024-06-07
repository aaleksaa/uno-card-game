package client_server;

import model.entities.Lobby;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    public static final int PORT = 12345;
    private final Set<UserThread> users;
    private final Set<Lobby> lobbies;

    public Server() {
        this.users = Collections.synchronizedSet(new HashSet<>());
        this.lobbies = Collections.synchronizedSet(new HashSet<>());
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.execute();
    }

    private void execute() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new UserThread(clientSocket, this).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public boolean isUsernameAvailable(String username) {
        return users.stream().noneMatch(user -> user.getUsername().equals(username));
    }

    public boolean isLobbyNameAvailable(String lobbyName) {
        return lobbies.stream().noneMatch(lobby -> lobby.getLobbyName().equals(lobbyName));
    }

    public void addNewUser(UserThread user) {
        users.add(user);
    }

    public void removeUser(UserThread user) {
        users.remove(user);
    }

    public void addNewLobby(Lobby lobby) {
        lobbies.add(lobby);
    }

    public void removeLobby(Lobby lobby) {
        lobbies.remove(lobby);
    }

    public String getConnectedUsers(UserThread user) {
        synchronized (users) {
            StringBuilder sb = new StringBuilder();
            for (UserThread u : users)
                if (u != user)
                    sb.append(u.getUsername()).append(" ");

            return sb.toString();
        }
    }

    public String getPlayersInLobby(UserThread user, Lobby lobby) {
        synchronized (lobby.getPlayers()) {
            StringBuilder sb = new StringBuilder();
            for (UserThread u : lobby.getPlayers())
                if (u != user)
                    sb.append(u.getUsername()).append(" ");

            return sb.toString();
        }
    }

    public UserThread getUserByUsername(String username) {
        for (UserThread user : users)
            if (user.getUsername().equals(username))
                return user;
        return null;
    }

    public Lobby getLobbyByName(String lobbyName) {
        for (Lobby lobby : lobbies)
            if (lobby.getLobbyName().equals(lobbyName))
                return lobby;
        return null;
    }

    public String getLobbies() {
        synchronized (lobbies) {
            StringBuilder sb = new StringBuilder();
            for (Lobby lobby : lobbies)
                sb.append(lobby).append(" ");

            return sb.toString();
        }
    }

    public void broadcastToAll(UserThread sender, String message) {
        synchronized (users) {
            users.stream().filter(user -> user != sender).forEach(user -> user.sendResponse(message));
        }
    }

    public void broadcastToLobby(UserThread sender, Lobby lobby, String message) {
        synchronized (lobby.getPlayers()) {
            lobby.getPlayers().stream().filter(player -> player != sender).forEach(player -> player.sendResponse(message));
        }
    }

    public void broadcastInGame(Lobby lobby, String message) {
        synchronized (lobby.getPlayers()) {
            lobby.getPlayers().forEach(player -> player.sendResponse(message));
        }
    }

    public void broadcast(UserThread receiver, String message) {
        receiver.sendResponse(message);
    }

    public void broadcastToSpectators(Lobby lobby, String message) {
        synchronized (lobby.getUno().getSpectators()) {
            lobby.getUno().getSpectators().forEach(spectator -> spectator.sendResponse(message));
        }
    }
}
