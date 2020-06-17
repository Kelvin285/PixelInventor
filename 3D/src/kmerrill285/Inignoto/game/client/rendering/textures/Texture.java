package kmerrill285.Inignoto.game.client.rendering.textures;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Texture {
	public String fileName;
	private int textureId;
	public int width, height;
	
	public Texture(File file) {
		try {
			PNGDecoder decoder = new PNGDecoder(new FileInputStream(file));
			ByteBuffer buf = ByteBuffer.allocateDirect(
				    4 * decoder.getWidth() * decoder.getHeight());
				decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
			buf.flip();
			
			setTextureId(GL11.glGenTextures());
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTextureId());
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, decoder.getWidth(),
				    decoder.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			
			this.width = decoder.getWidth();
			this.height = decoder.getHeight();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Texture(String modId, String fileName) {
		try {
			PNGDecoder decoder = new PNGDecoder(Texture.class.getResourceAsStream("/"+modId+"/"+fileName));
			ByteBuffer buf = ByteBuffer.allocateDirect(
				    4 * decoder.getWidth() * decoder.getHeight());
				decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
			buf.flip();
			
			setTextureId(GL11.glGenTextures());
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTextureId());
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, decoder.getWidth(),
				    decoder.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			
			this.width = decoder.getWidth();
			this.height = decoder.getHeight();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Texture(int width, int height, int pixelFormat, boolean shadow, boolean raytracing) throws Exception {
		if (raytracing) {
			this.textureId = GL11.glGenTextures();
			this.width = width;
			this.height = height;
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA32F, width, height, 0, GL30.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL15.GL_CLAMP_TO_BORDER);
		    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL15.GL_CLAMP_TO_BORDER);
			return;
		}
		if (!shadow) {
			this.textureId = GL11.glGenTextures();
		    this.width = width;
		    this.height = height;
		    GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
		    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		    GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, textureId, 0);

		    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL15.GL_CLAMP_TO_BORDER);
		    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL15.GL_CLAMP_TO_BORDER);
	    } else {
			this.textureId = GL11.glGenTextures();
		    this.width = width;
		    this.height = height;
		    GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
		    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT32F, this.width, this.height, 0, pixelFormat, GL11.GL_FLOAT, (ByteBuffer) null);
		    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL15.GL_CLAMP_TO_BORDER);
		    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL15.GL_CLAMP_TO_BORDER);
		}
	    
	}
	
	public Texture(int id) {
		this.textureId = id;
	}

	public int getTextureId() {
		return textureId;
	}

	public void setTextureId(int textureId) {
		this.textureId = textureId;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}

	public void dispose() {
		GL11.glDeleteTextures(textureId);
	}
}
