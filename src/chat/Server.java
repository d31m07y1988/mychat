package chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by Ramil on 30.11.2016.
 */
public class Server {
    public final int serverPort;
    private static Logger logger = Logger.getLogger(Server.class.getSimpleName());
    private Map<String, Connection> connectedClients = new ConcurrentHashMap<>();

    public Server(int port) {
        serverPort = port;
    }

    public static void main(String[] args) {
        Server server = new Server(13666);
        try (ServerSocket clientListener = new ServerSocket(server.serverPort)) {
            logger.info("Сервер стартовал");
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
        private Socket socket;
        private boolean authorized = true;

        public incomeClients(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (Connection connection = new Connection(socket)) {
                String clientName = serverHandshake(connection);
                while (authorized) {
                    String clientMessage = connection.receive();
                    logger.info(clientMessage);
                    if (clientMessage.equalsIgnoreCase("exit")) {
                        connectedClients.remove(clientName);
                        sendAll(clientName + " покинул чат");
                        break;
                    }
                    sendAll(clientName + ": " + clientMessage);
                }

            } catch (SocketException e) {
                System.err.println("связь с клиентом утеряна");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private String serverHandshake(Connection connection) throws IOException {
            logger.info("запрос имени");
            String userName = connection.receive();
            while (connectedClients.containsKey(userName)) {
                connection.send("Пользователь с таким именем уже существует. Введите другое имя.");
                userName = connection.receive();
            }
            if (!userName.equalsIgnoreCase("exit")) {
                connectedClients.put(userName, connection);
                sendAll(userName + " присоединился к чату");
            } else
                authorized = false;
            return userName;
        }

        private void sendAll(String message) throws IOException {
            for (Map.Entry<String, Connection> clientConnection : connectedClients.entrySet()) {
                try {
                    clientConnection.getValue().send(message);
                } catch (IOException e) {
                    if(e.getMessage().equals("Stream closed")) {
                        clientConnection.getValue().close();
                        connectedClients.remove(clientConnection.getKey());
                    } else {
                        System.err.println("Сообщение не было разослано");
                    }
                }

            }
        }
    }
}