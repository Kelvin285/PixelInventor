package kmerrill285.Inignoto.game.world.chunk;

import java.util.HashMap;

public class MetaChunk {
	public HashMap<String, TileData> tiles = new HashMap<String, TileData>();
	
	public TileData getTileData(int x, int y, int z) {
		return tiles.get(x+","+y+","+z);
	}
	
	public void setTileData(int x, int y, int z, TileData data) {
		tiles.put(x+","+y+","+z, data);
	}
}
