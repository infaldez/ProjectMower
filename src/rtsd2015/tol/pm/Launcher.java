package rtsd2015.tol.pm;

import java.io.*;
import java.util.Scanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import rtsd2015.tol.pm.view.RootLayoutController;

public class Launcher extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	private Thread serverThread = null;
	private Thread clientThread = null;

	/**
	 * Begin from constructing the stage
	 */
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Mower Madness 2016");
		initRootLayout();
	}

	/**
	 * Initialize the main window
	 */
	public void initRootLayout() {
		try {
			// Load layout
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Launcher.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			// Create a scene
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			// Give the controller access to the main app
			RootLayoutController controller = loader.getController();
			controller.setMainApp(this);
			// Show the stage
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setClient(String name) {
		Client client = new Client(name);
		Thread clientThread = new Thread(client);
		clientThread.start();

		Scanner scanner = new Scanner(System.in);
		scanner.next();
		scanner.close();

		clientThread.interrupt();
		serverThread.interrupt();
	}

	public void setHost(int port) throws Exception {
		Server server = new Server("localhost", port);
		Thread serverThread = new Thread(server);
		serverThread.start();
		setClient("player1");
	}

	public static void main(String[] args) throws Exception {
		launch(args);
	}

}
