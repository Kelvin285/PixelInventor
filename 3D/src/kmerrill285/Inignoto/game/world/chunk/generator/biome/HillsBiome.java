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

public class HillsBiome extends PlainsBiome {

	@Override
	public float getHeightAt(float x, float y, float z, FastNoise noise) {
		return Math.abs(MathHelper.average(noise.GetPerlinFractal(x * 0.25f, z * 0.25f), noise.GetPerlinFractal(x, z))) * 80;
	}
	
}
