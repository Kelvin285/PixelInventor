package kelvin.pixelinventor.game.tiles;

import kelvin.pixelinventor.util.Registry;
import kelvin.pixelinventor.util.events.Events;
import kelvin.pixelinventor.util.events.RegisterTileEvent;

public class Tiles extends RegisterTileEvent {

	public static Tiles INSTANCE;
	
	public static Tile DIRT;
	public static Tile AIR;
	public static Tile STONE;
	public static Tile GRASS;
	
	
	@Override
	public void run(Registry<Tile> registry) {
		registerAll(registry, 
				DIRT = new Tile("pixelinventor:dirt"),
				AIR = new Tile("pixelinventor:air"),
				STONE = new Tile("pixelinventor:stone"),
				GRASS = new Tile("pixelinventor:grass")
				);
	}
	
	private Tile register(Registry<Tile> registry, Tile tile) {
		return registry.register(tile.getLocation(), tile);
	}
	
	private Tile[] registerAll(Registry<Tile> registry, Tile... tiles) {
		for (Tile t : tiles) {
			register(registry, t);
		}
		return tiles;
	}
	
	static {
		Events.registerEvent(INSTANCE = new Tiles());
	}
	
}
