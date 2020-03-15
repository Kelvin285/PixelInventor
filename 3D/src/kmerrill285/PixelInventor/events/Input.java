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

	private static void doGameInput() {
		
		if (Mouse.locked) {
			Camera.rotation.y += (Mouse.x - Mouse.lastX) * Settings.MOUSE_SENSITIVITY;
			Camera.rotation.x += (Mouse.y - Mouse.lastY) * Settings.MOUSE_SENSITIVITY;
			
			if (Camera.rotation.x < -90) Camera.rotation.x = -90;
			if (Camera.rotation.x > 90) Camera.rotation.x = 90;
		}
		
		
		if (Settings.ATTACK.isPressed()) {
			PixelInventor game = PixelInventor.game;
			if (Camera.currentTile != null) {
				if (Camera.currentTile.getType() == RayTraceType.TILE) {
					if (PixelInventor.game.player != null)
						if (Settings.ATTACK.isJustPressed()) {
							game.world.setTile(Camera.currentTile.getPosition(), Tiles.AIR);
							PixelInventor.game.player.setUseTime(5);
						} else {
							if (PixelInventor.game.player.getUseTime() == 0)
							{
								game.world.setTile(Camera.currentTile.getPosition(), Tiles.AIR);
								PixelInventor.game.player.setUseTime(5);
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
						
						
					if (Settings.USE.isJustPressed()) {
						if (!stop) {
							game.world.setTile(pos, Tiles.GRASS);
							PixelInventor.game.player.setUseTime(5);
						}
					} else {
						if (PixelInventor.game.player.getUseTime() == 0)
						{
							if (!stop) {
								game.world.setTile(pos, Tiles.GRASS);
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
