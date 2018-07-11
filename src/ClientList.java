import java.util.LinkedList;

public class ClientList {

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
}