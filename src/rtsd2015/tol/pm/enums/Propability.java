package rtsd2015.tol.pm.enums;

public enum Propability {
	GRASS(0.25),
	GRASS_1(0.25),
	GRASS_2(0.25),
	DIRT(0.25),
	TREE(0.5),
	BIGROCK(0.2),
	SMALLROCK(0.3),
	PLAYER1(1.0),
	PLAYER2(1.0),
	BLUEFLOWER(0.5),
	REDFLOWER(0.5);

	private double propability;

	private Propability(double p) {
		this.propability = p;
	}

	public double getPropability() {
		return propability;
	}

	public static final Propability values[] = values();

}
