package kmerrill285.PixelInventor.game.world.chunk.generator;

import java.util.Random;

import imported.FastNoise;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.game.world.World;
import kmerrill285.PixelInventor.game.world.chunk.Chunk;

public class ChunkGenerator {
	
	protected FastNoise noise;
	protected Random random;
	protected World world;
	
	public ChunkGenerator(World world, long seed) {
		noise = new FastNoise((int)seed);
		random = new Random(seed);
		this.world = world;
	}
	
	public void generateChunk(Chunk chunk) {
		if (world.getWorldSaver().tryLoadChunk(chunk)) return;
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int y = 0; y < Chunk.SIZE; y++) {
				for (int z = 0; z < Chunk.SIZE; z++) {
					chunk.setTile(x, y, z, Tiles.AIR, false, false);
				}
			}
		}
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int z = 0; z < Chunk.SIZE; z++) {
				int X = x + chunk.getX() * Chunk.SIZE;
				int Z = z + chunk.getZ() * Chunk.SIZE;

				Tile topTile = getTopTile(X, Z);
				float height = getHeight(X, Z);
				
				for (int y = 0; y < Chunk.SIZE; y++) {
					
					int Y = y + chunk.getY() * Chunk.SIZE;
															
					if (Y < (int)height) {
						chunk.setTile(x, y, z, Tiles.DIRT, false, false);
						if (Y < (int)height - 3) {
							chunk.setTile(x, y, z, Tiles.STONE, false, false);
						}
					}
					if (Y == (int)height - 1 && chunk.getTile(x, y, z) == Tiles.DIRT) {
						chunk.setTile(x, y, z, topTile, false, false);
					}
					
				}
			}
		}
	}
	
	public float getHeight(float x, float z) {
		return noise.GetCubicFractal(x * 2, 0, z * 2) * 45;
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
