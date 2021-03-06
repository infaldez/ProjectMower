package rtsd2015.tol.pm.view;

import java.net.InetAddress;
import java.util.Random;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import rtsd2015.tol.pm.Launcher;

public class NewHostDialogController {

	@FXML
	private Button start;

	@FXML
	private Button cancel;

	@FXML
	private TextField port;

	@FXML
	private TextField seed;

	private Launcher mainApp;
	private Stage dialogStage;

	@FXML
	private void portHilite() {
		port.getStyleClass().add("invalid-hilite");
	}

	@FXML
	private void portNoHilite() {
		port.getStyleClass().remove("invalid-hilite");
	}

	@FXML
	private void initialize() {
	}

	/**
	 * Links the controller and the main application together
	 *
	 * @param dialogStage
	 * @param mainApp
	 */
	public void setMainApp(Stage dialogStage, Launcher mainApp) {
		this.dialogStage = dialogStage;
		this.mainApp = mainApp;
		port.setText(Integer.toString(3330));
		Random random = new Random();
		long randomValue = 16 + (long)(random.nextDouble()*(96 - 16));
		seed.setText(Long.toString(randomValue));
	}

	/**
	 * Attempts to start a new host
	 *
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	@FXML
	public void handleStart() throws NumberFormatException, Exception {
		if (isInputValid()) {
			Integer p = Integer.valueOf(port.getText());
			Long sd = Long.valueOf(seed.getText());
			mainApp.setHost(p, sd);
			dialogStage.close();

			try {
				InetAddress address = InetAddress.getByName("localhost");
				mainApp.setClient("player1", address, p, sd);
			}
			catch (java.net.UnknownHostException e) {
				e.printStackTrace(System.err);
			}
		}
	}

	/**
	 * Closes the new host window
	 *
	 */
	@FXML
	public void handleCancel() {
		dialogStage.close();
	}

	/**
	 * Makes sure everything is set correctly
	 *
	 * @return
	 */
	private boolean isInputValid() {
		String errMsg = "";
		if (port.getText() == null || port.getText().length() == 0) {
			errMsg = "Not valid port";
		} else {
			try {
				Integer p = Integer.parseInt(port.getText());
				if (p < 1024 || p > 65535) {
					errMsg = "Port must in range 1024 - 65535";
				}
			} catch (NumberFormatException e) {
				errMsg = "Not valid port. Must be an integer!";
			}
		}
		if (errMsg.length() == 0) {
			return true;
		} else {
			portHilite();
			// Show the error message.
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(dialogStage);
			alert.setTitle("Invalid Fields");
			alert.setHeaderText("Please correct invalid fields");
			alert.setContentText(errMsg);
			alert.showAndWait();
			return false;
		}
	}
}
