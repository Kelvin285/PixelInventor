package kmerrill285.PixelInventor.resources;

public class TPSCounter {
	private static int tps;
	private static long lastTPS;
	
	private static double delta;
	
	private static boolean tick = false;
	
	public static long getTime() {
		return System.nanoTime() / 1000000;
	}
	
	public static void start() {
		lastTPS = getTime();
	}
	
	public static void updateTPS() {
		tick = false;
		int TPS = 25;
		while (getTime() - lastTPS > TPS) {
			delta = FPSCounter.getDelta();
			
			tps = 0;
			tick = true;
			lastTPS += TPS;
		}
		tps++;
	}
	
	public static boolean canTick() {
		return tick;
	}

	public static double getDelta() {
		return delta;
	}
	
	public static int getFPS() {
		return tps;
	}
}
