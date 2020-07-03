package kmerrill285.Inignoto.game.world.chunk.generator.biome;

import java.util.ArrayList;

import imported.FastNoise;

public class Biomes {
	public static ArrayList<Biome> biomes = new ArrayList<Biome>();
	
	public static final PlainsBiome PLAINS = (PlainsBiome) registerBiome(new PlainsBiome());
	
	public static Biome registerBiome(Biome biome) {
		biomes.add(biome);
		return biome;
	}
	
	public static Biome getSurfaceBiomeForLocation(int x, int z, float biome_size, FastNoise noise) {
		return PLAINS;
	}
}
