package kmerrill285.Inignoto.game.world.chunk.generator;

import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.World;

public class FlatChunkGenerator extends ChunkGenerator {

	public FlatChunkGenerator(World world, long seed) {
		super(world, seed);
	}
	public float getHeight(float x, float z) {
		return 5;
	}
	
	public Tile getTopTile(float x, float z) {
	
		return Tiles.GRASS;
	}
}
