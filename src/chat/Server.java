package chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * Created by Ramil on 30.11.2016.
 */
public class Server {
    public static final int chatPort = 13666;
    static Logger logger = Logger.getLogger(Server.class.getSimpleName());
    private static Map<String, Connection> connectedClients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        Server server = new Server();
        try (ServerSocket clientListener = new ServerSocket(Server.chatPort)) {

            while (true) {
                Socket socket = clientListener.accept();
                logger.info("Client connect");
                incomeClients newClient = server.new incomeClients(socket);
                newClient.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class incomeClients extends Thread {
        private final Socket socket;

        public incomeClients(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (Connection connection = new Connection(socket)) {

                String clientName = serverHandshake(connection);

                String clientMessage = "";
                while (true) {
                    clientMessage = connection.receive();
                    logger.info(clientMessage);
                    if (clientMessage.equalsIgnoreCase("exit")) break;
                    sendAll(clientName+": " +clientMessage);
                }

                sendAll(clientName+" покинул чат");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private String serverHandshake(Connection connection) throws IOException {
            logger.info("запрос имени");
            String userName = connection.receive();
            connectedClients.put(userName, connection);
            sendAll(userName + " присоединился к чату");
            return userName;
        }

        private void sendAll(String message) {
            for (Connection connection : connectedClients.values()) {
                try {
                    connection.send(message);
                } catch (IOException e) {
                    System.out.println("Сообщение не было разослано");
                }
            }
        }
    }
}