package kelvin.pixelinventor.game.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import kelvin.pixelinventor.game.PixelInventor;
import kelvin.pixelinventor.game.client.renderer.Camera;
import kelvin.pixelinventor.game.client.renderer.ChunkRenderer;
import kelvin.pixelinventor.game.tiles.Tile;
import kelvin.pixelinventor.game.tiles.Tiles;
import kelvin.pixelinventor.util.Constants;

public class Chunk {
	public static final int SIZE = 16;
	private int X, Y;
	
	private BufferedImage image;
	
	private Tile[][] tiles;
	
	private boolean rerender = true;
	
	private World world;
	
	public Chunk(int x, int y, World world) {
		this.X = x;
		this.Y = y;
		this.world = world;
		tiles = new Tile[Chunk.SIZE][Chunk.SIZE];
		image = (BufferedImage)PixelInventor.GAME.getFrame().createImage(Constants.TILESIZE * Chunk.SIZE, Constants.TILESIZE * Chunk.SIZE);
	}
	
	public void markForRerender() {
		rerender = true;
	}
	
	public void update() {
		
	}
	
	public void render(Graphics g) {
		if (rerender) {
			rerender = false;
			ChunkRenderer.render(tiles, image);
		}
		g.drawImage(image, X * Chunk.SIZE * Constants.TILESIZE - (int)Camera.X, Y * Chunk.SIZE * Constants.TILESIZE - (int)Camera.Y, null);
	}
	
	public int getX() {
		return this.X;
	}
	
	public int getY() {
		return this.Y;
	}

	public Tile[][] getTiles() {
		return tiles;
	}
	
	public Tile getTile(int x, int y) {
		int X = 0;
		int Y = 0;
		if (x < 0) {
			x += Chunk.SIZE;
			X = -1;
		}
		if (x > Chunk.SIZE - 1) {
			x -= Chunk.SIZE;
			X = 1;
		}
		
		if (y < 0) {
			y += Chunk.SIZE;
			Y = -1;
		}
		if (y > Chunk.SIZE - 1) {
			y -= Chunk.SIZE;
			Y = 1;
		}
		
		if (X != 0 || Y != 0) {
			Chunk c2 = world.getChunk(getX() + X, getY() + Y);
			if (c2 != null) {
				return c2.getTile(x, y);
			}
		}
		
		if (x < 0 || y < 0 || x > Chunk.SIZE - 1 || y > Chunk.SIZE - 1) {
			return Tiles.AIR;
		}
		return tiles[x][y];
	}
	
	public void setTile(int x, int y, Tile tile) {
		
		int X = 0;
		int Y = 0;
		if (x < 0) {
			x += Chunk.SIZE;
			X = -1;
		}
		if (x > Chunk.SIZE - 1) {
			x -= Chunk.SIZE;
			X = 1;
		}
		
		if (y < 0) {
			y += Chunk.SIZE;
			Y = -1;
		}
		if (y > Chunk.SIZE - 1) {
			y -= Chunk.SIZE;
			Y = 1;
		}
		
		if (X != 0 || Y != 0) {
			Chunk c2 = world.getChunk(getX() + X, getY() + Y);
			if (c2 != null) {
				c2.setTile(x, y, tile);
				return;
			}
		}
		
		if (x < 0 || y < 0 || x > Chunk.SIZE - 1 || y > Chunk.SIZE - 1) {
			return;
		}
		tiles[x][y] = tile;
	}
}
