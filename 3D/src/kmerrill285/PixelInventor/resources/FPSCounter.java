package kmerrill285.PixelInventor.resources;

public class FPSCounter {
	private static int fps;
	private static long lastFrame;
	private static long lastFPS;
	public static long getTime() {
		return System.nanoTime() / 1000000;
	}
	
	public static void start() {
		lastFPS = getTime();
	}
	
	public static void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}

	public static int getFPS() {
		return fps;
	}
}
