package chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * Created by Ramil on 30.11.2016.
 */
public class Server {
    protected static final int chatPort = 13666;
    protected static Logger logger = Logger.getLogger(Server.class.getSimpleName());
    private static List<Connection> connectedClients = new CopyOnWriteArrayList<Connection>();

    public static void main(String[] args) {
        Server server = new Server();
        try (ServerSocket clientListener = new ServerSocket(Server.chatPort)) {

            while (true) {
                Socket socket = clientListener.accept();
                logger.info("Client connect");
                Connection connection = server.new Connection(socket);
                connection.start();
                connectedClients.add(connection);
                logger.info("Total count of client:" + connectedClients.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class Connection extends Thread {
        private Socket socket;
        private String clientName;
        private BufferedReader dataFromClient;
        private BufferedWriter dataToClient;


        public Connection(Socket socket) {
            this.socket = socket;
            try {
                dataFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                dataToClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                clientName = dataFromClient.readLine();

                for (Connection connectedClient : connectedClients) {
                    connectedClient.dataToClient.write(clientName + " - connected to chat\n");
                }

                String clientMessage="";
                while (true) {
                    clientMessage = dataFromClient.readLine();
                    logger.info(clientMessage);
                    if (clientMessage.equalsIgnoreCase("exit")) break;

                    for (Connection connectedClient : connectedClients) {
                        connectedClient.dataToClient.write(clientName + ": " + clientMessage);
                    }
                }

                for (Connection connectedClient : connectedClients) {
                    connectedClient.dataToClient.write(clientName + " - left chat");
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connectedClients.remove(this);
                try {
                    dataToClient.close();
                    dataFromClient.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}