import javafx.application.Application;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatClient extends Application {

	private String name;
	private String ip;
	private int port;

	public static void main(String[] args) {
		Application.launch();
	}

	public void start(Stage stage) {
		stage.setTitle("Secure Chat");
		showMenu(stage);
		stage.show();
	}

	private void showMenu(Stage stage){
		int width = 300;
		int height = 200;

		StackPane menuPane = new StackPane();

		VBox vb = new VBox();
		menuPane.getChildren().add(vb);

		Label nameLabel = new Label("Name");
		TextField nameField = new TextField();

		Label ipLabel = new Label("IP");
		TextField ipField = new TextField();

		Button submit = new Button("Submit");

		vb.getChildren().addAll(nameLabel, nameField, ipLabel, ipField, submit);

		stage.setScene(new Scene(menuPane, width, height));
	}

	private void showChat(Stage stage){
		int width = 600;
		int height = 400;

		BorderPane chatPane = new BorderPane();

		ListView<String> chatArea = new ListView<String>();

		chatPane.getChildren().add(chatArea);

		stage.setScene(new Scene(chatPane, width, height));
	}

}
