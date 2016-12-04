package chat;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import static java.lang.Thread.sleep;

/**
 * Created by Ramil on 30.11.2016.
 */
public class Client {

    public static void main(String[] args) {
        Client client = new Client();

        try (Socket socket = new Socket("localhost", 13666);
             Connection connection = new Connection(socket);
             BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in))) {

            ServerMessageGetter messageGetter = client.new ServerMessageGetter(connection);
            messageGetter.setDaemon(true);
            messageGetter.start();

            System.out.println("Введите имя");
            String message = "";
            while (!"exit".equalsIgnoreCase(message)) {
                message = keyboardReader.readLine();
                connection.send(message);
            }
            sleep(10);
        } catch (SocketException e) {
            System.err.println("связь с сервером утеряна1");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ServerMessageGetter extends Thread {

        private Connection connection;

        public ServerMessageGetter(Connection connection) {
            this.connection = connection;
        }

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
