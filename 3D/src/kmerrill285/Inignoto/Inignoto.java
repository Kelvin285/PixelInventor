package kmerrill285.Inignoto;

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

import custom_models.Part;
import kmerrill285.Inignoto.events.Events;
import kmerrill285.Inignoto.events.Input;
import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.Mouse;
import kmerrill285.Inignoto.game.client.audio.Sounds;
import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.client.rendering.gui.GuiRenderer;
import kmerrill285.Inignoto.game.client.rendering.gui.MenuScreen;
import kmerrill285.Inignoto.game.client.rendering.gui.ModelerScreen;
import kmerrill285.Inignoto.game.client.rendering.postprocessing.FrameBuffer;
import kmerrill285.Inignoto.game.client.rendering.shadows.ShadowRenderer;
import kmerrill285.Inignoto.game.client.rendering.textures.Fonts;
import kmerrill285.Inignoto.game.client.rendering.textures.Textures;
import kmerrill285.Inignoto.game.entity.player.ClientPlayerEntity;
import kmerrill285.Inignoto.game.foliage.Foliage;
import kmerrill285.Inignoto.game.settings.Settings;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.resources.Constants;
import kmerrill285.Inignoto.resources.FPSCounter;
import kmerrill285.Inignoto.resources.TPSCounter;
import kmerrill285.Inignoto.resources.Utils;

public class Inignoto {
	public static Inignoto game;
	
	public World world;
	public GuiRenderer guiRenderer;
	public ClientPlayerEntity player;
	
	public FrameBuffer framebuffer;
	public FrameBuffer blurbuffer;
	public ShadowRenderer shadowRenderer;
	
	public Inignoto() {
		Inignoto.game = this;
		run();
	}
	
	public void run() {
		System.out.println("Started Inignoto (Into the Unknown) "+Constants.version + " with LWJGL " + Version.getVersion());
		System.out.println("Steamworks version: " + com.codedisaster.steamworks.Version.getVersion());
		try {
		init();
		loop();
		}catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
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
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 5);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
		
		Utils.window = GLFW.glfwCreateWindow(Utils.FRAME_WIDTH, Utils.FRAME_HEIGHT, "Inignoto " + Constants.version, MemoryUtil.NULL, MemoryUtil.NULL);
	
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
		GLFW.glfwSwapInterval(0);
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
						world.buildChunks();
						world.tickChunks();
						world.updateChunkManager();
					}catch (Exception e) {
						e.printStackTrace();
						System.exit(0);
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
				while (!GLFW.glfwWindowShouldClose(Utils.window)) {
					TPSCounter.startUpdate();
					update();
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
						System.exit(0);
					}
					TPSCounter.endUpdate();
				}
			}
		}.start();
		
		new Thread() {
			public void run() {
				while (!GLFW.glfwWindowShouldClose(Utils.window)) {
					world.updateLights();
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
						System.exit(0);
					}
				}
			}
		}.start();
		
		
		FPSCounter.start();
		TPSCounter.start();
		
		int ticks = 0;
		
		while (!GLFW.glfwWindowShouldClose(Utils.window)) {
			try {
				FPSCounter.startUpdate();

				if (guiRenderer.getOpenScreen() == null) {
					updateLight();
					updateLight();
				}
				Camera.update();
				
				Camera.updateView();
				render();
				
				
				boolean updateMouse = true;
				if (guiRenderer.getOpenScreen() instanceof ModelerScreen) {
					updateMouse = false;
				}
				if (updateMouse)
				Mouse.update();

				FPSCounter.endUpdate();

			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		stop = true;
	}
	
	private void updateLight() {
		if (world == null) return;
		Inignoto.game.world.updateLight();
		shadowRenderer.update(Inignoto.game.world.light.getPosition(), Inignoto.game.world.light.getDirection());
	}
	
	public void renderGUI() {
	    if (this.guiRenderer != null)
	    guiRenderer.render();
	    
	}
	
	public void render() {
		Mesh.BUILT = 0;
		
		if (world != null) {
			Vector3f skyColor = world.getSkyColor();
			GL11.glClearColor(skyColor.x, skyColor.y, skyColor.z, 0.0f);
		} else {
			GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		}
		
		
		
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
    	
    	
    	if (Settings.DISTANCE_BLUR) {
    		if (Settings.POST_PROCESSING) {
        		blurbuffer.bind();
        		Utils.blur_shader.bind();
        		
        		guiRenderer.renderBlur();
        		Utils.blur_shader.unbind();
        		
        	}
        	blurbuffer.unbind();
        	
        	if (Events.w != 0) {
    			GL11.glViewport((int)Events.left, 0, (int)Events.w, (int)Events.height);
    		} else {
    			GL11.glViewport(0, 0, Utils.FRAME_WIDTH, Utils.FRAME_HEIGHT);
    		}
    	}
    	
    	
		Utils.sprite_shader.bind();
		
		renderGUI();
		
		Utils.sprite_shader.unbind();

		if (Settings.SHADOWS) {
			Utils.object_shader.setUniformInt("hasShadows", 1);
			shadowRenderer.bind(0);
			renderWorldShadows(0);
			
			shadowRenderer.bind(1);
			renderWorldShadows(1);
			
			shadowRenderer.bind(2);
			renderWorldShadows(2);
			
			shadowRenderer.bind(3);
			renderWorldShadows(3);
		} else {
			Utils.object_shader.setUniformInt("hasShadows", 0);
		}
		shadowRenderer.unbind(0);
		shadowRenderer.unbind(1);
		shadowRenderer.unbind(2);
		shadowRenderer.unbind(3);

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
	
	public void renderWorldShadows(int cascade) {
		Utils.shadow_shader.bind();
		Utils.shadow_shader.setUniformMat4("projMatrix1", shadowRenderer.projectionMatrix);
		Utils.shadow_shader.setUniformMat4("projMatrix2", shadowRenderer.projectionMatrix1);
		Utils.shadow_shader.setUniformMat4("projMatrix3", shadowRenderer.projectionMatrix2);
		Utils.shadow_shader.setUniformMat4("projMatrix4", shadowRenderer.projectionMatrix3);

		Utils.shadow_shader.setUniformInt("cascade", cascade);
		
		if (cascade == 3) {
			Utils.shadow_shader.setUniformFloat("zAdd", 0);
			Utils.shadow_shader.setUniformFloat("cMul", 4f);
		} else if (cascade == 2) {
			Utils.shadow_shader.setUniformFloat("zAdd", 0);
			Utils.shadow_shader.setUniformFloat("cMul", 3f);
		} else {
			if (cascade == 1) {
				Utils.shadow_shader.setUniformFloat("zAdd", 0);
				Utils.shadow_shader.setUniformFloat("cMul", 3f);
			}
			else {
				Utils.shadow_shader.setUniformFloat("zAdd", 0);
				Utils.shadow_shader.setUniformFloat("cMul", 1f);
			}
		}

		world.renderShadow(Utils.shadow_shader, shadowRenderer);
		world.renderChunksShadow(Utils.shadow_shader, shadowRenderer);
		Utils.shadow_shader.unbind();
	}
	
	public void renderWorld() {
		if (this.guiRenderer != null)
		if (this.guiRenderer.getOpenScreen() instanceof MenuScreen == false) {
			Utils.object_shader.bind();
			
			Utils.setupProjection(Utils.object_shader);
			
			Utils.object_shader.setUniformInt("voxelRender", 0);
			world.render(Utils.object_shader);
			Utils.object_shader.setUniformInt("voxelRender", 1);
			world.renderChunks(Utils.object_shader);
			
			Utils.object_shader.unbind();
		} else {
			if (this.guiRenderer.getOpenScreen() instanceof ModelerScreen) {
				Utils.object_shader.bind();
				Utils.setupProjection(Utils.object_shader);
				
				((ModelerScreen)this.guiRenderer.getOpenScreen()).render3D(Utils.object_shader);
				
				Utils.object_shader.unbind();
			}
		}
		
		
	}
		
	public void update() {
		if (this.guiRenderer.getOpenScreen() instanceof MenuScreen == false)
			world.tick();
		
	}
	
	public void dispose() {
		
		Fonts.dispose();
		Camera.soundSource.delete();
		Part.originMesh.dispose();
		Sounds.dispose();
		Settings.saveSettings();
		Utils.sprite_shader.dispose();
		Utils.object_shader.dispose();
		Utils.depth_shader.dispose();
		Utils.blur_shader.dispose();
		Utils.shadow_shader.dispose();
		shadowRenderer.dispose();
		framebuffer.dispose();
		world.dispose();
		Foliage.disposeOfFoliage();
		Textures.dispose();
		
		GLFW.glfwDestroyCursor(Utils.HAND_CURSOR);
		GLFW.glfwDestroyCursor(Utils.HRESIZE_CURSOR);
		GLFW.glfwDestroyCursor(Utils.VRESIZE_CURSOR);
		GLFW.glfwDestroyCursor(Utils.NORMAL_CURSOR);
		GLFW.glfwDestroyCursor(Utils.TYPE_CURSOR);
	
		
		System.out.println("exit!");
		System.exit(0);
	}
	
}
