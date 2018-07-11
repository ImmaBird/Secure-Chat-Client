import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;

import javax.crypto.SealedObject;

public class Connection implements Closeable {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private AES aes;

    public Connection(Socket socket) {
        this.socket = socket;

        // Make object streams
        try {
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            this.in = new ObjectInputStream(this.socket.getInputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        BigInteger key;
        if ((this instanceof Client)) {
            key = DiffieHellman.serverTransaction(in, out);
        } else {
            key = DiffieHellman.clientTransaction(in, out);
        }

        this.aes = new AES(key);
    }

    public synchronized void send(Message message) throws IOException, Exception {
        SealedObject so = this.aes.encrypt(message);
        this.out.writeObject(so);
        this.out.flush();
    }

    public Message receive() throws IOException, ClassNotFoundException, Exception {
        SealedObject so = (SealedObject) this.in.readObject();
        return (Message) this.aes.decrypt(so);
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void close() {
        try {
            this.out.close();
            this.in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}