package kmerrill285.PixelInventor.game.client.rendering.postprocessing;

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

import kmerrill285.PixelInventor.PixelInventor;
import kmerrill285.PixelInventor.game.client.rendering.textures.Texture;

public class FrameBuffer {
	public static final int WIDTH = 1920;

    public static final int HEIGHT = 1080;

    private final int FBO;

    private final Texture texture;
    private final Texture depth_texture;

    public FrameBuffer() throws Exception {
    	FBO = GL30.glGenFramebuffers();
        texture = new Texture(WIDTH, HEIGHT, GL30.GL_RGBA, false, false);
        depth_texture = new Texture(WIDTH, HEIGHT, GL30.GL_DEPTH_COMPONENT, true, false);
        
        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
		GL30.glDrawBuffers(new int[] {GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1});

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getTextureId(), 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depth_texture.getTextureId(), 0);
        
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Could not create FrameBuffer");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind() {
    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, getDepthMapFBO());
		GL30.glDrawBuffers(new int[] {GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1});

		Vector3f skyColor = PixelInventor.game.world.getSkyColor();
		GL11.glClearColor(skyColor.x, skyColor.y, skyColor.z, 0.0f);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		//draw stuff I guess
		
		// Enable blending
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					
		GL11.glViewport(0, 0, FrameBuffer.WIDTH, FrameBuffer.HEIGHT);
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
    }
}
