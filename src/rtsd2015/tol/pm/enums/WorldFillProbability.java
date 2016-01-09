package rtsd2015.tol.pm.enums;

import java.util.Random;

public enum WorldFillProbability {
	TREE(100),
	SMALLROCK(40),
	BIGROCK(10);

	private double probability;
	Random random = new Random(128);

	private WorldFillProbability(double p) {
		this.probability = p;
	}

	public double getPropability() {
		return probability;
	}

	public static final WorldFillProbability values[] = values();

}
