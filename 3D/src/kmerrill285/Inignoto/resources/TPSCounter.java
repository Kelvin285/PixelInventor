package kmerrill285.Inignoto.resources;

public class TPSCounter {
	private static int tps;
	private static long lastTPS;
	private static long currentTime = System.currentTimeMillis();;
	private static long lastTime = currentTime;
	public static double delta;
	private static double totalGameTime = 0;
	public static final long MIN_DELTA = 1000000/20; // 20fps
	
	private static boolean tick = false;
	
	public static long getTime() {
		return System.nanoTime() / 1000000;
	}
	
	public static void start() {
		lastTPS = getTime();
	}
	
	public static void updateTPS() {
		tick = false;
		int TPS = 30;
		while (getTime() - lastTPS > TPS) {
			delta = FPSCounter.getDelta();
//			System.out.println("TPS: " + tps);
			tps = 0;
			tick = true;
			lastTPS += TPS;
		}
		tps++;
	}
	
	public static boolean canTick() {
		return tick;
	}
	
	public static void startUpdate() {
		currentTime = System.currentTimeMillis();
		delta = ((currentTime-lastTime) / 1000.0);
	}
	
	public static void endUpdate() {
		lastTime = currentTime;
		totalGameTime+=delta;
	}

	public static double getTrueDelta() {
		double d = delta;
		if (d > 0.012) d = 0.012;
		if (d < Double.MIN_VALUE) d = Double.MIN_VALUE;
		return d;
	}
	
	public static double getDelta() {
		double d = delta * 16.667 * 1.0;
		if (d > 0.012 * 16.667) d = 0.012 * 16.667;
		if (d < Double.MIN_VALUE) d = Double.MIN_VALUE;
		return d;
	}
}
