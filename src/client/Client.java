package client;

import server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final String hostname;
    private final int port;

    public Client(String hostname) {
        this.hostname = hostname;
        this.port = Server.PORT;
    }

    public static void main(String[] args) {
        Client client = new Client("localhost");
        System.out.println("Connecting to the port " + Server.PORT);
        client.execute();
    }

    private void execute() {
        try (Socket clientSocket = new Socket(hostname, port)) {
            Scanner sc = new Scanner(System.in);
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

            System.out.println("Connected to the server " + hostname + ":" + port);
            System.out.print("Enter username:");
            String username = sc.next();
            output.println(username);

            String response = input.readLine();

            if (response.startsWith("W")) {
                Thread rt = new ClientReadThread(username, clientSocket);
                Thread wt = new ClientWriteThread(username, clientSocket);

                rt.start();
                wt.start();

                rt.join();
                wt.join();
            } else
                System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
