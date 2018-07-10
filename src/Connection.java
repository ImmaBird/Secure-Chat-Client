import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection implements Closeable {

    private Socket socket;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;

    public Connection(Socket socket) {
        this.socket = socket;

        // Make object streams
        try {
            objectOut = new ObjectOutputStream(this.socket.getOutputStream());
            objectIn = new ObjectInputStream(this.socket.getInputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void send(Message message) throws IOException {
        objectOut.writeObject(message);
    }

    public synchronized Message receive() throws IOException, ClassNotFoundException {
        return (Message) objectIn.readObject();
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void close() {
        try {
            objectOut.close();
            objectIn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}