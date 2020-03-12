package kmerrill285.PixelInventor.game.settings;

import java.util.HashMap;

public class Settings {
	public static float FOV = 60.0f;
	public static float MOUSE_SENSITIVITY = 0.1f;
	
	public static int VIEW_X = 10, VIEW_Y = 8;
	
	public static HashMap<Integer, Boolean> keys = new HashMap<Integer, Boolean>();
	
	public static boolean isKeyDown(int key) {
		if (keys.get(key) != null) {
			return keys.get(key);
		}
		keys.put(key, false);
		return false;
	}
}
