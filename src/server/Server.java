package server;

import client.UserThread;

import javax.print.DocFlavor;
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

    public void removeUser(UserThread user) {
        users.remove(user);
    }


    public String getConnectedUsers() {
        synchronized (users) {
            return "Online users: " + users.stream().map(UserThread::getUsername).toList();
        }
    }


    public void broadcastToAll(UserThread sender, String message) {
        synchronized (users) {
            users.stream().filter(user -> user != sender).forEach(user -> user.sendMessage(message));
        }
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
