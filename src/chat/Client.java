package chat;

import java.io.*;
import java.net.Socket;

/**
 * Created by Ramil on 30.11.2016.
 */
public class Client{

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
        try{
            Client client = new Client();

            KeyboardReader.writeString("Enter your username");
            client.connection.send(KeyboardReader.readString());
            Resender resender = client.new Resender();
            resender.setDaemon(true);
            resender.start();

            String message="";
            while (!"exit".equalsIgnoreCase(message)){
                message= KeyboardReader.readString();
                client.connection.send(message);
            }
            resender.setStop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Resender extends Thread {

        private boolean stoped;

        public void setStop() {
            stoped = true;
        }

        @Override
        public void run() {
            try {
                while (!stoped) {
                    String serverMessage = connection.receive();
                    KeyboardReader.writeString(serverMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
