package kelvin.pixelinventor.game.world;

import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;

import kelvin.pixelinventor.game.client.renderer.Camera;
import kelvin.pixelinventor.game.world.generator.ChunkGenerator;
import kelvin.pixelinventor.util.Constants;
import kelvin.pixelinventor.util.MathFunc;

public class World {
	public HashMap<Point, Chunk> loadedChunks = new HashMap<Point, Chunk>();
	private ChunkGenerator generator;
	public World(ChunkGenerator generator) {
		this.generator = generator;
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				addChunk(x, y);
			}
		}
	}
	
	public void update() {
		for (Point p : loadedChunks.keySet()) {
			if (loadedChunks.containsKey(p) == false) continue;
			loadedChunks.get(p).update();
		}
		int X = (int) (Camera.X / (Chunk.SIZE * Constants.TILESIZE)) + 3;
		int Y = (int) (Camera.Y / (Chunk.SIZE * Constants.TILESIZE)) + 2;
		
		
		
		for (int x = X + -Camera.VIEW_DISTANCE; x < X + Camera.VIEW_DISTANCE; x++) {
			for (int y = Y + -Camera.VIEW_DISTANCE; y < Y + Camera.VIEW_DISTANCE; y++) {
				if (!chunkExists(x, y)) {
					addChunk(x, y);
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
	
	public Chunk getChunk(int x, int y) {
		for (Point p : loadedChunks.keySet()) {
			if (loadedChunks.containsKey(p) == false) continue;
			if (p.x == x && p.y == y) {
				return loadedChunks.get(p);
			}
		}
		return null;
	}
	
	public boolean chunkExists(int x, int y) {
		for (Point p : loadedChunks.keySet()) {
			if (loadedChunks.containsKey(p) == false) continue;
			if (p.x == x && p.y == y) return true;
		}
		return false;
	}
	
	public void addChunk(int cx, int cy) {
		Chunk chunk = new Chunk(cx, cy, this);
		generator.generate(chunk);
		loadedChunks.put(new Point(cx, cy), chunk);
	}
	
	public void render(Graphics g) {
		for (Point p : loadedChunks.keySet()) {
			if (loadedChunks.containsKey(p) == false) continue;
			loadedChunks.get(p).render(g);
		}
	}
	
}
