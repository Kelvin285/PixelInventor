package kmerrill285.PixelInventor.game.tile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import org.joml.Vector3f;

import kmerrill285.PixelInventor.game.client.rendering.BlockFace;
import kmerrill285.PixelInventor.game.entity.ItemDropEntity;
import kmerrill285.PixelInventor.game.settings.Translation;
import kmerrill285.PixelInventor.game.world.World;
import kmerrill285.PixelInventor.game.world.chunk.TilePos;

public class Tile {
	
	public enum TileRayTraceType {
		SOLID, LIQUID, GAS
	};
	
	private String name;
	
	private boolean fullCube = true;
	private boolean visible = true;
	private boolean blocksMovement = true;
	private boolean isReplaceable = false;
	
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
	
	private static int CURRENT_ID = 0;
	private int ID;
	
	private float hardness;
	
	private TileRayTraceType rayTraceType = TileRayTraceType.SOLID; 
	
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
		ID = CURRENT_ID++;
	}
	
	public TileRayTraceType getRayTraceType() {
		return this.rayTraceType;
	}
	
	public int getID() {
		return this.ID;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTranslatedName() {
		return Translation.translateText(getName());
	}
	
	public float getHardness() {
		return this.hardness;
	}
	
	public Tile setHardness(float hardness) {
		this.hardness = hardness;
		return this;
	}
	
	public Tile setFullCube(boolean fullCube) {
		this.fullCube = fullCube;
		return this;
	}
	
	public Tile setReplaceable() {
		this.isReplaceable = true;
		return this;
	}
	
	public boolean isReplaceable() {
		return this.isReplaceable;
	}
	
	public Tile setBlocksMovement(boolean blocksMovement) {
		this.blocksMovement = blocksMovement;
		return this;
	}
	
	public Tile setRayTraceType(TileRayTraceType rayTraceType) {
		this.rayTraceType = rayTraceType;
		return this;
	}
	
	public Tile setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}
	
	public boolean isFullCube() {
		return this.fullCube;
	}
	
	public boolean blocksMovement() {
		return this.blocksMovement;
	}
	
	public boolean isVisible() {
		return this.visible;
	}
	
	public int getWidth() {
		return this.width;
	}
	public int getHeight() {
		return this.height;
	}
	
	public void dropAsItem(World world, int x, int y, int z) {
		ItemDropEntity drop = new ItemDropEntity(new Vector3f(x+0.5f, y+0.5f, z+0.5f), world, this);
		world.entities.add(drop);
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
	
	public void tick(World world, TilePos pos, Random random) {
		
	}
	
	public double getTickPercent() {
		return 0;
	}
	
}
