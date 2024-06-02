package client;

import model.entities.PlayerDeck;
import model.entities.Uno;
import model.enums.Color;
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
    private boolean ready;
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

                if (userInput == null)
                    break;

                handleRequest(userInput);
            }
        } catch (IOException e) {
            server.removeUser(this);
            close();
        }
    }

    private void handleRequest(String command) throws IOException {
        String[] parts = command.split(" ");

        switch (parts[0]) {
            case "create_lobby":
                createLobbyHandler(parts[1]);
                break;
            case "PRIVATE_LOBBY":
                lobby.setPrivateLobby(parts[1].equals("true"));
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
            case "JOIN":
                handleJoinLobby(parts[1]);
                break;
            case "leave":
                leaveLobbyHandler();
                break;
            case "READY":
                this.ready = parts[1].equals("true");
                break;
            case "start":
                startGameHandler();
                break;
            case "play":
                lobby.getUno().playMove(parts[1]);
                break;
            case "username":
                handleAvailableUsername(parts[1]);
                break;
            case "change":
                handleChangeColor(parts[1]);
                break;
            case "DRAW":
                lobby.getUno().draw();
                break;
            case "DISCONNECT":
                handleDisconnect();
                break;
        }
    }

    private void handleChangeColor(String color) {
        Uno uno = lobby.getUno();
        uno.setCurrentColor(Color.fromString(color));
        UserThread player = lobby.getUno().getPlayerOnMove();
        player.sendMessage("UNBLOCK " + player.getDeck().availableCards(uno.getCurrentCard(), uno.getCurrentColor(), uno.isColorChanged()));

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
            server.broadcastToAll(this, "NEW LOBBY " + lobbyName);
            this.ready = true;
        }
    }


    private void handleJoinLobby(String lobbyName) {
        Lobby lobby = server.getLobbyByName(lobbyName);

        if (lobby.isPrivateLobby())
            sendMessage("SHOW_LABEL START Lobby " + lobbyName + " is private!");
        else if (lobby.isGameStarted())
            sendMessage("SHOW_LABEL START Game started in this lobby!");
        else {
            lobby.addPlayer(this);
            this.lobby = lobby;

            sendMessage("JOIN " + lobbyName);
            sendMessage("VIEW PLAYER " + server.getPlayersInLobby(this, lobby));

            server.broadcastToLobby(this, lobby, this.username + " joined lobby!");
            server.broadcastToLobby(this, lobby, "NEW PLAYER " + this.username);
        }
    }

    private void handleAvailableUsername(String username) {
        if (!server.isUsernameAvailable(username))
            sendMessage("USERNAME false " + username);
        else {
            server.addNewUser(this);
            sendMessage("USERNAME true " + username);

            server.broadcastToAll(this, username + " joined server!");
            sendMessage("VIEW USER " + server.getConnectedUsers(this));
            sendMessage("VIEW LOBBY " + server.getLobbies());
            System.out.println(server.getLobbies());
            System.out.println(server.getConnectedUsers(this));

            server.broadcastToAll(this, "NEW USER " + username);
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

    private void handleDisconnect() {
        server.broadcastToAll(this, username + " has disconnected!");
        server.broadcastToAll(this, "REMOVE USER " + username);
        server.broadcastToAll(this, "REMOVE PLAYER " + username);
        close();
        sendMessage("DISCONNECT");

        if (lobby != null)
            lobby.removePlayer(this);

        server.removeUser(this);
    }


    private void invitePlayerHandler(String lobbyName, String sender, String receiver) {
        UserThread user = server.getUserByUsername(receiver);
        Lobby lobby = server.getLobbyByName(lobbyName);

        if (lobby.isPlayerInLobby(user))
            sendMessage("SHOW_LABEL LOBBY " + user.getUsername() + " is already in lobby!");
        else if (user.isInGame())
            sendMessage("SHOW_LABEL LOBBY " + user.getUsername() + " is currently in game!");
        else
            server.broadcast(user, "INVITE " + lobbyName + " " + sender);
    }

    private void acceptInviteHandler(String lobbyName) {
        Lobby lobby = server.getLobbyByName(lobbyName);
        lobby.addPlayer(this);
        this.lobby = lobby;

        server.broadcastToLobby(this, lobby, this.username + " joined lobby!");
        server.broadcastToLobby(this, lobby, "NEW PLAYER " + this.username);

        sendMessage("ACCEPT " + lobbyName);
        sendMessage("VIEW PLAYER " + server.getPlayersInLobby(this, lobby));
    }

    private void declineInviteHandler(String lobbyName) {
        server.broadcastToLobby(this, server.getLobbyByName(lobbyName), username + " declined invite!");
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

    private void startGameHandler() {
        if (lobby.notEnoughPlayers())
            sendMessage("You need at least 2 players to start the game!");
        else if (!lobby.arePlayersReady())
            sendMessage("Players are not ready!");
        else {
            sendMessage("START");
            server.broadcastToLobby(this, lobby, "START");
            lobby.start();
            lobby.setInGamePlayers();
        }
//        if (!server.isAdmin(username))
//            sendMessage("Only admin can start the game!");
//        else if (lobby.notEnoughPlayers())
//            sendMessage("You need at least 2 players to players to play!");
//        else if (!lobby.arePlayersReady())
//            sendMessage("Players are not ready!");
//        else {
//            lobby.setInGamePlayers();
//            sendMessage("Game is starting...");
//            server.broadcastToLobby(this, lobby, "Game is starting...");
//            lobby.start();
//        }
    }

    @Override
    public String toString() {
        return username;
    }
}
