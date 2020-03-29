package kmerrill285.PixelInventor.resources;

import java.io.File;
import java.util.Random;
import java.util.Scanner;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import kmerrill285.PixelInventor.PixelInventor;
import kmerrill285.PixelInventor.events.Events;
import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.rendering.effects.shadows.ShadowMap;
import kmerrill285.PixelInventor.game.client.rendering.gui.GuiRenderer;
import kmerrill285.PixelInventor.game.client.rendering.postprocessing.FrameBuffer;
import kmerrill285.PixelInventor.game.client.rendering.raytracing.RayTracer;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.client.rendering.textures.Textures;
import kmerrill285.PixelInventor.game.entity.StaticEntities;
import kmerrill285.PixelInventor.game.entity.player.ClientPlayerEntity;
import kmerrill285.PixelInventor.game.settings.Settings;
import kmerrill285.PixelInventor.game.settings.Translation;
import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.game.world.World;

public class Utils {
	public static long window;
	public static ShaderProgram sprite_shader;
	public static ShaderProgram object_shader;
	public static ShaderProgram depth_shader;
	
	public static final float Z_NEAR = 0.01f;
	public static final float Z_FAR = 1000.0f;
	
	public static Matrix4f projectionMatrix;
	
	public static int FRAME_WIDTH = 1920 / 2, FRAME_HEIGHT = 1080 / 2;
	private static int P_WIDTH = 1920 / 2, P_HEIGHT = 1080 / 2;
	public static boolean WINDOW_FOCUSED = false;
	
	
	public static String loadResource(File file) {
		String str = "";
		try {
			System.out.println(file + ", " + file.exists());
			Scanner scanner = new Scanner(file);
			while (scanner.hasNext()) {
				str += scanner.nextLine() + "\n";
			}
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return str;
	}
	
	public static String loadResource(String modid, String directory) {
		return loadResource(getAsset(modid, directory));
	}
	
	public static File getAsset(String modid, String directory) {
		return new File(System.getProperty("user.dir")+"/assets/" + modid + "/" + directory).getAbsoluteFile();
	}
	
	public static String getResourcePath(String modid, String directory) {
		return System.getProperty("user.dir")+"/assets/"+modid+"/"+directory;
	}
	
	public static void setupGL() throws Exception {
		
		
		sprite_shader = new ShaderProgram();
		sprite_shader.createVertexShader(loadResource("PixelInventor", "shaders/sprite_vertex.glsl"));
		sprite_shader.createFragmentShader(loadResource("PixelInventor", "shaders/sprite_fragment.glsl"));
		sprite_shader.link();
		sprite_shader.createUniform("offset");
		sprite_shader.createUniform("scale");
		sprite_shader.createUniform("color");
		sprite_shader.createUniform("texture_sampler");
		sprite_shader.createUniform("post_processing");
		sprite_shader.createUniform("raycasting");
		sprite_shader.createUniform("exposure");

		object_shader = new ShaderProgram();
		object_shader.createVertexShader(loadResource("PixelInventor", "shaders/vertex.glsl"));
		object_shader.createFragmentShader(loadResource("PixelInventor", "shaders/fragment.glsl"));
		object_shader.link();
		object_shader.createUniform("projectionMatrix");
		object_shader.createUniform("modelMatrix");
		object_shader.createUniform("texture_sampler");
		object_shader.createFogUniform("fog");
		object_shader.createUniform("shadowMap");
		object_shader.createUniform("secondShadowMap");
		object_shader.createFogUniform("shadowBlendFog");
		object_shader.createUniform("cameraPos");
		object_shader.createUniform("sunPos");
		object_shader.createUniform("sunDirection");
		object_shader.createUniform("sunColor");
		object_shader.createUniform("cascadedShadows");
		
		object_shader.createUniform("modelLightViewMatrix");
		object_shader.createUniform("orthoProjectionMatrix");
		object_shader.createUniform("secondOrthoMatrix");
		object_shader.createUniform("hasShadows");
		object_shader.createUniform("renderToDepth");
		
		depth_shader = new ShaderProgram();
		depth_shader.createVertexShader(loadResource("PixelInventor", "shaders/depth_vertex.glsl"));
		depth_shader.createFragmentShader(loadResource("PixelInventor", "shaders/depth_fragment.glsl"));
		depth_shader.link();
		depth_shader.createUniform("orthoProjectionMatrix");
		depth_shader.createUniform("modelMatrix");

		GLFW.glfwSetWindowSizeCallback(window, Events::windowSize);
		setupProjection();
		
		Tiles.loadTiles();
		Translation.loadTranslations("PixelInventor");
		
		StaticEntities.load();

		Textures.load();
		PixelInventor.game.guiRenderer = new GuiRenderer(sprite_shader);
		PixelInventor.game.shadowMap = new ShadowMap();
		PixelInventor.game.secondShadowMap = new ShadowMap();
		PixelInventor.game.framebuffer = new FrameBuffer();

		
		PixelInventor.game.world = new World("World", new Random().nextLong());
		PixelInventor.game.player = new ClientPlayerEntity(new Vector3f(0.5f, 30, 0.5f), PixelInventor.game.world);
		
		Settings.loadSettings();
		
		PixelInventor.game.raytracer = new RayTracer();
		PixelInventor.game.raytracer.init();
	}
	
	public static void setupProjection() {
		float aspectRatio = (float)P_WIDTH / (float)P_HEIGHT;
		projectionMatrix = new Matrix4f().perspective((float)Math.toRadians(Settings.ACTUAL_FOV), aspectRatio, Z_NEAR, Z_FAR);
		object_shader.setUniformMat4("projectionMatrix", projectionMatrix);
	}
	
	public static Matrix4f getProjection() {
		float aspectRatio = (float)P_WIDTH / (float)P_HEIGHT;
		return new Matrix4f().perspective((float)Math.toRadians(Settings.ACTUAL_FOV), aspectRatio, Z_NEAR, Z_FAR);
	}
	
}
