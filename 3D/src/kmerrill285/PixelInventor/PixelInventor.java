package kmerrill285.PixelInventor;

import java.nio.IntBuffer;

import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import kmerrill285.PixelInventor.events.Events;
import kmerrill285.PixelInventor.events.Input;
import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.gui.GuiRenderer;
import kmerrill285.PixelInventor.game.client.rendering.gui.IngameMenuScreen;
import kmerrill285.PixelInventor.game.client.rendering.postprocessing.FrameBuffer;
import kmerrill285.PixelInventor.game.entity.player.ClientPlayerEntity;
import kmerrill285.PixelInventor.game.settings.Settings;
import kmerrill285.PixelInventor.game.world.World;
import kmerrill285.PixelInventor.resources.Constants;
import kmerrill285.PixelInventor.resources.FPSCounter;
import kmerrill285.PixelInventor.resources.TPSCounter;
import kmerrill285.PixelInventor.resources.Utils;

public class PixelInventor {
	public static PixelInventor game;
	
	public World world;
	public GuiRenderer guiRenderer;
	public ClientPlayerEntity player;
	
	public FrameBuffer framebuffer;
	
	public PixelInventor() {
		PixelInventor.game = this;
		run();
	}
	
	public void run() {
		System.out.println("Started PixelInventor "+Constants.version + " with LWJGL " + Version.getVersion());
		System.out.println("Steamworks version: " + com.codedisaster.steamworks.Version.getVersion());
		
		init();
		loop();
		
		dispose();

		Callbacks.glfwFreeCallbacks(Utils.window);
		GLFW.glfwDestroyWindow(Utils.window);
		
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}
	
	public void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		
		if (!GLFW.glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		
		System.out.println("GLFW Version: " + GLFW.glfwGetVersionString());
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
		
		Utils.window = GLFW.glfwCreateWindow(Utils.FRAME_WIDTH, Utils.FRAME_HEIGHT, "PixelInventor " + Constants.version, MemoryUtil.NULL, MemoryUtil.NULL);
	
		GLFW.glfwSetKeyCallback(Utils.window, Events::keyCallback);
		GLFW.glfwSetCursorPosCallback(Utils.window, Events::mousePos);
		GLFW.glfwSetMouseButtonCallback(Utils.window, Events::mouseClick);
		GLFW.glfwSetWindowFocusCallback(Utils.window, Events::windowFocus);
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);
			
			GLFW.glfwGetWindowSize(Utils.window, pWidth, pHeight);
			
			GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
			
			GLFW.glfwSetWindowPos(Utils.window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
					);
		}
		
		GLFW.glfwMakeContextCurrent(Utils.window);
		GLFW.glfwSwapInterval(1);
		GLFW.glfwShowWindow(Utils.window);
	}
	@SuppressWarnings("unused")
	private boolean stop = false;
	Thread thread = null;
	@SuppressWarnings("unused")
	private boolean finished = false;
	public void loop() {
		GL.createCapabilities();
				
		try {
			Utils.setupGL();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		thread = new Thread() {
			public void run() {
				while (!GLFW.glfwWindowShouldClose(Utils.window)) {
					try {
						updateWorld();
					}catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("finish chunk thread!");
				finished = true;
			}
			
		};
		thread.start();
		
		new Thread() {
			public void run() {
				while (true) {
					if (guiRenderer == null || guiRenderer != null && !(guiRenderer.getOpenScreen() instanceof IngameMenuScreen)) {
						try {
							world.buildMegachunks();
						}catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				}
			}
		}.start();
		
		FPSCounter.start();
		TPSCounter.start();
		
		int ticks = 0;
		
		while (!GLFW.glfwWindowShouldClose(Utils.window)) {
			try {
				update();
				if (ticks == 0) {
					render();
				} else {
					ticks++;
					ticks %= Settings.frameSkip + 1;
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		stop = true;
	}
	
	public void renderGUI() {
	    
	    guiRenderer.render();
	    
	}
	
	public void render() {
		Mesh.BUILT = 0;
		Vector3f skyColor = world.getSkyColor();
		GL11.glClearColor(skyColor.x, skyColor.y, skyColor.z, 0.0f);
		
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		//draw stuff I guess
		
		// Enable blending
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					
		if (Events.w != 0) {
			GL11.glViewport((int)Events.left, 0, (int)Events.w, (int)Events.height);
		} else {
			GL11.glViewport(0, 0, Utils.FRAME_WIDTH, Utils.FRAME_HEIGHT);
		}
		
    	if (!Settings.POST_PROCESSING) {
    		renderWorld();
		}
		
		
		Utils.sprite_shader.bind();
		
		renderGUI();
		
		Utils.sprite_shader.unbind();
		
		
		if (Settings.POST_PROCESSING) {
			framebuffer.bind();
			renderWorld();
		}
		framebuffer.unbind();
		
		GL11.glDisable(GL11.GL_BLEND);
		
		GLFW.glfwSwapBuffers(Utils.window);

		GLFW.glfwPollEvents();
		Input.doInput();
		FPSCounter.updateFPS();
	}
	
	public void renderWorld() {
		Utils.object_shader.bind();
		
		Utils.setupProjection(Utils.object_shader);
		
		Utils.object_shader.setUniformInt("voxelRender", 0);
		world.render(Utils.object_shader);
		Utils.object_shader.setUniformInt("voxelRender", 1);
		world.renderMegachunks(Utils.object_shader);
		
		Utils.object_shader.unbind();
		
	}
	
	private boolean updateWorld = false;
	
	public void update() {
		TPSCounter.updateTPS();
		
		if (TPSCounter.canTick()) {
			Camera.update();
			world.tick();
			
		}
		
	}
	
	public void updateWorld() {
		world.updateChunkManager();
		world.tickMegachunks();
	}
	
	public void dispose() {
		Settings.saveSettings();
		Utils.sprite_shader.dispose();
		Utils.object_shader.dispose();
		Utils.depth_shader.dispose();
		framebuffer.dispose();
		world.dispose();
		System.out.println("exit!");
		System.exit(0);
	}
	
}
