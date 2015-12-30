package rtsd2015.tol.pm.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import rtsd2015.tol.pm.Launcher;

public class NewHostDialogController {

	@FXML
	private Button start;

	@FXML
	private Button cancel;

	@FXML
	private CheckBox dedicated;

	@FXML
	private TextField port;

	@FXML
	private TextField seed;

	private Launcher mainApp;
	private Stage dialogStage;

	@FXML
	private void initialize() {}

	public void setMainApp(Stage dialogStage, Launcher mainApp) {
		this.dialogStage = dialogStage;
		this.mainApp = mainApp;
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
			mainApp.setHost(Integer.valueOf(port.getText()));
			dialogStage.close();
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
				Integer.parseInt(port.getText());
			} catch (NumberFormatException e) {
				errMsg = "Not valid port. Must be an integer!";
			}
		}
		if (errMsg.length() == 0) {
			return true;
		} else {
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
