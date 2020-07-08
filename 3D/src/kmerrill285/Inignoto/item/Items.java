package kmerrill285.Inignoto.item;

import java.util.HashMap;

import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.tile.Tiles;

public class Items {
	public static HashMap<String, Item> REGISTRY = new HashMap<String, Item>();
	
	public static void loadItems() {
		for (String str : Tiles.REGISTRY.keySet()) {
			Tile tile = Tiles.REGISTRY.get(str);
			registerItem(tile.getName(), new TileItem(tile));
		}
	}
	
	public static Item getItemForTile(Tile tile) {
		return REGISTRY.get(tile.getName());
	}
	
	public static Item registerItem(String name, Item item) {
		REGISTRY.put(name, item);
		return item;
	}
}
