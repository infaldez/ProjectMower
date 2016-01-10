package rtsd2015.tol.pm;

import java.io.*;
import java.net.InetAddress;
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

	public static int[] getContentSpace() {
		int[] space = new int[2];
		space[0] = (int) rootLayout.getWidth();
		space[1] = (int) rootLayout.getHeight()-48;
		return space;
	}

	/**
	 * Initializes the rendering space
	 *
	 */
	public static void setAppViewport() {
		Pane gfx = new Pane();
		canvases.add(new Canvas(getContentSpace()[0], getContentSpace()[1]));
		canvases.add(new Canvas(getContentSpace()[0], getContentSpace()[1]));
		canvases.add(new Canvas(getContentSpace()[0], getContentSpace()[1]));
		gfx.getChildren().add(canvases.get(0));
		gfx.getChildren().add(canvases.get(1));
		gfx.getChildren().add(canvases.get(2));
		canvases.get(0).toFront();
		canvases.get(2).toFront();
		rootLayout.setCenter(gfx);
		rootLayout.setStyle("-fx-background-color: BLACK");
	}

	/**
	 * Update size of the canvases to correspond the new screen dimensions
	 *
	 */
	public static void updateAppViewPortSize() {
		canvases.get(0).setWidth(getContentSpace()[0]);
		canvases.get(0).setHeight(getContentSpace()[1]);
		canvases.get(1).setWidth(getContentSpace()[0]);
		canvases.get(1).setHeight(getContentSpace()[1]);
		canvases.get(2).setWidth(getContentSpace()[0]);
		canvases.get(2).setHeight(getContentSpace()[1]);
	}

	/**
	 * Returns the desired canvas
	 * 0: dynamic, 1: static
	 *
	 * @param c
	 * @return
	 */
	public static Canvas getAppCanvas(int c) {
		return Launcher.canvases.get(c);
	}

	/**
	 * Set the App into a debug mode
	 *
	 * @param b
	 */
	public static void setAppDebug(boolean b) {
		debug = b;
	}

	public static boolean getAppDebug() {
		return debug;
	}

	public static Scene getAppScene(){
		return scene;
	}

	/**
	 * Setups a new client
	 *
	 * @param name
	 */
	public void setClient(String name, InetAddress address, int port, long seed) {
		controller.switchBtnClient();
		setAppViewport();
		Client client = new Client(controller, name, seed);
		try {
			client.joinServer(address, port);
		}
		catch (IOException e) {
			e.printStackTrace(System.err);
		}
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
		Server server = new Server(port, seed);
		controller.server = server;
		serverThread = new Thread(server);
		serverThread.start();
	}

	public static void main(String[] args) throws Exception {
		launch(args);
	}

}
