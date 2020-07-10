package kmerrill285.Inignoto.game.world.chunk.generator;

import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.tile.Tile.TileRayTraceType;
import kmerrill285.Inignoto.game.tile.data.TileState;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.game.world.chunk.Chunk;
import kmerrill285.Inignoto.game.world.chunk.MetaChunk;
import kmerrill285.Inignoto.game.world.chunk.generator.feature.Structure;
import kmerrill285.Inignoto.resources.MathHelper;

public class FlatChunkGenerator extends ChunkGenerator {

	public FlatChunkGenerator(World world, long seed) {
		super(world, seed);
	}
	
	public void generateChunk(Chunk chunk, MetaChunk metachunk, boolean structures) {
		if (chunk.load() == true) return;
		if (chunk.getTiles() == null) {
			chunk.setTiles(new TileState[Chunk.SIZE * Chunk.SIZE_Y * Chunk.SIZE]);
		}
		chunk.isGenerating = true;

		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int y = 0; y < Chunk.SIZE_Y; y++) {
				for (int z = 0; z < Chunk.SIZE; z++) {
					chunk.setLocalTile(x, y, z, Tiles.AIR);
				}
			}
		}
		int nx = chunk.getX() * Chunk.SIZE;
		int ny = chunk.getY() * Chunk.SIZE_Y;
		int nz = chunk.getZ() * Chunk.SIZE;
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int z = 0; z < Chunk.SIZE; z++) {
				int X = nx + x;
				int Z = nz + z;
				float baseheight = (int)getBaseHeight(X, Z);
				
				
				int height = (int)baseheight;
								
				for (int y = 0; y < Chunk.SIZE_Y; y++) {
					int Y = ny + y;
					
					if (Y < height){
						chunk.setLocalTile(x, y, z, Tiles.DIRT);
					}
					if (Y == height) {
						chunk.setLocalTile(x, y, z, getTopTile(X, Z));
					}
					
					
				}
				
			}
		}
		
		populateChunk(chunk, metachunk, true);
		
		chunk.isGenerating = false;
	}

	public void populateChunk(Chunk chunk, MetaChunk metachunk, boolean structures) {
		
		
	}
	
	public float getHillHeight(float x, float z) {
		return 0;
	}
	
	public float getRivers(float x, float z) {
		return 0;
	}
	
	public float getBaseHeight(float x, float z) {
		return 5;
	}
	
	public Tile getTopTile(float x, float z) {
	
		return Tiles.GRASS;
	}
}
