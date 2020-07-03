package kmerrill285.Inignoto.game.world.chunk.generator;

import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.game.world.chunk.Chunk;
import kmerrill285.Inignoto.game.world.chunk.MetaChunk;
import kmerrill285.Inignoto.game.world.chunk.TileData;
import kmerrill285.Inignoto.game.world.chunk.generator.biome.Biome;
import kmerrill285.Inignoto.game.world.chunk.generator.biome.Biomes;

public class BiomeChunkGenerator extends ChunkGenerator {
	
	public final float BIOME_SIZE = 1.0f;
	
	public BiomeChunkGenerator(World world, long seed) {
		super(world, seed);
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
				
				Biome biome = Biomes.getSurfaceBiomeForLocation(X, Z, BIOME_SIZE, noise);
				
				float height = biome.getHeightAt(X, Z, noise);
				
				for (int y = 0; y < Chunk.SIZE_Y; y++) {
					int Y = ny + y;
					
					Tile tile = biome.getTileAt(Y, height);
					
					chunk.setLocalTile(x, y, z, tile);
					
				}
				
			}
		}
		populateChunk(chunk, metachunk, structures);
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
				
				Biome biome = Biomes.getSurfaceBiomeForLocation(X, Z, BIOME_SIZE, noise);

				for (int y = 0; y < Chunk.SIZE_Y; y++) {
					int Y = ny + y;
					
					biome.populate(X, Y, Z, x, y, z, chunk, chunk.getWorld(), noise);
					
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
		Biome biome = Biomes.getSurfaceBiomeForLocation((int)x, (int)z, BIOME_SIZE, noise);
		
		float height = biome.getHeightAt(x, z, noise);
		return height;
	}
	
	public Tile getTopTile(float x, float z) {
		return Tiles.GRASS;
	}

}
