package kmerrill285.PixelInventor.game.settings;

import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

public class Settings {
	public static float FOV = 60.0f;
	public static float MOUSE_SENSITIVITY = 0.1f;
	
	public static int VIEW_X = 8, VIEW_Y = 8;
	
	public static boolean HEAD_BOB = true;
	
	public static HashMap<String, InputSetting> inputs = new HashMap<String, InputSetting>();
	
	public static InputSetting JUMP = new InputSetting(GLFW.GLFW_KEY_SPACE, false, "PixelInventor:input.jump");
	public static InputSetting SNEAK = new InputSetting(GLFW.GLFW_KEY_LEFT_SHIFT, false, "PixelInventor:input.sneak");
	public static InputSetting RUN = new InputSetting(GLFW.GLFW_KEY_LEFT_CONTROL, false, "PixelInventor:input.run");
	public static InputSetting EXIT = new InputSetting(GLFW.GLFW_KEY_ESCAPE, false, "PixelInventor:input.exit");
	public static InputSetting INVENTORY = new InputSetting(GLFW.GLFW_KEY_E, false, "PixelInventor:input.inventory");
	public static InputSetting FORWARD = new InputSetting(GLFW.GLFW_KEY_W, false, "PixelInventor:input.forward");
	public static InputSetting BACKWARD = new InputSetting(GLFW.GLFW_KEY_S, false, "PixelInventor:input.backward");
	public static InputSetting LEFT = new InputSetting(GLFW.GLFW_KEY_A, false, "PixelInventor:input.left");
	public static InputSetting RIGHT = new InputSetting(GLFW.GLFW_KEY_D, false, "PixelInventor:input.right");
	public static InputSetting ATTACK = new InputSetting(0, true, "PixelInventor:input.attack");
	public static InputSetting USE = new InputSetting(1, true, "PixelInventor:input.use");
	
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
