import java.net.Socket;
import java.net.SocketAddress;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ChatClient extends Application {

	private final String TITLE = "Secure Chat";
	private String name = "";
	private String ip = "";
	private int port = 0;
	private Connection server;
	private ListView<String> chatArea;

	public static void main(String[] args) {
		Application.launch();
	}

	public void start(Stage stage) {
		stage.setTitle(this.TITLE);
		stage.setResizable(false);
		stage.setOnCloseRequest(event -> {
			System.exit(0);
		});
		showMenu(stage);
		stage.show();
	}

	private void showMenu(Stage stage) {
		int width = 300;
		int height = 200;

		StackPane menuPane = new StackPane();

		VBox vb = new VBox();
		menuPane.getChildren().add(vb);

		Label nameLabel = new Label("Name");
		TextField nameField = new TextField("User");

		Label ipLabel = new Label("IP");
		TextField ipField = new TextField("localhost:7777");

		Button submit = new Button("Submit");
		submit.setOnMouseClicked(event -> {
			this.name = nameField.getText();
			String[] ipPort = ipField.getText().split(":");

			if (ipPort.length == 2) {
				this.ip = ipPort[0];
				try {
					this.port = Integer.parseInt(ipPort[1]);
				} catch (Exception ex) {
				}
			}

			if (!(this.name.isEmpty() || this.ip.isEmpty() || this.port == 0)) {
				showChat(stage);
			}
		});

		vb.getChildren().addAll(nameLabel, nameField, ipLabel, ipField, submit);

		stage.setScene(new Scene(menuPane, width, height));
	}

	private void showChat(Stage stage) {
		int width = 600;
		int height = 400;

		stage.setTitle(this.TITLE + " : " + name);

		BorderPane chatPane = new BorderPane();

		this.chatArea = new ListView<String>();

		this.chatArea.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			public ListCell<String> call(final ListView<String> list) {
				return new ListCell<String>() {
					{
						Text text = new Text();
						text.wrappingWidthProperty().bind(list.widthProperty().subtract(15));
						text.textProperty().bind(itemProperty());

						setPrefWidth(0);
						setGraphic(text);
					}
				};
			}
		});

		TextArea messageBox = new TextArea();
		messageBox.setWrapText(true);
		messageBox.setPrefHeight(60);
		messageBox.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.ENTER)) {
				String message = messageBox.getText().replace("\n", "");
				if (!message.isEmpty()) {
					sendMessage(message);
				}
				messageBox.clear();
			}
		});

		Button send = new Button("Send");
		send.setOnMouseClicked(event -> {
			String message = messageBox.getText();
			if (!message.isEmpty()) {
				sendMessage(message);
			}
			messageBox.clear();
		});

		HBox hb = new HBox();
		HBox.setHgrow(messageBox, Priority.ALWAYS);

		hb.getChildren().addAll(messageBox, send);

		chatPane.setCenter(this.chatArea);
		chatPane.setBottom(hb);

		Insets margin = new Insets(10, 10, 10, 10);
		for (Node child : chatPane.getChildren()) {
			chatPane.setMargin(child, margin);
		}

		joinServer();

		stage.setScene(new Scene(chatPane, width, height));
	}

	// --------------------------------- Connection stuff
	// --------------------------------
	private void joinServer() {
		try {
			Socket serverSocket = new Socket(this.ip, this.port);
			this.server = new Connection(serverSocket);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		listenForMessages();
	}

	private void listenForMessages() {
		new Thread(() -> {
			try {
				while (true) {
					Message message = this.server.receive();
					switch (message.getType()) {
					case text:
						message.getText();
						Platform.runLater(() -> {
							this.chatArea.getItems().add(message.getSenderName() + ": " + message.getText());
							this.chatArea.scrollTo(this.chatArea.getItems().size() - 1);
						});
						break;
					case picture:
						break;
					case serverReply:
						break;
					case serverCommand:
						break;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}).start();
	}

	private void sendMessage(String text) {
		// ui stuff
		this.chatArea.getItems().add("You: " + text);
		this.chatArea.scrollTo(this.chatArea.getItems().size() - 1);

		// sending stuff
		try {
			Message message = new Message(text, Message.messageType.text);
			message.setSenderName(this.name);
			this.server.send(message);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
