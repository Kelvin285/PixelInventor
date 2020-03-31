package kmerrill285.Inignoto.game.client.rendering.textures;

import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.swing.ImageIcon;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class TextureAtlas {
	public BufferedImage image;
	
	public HashMap<String, int[]> textures;
	
	public int textureID;
	public Texture texture;
	
	public TextureAtlas(String modid, String directory) {
		textures = new HashMap<String, int[]>();
		File dir = new File("assets/"+modid+"/textures/"+directory+"/");
		File[] files = dir.listFiles();
		for (File file : files) {
			String name = file.getName().replace(".png", "").trim();
			addToImage(modid, directory, name);
		}
		
		convertToOpenGL();
	}
	
	public TextureAtlas(Image capture) {
		textures = new HashMap<String, int[]>();
		addToImage(capture, "Inignoto", "image");
		convertToOpenGL();
	}

	private void convertToOpenGL() {
		int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        int BYTES_PER_PIXEL = 4;
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL);
        
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green
                buffer.put((byte) (pixel & 0xFF));             // Blue
                buffer.put((byte) ((pixel >> 24) & 0xFF));     // Alpha
            }
        }

        buffer.flip();
        
        textureID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		
		this.texture = new Texture(textureID);
	}
	
	public int[] getTexture(String name) {
		for (String str : textures.keySet()) {
			if (str.trim().contentEquals(name.trim())) { 
				return textures.get(str);
			}
		}
		return null;
	}
	
	public float[] convertToUV(float[] input, String name) {
		int[] texture = getTexture(name);
		int width = image.getWidth();
		int height = image.getHeight();
		
		if (texture != null) {
			float u = texture[0] / (float)width;
			float w = texture[2] / (float)width;
			float h = texture[3] / (float)height;

			
			for (int i = 0; i < input.length; i+=2) {
				input[i] *= w;
				input[i + 1] *= h;
				input[i] += u;
			}
		}
		
		return input;
	}
	
	public void addToImage(String modid, String directory, String texture) {
		ImageIcon icon = new ImageIcon("assets/"+modid+"/textures/"+directory+"/"+texture+".png");
		addToImage(icon.getImage(), modid, texture);
	}
	
	public void addToImage(Image add, String modid, String texture) {
		if (image == null) {
			image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(add.getWidth(null), add.getHeight(null), Transparency.TRANSLUCENT);
			textures.put(modid+":"+texture, new int[] {0, 0, add.getWidth(null), add.getHeight(null)});
			Graphics g = image.getGraphics();
			g.drawImage(add, 0, 0, null);
			g.dispose();
		} else {
			int last = image.getWidth();
			int newWidth = image.getWidth() + add.getWidth(null);
			int newHeight = image.getHeight();
			if (newHeight < add.getHeight(null)) newHeight = add.getHeight(null);
			BufferedImage image2 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(newWidth, newHeight, Transparency.TRANSLUCENT);
			Graphics g = image2.getGraphics();
			g.drawImage(image, 0, 0, null);
			g.drawImage(add, last, 0, null);
			g.dispose();
			
			textures.put(modid+":"+texture, new int[] {last, 0, add.getWidth(null), add.getHeight(null)});
			
			image = image2;
		}
	}
}
