package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientReadThread extends Thread {
    private final String username;
    private BufferedReader fromServer;

    public ClientReadThread(String username, Socket socket) {
        this.username = username;

        try {
            this.fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error getting input stream: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                String response = fromServer.readLine();

                if (response == null) {
                    System.err.println("\rConnection lost!");
                }

                System.out.println("\r" + response);
                System.out.printf("\r[%s]: ", username);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
