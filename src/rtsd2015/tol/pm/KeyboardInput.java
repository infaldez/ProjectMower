package rtsd2015.tol.pm;

import rtsd2015.tol.pm.enums.Facing;

import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.KeyEvent;

public class KeyboardInput {

	public KeyboardInput(Launcher mainApp, Game game, int clientId) {

		// Key Pressed increase speed
		mainApp.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case UP:
					game.getPlayers().get(clientId).setSpeed(1);
					game.getPlayers().get(clientId).setDir(Facing.NORTH);
					break;
				case DOWN:
					game.getPlayers().get(clientId).setSpeed(1);
					game.getPlayers().get(clientId).setDir(Facing.SOUTH);
					break;
				case LEFT:
					game.getPlayers().get(clientId).setSpeed(1);
					game.getPlayers().get(clientId).setDir(Facing.WEST);
					break;
				case RIGHT:
					game.getPlayers().get(clientId).setSpeed(1);
					game.getPlayers().get(clientId).setDir(Facing.EAST);
					break;
				}
			}
		});

		// Key Released reset speed
		mainApp.getScene().setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case UP:
					game.getPlayers().get(clientId).setSpeed(0);
					break;
				case DOWN:
					game.getPlayers().get(clientId).setSpeed(0);
					break;
				case LEFT:
					game.getPlayers().get(clientId).setSpeed(0);
					break;
				case RIGHT:
					game.getPlayers().get(clientId).setSpeed(0);
					break;
				}
			}
		});

	}

}
