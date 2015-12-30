package rtsd2015.tol.pm.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
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
	private MenuItem btnDisconnect;

	public void setMainApp(Launcher mainApp) {
		this.mainApp = mainApp;
		btnDisconnect.setDisable(true);
	}

	/**
	 * Close the application
	 *
	 */
	@FXML
	private void handleClose() {
		// TODO: Make sure sockets are cleared
		System.exit(0);
	}

	/**
	 * Connect to a new server pop-up
	 *
	 */
	@FXML
	private void handleConnect() {
		mainApp.setClient("player2", 3145);
	}

	/**
	 * Disconnect from the server
	 *
	 * @throws Exception
	 */
	@FXML
	private void handleDisconnect() {
		if (server == null) {
			client.disconnect();
		} else {
			client.disconnect();
			server.close();
		}
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
		alert.setContentText("Ari Höysniemi, Daniel Askeli, Janne Sänkiaho\nRTSD Software Project\nUniversity of Oulu, 2015");
		alert.showAndWait();
	}

	/**
	 * Update status message
	 *
	 * @param msg
	 */
	@FXML
	public void setStatus(String msg) {
		lblStatus.setText("Status: "+msg);
	}

	/**
	 * Update ping
	 *
	 * @param msg
	 */
	@FXML
	public void setPing(String msg) {
		lblPing.setText(msg);
	}

	/**
	 * Enable/disable Host button
	 *
	 */
	@FXML
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
	@FXML
	public void switchBtnClient() {
		if (btnJoin.isDisable()) {
			btnJoin.setDisable(false);
		} else {
			btnJoin.setDisable(true);
		}
	}

	/**
	 * Enable/disable Disconnect button
	 *
	 */
	@FXML
	public void switchBtnDisconnect() {
		if (btnDisconnect.isDisable()) {
			btnDisconnect.setDisable(false);
			btnHost.setDisable(true);
			btnJoin.setDisable(true);
		} else {
			btnDisconnect.setDisable(true);
			btnHost.setDisable(false);
			btnJoin.setDisable(false);
		}
	}
}
