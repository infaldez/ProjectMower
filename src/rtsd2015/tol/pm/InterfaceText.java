package rtsd2015.tol.pm;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class InterfaceText {

	private Text text = new Text();
	private Font font;
	private Color color;
	private int posX = 16;
	private int posY = 16;

	public InterfaceText(int x, int y, Font font, Color color) {
		this.posX = x;
		this.posY = y;
		this.font = font;
		this.color = color;
	}

	public void setTextString(String str) {
		text.setText(str);
	}

	public String getTextString() {
		return this.text.getText();
	}

	public void setTextFont(Font font) {
		this.font = font;
	}

	public Font getTextFont() {
		return this.font;
	}

	public void setTextColor(Color color) {
		this.color = color;
	}

	public Color getTextColor() {
		return this.color;
	}

	public Text getText() {
		return this.text;
	}

	public void setPosX(int s) {
		this.posX = s;
	}

	public int getPosX() {
		return this.posX;
	}

	public void setPosY(int s) {
		this.posY = s;
	}

	public int getPosY() {
		return this.posY;
	}
}
