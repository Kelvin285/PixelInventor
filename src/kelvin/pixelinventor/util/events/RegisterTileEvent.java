package kelvin.pixelinventor.util.events;

import kelvin.pixelinventor.game.tiles.Tile;
import kelvin.pixelinventor.util.Registry;

public abstract class RegisterTileEvent implements IEvent {
	public abstract void run(Registry<Tile> registry);
}
