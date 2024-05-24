package client;

import server.Server;
import view.ClientGUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {
    private InetAddress address;
    private int port;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String username;
    private ClientGUI clientGUI;

    public Client(ClientGUI clientGUI) {
        try {
            this.port = Server.PORT;
            this.address = InetAddress.getByName("localhost");
            this.socket = new Socket(address, port);
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(), true);
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
                String response = input.readLine();

                if (response == null) {
                    System.err.println("Connection lost!");
                    break;
                }

                handleResponse(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCommand(String command) {
        output.println(command);
    }

    private void handleResponse(String response) {
        String[] parts = response.split(" ");

        switch (parts[0]) {
            case "username":
                handleUsername(parts[1], parts[2]);
                break;
            case "connect":
                handleConnect(parts[1]);
                break;
            case "users":
                handleUsers(response);
                break;
            case "create_lobby":
                handleCreateLobby(parts[1], parts[2]);
                break;
            case "lobby_show":
                handleShowLobby(parts[1]);
                break;
            default:
                clientGUI.showTest(response);
                break;
        }
    }

    private void handleUsername(String valid, String username) {
        if (valid.equals("no"))
            clientGUI.showMessageLabel("Username " + username + " is already taken! Try again.");
        else {
            this.username = username;
            clientGUI.setStartScene();
        }
    }

    private void handleUsers(String users) {
        String[] parts = users.split(" ");

        for (int i = 1; i < parts.length; i++)
            clientGUI.addUserToList(parts[i]);
    }

    private void handleConnect(String username) {
        clientGUI.addUserToList(username);
    }

    private void handleCreateLobby(String valid, String lobbyName) {
        if (valid.equals("no"))
            clientGUI.showTest("ne valja!");
        else {
            clientGUI.addLobbyToList(lobbyName);
        }
    }

    private void handleShowLobby(String lobbyName) {
        clientGUI.addLobbyToList(lobbyName);
    }
}
