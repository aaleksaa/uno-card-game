package client_server;

import model.entities.PlayerDeck;
import model.entities.Uno;
import model.enums.Color;
import model.entities.Lobby;
import view.ViewUtil;

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
                handleCreateLobby(parts[1]);
                break;
            case "PRIVATE_LOBBY":
                lobby.setPrivateLobby(parts[1].equals("true"));
                break;
            case "invite":
                handlePlayerInvite(parts[1], parts[2], parts[3]);
                break;
            case "accept":
                handleAcceptInvite(parts[1]);
                break;
            case "decline":
                handleDeclineInvite(parts[1]);
                break;
            case "JOIN":
                handleJoinLobby(parts[1]);
                break;
            case "leave":
                handleLeaveLobby();
                break;
            case "READY":
                this.ready = parts[1].equals("true");
                break;
            case "start":
                handleStartGame();
                break;
            case "play":
                lobby.getUno().playMove(parts[1]);
                break;
            case "username":
                handleUserConnect(parts[1]);
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
        player.sendResponse(player.getDeck().availableCards(uno.getCurrentCard(), uno.getCurrentColor(), uno.isColorChanged()));

    }

    public void sendResponse(String response) {
        toUser.println(response);
    }

    private void handleCreateLobby(String lobbyName) {
        if (!server.isLobbyNameAvailable(lobbyName))
            sendResponse("CREATE_LOBBY false " + lobbyName);
        else {
            sendResponse("CREATE_LOBBY true " + lobbyName);

            lobby = new Lobby(server, this, lobbyName);
            server.addNewLobby(lobby);

            server.broadcastToAll(this, username + " created new lobby!");
            server.broadcastToAll(this, "ADD LOBBY " + lobbyName);
            this.ready = true;
        }
    }


    private void handleJoinLobby(String lobbyName) {
        Lobby lobby = server.getLobbyByName(lobbyName);

        if (lobby.isPrivateLobby())
            sendResponse("ERROR START Lobby " + lobbyName + " is private!");
        else if (lobby.isGameStarted())
            sendResponse("ERROR START Game started in this lobby!");
        else {
            lobby.addPlayer(this);
            this.lobby = lobby;

            sendResponse("JOIN " + lobbyName);
            sendResponse("ADD PLAYER " + server.getPlayersInLobby(this, lobby));

            server.broadcastToLobby(this, lobby, this.username + " joined lobby!");
            server.broadcastToLobby(this, lobby, "ADD PLAYER " + this.username);
        }
    }

    private void handleUserConnect(String username) {
        if (!server.isUsernameAvailable(username))
            sendResponse("CONNECT false " + username);
        else {
            server.addNewUser(this);
            sendResponse("CONNECT true " + username);

            server.broadcastToAll(this, username + " joined server!");
            sendResponse("ADD USER " + server.getConnectedUsers(this));
            sendResponse("ADD LOBBY " + server.getLobbies());

            server.broadcastToAll(this, "ADD USER " + username);
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
        sendResponse("DISCONNECT");

        if (lobby != null) {
            lobby.removePlayer(this);
            if (isInGame()) {
                lobby.getUno().removePlayer(this);
                lobby.getUno().returnCards(deck.getCards());
            }
        }

        server.removeUser(this);
    }


    private void handlePlayerInvite(String lobbyName, String sender, String receiver) {
        UserThread user = server.getUserByUsername(receiver);
        Lobby lobby = server.getLobbyByName(lobbyName);

        if (lobby.isPlayerInLobby(user))
            sendResponse("ERROR LOBBY " + user.getUsername() + " is already in lobby!");
        else if (user.isInGame())
            sendResponse("ERROR LOBBY " + user.getUsername() + " is currently in game!");
        else
            server.broadcast(user, "INVITE " + lobbyName + " " + sender);
    }

    private void handleAcceptInvite(String lobbyName) {
        Lobby lobby = server.getLobbyByName(lobbyName);
        lobby.addPlayer(this);
        this.lobby = lobby;

        server.broadcastToLobby(this, lobby, this.username + " joined lobby!");
        server.broadcastToLobby(this, lobby, "ADD PLAYER " + this.username);

        sendResponse("JOIN " + lobbyName);
        sendResponse("ADD PLAYER " + server.getPlayersInLobby(this, lobby));
    }

    private void handleDeclineInvite(String lobbyName) {
        server.broadcastToLobby(this, server.getLobbyByName(lobbyName), username + " declined invite!");
    }

    private void handleLeaveLobby() {
        lobby.removePlayer(this);
        sendResponse("LEAVE_LOBBY");
        server.broadcastToLobby(this, lobby, username + " left lobby!");
        server.broadcastToLobby(this, lobby, "REMOVE PLAYER " + username);

        if (lobby.isEmpty()) {
            server.removeLobby(lobby);
            server.broadcastToAll(this, "REMOVE LOBBY " + lobby.getLobbyName());
            server.broadcastToAll(this, lobby.getLobbyName() + " is removed!");
        } else {
            if (lobby.getAdmin().equals(this)) {
                lobby.setNewAdmin();
                lobby.getAdmin().setReady(true);
                lobby.getAdmin().sendResponse("ADMIN " + lobby.getLobbyName());
                server.broadcastToLobby(lobby.getAdmin(), lobby, lobby.getAdmin().getUsername() + " is new admin!");
            }
        }

//        if (lobby.getAdmin().equals(this)) {
//            lobby.setNewAdmin();
//            lobby.getAdmin().setReady(true);
//            sendMessage("ADMIN " + lobby.getLobbyName());
//        }

//        if (lobby.isEmpty()) {
//            server.removeLobby(lobby);
//            server.broadcastToAll(this, "REMOVE LOBBY " + lobby.getLobbyName());
//        }

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

    private void handleStartGame() {
        if (lobby.notEnoughPlayers())
            sendResponse("ERROR LOBBY " + ViewUtil.NOT_ENOUGH_PLAYERS_MESSAGE);
        else if (!lobby.arePlayersReady())
            sendResponse("ERROR LOBBY " + ViewUtil.PLAYERS_NOT_READY_MESSAGE);
        else {
            server.broadcastInGame(lobby, "START");
            lobby.start();
            lobby.setPlayersInGame();
        }
    }

    @Override
    public String toString() {
        return username;
    }
}
