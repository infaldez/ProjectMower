package rtsd2015.tol.pm;

import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.KeyEvent;

public class KeyboardInput {

	public KeyboardInput(Launcher mainApp, Game game){

		// testing input
        mainApp.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case UP:
                    	System.out.println("UP");
                    	game.getPlayers().get(0).setPos(game.getPlayers().get(0).getPos()[0], game.getPlayers().get(0).getPos()[1] -1);
                    	break;
                    case DOWN:
                    	game.getPlayers().get(0).setPos(game.getPlayers().get(0).getPos()[0], game.getPlayers().get(0).getPos()[1] +1);
	                	System.out.println("DOWN");
	                	break;
                    case LEFT:
                    	game.getPlayers().get(0).setPos(game.getPlayers().get(0).getPos()[0] -1, game.getPlayers().get(0).getPos()[1]);
	                	System.out.println("LEFT");
	                	break;
                    case RIGHT:
	                	System.out.println("RIGHT");
	                	game.getPlayers().get(0).setPos(game.getPlayers().get(0).getPos()[0] +1, game.getPlayers().get(0).getPos()[1]);
	                	break;
                }
            }
        });

	}

}
