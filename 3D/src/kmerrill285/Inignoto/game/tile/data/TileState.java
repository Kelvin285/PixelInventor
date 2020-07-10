package kmerrill285.Inignoto.game.tile.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import kmerrill285.Inignoto.game.client.rendering.BlockFace;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.tile.Tile.TileRayTraceType;

public class TileState {
	private final int tile;
	private final int state;
	
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
	private String model = "";
	private float pitch = 0;
	private float yaw = 0;
	private int width = 1;
	private int height = 1;
	
	private float hardness = 1.0f;
	private float density = 1.0f;
	
	public float offset_x, offset_y, offset_z;

	private TileRayTraceType rayTraceType = TileRayTraceType.SOLID; 
	
	private final int index;
	
	public TileState(int tile, int state, String state_file, int index) {
		this.tile = tile;
		this.state = state;
		
		File file = new File("assets/"+state_file.split(":")[0]+"/"+state_file.split(":")[1]+".tile");
		
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
						if (a.equals("offset_x")) offset_x = Float.parseFloat(b);
						if (a.equals("offset_y")) offset_y = Float.parseFloat(b);
						if (a.equals("offset_z")) offset_z = Float.parseFloat(b);
						if (a.equals("3dmodel")) model = b.split(":")[0]+":models/3dmodel/tiles/"+b.split(":")[1]+".3dmodel";
						if (a.equals("full_cube")) fullCube = Boolean.parseBoolean(b);
						if (a.equals("visible")) visible = Boolean.parseBoolean(b);
						if (a.equals("blocks_movement")) blocksMovement = Boolean.parseBoolean(b);
						if (a.equals("replaceable")) isReplaceable = Boolean.parseBoolean(b);
						if (a.equals("hardness")) hardness = Float.parseFloat(b);
						if (a.equals("density")) density = Float.parseFloat(b);
						if (a.equals("pitch")) pitch = Float.parseFloat(b);
						if (a.equals("yaw")) yaw = Float.parseFloat(b);
						if (a.equals("ray_trace_type")) this.rayTraceType = 
								b.equals("solid")?TileRayTraceType.SOLID:b.equals("liquid")?TileRayTraceType.LIQUID:b.equals("gas")?TileRayTraceType.GAS:TileRayTraceType.SOLID;

					}
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}

		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getTile() {
		return tile;
	}
	
	public int getState() {
		return this.state;
	}
	
	public TileRayTraceType getRayTraceType() {
		return this.rayTraceType;
	}
	
	public float getHardness() {
		return this.hardness;
	}
	
	public float getDensity() {
		return this.density;
	}
	
	public boolean isReplaceable() {
		return this.isReplaceable;
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
	
	public String getModel() {
		return this.model;
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
	
	public String toString() {
		return Tiles.getTile(tile).getTranslatedName()+", " + state;
	}

	public float getPitch() {
		return this.pitch;
	}
	
	public float getYaw() {
		return this.yaw;
	}
	
}
