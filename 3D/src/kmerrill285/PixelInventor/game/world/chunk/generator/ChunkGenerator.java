package kmerrill285.PixelInventor.game.world.chunk.generator;

import java.util.Random;

import imported.FastNoise;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.game.world.World;
import kmerrill285.PixelInventor.game.world.chunk.Chunk;
import kmerrill285.PixelInventor.game.world.chunk.Megachunk;

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
		int nx = cx * Chunk.SIZE + megachunk.getX() * Megachunk.SIZE * Chunk.SIZE;
		int ny = megachunk.getY() * Megachunk.SIZE * Chunk.SIZE_Y;
		int nz = cz * Chunk.SIZE + megachunk.getZ() * Megachunk.SIZE * Chunk.SIZE;
		int voxels = 0;
		Chunk chunk = new Chunk(cx, cz, megachunk);
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
						voxels++;
					}
				}
			}
		}
		if (voxels > 0) {
			return chunk;
		}
		return null;
	}
	
	public float getHeight(float x, float z) {
		return noise.GetCubicFractal(x * 2, 0, z * 2) * 45 + 64;
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
