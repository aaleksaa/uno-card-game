package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientWriteThread extends Thread {
    private final String username;
    private PrintWriter toServer;

    public ClientWriteThread(String username, Socket socket) {
        this.username = username;

        try {
            this.toServer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        toServer.println(username);
        try (Scanner sc = new Scanner(System.in)) {

            String userInput;

            do {
                System.out.printf("\r[%s]: ", username);
                userInput = sc.nextLine();

                toServer.println(userInput);
            } while (!userInput.equals("exit"));
        }
    }
}
