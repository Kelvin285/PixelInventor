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

public class HillsBiome extends Biome {

	public HillsBiome() {
		super(Tiles.GRASS, Tiles.DIRT, Tiles.SMOOTH_STONE, BiomeHumidity.MODERATE, BiomeTemperature.COOL, BiomeLocation.LAND, BiomeType.HILLS);
	}

	@Override
	public float getHeightAt(float x, float y, float z, FastNoise noise) {
		return Math.abs(MathHelper.average(noise.GetPerlinFractal(x * 0.25f, z * 0.25f), noise.GetPerlinFractal(x, z))) * 80;
	}

	@Override
	public void populate(int X, int Y, int Z, int x, int y, int z, Chunk chunk, World world, FastNoise noise) {
		if (chunk.getLocalTile(x, y, z) == Tiles.GRASS) {
			Structure.SIMPLE_TREE.addToChunk(chunk, x, y, z, X, Y, Z);
			if (world.getRandom().nextInt(25) <= 5) {
				if (world.getRandom().nextInt(25) <= 5) {
					chunk.setFoliage(x, y + 1, z, Foliage.PURPLE_FLOWER);
				} else {
					chunk.setFoliage(x, y + 1, z, Foliage.TALL_GRASS);
				}
			}
		}
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
