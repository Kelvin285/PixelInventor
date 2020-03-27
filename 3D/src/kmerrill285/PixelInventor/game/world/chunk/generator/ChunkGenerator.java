package kmerrill285.PixelInventor.game.world.chunk.generator;

import java.util.Random;

import imported.FastNoise;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.game.world.World;
import kmerrill285.PixelInventor.game.world.chunk.Chunk;
import kmerrill285.PixelInventor.game.world.chunk.Megachunk;
import kmerrill285.PixelInventor.game.world.chunk.TileData;

public class ChunkGenerator {
	
	protected FastNoise noise;
	protected Random random;
	protected World world;
	
	public ChunkGenerator(World world, long seed) {
		noise = new FastNoise((int)seed);
		random = new Random(seed);
		this.world = world;
	}
	
	public Chunk generateChunk(Megachunk megachunk, int cx, int cz) {
		if (Megachunk.CHUNKS_LOADED >= Megachunk.MAX_CHUNKS) return null;
		Chunk chunk = new Chunk(cx, cz, megachunk);
		if (chunk.canRender()) {
			chunk.setTiles(new TileData[Chunk.SIZE * Chunk.SIZE_Y * Chunk.SIZE]);
			generateChunk(chunk);
		}
		chunk.setParent(megachunk);

		return chunk;
	}

	public void generateChunk(Chunk chunk) {
		if (chunk.load() == true) return;
		if (chunk.voxels > 0) {
			return;
		}
		if (chunk.getTiles() == null) {
			chunk.setTiles(new TileData[Chunk.SIZE * Chunk.SIZE_Y * Chunk.SIZE]);
		}
		int nx = chunk.getX() * Chunk.SIZE + chunk.getParent().getX() * Megachunk.SIZE * Chunk.SIZE;
		int ny = chunk.getParent().getY() * Megachunk.SIZE * Chunk.SIZE_Y;
		int nz = chunk.getZ() * Chunk.SIZE + chunk.getParent().getZ() * Megachunk.SIZE * Chunk.SIZE;
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int z = 0; z < Chunk.SIZE; z++) {
				int X = nx + x;
				int Z = nz + z;
				int height = (int)getHeight(X, Z);
				Tile topTile = getTopTile(X, Z);
				for (int y = 0; y < Chunk.SIZE_Y; y++) {
					int Y = ny + y;
					if (Y > height) {
						chunk.setLocalTile(x, y, z, Tiles.AIR);
					} else {
						if (Y == height) {
							chunk.setLocalTile(x, y, z, topTile);
						} else {
							chunk.setLocalTile(x, y, z, Tiles.DIRT);
						}
					}
				}
			}
		}
	}
	
	public float getHeight(float x, float z) {
		float height = noise.GetSimplexFractal(x * 2, 0, z * 2) * 24;
		float mountain = noise.GetSimplex(x / 500.0f, 0.0f, z / 500.0f) * 512;
		
		return height + 64 + mountain;
	}
	
	public Tile getTopTile(float x, float z) {
		double purple = noise.GetSimplex(x, 0, z);
		Tile topTile = Tiles.GRASS;
		if (purple > 0) {
			topTile = Tiles.PURPLE_GRASS;
		}
		return topTile;
	}

}
