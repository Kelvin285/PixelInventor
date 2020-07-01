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

	public static Tile getTile(int ID) {
		for (String str : REGISTRY.keySet()) {
			Tile tile = REGISTRY.get(str);
			if (tile.getID() == ID) return tile;
		}
		return Tiles.AIR;
	}
	
	public static void loadTiles() {
		AIR = new Tile("Inignoto:air", null).setFullCube(false).setRayTraceType(TileRayTraceType.GAS).setBlocksMovement(false).setReplaceable().setDensity(1.225f / 1000.0f);
		DIRT = new FallingTile("Inignoto:dirt", TileSound.dirt).setHardness(1.0f).setDensity(2.6f);
		GRASS = new GrassTile("Inignoto:grass", TileSound.grass).setHardness(1.5f).setDensity(2.6f);
		STONE = new Tile("Inignoto:stone", TileSound.stone).setHardness(3.0f).setDensity(2.6f);
		PURPLE_GRASS = new GrassTile("Inignoto:purple_grass", TileSound.grass).setHardness(GRASS.getHardness()).setDensity(2.6f);
		LOG = new Tile("Inignoto:log", TileSound.wood).setHardness(2.5f).setDensity(2.6f);
		LEAVES = new Tile("Inignoto:leaves", TileSound.leaves).setHardness(0.2f).setDensity(2.6f);
		SMOOTH_STONE = new Tile("Inignoto:smooth_stone", TileSound.stone).setHardness(3.0f).setDensity(2.6f);
		WATER = new Tile("Inignoto:water", null).setDensity(1.0f).setRayTraceType(TileRayTraceType.LIQUID).setBlocksMovement(false).setReplaceable();
		SAND = new FallingTile("Inignoto:sand", TileSound.sand).setHardness(0.8f).setDensity(2.6f);
		MALECHITE = new Tile("Inignoto:malechite", TileSound.stone).setHardness(2.0f).setDensity(2.6f);

	}
}
