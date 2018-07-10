import java.net.Socket;

public class Client extends Connection {

    private static int nextID = 0;

    private int id;
    private Thread thread;

    public Client(Thread thread, Socket socket) {
        super(socket);
        this.id = nextID;
        nextID++;
        this.thread = thread;
    }

    public int getId() {
        return this.id;
    }

    public Thread getThread() {
        return thread;
    }
}