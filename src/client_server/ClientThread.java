package client_server;

import model.deck.PlayerDeck;
import model.lobby_uno.Uno;
import model.enums.Color;
import model.lobby_uno.Lobby;
import view.ViewUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The ClientThread class represents a client connected to the server.
 * It handles communication between the server and the client, processes
 * client requests, and manages the client's state within the game.
 */
public class ClientThread extends Thread {
    private String username;
    private final Server server;
    private final Socket socket;
    private BufferedReader fromUser;
    private PrintWriter toUser;
    private Lobby lobby;
    private boolean ready;
    private boolean inGame;
    private PlayerDeck deck;

    /**
     * Constructs a new ClientThread and initializes its communication streams.
     *
     * @param socket the client's socket
     * @param server the server instance
     * @throws IOException if an I/O error occurs
     */
    public ClientThread(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;

        try {
            this.fromUser = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.toUser = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error with streams: " + e.getMessage());
        }
    }

    /**
     * Returns the username of the client.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the player's deck.
     *
     * @return the player's deck
     */
    public PlayerDeck getDeck() {
        return deck;
    }

    /**
     * Checks if the player is ready.
     *
     * @return true if the player is ready, false otherwise
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * Checks if the player is in-game.
     *
     * @return true if the player is in-game, false otherwise
     */
    public boolean isInGame() {
        return inGame;
    }

    /**
     * Sets the player's deck.
     *
     * @param deck the player's deck
     */
    public void setDeck(PlayerDeck deck) {
        this.deck = deck;
    }

    /**
     * Sets the in-game status of the player.
     *
     * @param inGame true if the player is in-game, false otherwise
     */
    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    /**
     * Sets the ready status of the player.
     *
     * @param ready true if the player is ready, false otherwise
     */
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    /**
     * Returns the string representation of the client, which is the username.
     *
     * @return the username
     */
    @Override
    public String toString() {
        return username;
    }

    /**
     * The main method for the thread. It listens for messages from the client
     * and handles them.
     */
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
            handleDisconnect();
        }
    }

    /**
     * Handles a request from the client by parsing the command and
     * executing the appropriate action.
     *
     * @param command the command from the client
     */
    private void handleRequest(String command) {
        String[] parts = command.split(" ");

        switch (parts[0]) {
            case "CONNECT":
                handleUserConnect(parts[1]);
                break;
            case "CREATE_LOBBY":
                handleCreateLobby(parts[1]);
                break;
            case "JOIN":
                handleJoinLobby(parts[1]);
                break;
            case "READY":
                handleSetReady(parts[1]);
                break;
            case "PRIVATE_LOBBY":
                lobby.setPrivateLobby(parts[1].equals("true"));
                break;
            case "INVITE":
                handlePlayerInvite(parts[1], parts[2], parts[3]);
                break;
            case "ACCEPT":
                handleAcceptInvite(parts[1]);
                break;
            case "DECLINE":
                server.broadcastToLobby(this, server.getLobbyByName(parts[1]), username + " declined invite!");
                break;
            case "LEAVE":
                handleLeaveLobby();
                break;
            case "START":
                handleStartGame();
                break;
            case "PLAY":
                lobby.getUno().playMove(parts[1]);
                break;
            case "CHANGE":
                handleChangeColor(parts[1]);
                break;
            case "DRAW":
                lobby.getUno().drawCardIfNoPlayable();
                break;
            case "DISCONNECT":
                handleDisconnect();
                break;
        }
    }

    /**
     * Sends a response to the client.
     *
     * @param response the response message
     */
    public void sendResponse(String response) {
        toUser.println(response);
    }

    /**
     * Handles user connection by checking the username availability
     * and adding the user to the server.
     *
     * @param username the username of the client
     */
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

    /**
     * Handles lobby creation by checking the lobby name availability
     * and adding the new lobby to the server.
     *
     * @param lobbyName the name of the new lobby
     */
    private void handleCreateLobby(String lobbyName) {
        if (!server.isLobbyNameAvailable(lobbyName))
            sendResponse("CREATE_LOBBY false " + lobbyName);
        else {
            sendResponse("CREATE_LOBBY true " + lobbyName);
            sendResponse("ADD LOBBY " + lobbyName);

            lobby = new Lobby(server, this, lobbyName);
            server.addNewLobby(lobby);

            server.broadcastToAll(this, username + " created new lobby!");
            server.broadcastToAll(this, "ADD LOBBY " + lobbyName);
            setReady(true);
        }
    }

    /**
     * Handles joining a lobby by checking the lobby's status and adding
     * the client to the lobby if possible.
     *
     * @param lobbyName the name of the lobby to join
     */
    private void handleJoinLobby(String lobbyName) {
        Lobby lobby = server.getLobbyByName(lobbyName);

        if (lobby.isGameStarted())
            sendResponse("ERROR START Game started in this lobby!");
        else if (lobby.isPrivateLobby())
            sendResponse("ERROR START Lobby " + lobbyName + " is private!");
        else {
            lobby.addPlayer(this);
            this.lobby = lobby;

            sendResponse("JOIN " + lobbyName);
            sendResponse("ADD PLAYER " + server.getPlayersInLobby(this, lobby));

            server.broadcastToLobby(this, lobby, this.username + " joined lobby!");
            server.broadcastToLobby(this, lobby, "ADD PLAYER " + this.username);
        }
    }

    /**
     * Handles setting the ready status of the player and broadcasts
     * the status to the lobby.
     *
     * @param ready the ready status ("true" or "false")
     */
    private void handleSetReady(String ready) {
        setReady(ready.equals("true"));
        server.broadcastToLobby(this, lobby, username + " is " + (ready.equals("true") ? "ready!" : "not ready!"));

        if (lobby.arePlayersReady())
            lobby.getAdmin().sendResponse("Players are ready! You can start the game!");
    }

    /**
     * Handles inviting a player to the lobby by checking the player's
     * availability and sending an invite if possible.
     *
     * @param lobbyName the name of the lobby
     * @param sender the username of the sender
     * @param receiver the username of the receiver
     */
    private void handlePlayerInvite(String lobbyName, String sender, String receiver) {
        ClientThread user = server.getUserByUsername(receiver);
        Lobby lobby = server.getLobbyByName(lobbyName);

        if (lobby.isPlayerInLobby(user))
            sendResponse("ERROR LOBBY " + user.getUsername() + " is already in lobby!");
        else if (user.isInGame())
            sendResponse("ERROR LOBBY " + user.getUsername() + " is currently in game!");
        else
            user.sendResponse("INVITE " + lobbyName + " " + sender);
    }

    /**
     * Handles accepting an invite to a lobby by joining the lobby
     * and notifying other players in the lobby.
     *
     * @param lobbyName the name of the lobby
     */
    private void handleAcceptInvite(String lobbyName) {
        if (this.lobby != null)
            handleLeaveLobby();

        Lobby lobby = server.getLobbyByName(lobbyName);
        lobby.addPlayer(this);
        this.lobby = lobby;

        server.broadcastToLobby(this, lobby, this.username + " joined lobby!");
        server.broadcastToLobby(this, lobby, "ADD PLAYER " + this.username);

        sendResponse("JOIN " + lobbyName);
        sendResponse("ADD PLAYER " + server.getPlayersInLobby(this, lobby));
    }

    /**
     * Handles leaving a lobby by removing the player from the lobby
     * and notifying other players in the lobby.
     */
    private void handleLeaveLobby() {
        lobby.removePlayer(this);
        sendResponse("LEAVE");
        server.broadcastToLobby(this, lobby, username + " left lobby!");
        server.broadcastToLobby(this, lobby, "REMOVE PLAYER " + username);

        if (lobby.isEmpty()) {
            server.removeLobby(lobby);
            sendResponse("REMOVE LOBBY " + lobby.getLobbyName());
            server.broadcastToAll(this, "REMOVE LOBBY " + lobby.getLobbyName());
            server.broadcastToAll(this, "Lobby " + lobby.getLobbyName() + " is removed!");
        } else {
            if (lobby.getAdmin().equals(this)) {
                lobby.setNewAdmin();
                lobby.getAdmin().setReady(true);
                lobby.getAdmin().sendResponse("ADMIN " + lobby.getLobbyName());
                server.broadcastToLobby(lobby.getAdmin(), lobby, lobby.getAdmin().getUsername() + " is new admin!");
            }
        }
    }

    /**
     * Handles starting the game by checking if the lobby has enough players
     * and if all players are ready, then starting the game.
     */
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

    /**
     * Handles changing the color in the game by setting the new color
     * and notifying the next player.
     *
     * @param color the new color
     */
    private void handleChangeColor(String color) {
        Uno uno = lobby.getUno();
        uno.setCurrentColor(Color.fromString(color));
        ClientThread player = lobby.getUno().getPlayerOnMove();
        player.sendResponse(player.getDeck().getPlayableCards(uno.getCurrentCard(), uno.getCurrentColor(), uno.isColorChanged()));
    }

    /**
     * Closes the socket and communication streams.
     */
    private void close() {
        try {
            socket.close();
            fromUser.close();
            toUser.close();
        } catch (IOException e) {
            System.err.println("Error with closing resources!");
        }
    }

    /**
     * Handles disconnecting the client by notifying other players,
     * removing the client from the server, and cleaning up resources.
     */
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
                lobby.getUno().returnCardsFromDisconnectedPlayer(deck.getCards());

                if (lobby.getUno().getPlayerOnMove().equals(this) && lobby.getUno().getQueue().size() == 1)
                    lobby.getUno().endGame();
                else {
                    lobby.getUno().setPlayerOnMove(lobby.getUno().getNextPlayer());
                    lobby.getUno().continueGame();
                }
            }
        }

        server.removeUser(this);
    }
}
