package kmerrill285.PixelInventor.game.tile;

import java.util.HashMap;

import kmerrill285.PixelInventor.game.tile.Tile.TileRayTraceType;

public class Tiles {
	
	public static HashMap<String, Tile> REGISTRY = new HashMap<String, Tile>();
	
	public static Tile AIR;
	public static Tile DIRT;
	public static Tile GRASS;
	public static Tile STONE;
	public static Tile PURPLE_GRASS;
	
	public static Tile getTile(int ID) {
		for (String str : REGISTRY.keySet()) {
			Tile tile = REGISTRY.get(str);
			if (tile.getID() == ID) return tile;
		}
		return Tiles.AIR;
	}
	
	public static void loadTiles() {
		DIRT = new Tile("PixelInventor:dirt");
		AIR = new Tile("PixelInventor:air").setFullCube(false).setRayTraceType(TileRayTraceType.GAS).setBlocksMovement(false);
		GRASS = new Tile("PixelInventor:grass");
		STONE = new Tile("PixelInventor:stone");
		PURPLE_GRASS = new Tile("PixelInventor:purple_grass");
	}
}
