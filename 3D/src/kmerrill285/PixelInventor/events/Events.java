package kmerrill285.PixelInventor.events;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import kmerrill285.PixelInventor.game.client.Mouse;
import kmerrill285.PixelInventor.game.client.rendering.gui.GuiRenderer;
import kmerrill285.PixelInventor.game.settings.Settings;
import kmerrill285.PixelInventor.resources.Utils;

public class Events {
	public static void keyCallback(long window, int key, int scancode, int action, int mods) {
		
		if (action == GLFW.GLFW_PRESS) {
			Settings.keys.put(key, true);
			Settings.pressedKey.put(key, true);
		}
		if (action == GLFW.GLFW_RELEASE) {
			Settings.keys.put(key, false);
			Settings.pressedKey.put(key, false);
		}
	}
	
	public static float w;
	public static float left;
	public static float height;
	
	public static void windowSize(long window, int width, int height) {
		float aspect = (float)width/(float)height;
		float w = height * aspect;
		float left = (width - w) / 2;
		GL11.glViewport((int)left, 0, (int)w, height);
		Events.w = w;
		Events.left = left;
		Events.height = height;
		Utils.FRAME_WIDTH = width;
		Utils.FRAME_HEIGHT = height;
	}
	
	public static void mousePos(long window, double x, double y) {
		Mouse.x = (float)x;
		Mouse.y = (float)y;
	}
	
	public static void mouseClick(long window, int button, int press, int undefined) {
		if (GuiRenderer.currentScreen == null) {
			GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
			Mouse.locked = true;
		}
		Settings.buttons.put(button, press == 1);
		Settings.pressedButton.put(button, press == 1);
	}
	
	public static void windowFocus(long window, boolean focused) {
		Utils.WINDOW_FOCUSED = focused;
	}
	
	
}
