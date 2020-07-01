package kmerrill285.Inignoto.game.world.chunk.generator;

import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.game.world.chunk.Chunk;
import kmerrill285.Inignoto.game.world.chunk.MetaChunk;
import kmerrill285.Inignoto.game.world.chunk.TileData;
import kmerrill285.Inignoto.game.world.chunk.generator.continent.ContinentBiomeGenerator;
import kmerrill285.Inignoto.game.world.chunk.generator.feature.Structure;

public class ContinentalChunkGenerator extends ChunkGenerator {
	
	private ContinentBiomeGenerator biomeGenerator;
	
	public ContinentalChunkGenerator(World world, long seed) {
		super(world, seed);
		biomeGenerator = new ContinentBiomeGenerator(seed, noise);
	}

	
	public void generateChunk(Chunk chunk, MetaChunk metachunk, boolean structures) {
		if (chunk.load() == true) return;
		if (chunk.getTiles() == null) {
			chunk.setTiles(new TileData[Chunk.SIZE * Chunk.SIZE_Y * Chunk.SIZE]);
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
				
				int height = (int)getBaseHeight(X, Z);	
				for (int y = 0; y < Chunk.SIZE_Y; y++) {
					int Y = ny + y;
					
					if (Y <= height) {
						chunk.setLocalTile(x, y, z, getTopTile(X, Z));
					}
					if (Y >= -1) {
						if (chunk.getLocalTile(x, y, z) == Tiles.STONE) {
							chunk.setLocalTile(x, y, z, Tiles.SAND);
						}
					}
					
				}
				
			}
		}
		
		populateChunk(chunk, metachunk, true);
		
		chunk.isGenerating = false;
	}

	public void populateChunk(Chunk chunk, MetaChunk metachunk, boolean structures) {
		
		int nx = chunk.getX() * Chunk.SIZE;
		int ny = chunk.getY() * Chunk.SIZE_Y;
		int nz = chunk.getZ() * Chunk.SIZE;
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int z = 0; z < Chunk.SIZE; z++) {
				int X = nx + x;
				int Z = nz + z;
				
								
				for (int y = 0; y < Chunk.SIZE_Y; y++) {
					int Y = ny + y;
					
					if (Y < 0 && chunk.getLocalTile(x, y, z) == Tiles.AIR) {
						chunk.setLocalTile(x, y, z, Tiles.WATER);
					}
					
					if (chunk.getLocalTile(x, y, z) == Tiles.GRASS) {
						Structure.BIG_TREE.addToChunk(chunk, x, y, z, X, Y, Z);
						Structure.SIMPLE_TREE.addToChunk(chunk, x, y, z, X, Y, Z);
					}
					
				}
				
			}
		}
		
	}
	
	
	public float getHillHeight(float x, float z) {
		
		return 0;
	}
	
	public float getRivers(float x, float z) {
		
		return 0;
	}
	public float getBaseHeight(float x, float z) {	
		return this.biomeGenerator.getSmoothHillHeightAt((int)x, (int)z);
	}
	
	public Tile getTopTile(float x, float z) {
		return this.biomeGenerator.getTopTile((int)x, (int)z);
	}

}
