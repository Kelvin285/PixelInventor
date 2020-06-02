package kmerrill285.Inignoto.game.client.rendering.gui;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import kmerrill285.Inignoto.game.client.Mouse;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.textures.Textures;
import kmerrill285.Inignoto.game.settings.Settings;
import kmerrill285.Inignoto.game.settings.Translation;
import kmerrill285.Inignoto.resources.Utils;

public class MenuScreen extends GuiScreen {

	double frameCounter = 0;
	
	float moon_x = 1500;
	float moon_y = 600;
	
	float last_moon_x = 1500;
	float last_moon_y = 600;
	
	float moon_mx = 0;
	float moon_my = 0;
	
	boolean grabbed = false;
	
	public MenuScreen(GuiRenderer renderer) {
		super(renderer);
	}

	@Override
	public void tick() {
		frameCounter = (System.nanoTime() / 100000000) * 0.25f;
	}

	@Override
	public void render(ShaderProgram shader) {
		int frame = (int)frameCounter % 4;
		
		GL11.glEnable(GL11.GL_BLEND);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		shader.setUniformFloat("zv", -0.3f);
		this.renderer.drawString(Translation.translateText("Inignoto:gui.singleplayer"), 1920 / 2 - 15 * Translation.translateText("Inignoto:gui.singleplayer").length() - 450 + 25, 410, 2, new Vector4f(0, 0, 0, 1), true);
		this.renderer.drawString(Translation.translateText("Inignoto:gui.multiplayer"), 1920 / 2 - 15 * Translation.translateText("Inignoto:gui.multiplayer").length() + 25, 410, 2, new Vector4f(0, 0, 0, 1), true);
		this.renderer.drawString(Translation.translateText("Inignoto:gui.settings"), 1920 / 2 - 15 * Translation.translateText("Inignoto:gui.settings").length() + 450, 410, 2, new Vector4f(0, 0, 0, 1), true);
		this.renderer.drawString(Translation.translateText("Inignoto:gui.exit"), 1920 / 2 - 15 * Translation.translateText("Inignoto:gui.exit").length() + 25, 410 - (30 + 15) * 2, 2, new Vector4f(0, 0, 0, 1), true);

		
		shader.setUniformFloat("zv", 0.2f);
		this.renderer.drawTexture(Textures.TITLE_BACKGROUND[frame], 1920, 0, -1920, 1080, 0, new Vector4f(1, 1, 1, 1));

		shader.setUniformFloat("zv", 0);

		
		
		float yp = (float)Math.cos(Math.toRadians((System.nanoTime() / 10000000))) * 1.5f;
		this.renderer.drawTexture(Textures.INIGNOTO, 1920 + 25, yp, -1920, 1080, 0, new Vector4f(1, 1, 1, 1));
		
		
		
		//352, 340
		shader.setUniformFloat("zv", -0.1f);
		if (moon_y > 204)
		this.renderer.drawTexture(Textures.MOON, moon_x, moon_y, 352, 340, 0, new Vector4f(1, 1, 1, 1));
		shader.setUniformFloat("zv", -0.2f);
		this.renderer.drawTexture(Textures.MOON_CLIP, 1920, 0, -1920, 1080, 0, new Vector4f(1, 1, 1, 1));

		double mx = Mouse.x * (1920.0 / Utils.FRAME_WIDTH);
		double my = 1080 - Mouse.y * (1080.0 / Utils.FRAME_HEIGHT);

		boolean hovered = false;
		if (mx > 1920 - 48 - 64 && mx < 1920 - 48 + 64 - 64) {
			if (my > 32 && my < 32 + 64) {
				hovered = true;
			}
		}
		if (!hovered) {
			this.renderer.drawTexture(Textures.MODELER, 1920 - 48, 32, -64, 64, 0, new Vector4f(1, 1, 1, 1));
		} else {
			this.renderer.drawString(Translation.translateText("Inignoto:gui.model_creator"), (float)mx - Translation.translateText("Inignoto:gui.model_creator").length() * 15 * 2, (float)my, 2, new Vector4f(1, 1, 1, 1), true);

			this.renderer.drawTexture(Textures.MODELER, 1920 - 48, 32, -64, 64, 0, new Vector4f(0, 1, 0, 1));
			if (Settings.isMouseButtonJustDown(0)) {
				this.renderer.openScreen(new ModelerScreen(renderer));
			}
		}
		double dist = Math.sqrt(Math.pow(moon_x + 352 / 2 - mx, 2) + Math.pow(moon_y + 340 / 2 - my - 10, 2));
		if (Settings.isMouseButtonDown(0)) {
			if (dist <= 70) {
				if (grabbed == false) {
					grabbed = true;
				}
			}
		} else {
			grabbed = false;
		}
		
		if (grabbed) {
			moon_x = (float)mx - 352 / 2;
			moon_y = (float)my - 340 / 2;
		}
		
		double sy = Math.sin((mx * 3.14) / 1920.0) * 162 + 418;		

		if (!grabbed) {
			moon_x += moon_mx;
			moon_y += moon_my;
		} else {
			moon_mx = moon_x - last_moon_x;
			moon_my = moon_y - last_moon_y;
		}
		
		if (moon_y + 340 / 2 < sy) {
			moon_y = (float)sy - 340 / 2;
			moon_my *= -1;
		}
		
		if (moon_y + 340 / 2 > 1080) {
			moon_y = 1080 - 340 / 2;
			moon_my *= -1;
		}
		
		if (moon_x + 340 / 2 < 0) {
			moon_x = -340 / 2;
			moon_mx *= -1;
		}
		
		if (moon_x + 340 / 2 > 1920) {
			moon_x = 1920 - 340 / 2;
			moon_mx *= -1;
		}
		
		
		moon_mx *= 0.99f;
		moon_my *= 0.99f;
		
		last_moon_x = moon_x;
		last_moon_y = moon_y;
		
		shader.setUniformFloat("zv", 0);

	}

	@Override
	public void close() {
		
	}

}
