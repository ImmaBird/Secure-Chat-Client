import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class ChatServer {

	public static void main(String[] args) {
		new ChatServer(args);
	}

	// Input checking
	private ChatServer(String[] args) {
		if (args.length > 0) {
			int temp = 0;
			try {
				temp = Integer.parseInt(args[0]);
			} catch (Exception ex) {
			}

			if (temp > 1024 && temp <= 65535) {
				this.port = temp;
			} else {
				System.err.printf("Invalid port \"%s\", defaulting to \"%d\".\n", args[0], this.port);
			}
		} else {
			System.out.printf("Using default port \"%d\".\n", this.port);
		}

		start();
	}

	private volatile boolean serverRunning = true;
	private int port = 7777;
	private volatile ClientList clients = new ClientList();
	private Thread welcomeThread;

	private void start() {
		// starts the welcome socket on a new thread
		this.welcomeThread = new Thread(new Runnable() {
			public void run() {
				welcomeSocket();
			}
		});
		this.welcomeThread.start();

		// handle server commands
		System.out.println("Type \"stop\" to quit.");
		try (Scanner sc = new Scanner(System.in)) {
			while (this.serverRunning) {
				handleServerCommand(sc.nextLine());
			}
		}
	}

	private void welcomeSocket() {
		// auto retry if the welcome socket breaks
		while (this.serverRunning) {
			try (ServerSocket welcomeSocket = new ServerSocket(this.port)) {
				welcomeSocket.setSoTimeout(5000);

				// accepts new connections to the server
				while (this.serverRunning) {
					try {
						Socket newClient = welcomeSocket.accept();

						// handles clients in seperate threads
						new Thread(new Runnable() {
							public void run() {
								handleClient(newClient);
							}
						}).start();
					} catch (SocketTimeoutException ex) {
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void handleClient(Socket clientSocket) {
		int id = 0;
		try (Client client = new Client(Thread.currentThread(), clientSocket)) {
			clients.add(client);
			id = client.getId();

			while (this.serverRunning) {
				Message message = client.receive();
				message.setSenderId(client.getId()); // set sender id
				switch (message.getType()) {
				case serverCommand:
					handleClientCommand(client, message.getText());
					break;
				case text:
					System.out.println(message.getSenderId() + ": " + message.getText());
					System.out.flush();
					this.clients.broadcast(message);
					break;
				case picture:
					System.out.println(message.getSenderId() + ": This client has sent an image.");
					this.clients.broadcast(message);
					break;
				case serverReply:
					// disconnect this client for trying something sneaky
					return;
				}
			}
		} catch (Exception ex) {
			System.out.printf("Client \"%d\" has disconnected.\n", id);
		} finally {
			clients.remove(id);
		}
	}

	private void handleClientCommand(Client client, String command) {
		switch (command) {
		default:
			clientCommandNotFound(client);
			break;
		}
	}

	private void clientCommandNotFound(Client client) {
		try {
			client.send(new Message("Command not found.", Message.messageType.serverReply));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void handleServerCommand(String command) {
		switch (command) {
		case "stop":
			stop();
			break;
		case "des":
			Crypto.algorithm = Crypto.Algorithm.DES;
		case "aes":
			Crypto.algorithm = Crypto.Algorithm.AES;
		default:
			System.err.println("Command not found.");
			break;
		}
	}

	private void stop() {
		System.out.println("Server stopping...");
		this.serverRunning = false;
		this.welcomeThread.interrupt();
		for (Client client : this.clients) {
			client.close();
		}
	}
}
