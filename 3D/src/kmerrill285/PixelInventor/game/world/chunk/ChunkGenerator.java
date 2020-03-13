package kmerrill285.PixelInventor.game.world.chunk;

import java.util.Random;

import imported.FastNoise;
import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.game.world.World;

public class ChunkGenerator {
	
	private FastNoise noise;
	private Random random;
	private World world;
	
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
					chunk.setTile(x, y, z, Tiles.AIR, false);
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
					
					double height = noise.GetCubicFractal(X * 2, 0, Z * 2) * 45;
										
					if (Y < (int)height) {
						chunk.setTile(x, y, z, Tiles.DIRT, false);
						if (Y < (int)height - 3) {
							chunk.setTile(x, y, z, Tiles.STONE, false);
						}
					}
					if (Y == (int)height - 1 && chunk.getTile(x, y, z) == Tiles.DIRT) {
						chunk.setTile(x, y, z, Tiles.GRASS, false);
						if (purple > 0) {
							chunk.setTile(x, y, z, Tiles.PURPLE_GRASS, false);
						}
					}
					
				}
			}
		}
	}
}
