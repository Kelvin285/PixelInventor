package kmerrill285.Inignoto.events;

import java.awt.Rectangle;

import kmerrill285.Inignoto.Inignoto;
import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.Mouse;
import kmerrill285.Inignoto.game.client.rendering.gui.GuiRenderer;
import kmerrill285.Inignoto.game.client.rendering.gui.IngameMenuScreen;
import kmerrill285.Inignoto.game.client.rendering.gui.InventoryScreen;
import kmerrill285.Inignoto.game.client.rendering.gui.MenuScreen;
import kmerrill285.Inignoto.game.entity.Entity;
import kmerrill285.Inignoto.game.entity.player.PlayerEntity;
import kmerrill285.Inignoto.game.settings.Settings;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.chunk.TilePos;
import kmerrill285.Inignoto.resources.FPSCounter;
import kmerrill285.Inignoto.resources.RayTraceResult.Direction;
import kmerrill285.Inignoto.resources.RayTraceResult.RayTraceType;

public class Input {
	
	public static void doInput() {
		GuiRenderer renderer = Inignoto.game.guiRenderer;
		if (renderer.getOpenScreen() == null) {
			doGameInput();
		}
		
		if (!(GuiRenderer.currentScreen instanceof MenuScreen))
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
		if (!(GuiRenderer.currentScreen instanceof MenuScreen))
		if (Settings.EXIT.isJustPressed()) {
			if (renderer != null) {
				if (renderer.getOpenScreen() != null) {
					renderer.closeScreen();
				} else {
					renderer.openScreen(new IngameMenuScreen(renderer));
				}
			}
		}
		
		
	}
	
	private static void updateCamera() {
		if (FPSCounter.getDelta() <= 0) return;
		float speed = 0.5f;
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
		updateCamera();
		if (Mouse.locked) {
			Camera.rotation.y += (Mouse.x - Mouse.lastX) * Settings.MOUSE_SENSITIVITY * FPSCounter.getDelta();
			Camera.rotation.x += (Mouse.y - Mouse.lastY) * Settings.MOUSE_SENSITIVITY * FPSCounter.getDelta();
			
			if (Camera.rotation.x < -90) Camera.rotation.x = -90;
			if (Camera.rotation.x > 90) Camera.rotation.x = 90;
		}
		
		if (Settings.ATTACK.isPressed()) {
			Inignoto game = Inignoto.game;
			if (Camera.currentTile != null) {
				if (Camera.currentTile.getType() == RayTraceType.TILE) {
					if (Inignoto.game.player != null)
						if (Settings.ATTACK.isJustPressed()) {
							game.world.mineTile(Camera.currentTile.getPosition(), 5.0f);
						} else {
							if (Inignoto.game.player.getUseTime() == 0)
							{
								game.world.mineTile(Camera.currentTile.getPosition(), 5.0f);
							}
						}
				}
			}
		}
		
		if (Settings.USE.isPressed()) {
			Inignoto game = Inignoto.game;
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
							if (new Rectangle(pos.x , pos.z, 1, 1).intersects((e.position.x), (e.position.z), (e.size.x), e.size.z))
								if (!(e instanceof PlayerEntity)) {
									if (pos.y == e.getTilePos().y || pos.y == e.getTilePos().y + 1) {
										stop = true;
										break;
									}
								} else {
									if (((PlayerEntity)e).isCrawling()) {
										if (pos.y == e.getTilePos().y) {
											stop = true;
											break;
										}
									} else {
										if (pos.y == e.getTilePos().y || pos.y == e.getTilePos().y + 1) {
											stop = true;
											break;
										}
									}
								}
						}
						
					if (game.world.getTile(pos).isReplaceable())
					if (Settings.USE.isJustPressed()) {
						if (!stop) {
							game.world.setTile(pos, Tiles.STONE);
							Inignoto.game.player.setUseTime(5);
						}
					} else {
						if (Inignoto.game.player.getUseTime() == 0)
						{
							if (!stop) {
								game.world.setTile(pos, Tiles.STONE);
								Inignoto.game.player.setUseTime(5);
							}
						}
					}
				}
					
				}
			}
		}
	}
}
