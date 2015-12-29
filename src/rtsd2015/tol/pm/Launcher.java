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
	 * Begin to construct the stage
	 *
	 */
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Mower Madness 2016");
		initRootLayout();
	}

	/**
	 * Initializes the main BorderPane window
	 *
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

	/**
	 * Setups a new client
	 *
	 * @param name
	 */
	public void setClient(String name) {
		Client client = new Client(name);
		this.clientThread = new Thread(client);
		this.clientThread.start();
		Scanner scanner = new Scanner(System.in);
		scanner.next();
		scanner.close();
		clientThread.interrupt();
		serverThread.interrupt();
	}

	/**
	 * Setups a new host
	 *
	 * @param port
	 * @throws Exception
	 */
	public void setHost(int port) throws Exception {
		Server server = new Server("localhost", port);
		this.serverThread = new Thread(server);
		this.serverThread.start();
		setClient("player1");
	}

	public static void main(String[] args) throws Exception {
		launch(args);
	}

}
