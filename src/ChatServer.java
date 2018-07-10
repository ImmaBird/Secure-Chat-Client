import java.net.ServerSocket;
import java.net.Socket;
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

	private boolean serverRunning = true;
	private int port = 7777;
	private ClientList clients = new ClientList();

	private void start() {
		// starts the welcome socket on a new thread
		Thread welcomeThread = new Thread(new Runnable() {
			public void run() {
				welcomeSocket();
			}
		});
		welcomeThread.start();

		// waits for the command to stop the server
		System.out.println("Type \"stop\" to quit.");
		try (Scanner sc = new Scanner(System.in)) {
			while (this.serverRunning) {
				if (sc.nextLine().equals("stop")) {
					System.out.println("Server stopping...");
					this.serverRunning = false;
					welcomeThread.interrupt();
				}
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
					Socket newClient = welcomeSocket.accept();

					// handles clients in seperate threads
					new Thread(new Runnable() {
						public void run() {
							handleClient(newClient);
						}
					}).start();
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
				// TODO handle client
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			clients.remove(id);
		}
	}
}
