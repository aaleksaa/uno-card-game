package server;

import client.UserThread;

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

                UserThread user = new UserThread(clientSocket, this);
                user.start();
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

    public String getConnectedUsers() {
        synchronized (users) {
            return "Online users: " + users.stream().map(UserThread::getUsername).toList();
        }
    }

    public UserThread getUserByUsername(String username) {
        for (UserThread user : users)
            if (user.getUsername().equals(username))
                return user;
        return null;
    }

    public boolean isAdmin(String username) {
        for (Lobby lobby : lobbies)
            if (lobby.getAdmin().getUsername().equals(username))
                return true;
        return false;
    }

    public Lobby getLobbyByName(String lobbyName) {
        for (Lobby lobby : lobbies)
            if (lobby.getLobbyName().equals(lobbyName))
                return lobby;
        return null;
    }

    public String getLobbies() {
        synchronized (lobbies) {
            return lobbies.isEmpty() ?
                    "There are currently no active game lobbies. Please create a new lobby to start a game." : lobbies.toString();
        }
    }

    public void broadcastToAll(UserThread sender, String message) {
        synchronized (users) {
            users.stream().filter(user -> user != sender).forEach(user -> user.sendMessage(message));
        }
    }

    public void broadcastToLobby(UserThread sender, Lobby lobby,String message) {
        synchronized (lobby.getPlayers()) {
            lobby.getPlayers().stream().filter(player -> player != sender).forEach(player -> player.sendMessage(message));
        }
    }

    public void broadcast(UserThread receiver, String message) {
        receiver.sendMessage(message);
    }

    public String listCommands() {
        StringBuilder sb = new StringBuilder();

        sb.append("----------------------------------------------\n");
        sb.append("help             \t\t\tList commands\n");
        sb.append("view_users       \t\t\tView online users\n");
        sb.append("view_lobbies     \t\t\tView current lobies\n");
        sb.append("create_lobby <lobby name> \tCreate lobby\n");
        sb.append("invite <username>        \tSend request\n");
        sb.append("set_private             \tSet your lobby to private\n");
        sb.append("set_public              \tSet your lobby to public\n");
        sb.append("accept                  \tAccept request\n");
        sb.append("decline                 \tDecline request\n");
        sb.append("join <lobby name>       \tJoin lobby\n");
        sb.append("exit                    \tDisconnect\n");
        sb.append("----------------------------------------------");


        return sb.toString();
    }
}
