package client;

import server.Server;
import view.ClientView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread {
    private InetAddress address;
    private int port;
    private Socket socket;
    private BufferedReader fromServer;
    private PrintWriter toServer;
    private String username;
    private ClientView clientView;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public void sendRequest(String command) {
        toServer.println(command);
    }

    private void handleResponse(String response) {
        String[] parts = response.split(" ", 3);

        switch (parts[0]) {
            case "SHOW_LABEL":
                clientView.handleError(parts[1], parts[2]);
                break;
            case "USERNAME":
                clientView.handleConnect(parts[1], parts[2]);
                break;
            case "NEW_USER":
                clientView.addItemToList(clientView.getLvUsers(), parts[1]);
                break;
            case "VIEW":
                clientView.handleViewItems(parts[1], parts[2]);
                break;
            case "CREATE_LOBBY":
                clientView.handleCreateLobby(parts[1], parts[2]);
                break;
            case "JOIN":
                clientView.setLobbyScene(parts[1]);
                break;
            case "NEW_PLAYER_JOIN":
                clientView.addItemToList(clientView.getLvPlayers(), parts[1]);
                break;
            case "NEW_LOBBY":
                clientView.addItemToList(clientView.getLvLobbies(), parts[1]);
                break;
            case "INVITE":
                clientView.showInviteAlert(parts[1], parts[2]);
                break;
            case "LEAVE_LOBBY":
                clientView.setStartScene();
                break;
            case "LEAVE":
                clientView.removePlayerFromList(parts[1]);
                break;
            case "ACCEPT":
                clientView.setLobbyScene(parts[1]);
                break;
            case "START":
                clientView.setGameScene();
                break;
            case "CARDS":
                clientView.setCards(response);
                break;
            case "CURRENT":
                clientView.setCurrentCard(parts[1]);
                break;
            case "BLOCK":
                clientView.disableCards();
                break;
            case "UNBLOCK":
                clientView.enableCards(response);
                break;
            case "GAME_INFO":
                clientView.showGameInfo(parts[1], parts[2]);
                break;
            case "DRAW":
                clientView.addCards(response);
                break;
            case "CHANGE":
                clientView.showChangeColorAlert();
                break;
            case "NO_CARDS":
                sendRequest("DRAW");
                break;
            case "FINISH":
                clientView.showFinishAlert(response);
                break;
//            case "REMOVE":
//                clientGUI.remove(parts[1], parts[2]);
//                break;
            default:
                clientView.setTextLabel(clientView.getLblMessage(), response);
                break;
        }
    }

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
