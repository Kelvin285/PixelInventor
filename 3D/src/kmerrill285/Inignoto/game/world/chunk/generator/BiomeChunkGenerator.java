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

		float[][][] heights = new float[Chunk.SIZE][Chunk.SIZE_Y][Chunk.SIZE];
		Biome[][][] biomes = new Biome[Chunk.SIZE][Chunk.SIZE_Y][Chunk.SIZE];
		int nx = chunk.getX() * Chunk.SIZE;
		int ny = chunk.getY() * Chunk.SIZE_Y;
		int nz = chunk.getZ() * Chunk.SIZE;
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int z = 0; z < Chunk.SIZE; z++) {
				int X = nx + x;
				int Z = nz + z;
				
				for (int y = 0; y < Chunk.SIZE_Y; y++) {
					int Y = ny + y;
					chunk.setLocalTile(x, y, z, Tiles.AIR);
					heights[x][y][z] = getBaseHeight(X, Y, Z);
					biomes[x][y][z] = Biomes.getSurfaceBiomeForLocation(X, Y, Z, BIOME_SIZE, noise);
				}
			}
		}
		
		
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int z = 0; z < Chunk.SIZE; z++) {
				int X = nx + x;
				int Z = nz + z;
				
				
				
				for (int y = 0; y < Chunk.SIZE_Y; y++) {
					int Y = ny + y;
					float height = heights[x][y][z];
					
					Biome biome = biomes[x][y][z];

					Tile tile = biome.getTileAt(Y, height);
					
					chunk.setLocalTile(x, y, z, tile);
					
				}
				
			}
		}
		populateChunk(chunk, metachunk, structures, heights, biomes);
		chunk.isGenerating = false;
	}

	public void populateChunk(Chunk chunk, MetaChunk metachunk, boolean structures, float[][][] heights, Biome[][][] biomes) {
		
		int nx = chunk.getX() * Chunk.SIZE;
		int ny = chunk.getY() * Chunk.SIZE_Y;
		int nz = chunk.getZ() * Chunk.SIZE;
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int z = 0; z < Chunk.SIZE; z++) {
				int X = nx + x;
				int Z = nz + z;
				
				for (int y = 0; y < Chunk.SIZE_Y; y++) {
					int Y = ny + y;
					Biome biome = biomes[x][y][z];
					biome.populate(X, Y, Z, x, y, z, chunk, chunk.getWorld(), noise);
					
					if (Y <= -5 && chunk.getLocalTile(x, y, z) == Tiles.AIR) {
						chunk.setLocalTile(x, y, z, Tiles.WATER);
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
	public float getBaseHeight(float x, float y, float z) {
		float height = 0;
		int i = 0;
		int size = 3;
		for (int x1 = -size; x1 < size + 1; x1++) {
			for (int z1 = -size; z1 < size + 1; z1++) {
				for (int y1 = -size; y1 < size + 1; y1++) {
					Biome biome = Biomes.getSurfaceBiomeForLocation((int)x + x1, (int)y + y1, (int)z + z1, BIOME_SIZE, noise);
					
					height += biome.getHeightAt(x + x1, y + y1, z + z1, noise);
					i++;
				}
			}
			i++;
		}
		
		return height / i;
	}
	
	public Tile getTopTile(float x, float z) {
		return Tiles.GRASS;
	}

}
