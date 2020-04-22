package agora;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * Logger class for recording Client / Server activities to a file
 */
public class Logger {
    private String filename = null;

    public Logger(String filename) {
        this.filename = filename;
    }

    /**
     * Saves message (along with current epoch time in millis) to a file
     * @param message message to write to a log file
     */
    public synchronized void log(String message) {
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.filename, true));
            String newMessage = filename + " " + timestamp.toString() + ": " + "Thread ID: " + Thread.currentThread().getId()
                    + ": " + message;
            writer.write(newMessage + "\n");
            writer.flush();
            writer.close();
            System.out.println(message);
            System.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

