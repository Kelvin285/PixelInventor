package kmerrill285.Inignoto.resources;

import java.io.PrintStream;

public class FPSCounter {
	private static int fps;
	private static long lastFPS;
	private static long currentTime;
	private static long lastTime;
	private static double delta;
	private static long totalGameTime = 0;
	public static final long MIN_DELTA = 1000000/20; // 20fps
	
	public static long getTime() {
		return totalGameTime;
	}
	
	public static void start() {
		lastFPS = getTime();
	}
	
	public static void updateFPS() {
		if (System.nanoTime() / 1000000 - lastFPS > 1000) {
//			System.out.println(fps);
			fps = 0;
			lastFPS = System.nanoTime() / 1000000;
		}
		fps++;
	}
	public static void startUpdate() {
		currentTime = System.nanoTime();
		delta = (Math.max(currentTime-lastTime,MIN_DELTA) / 1000000.0) / 20.0;
		if (delta < 1.5f) delta = 1.5f;
	}
	
	public static void endUpdate() {
		lastTime = currentTime;
		totalGameTime+=delta;
	}

	public static double getDelta() {
		return delta;
	}
	
	public static int getFPS() {
		return fps;
	}
}
