package kmerrill285.PixelInventor.game.client.rendering.gui;

import org.joml.Vector2f;
import org.joml.Vector4f;

import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.client.rendering.textures.Texture;
import kmerrill285.PixelInventor.game.client.rendering.textures.Textures;
import kmerrill285.PixelInventor.resources.RayTraceResult;
import kmerrill285.PixelInventor.resources.RayTraceResult.RayTraceType;

public class GuiRenderer {
	private static Mesh sprite;
	
	private static ShaderProgram shader;
	
	public GuiRenderer(ShaderProgram shader) {
		this.shader = shader;
		float[] vertices = new float[] {
			0.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			1.0f, 1.0f, 0.0f,
			1.0f, 0.0f, 0.0f
		};
		float[] texCoords = new float[] {
			0.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 1.0f,
			1.0f, 0.0f,
		};
		int[] indices = new int[] {
			0, 1, 2, 2, 3, 0
		};
		sprite = new Mesh(vertices, texCoords, indices, Textures.TILE_SELECTION);
	}
	
	public void render() {
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
		
		if (hover == false) {
			drawTexture(Textures.FP_CURSOR, 1920 / 2 - 25, 1080 / 2 - 25, 50, 50, 0, new Vector4f(0.5f, 0.5f, 0.5f, 1));
		} else {
			drawTexture(Textures.FP_CURSOR, 1920 / 2 - 25, 1080 / 2 - 25, 50, 50, 0, new Vector4f(1, 1, 1, 1));
		}
		
		drawTexture(Textures.VIGINETTE, -1920*(1.0f/4.0f), -1080*(1.0f/4.0f), 1920 * 1.5f, 1080 * 1.5f, 0, new Vector4f(1, 1, 1, 1));
	}
	
	public void drawTexture(Texture texture, float x, float y, float width, float height, float rotation, Vector4f color) {
		
		sprite.texture = texture;
				
        shader.setUniformVec4("color", color);
		shader.setUniformInt("texture_sampler", 0);
        shader.setUniformVec2("offset", new Vector2f(x, y));
        shader.setUniformVec2("scale", new Vector2f(width, height));
        shader.setUniformFloat("rotation", rotation);
        sprite.render();
		
	}
    
   
}
