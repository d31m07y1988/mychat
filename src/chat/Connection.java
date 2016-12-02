package chat;

import java.io.*;
import java.net.Socket;

/**
 * Created by Ramil on 01.12.2016.
 */
public class Connection implements Closeable {

    private final Socket socket;
    private BufferedReader incomeData;
    private BufferedWriter outcomeData;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        incomeData = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outcomeData = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void send(String message) throws IOException {
        synchronized (outcomeData) {
            outcomeData.write(message + "\n");
            outcomeData.flush();
        }
    }

    public String receive() throws IOException {
        synchronized (incomeData) {
            return incomeData.readLine();
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
        incomeData.close();
        outcomeData.close();
    }
}
