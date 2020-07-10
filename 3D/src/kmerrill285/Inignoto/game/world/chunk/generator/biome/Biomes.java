package kmerrill285.Inignoto.game.world.chunk.generator.biome;

import java.util.ArrayList;

import imported.FastNoise;
import kmerrill285.Inignoto.game.world.chunk.generator.biome.properties.BiomeTemperature;

public class Biomes {
	public static ArrayList<Biome> biomes = new ArrayList<Biome>();

	public static final PlainsBiome PLAINS = (PlainsBiome) registerBiome(new PlainsBiome());
	public static final DesertBiome DESERT = (DesertBiome) registerBiome(new DesertBiome());
	public static final DesertHillsBiome DESERT_HILLS = (DesertHillsBiome) registerBiome(new DesertHillsBiome());

	public static final HillsBiome HILLS = (HillsBiome) registerBiome(new HillsBiome());
	public static final ShallowOceanBiome SHALLOW_OCEAN = (ShallowOceanBiome) registerBiome(new ShallowOceanBiome());
	public static final MountainsBiome MOUNTAINS = (MountainsBiome) registerBiome(new MountainsBiome());
	public static final BeachBiome BEACH = (BeachBiome) registerBiome(new BeachBiome());
	public static final GrassShorelineBiome GRASS_SHORELINE = (GrassShorelineBiome) registerBiome(new GrassShorelineBiome());

	

	public static Biome registerBiome(Biome biome) {
		biomes.add(biome);
		return biome;
	}
	
	public static Biome getMountainBiome(float cellular, float height, float extremes, BiomeTemperature temperature, int x, int y, int z) {
		return Biomes.MOUNTAINS;
	}
	
	public static Biome getHillsBiome(float cellular, float height, float extremes, BiomeTemperature temperature, int x, int y, int z) {
		if (extremes > 0.75f) {
			return getMountainBiome(cellular, height, extremes, temperature, x, y, z);
		}
		if (temperature == BiomeTemperature.EXTREME_HEAT) {
			return getDesertHillsBiome(cellular, height, extremes, temperature, x, y, z);
		}
		return Biomes.HILLS;
	}
	public static Biome getDesertHillsBiome(float cellular, float height, float extremes, BiomeTemperature temperature, int x, int y, int z) {
		return Biomes.DESERT_HILLS;
	}
	public static Biome getDesertBiome(float cellular, float height, float extremes, BiomeTemperature temperature, int x, int y, int z) {
		if ((int)(cellular * 2) == 1) {
			return Biomes.DESERT_HILLS;
		}
		return Biomes.DESERT;
	}
	
	public static Biome getPlainsBiome(float cellular, float height, float extremes, BiomeTemperature temperature, int x, int y, int z) {
		if (temperature == BiomeTemperature.EXTREME_HEAT) {
			return getDesertBiome(cellular, height, extremes, temperature, x, y, z);
		}
		if ((int)(cellular * 2) == 1) {
			return Biomes.HILLS;
		}
		return Biomes.PLAINS;
	}
	
	public static Biome getBeachBiome(float cellular, float height, float extremes, BiomeTemperature temperature, int x, int y, int z) {
		if (temperature.getValue() < BiomeTemperature.WARM.getValue()) {
			return Biomes.GRASS_SHORELINE;
		}
		return Biomes.BEACH;
	}
	
	public static Biome getLandBiome(float cellular, float height, float extremes, BiomeTemperature temperature, int x, int y, int z) {
		if (height > 0.5f) {
			getHillsBiome(cellular, height, extremes, temperature, x, y, z);
		}
		if (height < 0.15f) {
			return getBeachBiome(cellular, height, extremes, temperature, x, y, z);
		}
		return getPlainsBiome(cellular, height, extremes, temperature, x, y, z);
	}
	
	public static Biome getOceanBiome(float cellular, float height, float extremes, BiomeTemperature temperature, int x, int y, int z) {
		return Biomes.SHALLOW_OCEAN;
	}
	
	public static Biome getSurfaceBiomeForLocation(int x, int y, int z, float biome_size, FastNoise noise) {
		float cellular = Math.abs(noise.GetCellular(x * 0.35f, z * 0.35f));
		float height = noise.GetSimplexFractal(x * 0.05f, z * 0.05f) * 10;
		float extremes = 0;
		float temperature = (float)Math.sin(z / 1000.0f) * noise.GetSimplexFractal(x * 0.1f, z * 0.1f) * 10;
		if (height >= 0.2f) {
			temperature -= 0.2f;
		}
		if (height >= 0.5f) {
			temperature -= 0.2f;
		}
		if (height >= 0.75f) {
			temperature -= 0.2f;
		}

		Biome biome = biomes.get((int)(cellular * (biomes.size())));

		if (height > 0) {
			biome = getLandBiome(cellular, height, extremes, BiomeTemperature.getTemperature(temperature), x, y, z);
		} else {
			biome = getOceanBiome(cellular, height, extremes, BiomeTemperature.getTemperature(temperature), x, y, z);
		}
	
		return biome;
	}
}
