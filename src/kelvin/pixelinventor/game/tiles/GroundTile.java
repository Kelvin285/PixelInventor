package kelvin.pixelinventor.game.tiles;

import kelvin.pixelinventor.game.world.Chunk;

public class GroundTile extends Tile {

	public GroundTile(String location) {
		super(location);
	}

	public int getShape(Chunk chunk, int x, int y) {
		int state = 0;
		if (chunk.getTile(x, y + 1) != Tiles.AIR) {
			state += 1;
		}
		if (chunk.getTile(x, y - 1) != Tiles.AIR) {
			state += 4;
		}
		if (chunk.getTile(x - 1, y) != Tiles.AIR) {
			state += 2;
		}
		if (chunk.getTile(x + 1, y) != Tiles.AIR) {
			state += 8;
		}
		
		if (state == 7 && chunk.getTile(x + 1, y - 1) == Tiles.AIR) {
			state = 2;
		}
		
		if (state == 7 && chunk.getTile(x + 1, y + 1) == Tiles.AIR) {
			state = 2;
		}
		
		if (state == 13 && chunk.getTile(x - 1, y - 1) == Tiles.AIR) {
			state = 8;
		}
		
		if (state == 13 && chunk.getTile(x - 1, y + 1) == Tiles.AIR) {
			state = 8;
		}
		
		if (state == 11 && chunk.getTile(x - 1, y - 1) == Tiles.AIR) {
			state = 1;
		}
		
		if (state == 11 && chunk.getTile(x + 1, y - 1) == Tiles.AIR) {
			state = 1;
		}
		
		if (state == 14 && chunk.getTile(x - 1, y + 1) == Tiles.AIR) {
			state = 4;
		}
		
		if (state == 14 && chunk.getTile(x + 1, y + 1) == Tiles.AIR) {
			state = 4;
		}
		
		return state;
	}
}
