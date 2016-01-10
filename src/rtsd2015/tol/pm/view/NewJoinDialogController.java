package rtsd2015.tol.pm.view;

import java.net.InetAddress;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import rtsd2015.tol.pm.Launcher;

public class NewJoinDialogController {

	@FXML
	private Button join;

	@FXML
	private Button cancel;

	@FXML
	private TextField ip;

	@FXML
	private TextField port;

	private Launcher mainApp;
	private Stage dialogStage;

	@FXML
	private void initialize() {}

	/**
	 * Links the controller and the main application together
	 *
	 * @param dialogStage
	 * @param mainApp
	 */
	public void setMainApp(Stage dialogStage, Launcher mainApp) {
		this.dialogStage = dialogStage;
		this.mainApp = mainApp;
	}

	/**
	 * Handles the join button
	 *
	 */
	@FXML
	public void handleJoin() {
		dialogStage.close();
		InetAddress address;
		try {
			address = InetAddress.getByName(ip.getText());
			mainApp.setClient("player2", address, Integer.valueOf(port.getText()), 128);
		}
		catch (java.net.UnknownHostException e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Handles the cancel button
	 *
	 */
	@FXML
	public void handleCancel() {
		dialogStage.close();
	}
}
