package client;

import model.entities.PlayerDeck;
import server.Lobby;
import server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.IllegalFormatCodePointException;

public class UserThread extends Thread {
    private String username;
    private Server server;
    private Socket socket;
    private BufferedReader fromUser;
    private PrintWriter toUser;
    private Lobby lobby;
    private boolean hasInvite;
    private boolean ready;
    private boolean inLobby;
    private boolean inGame;
    private PlayerDeck deck;

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

    public String getUsername() {
        return username;
    }

    public PlayerDeck getDeck() {
        return deck;
    }

    public void setDeck(PlayerDeck deck) {
        this.deck = deck;
    }

    public boolean isReady() {
        return ready;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public void run() {
        try {
            String userInput;
            while (true) {
                userInput = fromUser.readLine();

                if (userInput == null || userInput.equals("exit"))
                    break;

                executeCommand(userInput);
            }
        } catch (IOException e) {
            server.removeUser(this);
            close();
        }
    }

    private void executeCommand(String command) throws IOException {
        String[] parts = command.split(" ");

        switch (parts[0]) {
//            case "view_users":
//                sendMessage(server.getConnectedUsers());
//                break;
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
                invitePlayerHandler(parts[1], parts[2], parts[3]);
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
            case "ready":
                ready = true;
                break;
            case "start":
                startGameHandler();
                break;
            case "play":
                playHandler(parts[1]);
                break;
            case "username":
                handleAvailableUsername(parts[1]);
                break;
        }
    }

    public void sendMessage(String message) {
        toUser.println(message);
    }

    private void createLobbyHandler(String lobbyName) {
        if (!server.isLobbyNameAvailable(lobbyName))
            sendMessage("CREATE_LOBBY false " + lobbyName);
        else {
            sendMessage("CREATE_LOBBY true " + lobbyName);
            lobby = new Lobby(server, this, lobbyName);
            server.addNewLobby(lobby);
            server.broadcastToAll(this, username + " created new lobby!");
            server.broadcastToAll(this, "NEW_LOBBY " + lobbyName);
            this.ready = true;
        }
    }

    private void joinLobbyHandler(String lobbyName) {
        Lobby lobby = server.getLobbyByName(lobbyName);

        if (lobby.isPrivateLobby())
            sendMessage("JOIN false " + lobbyName);
        else {
            lobby.addPlayer(this);
            this.lobby = lobby;

            sendMessage("JOIN true " + lobbyName);

            sendMessage("VIEW_PLAYERS " + server.getPlayersInLobby(this, lobby));
            server.broadcastToLobby(this, lobby, this.username + " joined lobby!");
            server.broadcastToLobby(this, lobby, "NEW_PLAYER_JOIN " + this.username);
        }
    }

    private void handleAvailableUsername(String username) {
        if (!server.isUsernameAvailable(username))
            sendMessage("USERNAME false " + username);
        else {
            server.addNewUser(this);
            sendMessage("USERNAME true " + username);

            server.broadcastToAll(this, username + " joined server!");
            sendMessage("VIEW_USERS " + server.getConnectedUsers(this));
            sendMessage("VIEW_LOBBIES " + server.getLobbies());

            server.broadcastToAll(this, "NEW_USER " + username);
            this.username = username;
        }
    }

    private void close() {
        try {
            socket.close();
            fromUser.close();
            toUser.close();
        } catch (IOException e) {
            System.err.println("Error with closing resources!");
        }
    }


    private void invitePlayerHandler(String lobbyName, String sender, String receiver) {
        UserThread user = server.getUserByUsername(receiver);
        server.broadcast(user, "INVITE " + lobbyName + " " + sender);
    }

    private void acceptInviteHandler(String lobbyName) {
        Lobby lobby = server.getLobbyByName(lobbyName);
        lobby.addPlayer(this);
        this.lobby = lobby;


        server.broadcastToLobby(this, lobby, this.username + " joined lobby!");
        server.broadcastToLobby(this, lobby, "NEW_PLAYER_JOIN " + this.username);
        //        if (!hasInvite)
//            sendMessage("You don't have any invites!");
//        else {
//            Lobby lobby = server.getLobbyByName(lobbyName);
//            lobby.addPlayer(this);
//            this.lobby = lobby;
//
//            sendMessage("You joined " + lobbyName + "! Type \"options\" for info!");
//            server.broadcastToLobby(this, lobby, this.username + " joined!");
//        }
    }

    private void declineInviteHandler(String lobbyName) {
        if (!hasInvite)
            sendMessage("You don't have any invites!");
        else
            server.broadcastToLobby(this, server.getLobbyByName(lobbyName),username + " declined invite!");
    }

    private void leaveLobbyHandler() {
        lobby.removePlayer(this);
        sendMessage("LEAVE_LOBBY");
        server.broadcastToLobby(this, lobby, username + " left lobby!");
        server.broadcastToLobby(this, lobby, "LEAVE " + username);
//        lobby.removePlayer(this);
//        sendMessage("You left " + lobby.getLobbyName());
//
//        if (lobby.isEmpty())
//            server.removeLobby(lobby);
//        else {
//            server.broadcastToLobby(this, lobby, username + " left lobby!");
//            if (lobby.getAdmin().equals(this)) {
//                lobby.setNewAdmin();
//                lobby.getAdmin().setReady(true);
//                server.broadcastToLobby(this, lobby, lobby.getAdmin() + " is new admin!");
//            }
//        }
    }

    private synchronized void startGameHandler() {
        if (!server.isAdmin(username))
            sendMessage("Only admin can start the game!");
        else if (lobby.notEnoughPlayers())
            sendMessage("You need at least 2 players to players to play!");
        else if (!lobby.arePlayersReady())
            sendMessage("Players are not ready!");
        else {
            lobby.setInGamePlayers();
            sendMessage("Game is starting...");
            server.broadcastToLobby(this, lobby, "Game is starting...");
            lobby.start();
        }
    }

    private void playHandler(String move) {
        if (!lobby.getUno().getPlayerOnMove().equals(this))
            sendMessage("It's not your turn!");
        else
            lobby.getUno().playMove(move);
    }

    @Override
    public String toString() {
        return username;
    }
}
