package kmerrill285.Inignoto.resources;

import java.io.File;
import java.util.Random;
import java.util.Scanner;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import kmerrill285.Inignoto.Inignoto;
import kmerrill285.Inignoto.events.Events;
import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.audio.SoundSource;
import kmerrill285.Inignoto.game.client.audio.Sounds;
import kmerrill285.Inignoto.game.client.rendering.gui.GuiRenderer;
import kmerrill285.Inignoto.game.client.rendering.postprocessing.FrameBuffer;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.shadows.ShadowRenderer;
import kmerrill285.Inignoto.game.client.rendering.textures.Fonts;
import kmerrill285.Inignoto.game.client.rendering.textures.Textures;
import kmerrill285.Inignoto.game.entity.player.ClientPlayerEntity;
import kmerrill285.Inignoto.game.foliage.Foliage;
import kmerrill285.Inignoto.game.settings.Settings;
import kmerrill285.Inignoto.game.settings.Translation;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.game.world.chunk.generator.feature.Structure;
import kmerrill285.Inignoto.item.Items;

public class Utils {
	public static long window;
	public static ShaderProgram sprite_shader;
	public static ShaderProgram object_shader;
	public static ShaderProgram depth_shader;
	public static ShaderProgram blur_shader;
	public static ShaderProgram shadow_shader;

	public static final float Z_NEAR = 0.01f;
	public static final float Z_FAR = 1000.0f;
	
	public static Matrix4f projectionMatrix;
	
	public static int FRAME_WIDTH = 1920 / 2, FRAME_HEIGHT = 1080 / 2;
	public static boolean WINDOW_FOCUSED = false;
	
	public static long NORMAL_CURSOR, HAND_CURSOR, HRESIZE_CURSOR, VRESIZE_CURSOR, TYPE_CURSOR;
	
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
		Sounds.initAL();
		
		NORMAL_CURSOR = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
		HAND_CURSOR = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);
		HRESIZE_CURSOR = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR);
		VRESIZE_CURSOR = GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR);
		TYPE_CURSOR = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR);
		
		sprite_shader = new ShaderProgram();
		sprite_shader.createVertexShader(loadResource("Inignoto", "shaders/sprite_vertex.glsl"));
		sprite_shader.createFragmentShader(loadResource("Inignoto", "shaders/sprite_fragment.glsl"));
		sprite_shader.link();
		sprite_shader.createUniform("offset");
		sprite_shader.createUniform("scale");
		sprite_shader.createUniform("color");
		sprite_shader.createUniform("texture_sampler");
		sprite_shader.createUniform("post_processing");
		sprite_shader.createUniform("raycasting");
		sprite_shader.createUniform("exposure");
		sprite_shader.createUniform("depth_texture");
		sprite_shader.createUniform("blur_texture");
		sprite_shader.createUniform("distance_blur");
		sprite_shader.createUniform("fogColor");
		sprite_shader.createUniform("fogDensity");
		sprite_shader.createUniform("zv");

		
		blur_shader = new ShaderProgram();
		blur_shader.createVertexShader(loadResource("Inignoto", "shaders/blur_vertex.glsl"));
		blur_shader.createFragmentShader(loadResource("Inignoto", "shaders/blur_fragment.glsl"));
		blur_shader.link();
		blur_shader.createUniform("offset");
		blur_shader.createUniform("scale");
		blur_shader.createUniform("color");
		blur_shader.createUniform("texture_sampler");
		blur_shader.createUniform("post_processing");
		blur_shader.createUniform("raycasting");
		blur_shader.createUniform("exposure");
		blur_shader.createUniform("depth_texture");
		blur_shader.createUniform("blur_texture");

		object_shader = new ShaderProgram();
		object_shader.createVertexShader(loadResource("Inignoto", "shaders/vertex.glsl"));
		object_shader.createFragmentShader(loadResource("Inignoto", "shaders/fragment.glsl"));
		object_shader.link();
		object_shader.createUniform("projectionMatrix");
		object_shader.createUniform("modelMatrix");
		object_shader.createUniform("texture_sampler");
		object_shader.createFogUniform("fog");
		object_shader.createUniform("shadowMap");
		object_shader.createUniform("shadowMap2");
		object_shader.createUniform("shadowMap3");
		object_shader.createUniform("shadowMap4");

		object_shader.createFogUniform("shadowBlendFog");
		object_shader.createUniform("cameraPos");
		object_shader.createUniform("sunPos");
		object_shader.createUniform("sunDirection");
		object_shader.createUniform("sunColor");
		object_shader.createUniform("cascadedShadows");
		object_shader.createUniform("voxelRender");

		
		
		object_shader.createUniform("modelLightViewMatrix");
		object_shader.createUniform("orthoProjectionMatrix");
		object_shader.createUniform("secondOrthoMatrix");
		object_shader.createUniform("thirdOrthoMatrix");
		object_shader.createUniform("fourthOrthoMatrix");

		object_shader.createUniform("hasShadows");
		object_shader.createUniform("renderToDepth");
		object_shader.createUniform("loadValue");
		
		depth_shader = new ShaderProgram();
		depth_shader.createVertexShader(loadResource("Inignoto", "shaders/depth_vertex.glsl"));
		depth_shader.createFragmentShader(loadResource("Inignoto", "shaders/depth_fragment.glsl"));
		depth_shader.link();
		depth_shader.createUniform("orthoProjectionMatrix");
		depth_shader.createUniform("modelMatrix");
		
		shadow_shader = new ShaderProgram();
		shadow_shader.createVertexShader(loadResource("Inignoto", "shaders/shadow_vertex.glsl"));
		shadow_shader.createFragmentShader(loadResource("Inignoto", "shaders/shadow_fragment.glsl"));
		shadow_shader.link();
		shadow_shader.createUniform("projMatrix1");
		shadow_shader.createUniform("projMatrix2");
		shadow_shader.createUniform("projMatrix3");
		shadow_shader.createUniform("projMatrix4");

		shadow_shader.createUniform("zAdd");
		shadow_shader.createUniform("cMul");

		shadow_shader.createUniform("mvMatrix");
		shadow_shader.createUniform("texture_sampler");
		shadow_shader.createUniform("loadValue");
		shadow_shader.createUniform("cameraPos");
		shadow_shader.createUniform("cascade");

		GLFW.glfwSetWindowSizeCallback(window, Events::windowSize);
		setupProjection(object_shader);
		Tiles.loadTiles();
		Items.loadItems();
		Translation.loadTranslations("Inignoto");
		
		Textures.load();
		Foliage.loadFoliage();

		Inignoto.game.guiRenderer = new GuiRenderer(sprite_shader);
		Inignoto.game.framebuffer = new FrameBuffer();
		Inignoto.game.blurbuffer = new FrameBuffer();
		Inignoto.game.shadowRenderer = new ShadowRenderer();
		
		Inignoto.game.world = new World("World", new Random().nextLong());
		Inignoto.game.player = new ClientPlayerEntity(new Vector3f(0.5f, Inignoto.game.world.getChunkGenerator().getBaseHeight(0,0, 0), 0.5f), Inignoto.game.world);
		
		Settings.loadSettings();
		
		Structure.structures = Structure.loadAll();
		
		Camera.soundSource = new SoundSource();
		
		Fonts.loadFonts();
		
//		Inignoto.game.guiRenderer.openScreen(new MenuScreen(Inignoto.game.guiRenderer));
	}
	
	public static void setupProjection(ShaderProgram shader) {
		
		int[] width = new int[1];
		int[] height = new int[1];
		GLFW.glfwGetWindowSize(Utils.window, width, height);
		double ax = (width[0] / (1920.0 * 0.55f));
		double ay = (height[0] / 1080.0);
		
		float aspectRatio = (float)ax / (float)ay;
		
		
		projectionMatrix = new Matrix4f().perspective((float)Math.toRadians(Settings.ACTUAL_FOV), aspectRatio, Z_NEAR, Z_FAR);
		shader.setUniformMat4("projectionMatrix", projectionMatrix);
	}
	
	public static Matrix4f getProjection() {
		int[] width = new int[1];
		int[] height = new int[1];
		GLFW.glfwGetWindowSize(Utils.window, width, height);
		double ax = (width[0] / (1920.0 * 0.55f));
		double ay = (height[0] / 1080.0);
		
		float aspectRatio = (float)ax / (float)ay;
		return new Matrix4f().perspective((float)Math.toRadians(Settings.ACTUAL_FOV), aspectRatio, Z_NEAR, Z_FAR);
	}
	
}
