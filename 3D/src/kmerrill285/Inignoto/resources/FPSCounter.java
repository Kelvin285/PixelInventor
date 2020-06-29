package kmerrill285.Inignoto.resources;

public class FPSCounter {
	private static int fps;
	private static long lastFPS;
	private static long currentTime = System.currentTimeMillis();;
	private static long lastTime = currentTime;
	private static double delta;
	private static double totalGameTime = 0;
	public static final long MIN_DELTA = 1000000/20; // 20fps
	
	public static long getTime() {
		return (long)totalGameTime;
	}
	
	public static void start() {
		lastFPS = getTime();
	}
	
	public static void updateFPS() {
		if (System.nanoTime() / 1000000 - lastFPS > 1000) {
			System.out.println(fps);
			fps = 0;
			lastFPS = System.nanoTime() / 1000000;
		}
		fps++;
	}
	public static void startUpdate() {
		currentTime = System.currentTimeMillis();
		delta = ((currentTime-lastTime) / 1000.0);
		if (delta < 0) delta = 0;
		if (delta > 1) delta = 1;
		
	}
	
	public static void endUpdate() {
		lastTime = currentTime;
		totalGameTime+=delta;
	}

	public static double getDelta() {
		
		return delta * 16.667 * 10.0;
	}
	
	public static int getFPS() {
		return fps;
	}
}
