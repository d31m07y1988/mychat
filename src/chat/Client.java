package chat;

import java.io.*;
import java.net.Socket;

/**
 * Created by Ramil on 30.11.2016.
 */
public class Client{

    private BufferedReader dataFromServer;
    private BufferedWriter dataToServer;
    private Socket socket;

    public Client() {
        try {
            socket = new Socket("localhost", Server.chatPort);
            dataFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dataToServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try{
            Client client = new Client();

            Resender resender = client.new Resender();
            resender.start();

            KeyboardReader.writeString("Enter your name");
            client.dataToServer.write(KeyboardReader.readString());
            client.dataToServer.newLine();
            client.dataToServer.flush();



            String message="";
            while (!"exit".equalsIgnoreCase(message)){
                message= KeyboardReader.readString();
                client.dataToServer.write(message);
                client.dataToServer.newLine();
                client.dataToServer.flush();
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

                    String str = dataFromServer.readLine();
                    KeyboardReader.writeString(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
