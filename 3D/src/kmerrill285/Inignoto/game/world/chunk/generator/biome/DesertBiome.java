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

public class DesertBiome extends Biome {

	public DesertBiome() {
		super(Tiles.SAND, Tiles.SAND, Tiles.SMOOTH_STONE, BiomeHumidity.DRY, BiomeTemperature.HOT, BiomeLocation.LAND, BiomeType.DESERT);
	}

	@Override
	public float getHeightAt(float x, float y, float z, FastNoise noise) {
		return (1.0f - Math.abs(noise.GetSimplexFractal(x * 2, z)) * 2) * 10 + noise.GetSimplexFractal(x, z) * 10;
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
