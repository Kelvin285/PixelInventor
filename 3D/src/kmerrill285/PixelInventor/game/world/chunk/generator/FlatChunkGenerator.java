package kmerrill285.PixelInventor.game.world.chunk.generator;

import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.game.world.World;
import kmerrill285.PixelInventor.game.world.chunk.Chunk;

public class FlatChunkGenerator extends ChunkGenerator {

	public FlatChunkGenerator(World world, long seed) {
		super(world, seed);
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

				for (int y = 0; y < Chunk.SIZE; y++) {
					
					int Y = y + chunk.getY() * Chunk.SIZE;
					
										
					if (Y < 0) {
						chunk.setTile(x, y, z, Tiles.DIRT, false, false);
						if (Y < -3) {
							chunk.setTile(x, y, z, Tiles.STONE, false, false);
						}
					}
					if (Y == 0) {
						chunk.setTile(x, y, z, Tiles.GRASS, false, false);
					}
					
				}
			}
		}
	}

}
