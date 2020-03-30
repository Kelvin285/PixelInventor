package kmerrill285.PixelInventor.game.world.chunk;

import org.joml.Vector2i;
import org.joml.Vector3f;

import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.settings.Settings;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.game.world.World;

public class Megachunk {
	private int x, y, z;
	
	public static final int SIZE = 16;
	private static final int NUM_CHUNKS = SIZE * SIZE;

	private Chunk[] chunks;
		
	private int voxels = 0;
	
	private final World world;
	
	public static Chunk pseudochunk = new Chunk(0, 0, null);
	public static Chunk structurechunk = new Chunk(0, 0, null);
				
	public Megachunk(int x, int y, int z, World world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		chunks = new Chunk[NUM_CHUNKS];
	}
	
	public void setLocalChunk(int x, int z, Chunk chunk) {
		if (x >= 0 && z >= 0 && x < SIZE && z < SIZE) {
			if (chunks == null) return;
			chunks[x + z * SIZE] = chunk;
		}
	}
	
	public Chunk getLocalChunk(int x, int z) {
		if (x >= 0 && z >= 0 && x < SIZE && z < SIZE) {
			if (chunks == null) return null;
			return chunks[x + z * SIZE];
		}
		return null;
	}
	
	public Tile getTileAt(int x, int y, int z) {
		int cx = x / Chunk.SIZE;
		int cz = z / Chunk.SIZE;
		if (getLocalChunk(cx, cz) == null) return Tiles.AIR;
		return getLocalChunk(cx, cz).getLocalTile(x % 16, y, z % 16);
	}
	
	public TileData getTileDataAt(int x, int y, int z, boolean modifying) {
		int cx = x / Chunk.SIZE;
		int cz = z / Chunk.SIZE;
		if (getLocalChunk(cx, cz) == null) return null;
		return getLocalChunk(cx, cz).getTileData(x % 16, y, z % 16, modifying);
	}
	
	public void setTileAt(int x, int y, int z, Tile tile) {
		int cx = x / Chunk.SIZE;
		int cz = z / Chunk.SIZE;
		if (getLocalChunk(cx, cz) == null) {
			return;
		}
		getLocalChunk(cx, cz).setLocalTile(x % 16, y, z % 16, tile);
	}


	public void setTileData(int x, int y, int z, TileData data) {
		int cx = x / Chunk.SIZE;
		int cz = z / Chunk.SIZE;
		if (getLocalChunk(cx, cz) == null) {
			return;
		}
		getLocalChunk(cx, cz).setTileData(x % 16, y, z % 16, data);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public boolean isEmpty() {
		return getVoxels() <= 0;
	}
	
	
	public void updateAndBuild(double[] distance, Megachunk[] c, Vector2i closest, Vector3f pos) {
		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				if (getLocalChunk(x, z) == null) {
					double dist = pos.distance(getX() * SIZE * Chunk.SIZE + x * Chunk.SIZE + Chunk.SIZE / 2, getY() * Chunk.SIZE_Y + Chunk.SIZE_Y / 2, getZ() * SIZE * Chunk.SIZE + z * Chunk.SIZE + Chunk.SIZE / 2);
					if (dist < distance[0]) {
						distance[0] = dist;
						c[0] = this;
						closest.x = x;
						closest.y = z;
					}
				}
			}
		}
	}
	
	public void tick() {
		if (chunks != null)
		for (int i = 0; i < chunks.length; i++) {
			if (chunks[i] != null) {
				chunks[i].tick();
			}
		}
	}
	
	public void render(ShaderProgram shader) {
		int maxUpdates = 100;
		int updated = 0;
		if (chunks == null) return;
		for (int i = 0; i < chunks.length; i++) {
			if (chunks[i] != null) {
				if (chunks[i].getParent() != this) {
					chunks[i].setParent(this);
				}
				if (chunks[i].mesh != null) 
				{
					if (chunks[i].mesh.empty) {
						updated++;
						if (updated > maxUpdates) break;
					}
					chunks[i].render(shader);
				}
				
			}
		}
	}
	
	public void dispose() {
		for (int i = 0; i < chunks.length; i++) {
			if (chunks[i] != null) chunks[i].dispose();
			chunks[i] = null;
		}
		chunks = null;
	}

	public int getVoxels() {
		return voxels;
	}

	public void setVoxels(int voxels) {
		this.voxels = voxels;
	}

	public World getWorld() {
		return world;
	}
}
