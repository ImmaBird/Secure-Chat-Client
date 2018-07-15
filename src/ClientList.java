import java.util.LinkedList;
import java.util.Iterator;

public class ClientList implements Iterable<Client>{

    private LinkedList<Client> clients = new LinkedList<Client>();

    public synchronized void add(Client client) {
        clients.add(client);
    }

    public synchronized void remove(int id) {
        clients.removeIf(client -> client.getId() == id);
    }

    public void broadcast(Message message) {
        int senderId = message.getSenderId();
        for (Client client : clients) {
            if (client.getId() != senderId) {
                try {
                    client.send(message);
                } catch (Exception ex) {
                    System.out.printf("Failed to send from client \"%d\" to client \"%d\".\n", senderId,
                            client.getId());
                    ex.printStackTrace();
                }
            }
        }
    }

    public Iterator<Client> iterator() {
        return clients.iterator();
    }
}