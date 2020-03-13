package kmerrill285.PixelInventor.game.settings;

import java.util.HashMap;

public class Settings {
	public static float FOV = 60.0f;
	public static float MOUSE_SENSITIVITY = 0.1f;
	
	public static int VIEW_X = 8, VIEW_Y = 8;
	
	public static boolean HEAD_BOB = true;
	
	public static HashMap<Integer, Boolean> keys = new HashMap<Integer, Boolean>();
	public static HashMap<Integer, Boolean> pressedKey = new HashMap<Integer, Boolean>();
	public static HashMap<Integer, Boolean> buttons = new HashMap<Integer, Boolean>();
	public static HashMap<Integer, Boolean> pressedButton = new HashMap<Integer, Boolean>();

	
	public static boolean isKeyDown(int key) {
		if (keys.get(key) != null) {
			return keys.get(key);
		}
		keys.put(key, false);
		return false;
	}
	
	public static boolean isKeyJustDown(int key) {
		if (pressedKey.get(key) != null) {
			if (pressedKey.get(key)) {
				pressedKey.put(key, false);
				return true;
			}
		}
		pressedKey.put(key, false);
		return false;
	}
	
	public static boolean isMouseButtonDown(int button) {
		if (buttons.get(button) != null) {
			return buttons.get(button);
		}
		buttons.put(button, false);
		return false;
	}
	
	public static boolean isMouseButtonJustDown(int button) {
		if (pressedButton.get(button) != null) {
			if (pressedButton.get(button)) {
				pressedButton.put(button, false);
				return true;
			}
		}
		pressedButton.put(button, false);
		return false;
	}
}
