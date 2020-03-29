package kmerrill285.PixelInventor.game.world.chunk;

public class TilePos {
	public int x = 0, y = 0, z = 0;
	public TilePos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public TilePos(double x, double y, double z) {
		this.x = (int)Math.floor(x);
		this.y = (int)Math.floor(y);
		this.z = (int)Math.floor(z);
	}
	
	public void setPosition(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setPosition(double x, double y, double z) {
		this.x = (int)Math.floor(x);
		this.y = (int)Math.floor(y);
		this.z = (int)Math.floor(z);
	}
	
	public TilePos add(int i, int j, int k) {
		return new TilePos(x + i, y + j, z + k);
	}
	
	public TilePos add(double i, double j, double k) {
		return new TilePos(x + i, y + j, z + k);
	}
}
