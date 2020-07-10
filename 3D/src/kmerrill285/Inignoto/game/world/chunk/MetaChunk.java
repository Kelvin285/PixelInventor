package kmerrill285.Inignoto.game.world.chunk;

import java.util.HashMap;

import kmerrill285.Inignoto.game.tile.data.TileState;

public class MetaChunk {
	public HashMap<String, TileState> tiles = new HashMap<String, TileState>();
	
	public final int x, y, z;
	public MetaChunk(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public TileState getTileState(int x, int y, int z) {
		return tiles.get(x+","+y+","+z);
	}
	
	public void setTileState(int x, int y, int z, TileState data) {
		tiles.put(x+","+y+","+z, data);
	}
}
