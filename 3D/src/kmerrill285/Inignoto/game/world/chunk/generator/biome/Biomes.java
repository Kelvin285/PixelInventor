package kmerrill285.Inignoto.game.world.chunk.generator.biome;

import java.util.ArrayList;

import imported.FastNoise;

public class Biomes {
	public static ArrayList<Biome> biomes = new ArrayList<Biome>();
	
	public static final PlainsBiome PLAINS = (PlainsBiome) registerBiome(new PlainsBiome());
	public static final DesertBiome DESERT = (DesertBiome) registerBiome(new DesertBiome());
	public static final HillsBiome HILLS = (HillsBiome) registerBiome(new HillsBiome());
	public static final ShallowOceanBiome SHALLOW_OCEAN = (ShallowOceanBiome) registerBiome(new ShallowOceanBiome());
	public static final MountainsBiome MOUNTAINS = (MountainsBiome) registerBiome(new MountainsBiome());

	public static Biome registerBiome(Biome biome) {
		biomes.add(biome);
		return biome;
	}
	
	public static Biome getSurfaceBiomeForLocation(int x, int y, int z, float biome_size, FastNoise noise) {
		return biomes.get((int)(Math.abs(noise.GetCellular(x * 0.35f, z * 0.35f)) * (biomes.size())));
	}
}
