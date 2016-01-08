package rtsd2015.tol.pm;

import rtsd2015.tol.pm.enums.Facing;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

public class KeyboardInput {

	public KeyboardInput(Client client) {

		// Key Pressed increase speed
		Launcher.getAppScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				Game game = client.getGame();
				int clientId = client.getPlayerId();
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
				case ENTER:
					client.startGame();
					break;
				case F:
					client.getRenderer().flush(true);
					break;
				default:
					break;
				}
			}
		});

		// Key Released reset speed
		Launcher.getAppScene().setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				Game game = client.getGame();
				int clientId = client.getPlayerId();
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
				default:
					break;
				}
			}
		});

	}

}
