package client_server;

import model.lobby_uno.Lobby;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The Server class represents a server for handling client connections, managing lobbies, and broadcasting messages.
 */
public class Server {
    public static final int PORT = 12345;
    private final Set<ClientThread> users;
    private final Set<Lobby> lobbies;

    /**
     * Constructs a new Server instance, initializing the sets for users and lobbies.
     */
    public Server() {
        this.users = Collections.synchronizedSet(new HashSet<>());
        this.lobbies = Collections.synchronizedSet(new HashSet<>());
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.execute();
    }

    /**
     * Executes the server to listen for client connections and handle them.
     */
    private void execute() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientThread(clientSocket, this).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    /**
     * Checks if a given username is available.
     *
     * @param username the username to check
     * @return true if the username is available, false otherwise
     */
    public boolean isUsernameAvailable(String username) {
        return users.stream().noneMatch(user -> user.getUsername().equals(username));
    }

    /**
     * Checks if a given lobby name is available.
     *
     * @param lobbyName the lobby name to check
     * @return true if the lobby name is available, false otherwise
     */
    public boolean isLobbyNameAvailable(String lobbyName) {
        return lobbies.stream().noneMatch(lobby -> lobby.getLobbyName().equals(lobbyName));
    }

    /**
     * Adds a new user to the server.
     *
     * @param user the ClientThread representing the user
     */
    public void addNewUser(ClientThread user) {
        users.add(user);
    }

    /**
     * Removes a user from the server.
     *
     * @param user the ClientThread representing the user
     */
    public void removeUser(ClientThread user) {
        users.remove(user);
    }

    /**
     * Adds a new lobby to the server.
     *
     * @param lobby the Lobby to add
     */
    public void addNewLobby(Lobby lobby) {
        lobbies.add(lobby);
    }

    /**
     * Removes a lobby from the server.
     *
     * @param lobby the Lobby to remove
     */
    public void removeLobby(Lobby lobby) {
        lobbies.remove(lobby);
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user
     * @return the ClientThread representing the user, or null if not found
     */
    public ClientThread getUserByUsername(String username) {
        return users.stream().filter(user -> user.getUsername().equals(username)).findFirst().orElse(null);
    }

    /**
     * Retrieves a lobby by its name.
     *
     * @param lobbyName the name of the lobby
     * @return the Lobby, or null if not found
     */
    public Lobby getLobbyByName(String lobbyName) {
        return lobbies.stream().filter(lobby -> lobby.getLobbyName().equals(lobbyName)).findFirst().orElse(null);
    }

    /**
     * Retrieves a string representation of all connected users except the specified user.
     *
     * @param user the user to exclude from the list
     * @return a string representation of all other connected users
     */
    public String getConnectedUsers(ClientThread user) {
        synchronized (users) {
            return users.stream().filter(u -> u != user).map(ClientThread::toString).collect(Collectors.joining(" "));
        }
    }

    /**
     * Retrieves a string representation of all players in a lobby except the specified user.
     *
     * @param user the user to exclude from the list
     * @param lobby the lobby to retrieve players from
     * @return a string representation of all other players in the lobby
     */
    public String getPlayersInLobby(ClientThread user, Lobby lobby) {
        synchronized (lobby.getPlayers()) {
            return lobby.getPlayers().stream().filter(u -> u != user).map(ClientThread::toString).collect(Collectors.joining(" "));
        }
    }

    /**
     * Retrieves a string representation of all lobbies.
     *
     * @return a string representation of all lobbies
     */
    public String getLobbies() {
        synchronized (lobbies) {
            return lobbies.stream().map(Lobby::toString).collect(Collectors.joining(" "));
        }
    }

    /**
     * Broadcasts a message to all connected users except the sender.
     *
     * @param sender the user sending the message
     * @param message the message to broadcast
     */
    public void broadcastToAll(ClientThread sender, String message) {
        synchronized (users) {
            users.stream().filter(user -> user != sender).forEach(user -> user.sendResponse(message));
        }
    }

    /**
     * Broadcasts a message to all players in a lobby except the sender.
     *
     * @param sender the user sending the message
     * @param lobby the lobby to broadcast the message to
     * @param message the message to broadcast
     */
    public void broadcastToLobby(ClientThread sender, Lobby lobby, String message) {
        synchronized (lobby.getPlayers()) {
            lobby.getPlayers().stream().filter(player -> player != sender).forEach(player -> player.sendResponse(message));
        }
    }

    /**
     * Broadcasts a message to all players in a game.
     *
     * @param lobby the lobby containing the game
     * @param message the message to broadcast
     */
    public void broadcastInGame(Lobby lobby, String message) {
        synchronized (lobby.getPlayers()) {
            lobby.getPlayers().forEach(player -> player.sendResponse(message));
        }
    }

    /**
     * Broadcasts a message to all spectators of a game.
     *
     * @param lobby the lobby containing the game
     * @param message the message to broadcast
     */
    public void broadcastToSpectators(Lobby lobby, String message) {
        synchronized (lobby.getUno().getSpectators()) {
            lobby.getUno().getSpectators().forEach(spectator -> spectator.sendResponse(message));
        }
    }
}
