package kmerrill285.PixelInventor.events;

import org.lwjgl.glfw.GLFW;

import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.Mouse;
import kmerrill285.PixelInventor.game.settings.Settings;

public class Input {
	public static void doInput() {
		float speed = 0.5f;
		float rotSpeed = 2.0f;
		if (Settings.isKeyDown(GLFW.GLFW_KEY_D)) {
			float yaw = 90;
			Camera.position.x+=speed * Camera.getForward(0, Camera.rotation.y + yaw).x;
			Camera.position.y+=speed * Camera.getForward(0, Camera.rotation.y + yaw).y;
			Camera.position.z+=speed * Camera.getForward(0, Camera.rotation.y + yaw).z;
		}
		if (Settings.isKeyDown(GLFW.GLFW_KEY_A)) {
			float yaw = -90;
			Camera.position.x+=speed * Camera.getForward(0, Camera.rotation.y + yaw).x;
			Camera.position.y+=speed * Camera.getForward(0, Camera.rotation.y + yaw).y;
			Camera.position.z+=speed * Camera.getForward(0, Camera.rotation.y + yaw).z;
		}
		if (Settings.isKeyDown(GLFW.GLFW_KEY_W)) {
			float yaw = 0;
			float pitch = 0;
			Camera.position.x+=speed * Camera.getForward(Camera.rotation.x * -1 + pitch, Camera.rotation.y + yaw).x;
			Camera.position.y+=speed * Camera.getForward(Camera.rotation.x * -1 + pitch, Camera.rotation.y + yaw).y;
			Camera.position.z+=speed * Camera.getForward(Camera.rotation.x * -1 + pitch, Camera.rotation.y + yaw).z;
		}
		if (Settings.isKeyDown(GLFW.GLFW_KEY_S)) {
			float yaw = 180;
			float pitch = 0;
			Camera.position.x+=speed * Camera.getForward(Camera.rotation.x + pitch, Camera.rotation.y + yaw).x;
			Camera.position.y+=speed * Camera.getForward(Camera.rotation.x + pitch, Camera.rotation.y + yaw).y;
			Camera.position.z+=speed * Camera.getForward(Camera.rotation.x + pitch, Camera.rotation.y + yaw).z;
		}
		if (Settings.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
			Camera.position.y+=speed;
		}
		if (Settings.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			Camera.position.y-=speed;
		}
		
		if (Mouse.locked) {
			Camera.rotation.y += (Mouse.x - Mouse.lastX) * Settings.MOUSE_SENSITIVITY;
			Camera.rotation.x += (Mouse.y - Mouse.lastY) * Settings.MOUSE_SENSITIVITY;
			
			if (Camera.rotation.x < -90) Camera.rotation.x = -90;
			if (Camera.rotation.x > 90) Camera.rotation.x = 90;
		}
		
		
		
		Mouse.update();
	}
}
