package client;

import server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UserThread extends Thread {
    private String username;
    private Server server;
    private Socket socket;
    private BufferedReader fromUser;
    private PrintWriter toUser;

    public UserThread(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;

        try {
            this.fromUser = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.toUser = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error with streams: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            username = fromUser.readLine();

            if (!server.isUsernameAvailable(username))
                toUser.println("Username " + username + " is taken! Try again.");
            else {
                toUser.println("Welcome " + username + "!");
                server.addNewUser(this);
                server.broadcastToAll(this, "New user connected " + username);
                sendMessage(server.getConnectedUsers(this));

                String userInput;
                do {
                    userInput = fromUser.readLine();
                } while (!userInput.equals("exit"));

                server.broadcastToAll(this, "User " + username + " disconnected!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) {
        toUser.println(message);
    }
}
