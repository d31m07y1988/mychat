package chat;

import java.io.*;
import java.net.Socket;

/**
 * Created by Ramil on 30.11.2016.
 */
public class Client {

    private Socket socket;
    private Connection connection;

    public Client() {
        try {
            socket = new Socket("localhost", Server.chatPort);
            connection = new Connection(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        ServerMessageGetter messageGetter = client.new ServerMessageGetter();
        messageGetter.setDaemon(true);
        messageGetter.start();

        System.out.println("Enter your username");
        try (BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in))) {
            String message = "";
            while (!"exit".equalsIgnoreCase(message)) {
                message = keyboardReader.readLine();
                client.connection.send(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ServerMessageGetter extends Thread {

        @Override
        public void run() {
            try {
                while (true) {
                    String serverMessage = connection.receive();
                    if (serverMessage != null)
                        System.out.println(serverMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
