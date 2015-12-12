package rtsd2015.tol.pm;

public class StopWatch {
	private long startTime;
	public boolean running = false;

	public void start() {
		startTime = System.nanoTime();
		running = true;
	}
	
	public long stop() {
		long time = get();
		running = false;
		return time;
	}
	
	public long get() {
		return System.nanoTime() - startTime;
	}
	
	public long lap() {
		long time = stop();
		start();
		return time;
	}
	
	public static double ms(long time) {
		return (double) time / 1e6;
	}

	public double msGet() {
		return ms(get());
	}
	
	public double msStop() {
		return ms(stop());
	}
	
	public double msLap() {
		return ms(lap());
	}
}
