package kelvin.pixelinventor.game;

import java.awt.event.KeyEvent;

public class Settings {
	public static int UP = KeyEvent.VK_W;
	public static int DOWN = KeyEvent.VK_S;
	public static int LEFT = KeyEvent.VK_A;
	public static int RIGHT = KeyEvent.VK_D;
	
	public static boolean[] keys = new boolean[1000];
	public static boolean[] pressed = new boolean[1000];

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
