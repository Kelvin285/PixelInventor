package kmerrill285.PixelInventor.game.tile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import kmerrill285.PixelInventor.game.client.rendering.BlockFace;
import kmerrill285.PixelInventor.game.settings.Translation;

public class Tile {
	private String name;
	
	private boolean fullCube = true;
	
	private String texture = "";
	private String side_texture = "";
	private String front_texture = "";
	private String back_texture = "";
	private String left_texture = "";
	private String right_texture = "";
	private String top_texture = "";
	private String bottom_texture = "";
	private int width = 1;
	private int height = 1;
	
	public Tile(String name) {
		this.name = name;
		Tiles.REGISTRY.put(this.name, this);
		File file = new File("assets/"+name.split(":")[0]+"/models/tiles/"+name.split(":")[1]+".model");
		if (file.exists()) {
			try {
				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					String str = scanner.nextLine();
					String[] data = str.split("=");
					boolean start = false;
					String a = "";
					String b = "";
					
					for (char c : data[0].toCharArray()) {
						if (start == true && c == '"') break;
						if (start == true) a += c;
						if (c == '"') start = true;
					}
					
					start = false;
					for (char c : data[1].toCharArray()) {
						if (start == true && c == '"') break;
						if (start == true) b += c;
						if (c == '"') start = true;
					}
					
					if (!a.isEmpty() && !b.isEmpty()) {
						if (a.equals("num_x")) width = Integer.parseInt(b);
						if (a.equals("num_y")) height = Integer.parseInt(b);
						if (a.equals("texture")) texture = b;
						if (a.equals("side")) side_texture = b;
						if (a.equals("top")) top_texture = b;
						if (a.equals("bottom")) bottom_texture = b;
						if (a.equals("left")) left_texture = b;
						if (a.equals("right")) right_texture = b;
						if (a.equals("front")) front_texture = b;
						if (a.equals("back")) back_texture = b;
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getTranslatedName() {
		return Translation.translateText(getName());
	}
	
	public Tile setFullCube(boolean fullCube) {
		this.fullCube = fullCube;
		return this;
	}
	
	public boolean isFullCube() {
		return this.fullCube;
	}
	
	public int getWidth() {
		return this.width;
	}
	public int getHeight() {
		return this.height;
	}
	
	public String getTextureFor(BlockFace face) {
		if (face == BlockFace.LEFT) 
		{
			if (left_texture.isEmpty()) return getSideTexture();
			return left_texture;
		}
		if (face == BlockFace.RIGHT) 
		{
			if (right_texture.isEmpty()) return getSideTexture();
			return right_texture;
		}
		if (face == BlockFace.FRONT) 
		{
			if (front_texture.isEmpty()) return getSideTexture();
			return front_texture;
		}
		if (face == BlockFace.BACK) 
		{
			if (back_texture.isEmpty()) return getSideTexture();
			return back_texture;
		}
		if (face == BlockFace.UP) 
		{
			if (top_texture.isEmpty()) return texture;
			return top_texture;
		}
		if (face == BlockFace.DOWN) 
		{
			if (bottom_texture.isEmpty()) return texture;
			return bottom_texture;
		}
		return texture;
	}
	
	public String getSideTexture() {
		if (side_texture.isEmpty()) return texture;
		return side_texture;
	}
	
	
}
