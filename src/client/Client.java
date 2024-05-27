package client;

import server.Server;
import view.ClientGUI;

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
    private ClientGUI clientGUI;

    public Client(ClientGUI clientGUI) {
        try {
            this.port = Server.PORT;
            this.address = InetAddress.getByName("localhost");
            this.socket = new Socket(address, port);
            this.fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.toServer = new PrintWriter(socket.getOutputStream(), true);
            this.clientGUI = clientGUI;
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
        String[] parts = response.split(" ");

        switch (parts[0]) {
            case "USERNAME":
                clientGUI.connectEvent(parts[1], parts[2]);
                break;
            case "NEW_USER":
                clientGUI.addItemToList(clientGUI.getLvUsers(), parts[1]);
                break;
            case "VIEW_USERS":
                clientGUI.handleViewUsers(response);
                break;
            case "VIEW_LOBBIES":
                clientGUI.handleViewLobbies(response);
                break;
            case "VIEW_PLAYERS":
                clientGUI.handleViewPlayers(response);
                break;
            case "CREATE_LOBBY":
                clientGUI.handleCreateLobby(parts[1], parts[2]);
                break;
            case "JOIN":
                clientGUI.handleJoinLobby(parts[1], parts[2]);
                break;
            case "NEW_PLAYER_JOIN":
                clientGUI.addItemToList(clientGUI.getLvPlayers(), parts[1]);
                break;
            case "NEW_LOBBY":
                clientGUI.addItemToList(clientGUI.getLvLobbies(), parts[1]);
                break;
            case "INVITE":
                clientGUI.showInviteAlert(parts[1], parts[2]);
                break;
            case "LEAVE_LOBBY":
                clientGUI.setStartScene();
                break;
            case "LEAVE":
                clientGUI.removePlayerFromList(parts[1]);
                break;
            case "ACCEPT":
                clientGUI.setLobbyScene(parts[1]);
                break;
            case "START":
                clientGUI.setGameScene();
                break;
            default:
                clientGUI.showMessageLabel(response);
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
