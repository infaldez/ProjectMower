package rtsd2015.tol.pm.enums;

import java.util.Random;

public enum WorldFillProbability {
	TREE(100),
	SMALLROCK(40),
	BIGROCK(10);

	private double probability;
	private Random random = new Random(120);

	private WorldFillProbability(double p) {
		this.probability = p;
	}

	private double getPropability() {
		return probability;
	}

	public boolean getWillBePlaced() {
		int number = random.nextInt((100-0) + 0) + 1;
		if (number < getPropability()) {
			return true;
		} else {
			return false;
		}
	}

	public static final WorldFillProbability values[] = values();

}
