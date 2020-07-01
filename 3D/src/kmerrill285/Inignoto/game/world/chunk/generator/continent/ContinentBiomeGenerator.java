package kmerrill285.Inignoto.game.world.chunk.generator.continent;

import imported.FastNoise;
import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.chunk.generator.noise.VoronoiMap;

public class ContinentBiomeGenerator {
	public VoronoiMap continents;
	public VoronoiMap landscaping;
	public VoronoiMap biomes;
	public VoronoiMap lakes;
	
	private FastNoise noise;
		
	public ContinentBiomeGenerator(long seed, FastNoise noise) {
		continents = new VoronoiMap(seed, 100);
		landscaping = new VoronoiMap(seed + 1, 15);
		this.noise = noise;
	}
	
	public Tile getTopTile(int x, int z) {
		if (isOnLand(x, z)) {
			return Tiles.GRASS;
		}
		return Tiles.STONE;
	}
	
	public float getHillHeightAt(int x, int z) {
		float height = (noise.GetSimplex(x * 0.2f, z * 0.2f) + 1) * 10 * 5.0f;
		height *= getContinentHeightAt(x, z);
		return height;
	}
	
	public float getSmoothHillHeightAt(int x, int z) {
		float height = 0;
		int i = 0;
		for (int xx = -3; xx < 4; xx++) {
			for (int zz = -3; zz < 4; zz++) {
				height += getHillHeightAt(x + xx, z + zz);
				i++;
			}
			i++;
		}
		height /= i;
		return height;
	}
	
	public boolean isOnLand(int x, int z) {		
		return getActualContinentHeightAt(x, z) > 0;
	}
	
	public float getActualContinentHeightAt(int x, int z) {
		if (continents.borderingValue(x, z, noise, 0))
			return landscaping.getHeightAt(x, z, noise);
		
		return continents.getHeightAt(x, z, noise);
	}
	
	public float getContinentHeightAt(int x, int z) {
		float height = getActualContinentHeightAt(x, z);
		if (height <= 0) {
			height *= 1.25f;
		}
		return height;
	}
}
