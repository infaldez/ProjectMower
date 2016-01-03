package rtsd2015.tol.pm.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
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

	public void setMainApp(Stage dialogStage, Launcher mainApp) {
		this.dialogStage = dialogStage;
		this.mainApp = mainApp;
	}

	@FXML
	public void handleJoin() {
		dialogStage.close();
		mainApp.setClient("player2", Integer.valueOf(port.getText()));
	}

	@FXML
	public void handleCancel() {
		dialogStage.close();
	}

	private boolean isInputValid() {
		String errMsg = "";
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
