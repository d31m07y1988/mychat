package chat;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Ramil on 30.11.2016.
 */
public final class KeyboardReader implements Closeable{
    private KeyboardReader() {
    }

    private static BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));

    public static String readString() throws IOException {
        return keyboardReader.readLine();
    }

    public static void writeString(String message) {
        System.out.println(message);
    }

    @Override
    public void close() throws IOException {
        keyboardReader.close();
    }
}
