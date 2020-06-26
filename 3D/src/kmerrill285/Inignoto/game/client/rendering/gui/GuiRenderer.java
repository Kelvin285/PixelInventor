package kmerrill285.Inignoto.game.client.rendering.gui;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL15;

import kmerrill285.Inignoto.Inignoto;
import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.Mouse;
import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.client.rendering.postprocessing.FrameBuffer;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.textures.Fonts;
import kmerrill285.Inignoto.game.client.rendering.textures.Texture;
import kmerrill285.Inignoto.game.client.rendering.textures.TextureAtlas;
import kmerrill285.Inignoto.game.client.rendering.textures.Textures;
import kmerrill285.Inignoto.game.settings.Settings;
import kmerrill285.Inignoto.resources.RayTraceResult;
import kmerrill285.Inignoto.resources.RayTraceResult.RayTraceType;
import kmerrill285.Inignoto.resources.Utils;

public class GuiRenderer {
	private static Mesh sprite;
	
	private static ShaderProgram shader;
	
	public static GuiScreen currentScreen;
	
	public GuiRenderer(ShaderProgram shader) {
		GuiRenderer.shader = shader;
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
	
	public static Texture getScreenshot() throws Exception {
		int[] xpos = new int[2], ypos = new int[2];
		int[] w = new int[2], h = new int[2];
		GLFW.glfwGetWindowPos(Utils.window, xpos, ypos);
		GLFW.glfwGetWindowSize(Utils.window, w, h);
//	    System.out.println("Saving screenshot!");
//	    
//	    
//	    ImageIO.write(capture, "png", new File("doc/saved/screenshot.png"));
		int x = xpos[0];
		int y = ypos[0];
		int width = w[0];
		int height = h[0];
		Rectangle screenRect = new Rectangle(x, y, width, height);
		BufferedImage capture = new Robot().createScreenCapture(screenRect);
		TextureAtlas atlas = new TextureAtlas(capture);
		return atlas.texture;
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
		
		if (!(currentScreen instanceof MenuScreen)) {

			drawTexture(Textures.HOTBAR, 1920 / 2 - (382 * 3) / 2, 0, 382 * 3, 35 * 3, 0, new Vector4f(1, 1, 1, 1));
			
			for (int i = 0; i < 10; i++) {
				drawTexture(Textures.HEALTH_ICON, 7 * 5 + 1920 - 10 - 77 * 5 + 3 * 5 + i * 7 * 5, 10 + 3 * 5, -7 * 5, 7 * 5, 0, new Vector4f(52.0f / 255.0f, 224.0f / 255.0f, 81.0f / 255.0f, 1));
			}
			
			drawTexture(Textures.HEALTHBAR, 1920 - 10, 10, -77 * 5, 13 * 5, 0, new Vector4f(1, 1, 1, 1));
			
		}
		
		
		
		if (currentScreen != null) {
			currentScreen.tick();
			currentScreen.render(shader);
		} else {
			if (Inignoto.game.player == null ||
					Inignoto.game.player != null && Inignoto.game.player.ZOOM == 0)
			if (hover == false) {
				drawTexture(Textures.FP_CURSOR, 1920 / 2 - 25/2, 1080 / 2 - 25/2, 25, 25, 0, new Vector4f(0.5f, 0.5f, 0.5f, 1));
			} else {
				drawTexture(Textures.FP_CURSOR, 1920 / 2 - 25/2, 1080 / 2 - 25/2, 25, 25, 0, new Vector4f(1, 1, 1, 1));
			}
		}
		
		
		if (!(currentScreen instanceof MenuScreen)) {
			if (Settings.POST_PROCESSING) {
				drawTexture(Inignoto.game.framebuffer, Inignoto.game.blurbuffer, 1920, 1080, -1920, -1080, 0, new Vector4f(1, 1, 1, 1), true);
			}
			
	
			drawTexture(Textures.VIGINETTE, 0, 0, 1920, 1080, 0, new Vector4f(1, 1, 1, 1));
			
			
			if (Inignoto.game.player != null) {
				if (Inignoto.game.player.headInGround) {
					drawTexture(Textures.WHITE_SQUARE, 0, 0, 1920, 1080, 0, new Vector4f(0, 0, 0, 1));
				}
			}
		}
	}
	


	public void renderBlur() {
		if (Settings.POST_PROCESSING) {
			drawTexture(Inignoto.game.framebuffer, null, 1920, 1080, -1920, -1080, 0, new Vector4f(1, 1, 1, 1), true);
		}
	}
	
	public GuiScreen getOpenScreen() {
		return currentScreen;
	}
	
	public void openScreen(GuiScreen screen) {
		if (currentScreen != null) {
			currentScreen.close();
		}
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
	
	
	public void drawNormalTexture(Texture texture, float x, float y, float width, float height, float rotation, Vector4f color) {
		drawTexture(texture, x + width, y, -width, height, rotation, color, false);
	}
	
	
	public void drawTexture(Texture texture, float x, float y, float width, float height, float rotation, Vector4f color) {
		drawTexture(texture, x, y, width, height, rotation, color, false);
	}
	
	public void drawNormalTexture(Texture texture, float x, float y, float width, float height, float rotation, Vector4f color, boolean postProcessing) {
		drawTexture(texture, x + width, y, -width, height, rotation, color, postProcessing, false);
	}
	
	public void drawTexture(Texture texture, float x, float y, float width, float height, float rotation, Vector4f color, boolean postProcessing) {
		drawTexture(texture, x, y, width, height, rotation, color, postProcessing, false);
	}
	
	public void drawNormalTexture(FrameBuffer texture, FrameBuffer blur, float x, float y, float width, float height, float rotation, Vector4f color) {
		drawTexture(texture, blur, x + width, y, -width, height, rotation, color, false);
	}
	
	public void drawTexture(FrameBuffer texture, FrameBuffer blur, float x, float y, float width, float height, float rotation, Vector4f color) {
		drawTexture(texture, blur, x, y, width, height, rotation, color, false);
	}
	
	public void drawNormalTexture(FrameBuffer texture, FrameBuffer blur, float x, float y, float width, float height, float rotation, Vector4f color, boolean postProcessing) {
		drawTexture(texture, blur, x + width, y, -width, height, rotation, color, postProcessing, false);
	}
	
	public void drawTexture(FrameBuffer texture, FrameBuffer blur, float x, float y, float width, float height, float rotation, Vector4f color, boolean postProcessing) {
		drawTexture(texture, blur, x, y, width, height, rotation, color, postProcessing, false);
	}
	
	public void drawTexture(Texture texture, float x, float y, float width, float height, float rotation, Vector4f color, boolean postProcessing, boolean raycasting) {
		
		sprite.texture = texture;
		shader.setUniformInt("raycasting", raycasting ? 1 : 0);
		shader.setUniformInt("post_processing", postProcessing ? 1 : 0);
        shader.setUniformVec4("color", color);
		shader.setUniformInt("texture_sampler", 0);
        shader.setUniformVec2("offset", new Vector2f(x, y));
        shader.setUniformVec2("scale", new Vector2f(width, height));
        shader.setUniformFloat("exposure", Settings.EXPOSURE);
        sprite.render();
		
	}
	
	public void drawTexture(FrameBuffer texture, FrameBuffer blur, float x, float y, float width, float height, float rotation, Vector4f color, boolean postProcessing, boolean raycasting) {
		
		sprite.texture = texture.getTexture();
		shader.setUniformInt("raycasting", raycasting ? 1 : 0);
		shader.setUniformInt("post_processing", postProcessing ? 1 : 0);
        shader.setUniformVec4("color", color);
		shader.setUniformInt("texture_sampler", 0);
        shader.setUniformVec2("offset", new Vector2f(x, y));
        shader.setUniformVec2("scale", new Vector2f(width, height));
        shader.setUniformFloat("exposure", Settings.EXPOSURE);
        glActiveTexture(GL15.GL_TEXTURE1);
    	glBindTexture(GL_TEXTURE_2D, texture.getDepthMapTexture().getTextureId());
		shader.setUniformInt("depth_texture", 1);
		if (blur != null) {
			glActiveTexture(GL15.GL_TEXTURE2);
			glBindTexture(GL_TEXTURE_2D, blur.getTexture().getTextureId());
			shader.setUniformInt("blur_texture", 2);
			shader.setUniformInt("distance_blur", Settings.DISTANCE_BLUR ? 1 : 0);
		}
		shader.setUniformVec3("fogColor", Inignoto.game.world.getFog().color);
		shader.setUniformFloat("fogDensity", Inignoto.game.world.getFog().density);
        sprite.render();
		
	}
	
	public void drawString (String str, float x, float y, float scale, Vector4f color, boolean bold) {
		for (int i = 0; i < str.length(); i++) {
			char c = str.toCharArray()[i];
			if (!bold) {
				Texture texture = Fonts.chars.get(c);
				drawTexture(texture, x + i * 15 * scale, y, -15 * scale, 30 * scale, 0, color);
			} else {
				Texture texture = Fonts.bold.get(c);
				drawTexture(texture, x + i * 15 * scale, y, -15 * scale, 30 * scale, 0, color);
			}
			
		}
	}
    
   
}
