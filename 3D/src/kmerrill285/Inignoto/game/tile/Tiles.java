package kmerrill285.Inignoto.game.tile;

import java.util.HashMap;

import kmerrill285.Inignoto.game.client.audio.TileSound;
import kmerrill285.Inignoto.game.tile.Tile.TileRayTraceType;

public class Tiles {
	
	public static HashMap<String, Tile> REGISTRY = new HashMap<String, Tile>();
	
	public static Tile AIR;
	public static Tile DIRT;
	public static Tile GRASS;
	public static Tile STONE;
	public static Tile SMOOTH_STONE;
	public static Tile PURPLE_GRASS;
	public static Tile LOG;
	public static Tile LEAVES;
	public static Tile WATER;
	public static Tile SAND;
	public static Tile MALECHITE;
	public static Tile SMOOTH_STONE_STAIRS;

	public static Tile getTile(int ID) {
		for (String str : REGISTRY.keySet()) {
			Tile tile = REGISTRY.get(str);
			if (tile.getID() == ID) return tile;
		}
		return Tiles.AIR;
	}
	
	public static void loadTiles() {
		AIR = new Tile("Inignoto:air", null);
		DIRT = new FallingTile("Inignoto:dirt", TileSound.dirt);
		GRASS = new GrassTile("Inignoto:grass", TileSound.grass);
		STONE = new Tile("Inignoto:stone", TileSound.stone);
		PURPLE_GRASS = new GrassTile("Inignoto:purple_grass", TileSound.grass);
		LOG = new Tile("Inignoto:log", TileSound.wood);
		LEAVES = new Tile("Inignoto:leaves", TileSound.leaves);
		SMOOTH_STONE = new Tile("Inignoto:smooth_stone", TileSound.stone);
		WATER = new WaterTile("Inignoto:water", null);
		SAND = new FallingTile("Inignoto:sand", TileSound.sand);
		MALECHITE = new Tile("Inignoto:malechite", TileSound.stone);
		SMOOTH_STONE_STAIRS = new StairTile("Inignoto:smooth_stone_stairs", TileSound.stone);

	}
}
