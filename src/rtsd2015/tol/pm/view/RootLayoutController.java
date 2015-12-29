package rtsd2015.tol.pm.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import rtsd2015.tol.pm.Launcher;

public class RootLayoutController {
	private Launcher mainApp;

	public void setMainApp(Launcher mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * Close the application
	 */
	@FXML
	private void handleClose() {
		// TODO: Make sure sockets are cleared
		System.exit(0);
	}

	/**
	 * Connect to a new server pop-up
	 */
	@FXML
	private void handleConnect() {
		mainApp.setClient("player2");
	}

	/**
	 * Disconnect from the server
	 * @throws Exception
	 */
	@FXML
	private void handleDisconnect() {

	}

	/**
	 * Host a new game pop-up
	 * @throws Exception
	 */
	@FXML
	private void handleNewHost() throws Exception {
		mainApp.setHost(3144);
	}

	/**
	 * Credits pop-up
	 */
	@FXML
	private void handleCredits() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Credits");
		alert.setHeaderText("Team Mower");
		alert.setContentText("Ari Höysniemi, Daniel Askeli, Janne Sänkiaho\nRTSD Software Project\nUniversity of Oulu, 2015");
		alert.showAndWait();
	}
}
