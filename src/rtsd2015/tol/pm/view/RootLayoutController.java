package rtsd2015.tol.pm.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.text.Text;
import rtsd2015.tol.pm.Client;
import rtsd2015.tol.pm.Launcher;
import rtsd2015.tol.pm.Server;

public class RootLayoutController {
	private Launcher mainApp;
	public Client client;
	public Server server = null;

	@FXML
	private Text lblStatus;

	@FXML
	private Text lblPing;

	@FXML
	private MenuItem btnHost;

	@FXML
	private MenuItem btnJoin;

	@FXML
	private RadioMenuItem btnDebug;

	public void setMainApp(Launcher mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * Close the application
	 *
	 */
	@FXML
	private void handleClose() {
		System.exit(0);
	}

	/**
	 * Connect to a new server pop-up
	 *
	 */
	@FXML
	private void handleConnect() {
		mainApp.showNewJoinDialog();
	}

	/**
	 * Host a new game pop-up
	 *
	 * @throws Exception
	 */
	@FXML
	private void handleNewHost() throws Exception {
		//mainApp.setHost(3145);
		mainApp.showNewHostDialog();
	}

	/**
	 * Credits pop-up
	 *
	 */
	@FXML
	private void handleCredits() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Credits");
		alert.setHeaderText("Team Mower");
		alert.setContentText("Ari H�ysniemi, Daniel Askeli, Janne S�nkiaho\nRTSD Software Project\nUniversity of Oulu, 2015");
		alert.showAndWait();
	}

	/**
	 * Handles enabling/disabling debug information
	 *
	 */
	@FXML void handleDebug() {
		if (btnDebug.isSelected()) {
			Launcher.setAppDebug(true);
		} else {
			Launcher.setAppDebug(false);
		}
	}

	/**
	 * Update status message
	 *
	 * @param msg
	 */
	public void setStatus(String msg) {
		lblStatus.setText("Status: "+msg);
	}

	/**
	 * Update ping
	 *
	 * @param msg
	 */
	public void setPing(String msg) {
		lblPing.setText(msg);
	}

	/**
	 * Enable/disable Host button
	 *
	 */
	public void switchBtnHost() {
		if (btnHost.isDisable()) {
			btnHost.setDisable(false);
		} else {
			btnHost.setDisable(true);
		}
	}

	/**
	 * Enable/disable Join button
	 *
	 */
	public void switchBtnClient() {
		if (btnJoin.isDisable()) {
			btnJoin.setDisable(false);
		} else {
			btnJoin.setDisable(true);
		}
	}
}
