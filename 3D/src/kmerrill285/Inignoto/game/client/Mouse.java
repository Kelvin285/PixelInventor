package kmerrill285.Inignoto.game.client;

public class Mouse {
	public static float x;
	public static float y;
	
	public static float lastX;
	public static float lastY;
	
	public static boolean locked = false;
	
	public static void update() {
		Mouse.lastX = Mouse.x;
		Mouse.lastY = Mouse.y;
	}
}
