package client_server;

import view.ClientView;
import view.ViewUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Client class responsible for handling the connection and communication with the server.
 */
public class Client extends Thread {
    private InetAddress address;
    private int port;
    private Socket socket;
    private BufferedReader fromServer;
    private PrintWriter toServer;
    private String username;
    private ClientView clientView;

    /**
     * Constructor for the Client class.
     *
     * @param clientView the gui window associated with this client
     */
    public Client(ClientView clientView) {
        try {
            this.port = Server.PORT;
            this.address = InetAddress.getByName("localhost");
            this.socket = new Socket(address, port);
            this.fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.toServer = new PrintWriter(socket.getOutputStream(), true);
            this.clientView = clientView;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the username of the client.
     *
     * @return the username of the client
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the client.
     *
     * @param username the username to be set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * The main method of the client thread. Reads responses from the server and handles them.
     */
    @Override
    public void run() {
        try {
            while (true) {
                String response = fromServer.readLine();

                if (response == null) {
                    System.err.println("Connection lost!");
                    break;
                }

                handleResponse(response);
            }
        } catch (IOException e) {
            close();
        }
    }

    /**
     * Sends a request to the server.
     *
     * @param request the request to be sent
     */
    public void sendRequest(String request) {
        toServer.println(request);
    }

    /**
     * Handles the response received from the server.
     *
     * @param response the response from the server
     */
    private void handleResponse(String response) {
        String[] parts = response.split(" ", 3);

        switch (parts[0]) {
            case "CONNECT":
                clientView.handleConnect(parts[1], parts[2]);
                break;
            case "ERROR":
                ViewUtil.showErrorAlert(parts[2]);
                break;
            case "ADD":
                clientView.handleAddItems(parts[1], parts[2]);
                break;
            case "REMOVE":
                clientView.handleRemoveItem(parts[1], parts[2]);
                break;
            case "CREATE_LOBBY":
                clientView.handleCreateLobby(parts[1], parts[2]);
                break;
            case "JOIN":
                clientView.setLobbyScene(parts[1]);
                break;
            case "INVITE":
                clientView.showInviteAlert(parts[1], parts[2]);
                break;
            case "LEAVE":
                clientView.setStartScene();
                break;
            case "START":
                clientView.setGameScene();
                break;
            case "CARDS":
                clientView.setCards(parts[2]);
                break;
            case "CURRENT":
                clientView.setCurrentCard(parts[1]);
                break;
            case "BLOCK":
                clientView.disableCards();
                break;
            case "UNBLOCK":
                clientView.enableCards(parts[2]);
                break;
            case "GAME_INFO":
                clientView.showGameInfo(parts[1], parts[2]);
                break;
            case "CHANGE":
                clientView.showChangeColorAlert();
                break;
            case "NO_CARDS":
                clientView.enableDrawCard();
                break;
            case "FINISH":
                clientView.showFinishAlert(parts[2]);
                break;
            case "ADMIN":
                clientView.setAdminLobbyScene(parts[1], true);
                break;
            case "DISCONNECT":
                close();
                break;
            default:
                ViewUtil.setTextLabel(clientView.getLblMessage(), response);
                break;
        }
    }

    /**
     * Closes the socket and streams associated with the client.
     */
    private void close() {
        try {
            socket.close();
            fromServer.close();
            toServer.close();
        } catch (IOException e) {
            System.err.println("Error with closing resources!");
        }
    }
}
