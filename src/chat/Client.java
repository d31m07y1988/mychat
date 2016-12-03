package chat;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Ramil on 30.11.2016.
 */
public class Client {

    private int serverPort;
    private Socket socket;
    private Connection connection;

    public Client(int port) throws IOException {
        serverPort = port;
        socket = new Socket("localhost", serverPort);
        connection = new Connection(socket);
    }

    public static void main(String[] args) {
        try (BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in))) {
            Client client = new Client(13666);
            ServerMessageGetter messageGetter = client.new ServerMessageGetter();
            messageGetter.setDaemon(true);
            messageGetter.start();

            System.out.println("Enter your username");
            String message = "";
            while (!"exit".equalsIgnoreCase(message)) {
                message = keyboardReader.readLine();
                client.connection.send(message);
            }
        } catch (SocketException e) {
            System.err.println("связь с сервером утеряна1");

        } catch (IOException e) {
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
            } catch (SocketException e) {
                System.err.println("связь с сервером утеряна2");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
