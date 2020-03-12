package kmerrill285.PixelInventor.game.tile;

import java.util.HashMap;

public class Tiles {
	
	public static HashMap<String, Tile> REGISTRY = new HashMap<String, Tile>();
	
	public static Tile AIR;
	public static Tile DIRT;
	public static Tile GRASS;
	public static Tile STONE;
	public static Tile PURPLE_GRASS;
	
	public static void loadTiles() {
		DIRT = new Tile("PixelInventor:dirt");
		AIR = new Tile("PixelInventor:air").setFullCube(false);
		GRASS = new Tile("PixelInventor:grass");
		STONE = new Tile("PixelInventor:stone");
		PURPLE_GRASS = new Tile("PixelInventor:purple_grass");
	}
}
