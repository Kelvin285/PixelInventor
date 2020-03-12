package kmerrill285.PixelInventor.game.world.chunk;

import java.util.Random;

import imported.FastNoise;
import kmerrill285.PixelInventor.game.tile.Tiles;

public class ChunkGenerator {
	
	private FastNoise noise;
	private Random random;
	
	public ChunkGenerator(long seed) {
		noise = new FastNoise((int)seed);
		random = new Random(seed);
	}
	
	public void generateChunk(Chunk chunk) {
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int y = 0; y < Chunk.SIZE; y++) {
				for (int z = 0; z < Chunk.SIZE; z++) {
					chunk.setTile(x, y, z, Tiles.AIR);
				}
			}
		}
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int z = 0; z < Chunk.SIZE; z++) {
				int X = x + chunk.getX() * Chunk.SIZE;
				int Z = z + chunk.getZ() * Chunk.SIZE;

				double purple = noise.GetSimplex(X, 0, Z);
				
				for (int y = 0; y < Chunk.SIZE; y++) {
					
					int Y = y + chunk.getY() * Chunk.SIZE;
					
					double height = noise.GetSimplex(X, 0, Z) * 10;
					
					double overhang = noise.GetSimplex(X * 5.0f, Y / 10.0f, Z * 5.0f) * 10;
					
					if (Y < (int)height && Y < (int)overhang) {
						chunk.setTile(x, y, z, Tiles.DIRT);
					}
					if (Y == (int)height - 1 && chunk.getTile(x, y, z) == Tiles.DIRT) {
						chunk.setTile(x, y, z, Tiles.GRASS);
						if (purple > 0) {
							chunk.setTile(x, y, z, Tiles.PURPLE_GRASS);
						}
					}
					if (Y < (int)height - 3) {
						chunk.setTile(x, y, z, Tiles.STONE);
					}
				}
			}
		}
		chunk.markForRerender();
	}
}
