import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

class ChatClient extends Application {

	public static void main(String[] args) {
		Application.launch();
	}

	private static int width = 600;
	private static int height = 400;

	public void start(Stage stage) {
		Pane pane = new Pane();
		stage.setScene(new Scene(pane, width, height));
		stage.show();
		while (true) {

		}
	}

}
