package kelvin.pixelinventor.game.world;

import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.Random;

import kelvin.pixelinventor.game.client.renderer.Camera;
import kelvin.pixelinventor.game.world.generator.ChunkGenerator;
import kelvin.pixelinventor.util.Constants;
import kelvin.pixelinventor.util.math.MathFunc;

public class World {
	public HashMap<Point, Chunk> loadedChunks = new HashMap<Point, Chunk>();
	private ChunkGenerator generator;
	private Random random;
	public World(ChunkGenerator generator) {
		this.generator = generator;
		random = new Random();
	}
	
	private int i = 0;
	public void update() {
		if (loadedChunks.size() > 800) loadedChunks.clear();
		for (Point p : loadedChunks.keySet()) {
			if (loadedChunks.containsKey(p) == false) continue;
			loadedChunks.get(p).update();
		}
		i++;
		if (i > 10) {
			i = 0;
			int X = (int) (Camera.X / (Chunk.SIZE * Constants.TILESIZE)) + 7;
			int Y = (int) (Camera.Y / (Chunk.SIZE * Constants.TILESIZE)) + 4;
			
			Point pt = new Point(0, 0);
			A:
			{
				for (int x = X + -Camera.VIEW_X * 2; x < X + Camera.VIEW_X * 2 + 1; x++) {
					for (int y = Y + -Camera.VIEW_Y * 2; y < Y + Camera.VIEW_Y * 2 + 1; y++) {
						pt.setLocation(x, y);
						if (Math.abs(x - X) <= Camera.VIEW_X && Math.abs(y - Y) <= Camera.VIEW_Y + 3) {
							if (!loadedChunks.containsKey(pt)) {
								addChunk(x, y);
								for (int xx = -1; xx < 2; xx++) {
									for (int yy = -1; yy < 2; yy++) {
										reshapeChunk(x + xx, y + yy);
									}
								}
							}
						} 
						else {
							if (loadedChunks.containsKey(pt)) {
								loadedChunks.remove(pt);
							}
						}
						
					}
				}
			}
		}
			
		
		
	}
	
	public void render(Graphics g) {
		Point pt = new Point(0, 0);
		int X = (int) (Camera.X / (Chunk.SIZE * Constants.TILESIZE)) + 7;
		int Y = (int) (Camera.Y / (Chunk.SIZE * Constants.TILESIZE)) + 4;
		A:
		for (int x = X + -Camera.VIEW_X * 3; x < X + Camera.VIEW_X * 3 + 1; x++) {
			for (int y = Y + -Camera.VIEW_Y * 3; y < Y + Camera.VIEW_Y * 3 + 1; y++) {
				pt.setLocation(x, y);
				if (Math.abs(x - X) <= Camera.VIEW_X && Math.abs(y - Y) <= Camera.VIEW_Y) {
					if (loadedChunks.containsKey(pt) == false) continue;
					loadedChunks.get(pt).render(g);
				}
			}
		}
	}
	
	public int[] getSkyColor() {
		return new int[] {64, 144, 203};
	}
	
	public void reshapeChunk(int x, int y) {
		for (int xx = -1; xx < 2; xx++) {
			for (int yy = -1; yy < 2; yy++) {
				reshapeNextChunk(x + xx, y + yy);
			}
		}
	}
	
	private void reshapeNextChunk(int x, int y) {
		Chunk chunk = getChunk(x, y);
		if (chunk == null) return;
		chunk.reshape();
		chunk.markForRerender();
	}
	
	public Chunk getChunk(int x, int y) {
		Chunk c1 = loadedChunks.get(new Point(x, y));
		if (c1 != null) {
			return c1;
		}
		
		return null;
	}
	
	public boolean chunkExists(int x, int y) {
		Chunk c1 = loadedChunks.get(new Point(x, y));
		if (c1 != null) {
			return true;
		}
		return false;
	}
	
	public void addChunk(int cx, int cy) {
		Chunk chunk = new Chunk(cx, cy, this, random);
		generator.generate(chunk);
		loadedChunks.put(new Point(cx, cy), chunk);
	}
	
}
