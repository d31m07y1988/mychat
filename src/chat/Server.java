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
                logger.info("Соединение с клиентом установлено");
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
            String clientName = null;
            try (Connection connection = new Connection(socket)) {
                clientName = serverHandshake(connection);
                while (authorized) {
                    String clientMessage = connection.receive();
                    if (clientMessage==null || clientMessage.equalsIgnoreCase("exit")) {
                        connectedClients.remove(clientName);
                        sendAll(clientName + " покинул чат");
                        logger.info(clientName + " покинул чат");
                        break;
                    }
                    sendAll(clientName + ": " + clientMessage);
                }

            } catch (SocketException e) {
                System.err.println("Связь с клиентом утеряна");
                if(clientName!=null) {
                    connectedClients.remove(clientName);
                    sendAll(clientName + " покинул чат");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private String serverHandshake(Connection connection) throws IOException {
            logger.info("Запрос имени");
            String userName = connection.receive();
            while (userName!=null && connectedClients.containsKey(userName)) {
                connection.send("Пользователь с таким именем уже существует. Введите другое имя.");
                userName = connection.receive();
            }
            if (userName!=null && !userName.equalsIgnoreCase("exit")) {
                connectedClients.put(userName, connection);
                logger.info("Пользователь авторизован");
                sendAll(userName + " присоединился к чату");
            } else {
                authorized = false;
                logger.info("Пользователь не авторизован");
            }
            return userName;
        }

        private void sendAll(String message) {
            for (Connection connection : connectedClients.values()) {
                try {
                    connection.send(message);
                } catch (IOException e) {
                    System.err.println("Сообщение не было разослано");
                }
            }
        }
    }
}