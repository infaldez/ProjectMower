package rtsd2015.tol.pm;

import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class InterfaceText {

	private Text text = new Text();
	private TextField textField = new TextField();
	private int spacingX = 16;
	private int spacingY = 16;

	public InterfaceText() {}

	public InterfaceText(int x, int y) {
		this.spacingX = x;
		this.spacingY = y;
	}

	public void setTextString(String str) {
		text.setText(str);
	}

	public String getTextString() {
		return this.text.getText();
	}

	public Text getText() {
		return this.text;
	}

	public TextField getTextField() {
		return this.textField;
	}

	public void setSpacingX(int s) {
		this.spacingX = s;
	}

	public int getSpacingX() {
		return this.spacingX;
	}

	public void setSpacingY(int s) {
		this.spacingY = s;
	}

	public int getSpacingY() {
		return this.spacingY;
	}
}
