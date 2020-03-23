package kmerrill285.PixelInventor.events;

import java.awt.Rectangle;

import kmerrill285.PixelInventor.PixelInventor;
import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.Mouse;
import kmerrill285.PixelInventor.game.client.rendering.gui.GuiRenderer;
import kmerrill285.PixelInventor.game.client.rendering.gui.IngameMenuScreen;
import kmerrill285.PixelInventor.game.client.rendering.gui.InventoryScreen;
import kmerrill285.PixelInventor.game.entity.Entity;
import kmerrill285.PixelInventor.game.settings.Settings;
import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.game.world.chunk.TilePos;
import kmerrill285.PixelInventor.resources.FPSCounter;
import kmerrill285.PixelInventor.resources.RayTraceResult.Direction;
import kmerrill285.PixelInventor.resources.RayTraceResult.RayTraceType;

public class Input {
	
	public static void doInput() {
		GuiRenderer renderer = PixelInventor.game.guiRenderer;
		if (renderer.getOpenScreen() == null) {
			doGameInput();
		}
		
		if (Settings.INVENTORY.isJustPressed()) {
			if (renderer != null) {
				if (!(renderer.getOpenScreen() instanceof IngameMenuScreen)) {
					if (renderer.getOpenScreen() != null) {
						renderer.closeScreen();
					} else {
						renderer.openScreen(new InventoryScreen(renderer));
					}
				}
			}
		}
		if (Settings.EXIT.isJustPressed()) {
			if (renderer != null) {
				if (renderer.getOpenScreen() != null) {
					renderer.closeScreen();
				} else {
					renderer.openScreen(new IngameMenuScreen(renderer));
				}
			}
		}
		
		Mouse.update();
	}
	
	private static void updateCamera() {
		float speed = 0.1f;
		float rotSpeed = 2.0f;
		if (Settings.RIGHT.isPressed()) {
			float yaw = 90;
			Camera.position.x+=speed * Camera.getForward(0, Camera.rotation.y + yaw).x * FPSCounter.getDelta();
			Camera.position.y+=speed * Camera.getForward(0, Camera.rotation.y + yaw).y * FPSCounter.getDelta();
			Camera.position.z+=speed * Camera.getForward(0, Camera.rotation.y + yaw).z * FPSCounter.getDelta();
		}
		
		if (Settings.LEFT.isPressed()) {
			float yaw = -90;
			Camera.position.x+=speed * Camera.getForward(0, Camera.rotation.y + yaw).x * FPSCounter.getDelta();
			Camera.position.y+=speed * Camera.getForward(0, Camera.rotation.y + yaw).y * FPSCounter.getDelta();
			Camera.position.z+=speed * Camera.getForward(0, Camera.rotation.y + yaw).z * FPSCounter.getDelta();
		}

		if (Settings.FORWARD.isPressed()) {
			float yaw = 0;
			float pitch = 0;
			Camera.position.x+=speed * Camera.getForward(0, Camera.rotation.y + yaw).x * FPSCounter.getDelta();
			Camera.position.z+=speed * Camera.getForward(0, Camera.rotation.y + yaw).z * FPSCounter.getDelta();
		}

		if (Settings.BACKWARD.isPressed()) {
			float yaw = 180;
			float pitch = 0;
			Camera.position.x+=speed * Camera.getForward(0, Camera.rotation.y + yaw).x * FPSCounter.getDelta();
			Camera.position.z+=speed * Camera.getForward(0, Camera.rotation.y + yaw).z * FPSCounter.getDelta();
		}

		if (Settings.JUMP.isPressed()) {
			Camera.position.y+=speed * FPSCounter.getDelta();
		}

		if (Settings.SNEAK.isPressed()) {
			Camera.position.y-=speed * FPSCounter.getDelta();
		}


	}
	
	private static void doGameInput() {
		
		if (Mouse.locked) {
			Camera.rotation.y += (Mouse.x - Mouse.lastX) * Settings.MOUSE_SENSITIVITY;
			Camera.rotation.x += (Mouse.y - Mouse.lastY) * Settings.MOUSE_SENSITIVITY;
			
			if (Camera.rotation.x < -90) Camera.rotation.x = -90;
			if (Camera.rotation.x > 90) Camera.rotation.x = 90;
		}
		
		if (Settings.RAYTRACING) {
//			updateCamera();
		}
		
		if (Settings.ATTACK.isPressed()) {
			PixelInventor game = PixelInventor.game;
			if (Camera.currentTile != null) {
				if (Camera.currentTile.getType() == RayTraceType.TILE) {
					if (PixelInventor.game.player != null)
						if (Settings.ATTACK.isJustPressed()) {
							game.world.mineTile(Camera.currentTile.getPosition(), 5.0f);
						} else {
							if (PixelInventor.game.player.getUseTime() == 0)
							{
								game.world.mineTile(Camera.currentTile.getPosition(), 5.0f);
							}
						}
				}
			}
		}
		
		if (Settings.USE.isPressed()) {
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
							if (new Rectangle(pos.x, pos.z, 1, 1).intersects(e.position.x-e.size.x/2.0, e.position.z-e.size.z/2.0, e.size.x/2.0, e.size.z/2.0))
								if (pos.y == e.getTilePos().y || pos.y == e.getTilePos().y + 1) {
										stop = true;
										break;
									}
						}
						
					if (game.world.getTile(pos).isReplaceable())
					if (Settings.USE.isJustPressed()) {
						if (!stop) {
							game.world.setTile(pos, Tiles.STONE);
							PixelInventor.game.player.setUseTime(5);
						}
					} else {
						if (PixelInventor.game.player.getUseTime() == 0)
						{
							if (!stop) {
								game.world.setTile(pos, Tiles.STONE);
								PixelInventor.game.player.setUseTime(5);
							}
						}
					}
				}
					
				}
			}
		}
	}
}
