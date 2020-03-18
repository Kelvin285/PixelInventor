package kmerrill285.PixelInventor.game.client.rendering.gui;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import kmerrill285.PixelInventor.PixelInventor;
import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.Mouse;
import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.client.rendering.textures.Texture;
import kmerrill285.PixelInventor.game.client.rendering.textures.Textures;
import kmerrill285.PixelInventor.resources.RayTraceResult;
import kmerrill285.PixelInventor.resources.RayTraceResult.RayTraceType;
import kmerrill285.PixelInventor.resources.Utils;

public class GuiRenderer {
	private static Mesh sprite;
	
	private static ShaderProgram shader;
	
	public static GuiScreen currentScreen;
	
	public GuiRenderer(ShaderProgram shader) {
		this.shader = shader;
		float[] vertices = new float[] {
			0.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			1.0f, 1.0f, 0.0f,
			1.0f, 0.0f, 0.0f
		};
		float[] texCoords = new float[] {
			1.0f, 1.0f,
			1.0f, 0.0f,
			0.0f, 0.0f,
			0.0f, 1.0f,
		};
		int[] indices = new int[] {
			0, 1, 2, 2, 3, 0
		};
		sprite = new Mesh(vertices, texCoords, indices, Textures.TILE_SELECTION);
	}
	
	public void render() {
		if (currentScreen != null) {
			GLFW.glfwSetInputMode(Utils.window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
			Mouse.locked = false;
		}
		boolean hover = false;
		
		if (Camera.currentTile != null) {
			RayTraceResult result = Camera.currentTile;
			if (result.getType() == RayTraceType.TILE) {
				if (result.getDirection() != null) {
					hover = true;
				}
			} else {
				if (result.getType() == RayTraceType.ENTITY) {
					hover = true;
				}
			}
		}
		
		
		
		drawTexture(Textures.HOTBAR, 1920 / 2 - (382 * 3) / 2, 0, 382 * 3, 35 * 3, 0, new Vector4f(1, 1, 1, 1));
		
		if (currentScreen != null) {
			currentScreen.tick();
			currentScreen.render(shader);
		} else {
			if (hover == false) {
				drawTexture(Textures.FP_CURSOR, 1920 / 2 - 25/2, 1080 / 2 - 25/2, 25, 25, 0, new Vector4f(0.5f, 0.5f, 0.5f, 1));
			} else {
				drawTexture(Textures.FP_CURSOR, 1920 / 2 - 25/2, 1080 / 2 - 25/2, 25, 25, 0, new Vector4f(1, 1, 1, 1));
			}
		}
//		drawTexture(PixelInventor.game.shadowMap.getDepthMapTexture(), 1920, 1080, -1920, -1080, 0, new Vector4f(1, 1, 1, 1));
		drawTexture(Textures.VIGINETTE, 0, 0, 1920, 1080, 0, new Vector4f(1, 1, 1, 1));
		
		if (PixelInventor.game.player != null) {
			if (PixelInventor.game.player.headInGround) {
				drawTexture(Textures.WHITE_SQUARE, 0, 0, 1920, 1080, 0, new Vector4f(0, 0, 0, 1));
			}
		}
	}
	
	public GuiScreen getOpenScreen() {
		return currentScreen;
	}
	
	public void openScreen(GuiScreen screen) {
		currentScreen = screen;
	}
	
	public void closeScreen() {
		if (currentScreen != null) {
			currentScreen.close();
			currentScreen = null;
		}
		GLFW.glfwSetInputMode(Utils.window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		Mouse.locked = true;
	}
	
	public void drawTexture(Texture texture, float x, float y, float width, float height, float rotation, Vector4f color) {
		
		sprite.texture = texture;
				
        shader.setUniformVec4("color", color);
		shader.setUniformInt("texture_sampler", 0);
        shader.setUniformVec2("offset", new Vector2f(x, y));
        shader.setUniformVec2("scale", new Vector2f(width, height));
        sprite.render();
		
	}
    
   
}
