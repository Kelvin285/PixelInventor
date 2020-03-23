package kmerrill285.PixelInventor.game.client.rendering.raytracing;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glMemoryBarrier;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

import imported.RayCamera;
import kmerrill285.PixelInventor.PixelInventor;
import kmerrill285.PixelInventor.game.client.rendering.effects.lights.DirectionalLight;
import kmerrill285.PixelInventor.game.client.rendering.textures.Textures;
import kmerrill285.PixelInventor.game.settings.Settings;
import kmerrill285.PixelInventor.resources.MathHelper;


public class RayTracer {

	private long window;
	private int width = 1024;
	private int height = 768;

	private int tex;

	private RayCamera camera;
	
	public RayShader shader;

	private final Vector3f eyeRay = new Vector3f();
	
	public void init() throws IOException {
		try {
			shader = new RayShader();
		}catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		tex = createFramebufferTexture();
		

		camera = new RayCamera();
		getCamera().setFrustumPerspective(60.0f, (float) width / height, 1f, 2f);
		getCamera().setLookAt(new Vector3f(3.0f, 2.0f, 7.0f), new Vector3f(0.0f, 0.5f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));
	}
	
	

	/**
	 * Create the texture that will serve as our framebuffer.
	 * 
	 * @return the texture id
	 */
	private int createFramebufferTexture() {
		int tex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, tex);
		ByteBuffer black = null;
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, width, height, 0, GL_RGBA, GL_FLOAT, black);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		glBindTexture(GL_TEXTURE_2D, 0);
		return tex;
	}

	private RayTraceWorld world = new RayTraceWorld();
	
	/**
	 * Compute one frame by tracing the scene using our compute shader and
	 * presenting that image on the screen.
	 */
	public void render() {
		shader.bind();
		
		org.joml.Vector3f skyColor = 	PixelInventor.game.world.getSkyColor();
		Vector3f sc = new Vector3f(skyColor.x, skyColor.y, skyColor.z);
		
		PixelInventor.game.world.updateLight();
		DirectionalLight sun = PixelInventor.game.world.light;
		
		getCamera().setFrustumPerspective(Settings.ACTUAL_FOV, (float) width / height, 1f, 2f);
		
		/* Set viewing frustum corner rays in shader */
		shader.setVec3("eye", getCamera().getPosition());
		getCamera().getEyeRay(-1, -1, eyeRay);
		shader.setVec3("ray00", eyeRay);
		getCamera().getEyeRay(-1, 1, eyeRay);
		shader.setVec3("ray01", eyeRay);
		getCamera().getEyeRay(1, -1, eyeRay);
		shader.setVec3("ray10", eyeRay);
		getCamera().getEyeRay(1, 1, eyeRay);
		shader.setVec3("ray11", eyeRay);
		shader.setVec3("skyColor", sc);
		shader.setVec3("sunColor", sun.getColor().x, sun.getColor().y, sun.getColor().z);
		shader.setVec3("sunPosition", sun.getPosition().x, sun.getPosition().y, sun.getPosition().z);
		shader.setInt("shadows", Settings.SHADOWS ? 1 : 0);
		shader.setInt("reflections", Settings.REFLECTIONS ? 1 : 0);
		/* Bind level 0 of framebuffer texture as writable image in the shader. */
		glBindImageTexture(0, tex, 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		
		getWorld().build(shader);
		
		GL30.glActiveTexture(GL30.GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, Textures.TILES.textureID);
		
		/* Compute appropriate invocation dimension. */
		int worksizeX = MathHelper.nextPowerOfTwo(width);
		int worksizeY = MathHelper.nextPowerOfTwo(height);
		
		/* Invoke the compute shader. */
		glDispatchCompute(worksizeX / shader.workGroupSizeX, worksizeY / shader.workGroupSizeY, 1);
		
		/* Reset image binding. */
		glBindImageTexture(0, 0, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
		
		glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
		shader.unbind();
	}

	public int getTexture() {
		return this.tex;
	}

	public RayCamera getCamera() {
		return camera;
	}
	
	public void dispose() {
		GL11.glDeleteTextures(tex);
		shader.dispose();
		getWorld().dispose();
	}



	public RayTraceWorld getWorld() {
		return world;
	}

}