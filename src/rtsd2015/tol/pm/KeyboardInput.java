package rtsd2015.tol.pm;

import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.KeyEvent;

public class KeyboardInput {

	public KeyboardInput(Launcher mainApp){

		// testing input
        mainApp.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case UP:
                    	System.out.println("UP");
                    	break;
                    case DOWN:
	                	System.out.println("DOWN");
	                	break;
                    case LEFT:
	                	System.out.println("LEFT");
	                	break;
                    case RIGHT:
	                	System.out.println("RIGHT");
	                	break;
                }
            }
        });

	}

}
