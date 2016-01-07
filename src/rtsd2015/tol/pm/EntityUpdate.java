package rtsd2015.tol.pm;

import java.io.Serializable;

import rtsd2015.tol.pm.enums.Facing;

public class EntityUpdate implements Serializable {
	public int id;
	public int x;
	public int y;
	public Facing dir;
	public int speed;
	public int health;
	
	EntityUpdate(int id, int x, int y, Facing dir, int speed, int health) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.speed = speed;
		this.health = health;
	}
}