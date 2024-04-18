package client;

import server.Lobby;
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
    private Lobby lobby;

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
                sendMessage("Welcome " + username + "! Type \"help\" for more info!");
                server.addNewUser(this);
                server.broadcastToAll(this, "User " + username + " connected!");

                String userInput;
                do {
                    userInput = fromUser.readLine();
                    String[] parts = userInput.split(" ");

                    switch (parts[0]) {
                        case "help":
                            sendMessage(server.listCommands());
                            break;
                        case "view_users":
                            sendMessage(server.getConnectedUsers());
                            break;
                        case "view_lobbies":
                            sendMessage(server.getLobbies());
                            break;
                        case "create_lobby":
                            sendMessage("Lobby successfully created!");
                            lobby = new Lobby(server, this, parts[1]);
                            server.addNewLobby(lobby);
                            server.broadcastToAll(this, username + " created new lobby!");
                            break;
                        case "set_public":
                            lobby.setPrivateLobby(false);
                            break;
                        case "set_private":
                            lobby.setPrivateLobby(true);
                            break;
                    }

                } while (!userInput.equals("exit"));

                server.broadcastToAll(this, "User " + username + " disconnected!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.removeUser(this);

            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) {
        toUser.println(message);
    }

    @Override
    public String toString() {
        return username;
    }
}
