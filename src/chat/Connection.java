package chat;

import java.io.*;
import java.net.Socket;

/**
 * Created by Ramil on 01.12.2016.
 */
public class Connection implements Closeable{

    private final Socket socket;
    private final BufferedReader incomeData;
    private final BufferedWriter outcomeData;

    public Connection(Socket socket) {
        this.socket = socket;
        InputStream socketInStream=null;
        OutputStream socketOutStream=null;
        try {
            socketInStream = socket.getInputStream();
            socketOutStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        incomeData = new BufferedReader(new InputStreamReader(socketInStream));
        outcomeData = new BufferedWriter(new OutputStreamWriter(socketOutStream));
    }

    public void send(String message) throws IOException {
        synchronized (outcomeData)
        {
            outcomeData.write(message+"\n");
            outcomeData.flush();
        }
    }
    public String receive() throws IOException {
        synchronized (incomeData)
        {
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
