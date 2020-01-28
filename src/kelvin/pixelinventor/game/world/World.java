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
	
	public void update() {
		for (Point p : loadedChunks.keySet()) {
			if (loadedChunks.containsKey(p) == false) continue;
			loadedChunks.get(p).update();
		}
		int X = (int) (Camera.X / (Chunk.SIZE * Constants.TILESIZE)) + 6;
		int Y = (int) (Camera.Y / (Chunk.SIZE * Constants.TILESIZE)) + 2;
		
		
		for (int x = X + -Camera.VIEW_DISTANCE; x < X + Camera.VIEW_DISTANCE + 4; x++) {
			for (int y = Y + -Camera.VIEW_DISTANCE; y < Y + Camera.VIEW_DISTANCE + 4; y++) {
				if (!chunkExists(x, y)) {
					addChunk(x, y);
					for (int xx = -1; xx < 2; xx++) {
						for (int yy = -1; yy < 2; yy++) {
							reshapeChunk(x + xx, y + yy);
						}
					}
				}
			}
		}
		
		for (Point p : loadedChunks.keySet()) {
			if (loadedChunks.containsKey(p) == false) continue;
			Chunk chunk = loadedChunks.get(p);
			double dist = MathFunc.distance(chunk.getX(), chunk.getY(), X, Y);
			if (dist > Camera.VIEW_DISTANCE * 2) {
				loadedChunks.remove(p, chunk);
				break;
			}
		}
		
	}
	
	public void render(Graphics g) {
		for (Point p : loadedChunks.keySet()) {
			if (loadedChunks.containsKey(p) == false) continue;
			loadedChunks.get(p).render(g);
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
		for (Point p : loadedChunks.keySet()) {
			if (loadedChunks.containsKey(p) == false) continue;
			if (p.x == x && p.y == y) {
				return loadedChunks.get(p);
			}
		}
		return null;
	}
	
	public boolean chunkExists(int x, int y) {
		Chunk c1 = loadedChunks.get(new Point(x, y));
		if (c1 != null) {
			return true;
		}
		for (Point p : loadedChunks.keySet()) {
			if (loadedChunks.containsKey(p) == false) continue;
			if (p.x == x && p.y == y) return true;
		}
		return false;
	}
	
	public void addChunk(int cx, int cy) {
		Chunk chunk = new Chunk(cx, cy, this, random);
		generator.generate(chunk);
		loadedChunks.put(new Point(cx, cy), chunk);
	}
	
}
