package kmerrill285.Inignoto.events;

import kmerrill285.Inignoto.Inignoto;
import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.Mouse;
import kmerrill285.Inignoto.game.client.rendering.gui.GuiRenderer;
import kmerrill285.Inignoto.game.client.rendering.gui.IngameMenuScreen;
import kmerrill285.Inignoto.game.client.rendering.gui.InventoryScreen;
import kmerrill285.Inignoto.game.client.rendering.gui.MenuScreen;
import kmerrill285.Inignoto.game.inventory.InventoryItemStack;
import kmerrill285.Inignoto.game.inventory.PlayerInventory;
import kmerrill285.Inignoto.game.settings.Settings;
import kmerrill285.Inignoto.resources.FPSCounter;
import kmerrill285.Inignoto.resources.RayTraceResult.RayTraceType;
import kmerrill285.Inignoto.resources.TPSCounter;

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
			Camera.position.x+=speed * Camera.getForward(0, Camera.rotation.y + yaw).x * FPSCounter.getDelta();
			Camera.position.z+=speed * Camera.getForward(0, Camera.rotation.y + yaw).z * FPSCounter.getDelta();
		}

		if (Settings.BACKWARD.isPressed()) {
			float yaw = 180;
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
			if (TPSCounter.getDelta() < 2.0f) {
				Camera.rotation.y += (Mouse.x - Mouse.lastX) * Settings.MOUSE_SENSITIVITY * TPSCounter.getDelta() * 4;
				Camera.rotation.x += (Mouse.y - Mouse.lastY) * Settings.MOUSE_SENSITIVITY * TPSCounter.getDelta() * 4;
			} else {
				Camera.rotation.y += (Mouse.x - Mouse.lastX) * Settings.MOUSE_SENSITIVITY * 6;
				Camera.rotation.x += (Mouse.y - Mouse.lastY) * Settings.MOUSE_SENSITIVITY * 6;
			}
			
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
							if (Inignoto.game.player.arm_swing == 0) {
								Inignoto.game.player.arm_swing = 1.0f;
								game.world.mineTile(Camera.currentTile.getPosition(), 10.0f);
							}
						}
				}
			}
		}
		
		
		if (Settings.USE.isPressed()) {
			boolean use = false;
			
			int use_time = 5;
			
			if (Settings.USE.isJustPressed()) {
				use = true;
				Inignoto.game.player.setUseTime(use_time);
			} else {
				if (Inignoto.game.player.getUseTime() == 0)
				{
					use = true;
					Inignoto.game.player.setUseTime(use_time);
				}
			}
			
			if (use) {
				PlayerInventory inventory = Inignoto.game.player.inventory;
				if (inventory != null) {
					InventoryItemStack stack = inventory.hotbar[inventory.hotbarSelected].stack;
					if (stack != null) {
						stack.item.rightClick(Inignoto.game.world, Inignoto.game.player, Camera.currentTile);
						if (Inignoto.game.player.arm_swing == 0) {
							Inignoto.game.player.arm_swing = 1.0f;
						}
					}
				}
			}
			
		}
		

		if (Settings.HOTBAR_1.isPressed()) {
			if (Inignoto.game.player.inventory != null) {
				Inignoto.game.player.inventory.hotbarSelected = 0;
			}
		}
		if (Settings.HOTBAR_2.isPressed()) {
			if (Inignoto.game.player.inventory != null) {
				Inignoto.game.player.inventory.hotbarSelected = 1;
			}
		}
		if (Settings.HOTBAR_3.isPressed()) {
			if (Inignoto.game.player.inventory != null) {
				Inignoto.game.player.inventory.hotbarSelected = 2;
			}
		}
		if (Settings.HOTBAR_4.isPressed()) {
			if (Inignoto.game.player.inventory != null) {
				Inignoto.game.player.inventory.hotbarSelected = 3;
			}
		}
		if (Settings.HOTBAR_5.isPressed()) {
			if (Inignoto.game.player.inventory != null) {
				Inignoto.game.player.inventory.hotbarSelected = 4;
			}
		}
		if (Settings.HOTBAR_6.isPressed()) {
			if (Inignoto.game.player.inventory != null) {
				Inignoto.game.player.inventory.hotbarSelected = 5;
			}
		}
		if (Settings.HOTBAR_7.isPressed()) {
			if (Inignoto.game.player.inventory != null) {
				Inignoto.game.player.inventory.hotbarSelected = 6;
			}
		}
		if (Settings.HOTBAR_8.isPressed()) {
			if (Inignoto.game.player.inventory != null) {
				Inignoto.game.player.inventory.hotbarSelected = 7;
			}
		}
		if (Settings.HOTBAR_9.isPressed()) {
			if (Inignoto.game.player.inventory != null) {
				Inignoto.game.player.inventory.hotbarSelected = 8;
			}
		}
		if (Settings.HOTBAR_10.isPressed()) {
			if (Inignoto.game.player.inventory != null) {
				Inignoto.game.player.inventory.hotbarSelected = 9;
			}
		}
		
	}
}
