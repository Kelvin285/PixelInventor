package kmerrill285.PixelInventor.game.client.rendering.textures;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import kmerrill285.PixelInventor.resources.Utils;

public class Texture {
	public String fileName;
	private int textureId;
	
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
		} catch (IOException e) {
			e.printStackTrace();
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
}
