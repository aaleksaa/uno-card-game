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
    private boolean hasInvite;
    private boolean inLobby;
    private boolean ingame;

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
                    executeCommand(userInput);
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

    private void executeCommand(String command) throws IOException {
        String[] parts = command.split(" ");

        switch (parts[0]) {
            case "help":
                sendMessage(server.listCommands());
                break;
            case "options":
                sendMessage(server.listLobbyCommands());
                break;
            case "view_users":
                sendMessage(server.getConnectedUsers());
                break;
            case "view_lobbies":
                sendMessage(server.getLobbies());
                break;
            case "create_lobby":
                createLobbyHandler(parts[1]);
                break;
            case "set_public":
                lobby.setPrivateLobby(false);
                break;
            case "set_private":
                lobby.setPrivateLobby(true);
                break;
            case "invite":
                invitePlayerHandler(parts[1]);
                break;
            case "accept":
                acceptInviteHandler(parts[1]);
                break;
            case "decline":
                declineInviteHandler(parts[1]);
                break;
            case "join":
                joinLobbyHandler(parts[1]);
                break;
            case "leave":
                leaveLobbyHandler();
                break;
        }
    }

    private void createLobbyHandler(String lobbyName) {
        if (!server.isLobbyNameAvailable(lobbyName))
            sendMessage("Lobby with " + lobbyName + " already exists!");
        else {
            sendMessage("Lobby successfully created! Type \"options\" for info!");
            lobby = new Lobby(server, this, lobbyName);
            server.addNewLobby(lobby);
            server.broadcastToAll(this, username + " created new lobby!");
        }
    }

    private void invitePlayerHandler(String username) throws IOException {
        UserThread user = server.getUserByUsername(username);

        if (user != null) {
            server.broadcast(user, this.username + " has sent you an invite! Type accept/decline " + lobby.getLobbyName() + ".");
            user.hasInvite = true;
        }
        else
            sendMessage(username + " is offline!");

    }

    private void acceptInviteHandler(String lobbyName) {
        if (!hasInvite)
            sendMessage("You don't have any invites!");
        else {
            Lobby lobby = server.getLobbyByName(lobbyName);
            lobby.addPlayer(this);
            this.lobby = lobby;

            sendMessage("You joined " + lobbyName + "! Type \"options\" for info!");
            server.broadcastToLobby(this, lobby, this.username + " joined!");
        }
    }

    private void declineInviteHandler(String lobbyName) {
        if (!hasInvite)
            sendMessage("You don't have any invites!");
        else
            server.broadcastToLobby(this, server.getLobbyByName(lobbyName),username + " declined invite!");

    }

    private void joinLobbyHandler(String lobbyName) {
        Lobby lobby = server.getLobbyByName(lobbyName);

        if (lobby == null)
            sendMessage("Lobby " + lobbyName + " does not exist!");
        else {
            if (lobby.isPrivateLobby())
                sendMessage("You can't join private lobby!");
            else {
                lobby.addPlayer(this);
                this.lobby = lobby;
                sendMessage("You joined " + lobbyName + "! Type \"options\" for info!");
                server.broadcastToLobby(this, lobby, this.username + " joined!");
            }
        }
    }

    private void leaveLobbyHandler() {
        lobby.removePlayer(this);
        sendMessage("You left " + lobby.getLobbyName());
        if (lobby.isEmpty())
            server.removeLobby(lobby);
        else
            server.broadcastToLobby(this, lobby, username + " left lobby!");
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
