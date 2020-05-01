package kmerrill285.Inignoto.game.client.rendering.postprocessing;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;

import kmerrill285.Inignoto.Inignoto;
import kmerrill285.Inignoto.game.client.rendering.textures.Texture;

public class FrameBuffer {
	public int WIDTH = 1920;

    public int HEIGHT = 1080;

    private final int FBO;

    private final Texture texture;
    private final Texture depth_texture;
    private final Texture DEPTH;

    public FrameBuffer() throws Exception {
    	this(1920, 1080);
    }
    
    public FrameBuffer(int w, int h) throws Exception {
    	this.WIDTH = w;
    	this.HEIGHT = h;
    	FBO = GL30.glGenFramebuffers();
        texture = new Texture(WIDTH, HEIGHT, GL30.GL_RGBA, false, false);
        depth_texture = new Texture(WIDTH, HEIGHT, GL30.GL_RGBA, false, false);
        DEPTH = new Texture(WIDTH, HEIGHT, GL30.GL_DEPTH_COMPONENT, true, false);
        
        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
		GL30.glDrawBuffers(new int[] {GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1});

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getTextureId(), 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, depth_texture.getTextureId(), 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, DEPTH.getTextureId(), 0);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Could not create FrameBuffer");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind() {
    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, getDepthMapFBO());
		GL30.glDrawBuffers(new int[] {GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1});

		Vector3f skyColor = Inignoto.game.world.getSkyColor();
		GL11.glClearColor(skyColor.x, skyColor.y, skyColor.z, 0.0f);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		//draw stuff I guess
		
		// Enable blending
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
    }
    
    public void unbind() {
    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    	GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
    
    public Texture getTexture() {
        return texture;
    }
    
    public Texture getDepthMapTexture() {
    	return this.depth_texture;
    }

    public int getDepthMapFBO() {
        return FBO;
    }

    public void dispose() {
        glDeleteFramebuffers(FBO);
        texture.dispose();
        depth_texture.dispose();
        DEPTH.dispose();
    }
}
