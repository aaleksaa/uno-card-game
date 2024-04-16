package server;

import client.UserThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    public static final int PORT = 12345;
    private final Set<UserThread> users;

    public Server() {
        this.users = Collections.synchronizedSet(new HashSet<>());
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

    public void addNewUser(UserThread user) {
        users.add(user);
    }

    public String getConnectedUsers(UserThread user) {
        StringBuilder sb = new StringBuilder();

        sb.append("Connected users: ");

        for (UserThread userThread : users)
            sb.append(userThread.getUsername()).append(", ");

        return sb.toString();
    }

    public void broadcastToAll(UserThread sender, String message) {
        synchronized (users) {
            users.stream().filter(user -> user != sender).forEach(user -> user.sendMessage(message));
        }
    }
}
