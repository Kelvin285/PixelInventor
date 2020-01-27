package kelvin.pixelinventor.util;

import java.util.HashMap;

import kelvin.pixelinventor.game.tiles.Tile;

public class Registry<T> {
	public static Registry<Tile> TILES = new Registry<Tile>();
	
	
	
	
	
	
	private HashMap<String, T> registry = new HashMap<String, T>();
	
	public T register(String location, T o) {
		return registry.put(location, o);
	}
	
	public Object get(String location) {
		return registry.get(location);
	}
	
	public HashMap<String, T> getRegistry() {
		return registry;
	}
}
