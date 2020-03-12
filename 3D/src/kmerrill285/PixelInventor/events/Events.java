package kmerrill285.PixelInventor.events;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import kmerrill285.PixelInventor.game.client.Mouse;
import kmerrill285.PixelInventor.game.settings.Settings;
import kmerrill285.PixelInventor.resources.Utils;

public class Events {
	public static void keyCallback(long window, int key, int scancode, int action, int mods) {
		if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
			GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
			Mouse.locked = false;
		}
		if (action == GLFW.GLFW_PRESS) {
			Settings.keys.put(key, true);
		}
		if (action == GLFW.GLFW_RELEASE) {
			Settings.keys.put(key, false);
		}
	}
	
	public static void windowSize(long window, int width, int height) {
		float aspect = (float)width/(float)height;
		float w = height * aspect;
		float left = (width - w) / 2;
		GL11.glViewport((int)left, 0, (int)w, height);
	}
	
	public static void mousePos(long window, double x, double y) {
		Mouse.x = (float)x;
		Mouse.y = (float)y;
	}
	
	public static void mouseClick(long window, int button, double x, double y) {
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		Mouse.locked = true;
	}
	
	
}
