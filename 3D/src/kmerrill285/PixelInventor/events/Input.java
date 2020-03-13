package kmerrill285.PixelInventor.events;

import org.lwjgl.glfw.GLFW;

import kmerrill285.PixelInventor.PixelInventor;
import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.Mouse;
import kmerrill285.PixelInventor.game.entity.Entity;
import kmerrill285.PixelInventor.game.settings.Settings;
import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.game.world.chunk.TilePos;
import kmerrill285.PixelInventor.resources.RayTraceResult.Direction;
import kmerrill285.PixelInventor.resources.RayTraceResult.RayTraceType;

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
		
		
		float pitchmul = 0;

		if (Settings.isKeyDown(GLFW.GLFW_KEY_W)) {
			float yaw = 0;
			Camera.position.x+=speed * Camera.getForward(Camera.rotation.x * -1 * pitchmul, Camera.rotation.y + yaw).x;
			Camera.position.y+=speed * Camera.getForward(Camera.rotation.x * -1 * pitchmul, Camera.rotation.y + yaw).y;
			Camera.position.z+=speed * Camera.getForward(Camera.rotation.x * -1 * pitchmul, Camera.rotation.y + yaw).z;
		}
		if (Settings.isKeyDown(GLFW.GLFW_KEY_S)) {
			float yaw = 180;
			Camera.position.x+=speed * Camera.getForward(Camera.rotation.x * pitchmul, Camera.rotation.y + yaw).x;
			Camera.position.y+=speed * Camera.getForward(Camera.rotation.x * pitchmul, Camera.rotation.y + yaw).y;
			Camera.position.z+=speed * Camera.getForward(Camera.rotation.x * pitchmul, Camera.rotation.y + yaw).z;
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
		
		if (Settings.isMouseButtonJustDown(0)) {
			PixelInventor game = PixelInventor.game;
			if (Camera.currentTile != null) {
				if (Camera.currentTile.getType() == RayTraceType.TILE) {
					game.world.setTile(Camera.currentTile.getPosition(), Tiles.AIR);
				}
			}
		}
		
		if (Settings.isMouseButtonJustDown(1)) {
			PixelInventor game = PixelInventor.game;
			if (Camera.currentTile != null) {
				if (Camera.currentTile.getType() == RayTraceType.TILE) {
					TilePos pos = new TilePos(Camera.currentTile.getPosition().x, Camera.currentTile.getPosition().y, Camera.currentTile.getPosition().z);
					Direction direction = Camera.currentTile.getDirection();
					if (direction != null) {
						pos.x += direction.x;
						pos.y += direction.y;
						pos.z += direction.z;
						boolean stop = false;
						for (Entity e : game.world.entities) {
							if (pos.x == e.getTilePos().x)
								if (pos.y == e.getTilePos().y || pos.y == e.getTilePos().y + 1)
									if (pos.z == e.getTilePos().z) {
										stop = true;
										break;
									}
						}
						if (!stop)
						game.world.setTile(pos, Tiles.GRASS);
					}
					
				}
			}
		}
		
		Mouse.update();
	}
}
