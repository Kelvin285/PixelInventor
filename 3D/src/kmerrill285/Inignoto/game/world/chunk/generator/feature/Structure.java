package kmerrill285.Inignoto.game.world.chunk.generator.feature;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.game.world.chunk.Chunk;
import kmerrill285.Inignoto.game.world.chunk.TileData;
import kmerrill285.Inignoto.game.world.chunk.TilePos;
import kmerrill285.Inignoto.game.world.chunk.generator.feature.trees.Tree;

public class Structure implements Serializable {
	private static final long serialVersionUID = 9193323629470774133L;
	public int length, width, height;
	public TileData[] tiles;
	public boolean[] generated;
	public boolean dead = false;
	
	public int x, y, z;
	public int wx, wy, wz;
	public Chunk chunk;
	public String name;
	
	public static Structure[] structures;
	
	private Structure structure;
	
	public static StoneCube STONE_CUBE = new StoneCube();
	public static Tree TREE = new Tree();
	
	protected boolean override;
	
	public Structure(int length, int width, int height) {
		this.length = length;
		this.width = width;
		this.height = height;
		tiles = new TileData[length * width * height];
		Arrays.fill(tiles, new TileData(Tiles.AIR.getID()));
		this.override = true;
	}
	
	public Structure(int x, int y, int z, int wx, int wy, int wz, Chunk chunk, Structure str) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.wx = wx;
		this.wy = wy;
		this.wz = wz;
		this.chunk = chunk;
		this.structure = str;
		generated = new boolean[str.tiles.length];
	}

	public TileData getTile(int x, int y, int z) {
		return getLocalTile(x - this.x, y - this.y, z - this.z);
	}
	
	public void setTile(int x, int y, int z, TileData data) {
		x -= this.x;
		y -= this.y;
		z -= this.z;
		if (x < 0 || y < 0 || z < 0 || x >= width || y >= height || z >= length) {
			return;
		}
		tiles[x + y * width + z * width * height] = data;
	}
	
	public TileData getLocalTile(int x, int y, int z) {
		if (x < 0 || y < 0 || z < 0 || x >= width || y >= height || z >= length) {
			return null;
		}
		return tiles[x + y * width + z * width * height];
	}	
	public boolean addToChunk(Chunk chunk, int x, int y, int z, int X, int Y, int Z) {
		return true;
	}
	
	
	public static void save(Structure s) {
		File dir = new File("Inignoto/structures/");
		dir.mkdirs();
		File file = new File("Inignoto/structures/"+s.name+".structure");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(s);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Structure load(String name) {
		Structure s = null;
		File dir = new File("Inignoto/structures/");
		dir.mkdirs();
		File file = new File("Inignoto/structures/"+name+".structure");
		if (file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis);
				s = (Structure)ois.readObject();
				ois.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return s;
	}
	
	public static Structure[] loadAll() {
		File dir = new File("Inignoto/structures/");
		dir.mkdirs();
		File[] files = dir.listFiles();
		Structure[] s = new Structure[files.length];
		for (int i = 0; i < files.length; i++) {
			s[i] = load(files[i]);
		}
		return s;
	}
	
	public static Structure load(File file) {
		Structure s = null;
		if (file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis);
				s = (Structure)ois.readObject();
				ois.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return s;
	}
	
	public static Structure createFromTiles(String name, int x1, int y1, int z1, int x2, int y2, int z2, World world) {
		Structure s = new Structure(Math.abs(x2 - x1), Math.abs(y2 - y1), Math.abs(z2 - z1));
		int sx = Math.min(x1, x2);
		int sy = Math.min(y1, y2);
		int sz = Math.min(z1, z2);
		int ex = Math.max(x1, x2);
		int ey = Math.max(y1, y2);
		int ez = Math.max(z1, z2);
		s.x = sx;
		s.y = sy;
		s.z = sz;
		TilePos pos = new TilePos(0, 0, 0);
		s.name = name;
		for (int x = sx; x < ex + 1; x++) {
			for (int y = sy; y < ey + 1; y++) {
				for (int z = sz; z < ez + 1; z++) {
					pos.x = x;
					pos.y = y;
					pos.z = z;
					TileData t = world.getTileData(pos, false);
					s.getTile(x, y, z).setTile(t.getTile());
					s.getTile(x, y, z).setWaterLevel(t.getWaterLevel());
				}
			}
		}
		return s;
	}

	public Structure getParent() {
		return this.structure;
	}
}
