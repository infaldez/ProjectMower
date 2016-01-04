package rtsd2015.tol.pm;

import java.io.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import rtsd2015.tol.pm.view.NewHostDialogController;
import rtsd2015.tol.pm.view.NewJoinDialogController;
import rtsd2015.tol.pm.view.RootLayoutController;

public class Launcher extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	private RootLayoutController controller;
	private Thread serverThread;
	private Thread clientThread;
	private GraphicsContext gc;

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
			controller = loader.getController();
			controller.setMainApp(this);

			// Show the stage
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays a dialog for creating a new host
	 *
	 */
	public void showNewHostDialog() {
		try {
			// Load layout
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Launcher.class.getResource("view/NewHostDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage dialogStage = new Stage();
	        dialogStage.setTitle("Host a New Game");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);

	        NewHostDialogController newHost = loader.getController();
	        newHost.setMainApp(dialogStage, this);

	        dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays a dialog for joining an existing host
	 *
	 */
	public void showNewJoinDialog() {
		try {
			// Load layout
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Launcher.class.getResource("view/NewJoinDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage dialogStage = new Stage();
	        dialogStage.setTitle("Join to a game");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);

	        NewJoinDialogController newJoin = loader.getController();
	        newJoin.setMainApp(dialogStage, this);

	        dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the rendering space
	 *
	 */
	public void initRenderCanvas() {
		Canvas canvas = new Canvas(800, 520);
		rootLayout.setCenter(canvas);
		rootLayout.setStyle("-fx-background-color: BLACK");

		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setStroke(Color.WHITE);
		gc.setFill(Color.RED);
		gc.strokeText("Receiving ...", 325, 225);
		gc.setFont(new Font("Consolas", 16));
		this.gc = gc;
	}

	/**
	 * Returns the rendering space for the client to use
	 *
	 * @return
	 */
	public GraphicsContext getCanvas() {
		return this.gc;
	}

	/**
	 * Setups a new client
	 *
	 * @param name
	 */
	public void setClient(String name, int port, int seed) {
		controller.switchBtnClient();
		initRenderCanvas();
		Client client = new Client(this, controller, name, port, seed);
		controller.client = client;
		this.clientThread = new Thread(client);
		this.clientThread.start();
	}

	/**
	 * Setups a new host
	 *
	 * @param port
	 * @throws Exception
	 */
	public void setHost(int port, int seed) throws Exception {
		controller.switchBtnHost();
		Server server = new Server("localhost", port, seed);
		controller.server = server;
		this.serverThread = new Thread(server);
		this.serverThread.start();
		setClient("player1", port, seed);
	}

	public static void main(String[] args) throws Exception {
		launch(args);
	}

}
