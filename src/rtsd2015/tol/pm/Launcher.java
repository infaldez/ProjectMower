package rtsd2015.tol.pm;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import rtsd2015.tol.pm.view.NewHostDialogController;
import rtsd2015.tol.pm.view.NewJoinDialogController;
import rtsd2015.tol.pm.view.RootLayoutController;
import javafx.event.*;

public class Launcher extends Application {

	private static Stage primaryStage;
	private static BorderPane rootLayout;
	private static RootLayoutController controller;
	private static Thread serverThread;
	private static Thread clientThread;
	private static List<Canvas> canvases = new ArrayList <Canvas>();
	private static int[] contentSpace = new int[2];
	private static boolean debug = false;
	private static Scene scene;
	/**
	 * Begin to construct the stage
	 *
	 */
	@Override
	public void start(Stage primaryStage) {
		Launcher.primaryStage = primaryStage;
		Launcher.primaryStage.setTitle("Mower Madness 2016");
		Launcher.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				System.exit(0);
			}
		});
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
			scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			// Give the controller access to the main app
			controller = loader.getController();
			controller.setMainApp(this);

			// Initialize space dedicated for the content
			contentSpace[0] = 800;
			contentSpace[1] = 520;

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
	public void setViewport() {
		canvases.add(new Canvas(contentSpace[0], contentSpace[1]));
		canvases.add(new Canvas(contentSpace[0], contentSpace[1]));
		Pane gfx = new Pane();
		gfx.getChildren().add(canvases.get(0));
		gfx.getChildren().add(canvases.get(1));
		canvases.get(0).toFront();
		rootLayout.setCenter(gfx);
		rootLayout.setStyle("-fx-background-color: BLACK");
	}

	public Canvas getCanvas(int c) {
		return Launcher.canvases.get(c);
	}

	public void setDebug(boolean b) {
		debug = b;
	}

	public boolean getDebug() {
		return debug;
	}

	public int[] getContentSpace() {
		return contentSpace;
	}

	public Scene getScene(){
		return scene;
	}

	/**
	 * Setups a new client
	 *
	 * @param name
	 */
	public void setClient(String name, int port, long seed) {
		controller.switchBtnClient();
		setViewport();
		Client client = new Client(this, controller, name, port, seed);
		controller.client = client;
		clientThread = new Thread(client);
		clientThread.start();
	}

	/**
	 * Setups a new host
	 *
	 * @param port
	 * @throws Exception
	 */
	public void setHost(int port, long seed) throws Exception {
		controller.switchBtnHost();
		Server server = new Server("localhost", port, seed);
		controller.server = server;
		serverThread = new Thread(server);
		serverThread.start();
		setClient("player1", port, seed);
	}

	public static void main(String[] args) throws Exception {
		launch(args);
	}

}
