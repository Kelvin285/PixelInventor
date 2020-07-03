package kmerrill285.Inignoto.game.world.chunk.generator.biome;

import imported.FastNoise;
import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.game.world.chunk.Chunk;
import kmerrill285.Inignoto.game.world.chunk.generator.biome.properties.BiomeHumidity;
import kmerrill285.Inignoto.game.world.chunk.generator.biome.properties.BiomeLocation;
import kmerrill285.Inignoto.game.world.chunk.generator.biome.properties.BiomeTemperature;
import kmerrill285.Inignoto.game.world.chunk.generator.biome.properties.BiomeType;

public abstract class Biome {
	public final Tile top_tile;
	public final Tile middle_tile;
	public final Tile stone_tile;
	public final BiomeHumidity humidity;
	public final BiomeTemperature temperature;
	public final BiomeLocation location;
	public final BiomeType type;
	
	public Biome(Tile top_tile, Tile middle_tile, Tile stone_tile, BiomeHumidity humidity, BiomeTemperature temperature, BiomeLocation location, BiomeType type) {
		this.top_tile = top_tile;
		this.middle_tile = middle_tile;
		this.stone_tile = stone_tile;
		this.humidity = humidity;
		this.temperature = temperature;
		this.location = location;
		this.type = type;
	}
	
	public abstract float getHeightAt(float WORLD_X, float WORLD_Z, FastNoise noise);
	public abstract void populate(int WORLD_X, int WORLD_Y, int WORLD_Z, int chunk_x, int chunk_y, int chunk_z, Chunk chunk, World world, FastNoise noise);
	
	public abstract boolean hasRivers();
	
	public abstract Tile getTileAt(int height, float heightmap_value);
}
