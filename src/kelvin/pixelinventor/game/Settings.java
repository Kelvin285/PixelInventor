package kelvin.pixelinventor.game;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Settings {
	public static int UP = KeyEvent.VK_W;
	public static int DOWN = KeyEvent.VK_S;
	public static int LEFT = KeyEvent.VK_A;
	public static int RIGHT = KeyEvent.VK_D;
	public static int ZOOM_IN = KeyEvent.VK_EQUALS;
	public static int ZOOM_OUT = KeyEvent.VK_MINUS;
	
	public static int ATTACK = MouseEvent.BUTTON1;
	public static int USE = MouseEvent.BUTTON3;
	
	public static boolean[] keys = new boolean[1000];
	public static boolean[] pressed = new boolean[1000];
	
	public static boolean[] buttons = new boolean[10];
	public static boolean[] mouseDown = new boolean[10];
	
	public static int frameSkip = 1;
	public static boolean distortion = true;
	
	public static boolean smoothLights = true;
	public static int lightCelValues = 255;
	
	public static boolean clouds = true;

	public static boolean isMouseButtonDown(int button) {
		return buttons[button];
	}
	
	public static boolean isMouseButtonPressed(int button) {
		if (!isKeyDown(button)) mouseDown[button] = false;
		if (isKeyDown(button) && !mouseDown[button]) {
			buttons[button] = true;
			return true;
		}
		return false;
	}
	
	public static boolean isKeyDown(int key) {
		return keys[key];
	}
	
	public static boolean isKeyPressed(int key) {
		if (!isKeyDown(key)) pressed[key] = false;
		if (isKeyDown(key) && !pressed[key]) {
			pressed[key] = true;
			return true;
		}
		return false;
	}
}
