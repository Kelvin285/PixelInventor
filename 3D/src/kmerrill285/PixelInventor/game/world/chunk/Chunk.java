package kmerrill285.PixelInventor.game.world.chunk;

import java.util.Arrays;

import org.joml.Vector3f;
import org.joml.Vector3i;

import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.tile.Tiles;

public class Chunk {
	

	public static final int SIZE = 16;
	public static final int SIZE_Y = SIZE * Megachunk.SIZE;
	
	public int[] chunks;
	
	private static final int NUM_TILES = Chunk.SIZE * Chunk.SIZE * Chunk.SIZE_Y;
	
	private static final Vector3f scale = new Vector3f(SIZE, SIZE_Y, SIZE);
	
	private final Megachunk parent;
	
	private final int x, z;
	private final Vector3i pos;
	
	public int voxels = 0;
	
	private Tile[] tiles;
	
	public Chunk(int x, int z, Megachunk parent) {
		setTiles(new Tile[NUM_TILES]);
		Arrays.fill(tiles, Tiles.AIR);
		chunks = new int[SIZE_Y / SIZE];
		this.x = x;
		this.z = z;
		
		int X = x * SIZE;
		int Y = 0;
		int Z = z * SIZE;
		pos = new Vector3i(X, Y, Z);
		
		this.parent = parent;
	}
	
	public int getX() {
		return x;
	}
	
	public int getZ() {
		return z;
	}
	
	public int getChunk(int y) {
		if (y >= 0 && y < SIZE_Y) {
			return chunks[y / SIZE];
		}
		return 0;
	}
	
	public void setChunk(int y, int chunk) {
		if (y >= 0 && y < SIZE_Y) {
			chunks[y / SIZE] = chunk;
		}
	}
	
	public Vector3i getWorldPos() {
		return this.pos;
	}
	
	public boolean isEmpty() {
		return voxels <= 0;
	}

	public Tile[] getTiles() {
		return tiles;
	}

	public void setTiles(Tile[] tiles) {
		this.tiles = tiles;
	}
	
	public Tile getLocalTile(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			return tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y];
		}
		return Tiles.AIR;
	}
	
	public void setLocalTile(int x, int y, int z, Tile tile) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			Tile local = getLocalTile(x, y, z);
			if (tile == Tiles.AIR && local != Tiles.AIR) {
				if (voxels > 0) {
					voxels--;
					setChunk(y, getChunk(y) - 1);
				}
				if (getParent().getVoxels() > 0) {
					getParent().setVoxels(getParent().getVoxels() - 1);
				}
			}
			if (tile != Tiles.AIR && local == Tiles.AIR) {
				voxels++;
				getParent().setVoxels(getParent().getVoxels() + 1);
				setChunk(y, getChunk(y) + 1);
			}
			tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] = tile;
		}
	}

	public boolean isLocalTileNotFull(int x, int y, int z) {
		Tile local = getLocalTile(x, y, z);
		return !local.isFullCube() || !local.isVisible();
	}

	public Megachunk getParent() {
		return parent;
	}
	
}
