package kmerrill285.Inignoto.game.world.chunk.generator.biome;

import imported.FastNoise;
import kmerrill285.Inignoto.game.foliage.Foliage;
import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.game.world.chunk.Chunk;
import kmerrill285.Inignoto.game.world.chunk.generator.biome.properties.BiomeHumidity;
import kmerrill285.Inignoto.game.world.chunk.generator.biome.properties.BiomeLocation;
import kmerrill285.Inignoto.game.world.chunk.generator.biome.properties.BiomeTemperature;
import kmerrill285.Inignoto.game.world.chunk.generator.biome.properties.BiomeType;
import kmerrill285.Inignoto.game.world.chunk.generator.feature.Structure;
import kmerrill285.Inignoto.resources.MathHelper;

public class GrassShorelineBiome extends Biome {

	public GrassShorelineBiome() {
		super(Tiles.GRASS, Tiles.DIRT, Tiles.SMOOTH_STONE, BiomeHumidity.HUMID, BiomeTemperature.WARM, BiomeLocation.LAND, BiomeType.SHALLOW_OCEAN);
	}

	@Override
	public float getHeightAt(float x, float y, float z, FastNoise noise) {
		return MathHelper.average(noise.GetSimplex(x * 0.25f, z * 0.25f), noise.GetPerlinFractal(x, z)) * 10 - 5;
	}

	@Override
	public void populate(int X, int Y, int Z, int x, int y, int z, Chunk chunk, World world, FastNoise noise) {
		 
	}

	@Override
	public boolean hasRivers() {
		return true;
	}

	@Override
	public Tile getTileAt(int height, float heightmap_value) {
		if (height == (int)heightmap_value) {
			return top_tile;
		}
		if (height < (int)heightmap_value) {
			if (height <= (int)heightmap_value - 3) {
				return stone_tile;
			} else {
				return middle_tile;
			}
		}
		return Tiles.AIR;
	}
	
}
