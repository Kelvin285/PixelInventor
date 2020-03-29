package kmerrill285.PixelInventor.game.world.chunk;

import kmerrill285.PixelInventor.game.tile.Tile;

public class TileData {
	public Tile tile;
	public int x;
	public int y;
	public int z;
	public TileData(int x, int y, int z, Tile tile) 
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.tile = tile;
	}
};