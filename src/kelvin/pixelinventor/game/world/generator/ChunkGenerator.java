package kelvin.pixelinventor.game.world.generator;

import java.util.Random;

import kelvin.pixelinventor.game.tiles.Tile;
import kelvin.pixelinventor.game.tiles.Tiles;
import kelvin.pixelinventor.game.world.Chunk;
import kelvin.pixelinventor.game.world.generator.noise.VoronoiNoise;
import kelvin.pixelinventor.util.Constants;

public class ChunkGenerator {
	private Random random;
	private VoronoiNoise height;
	
	private double terrainMultiplier = 100.0;
	
	public ChunkGenerator(long seed) {
		height = new VoronoiNoise(seed);
		random = new Random(seed);
	}
	
	public void generate(Chunk chunk) {
		Tile[][] tiles = chunk.getTiles();
		for (int x1 = 0; x1 < Chunk.SIZE; x1++) {
			for (int y1 = 0; y1 < Chunk.SIZE; y1++) {
				int x = x1 + chunk.getX() * Chunk.SIZE;
				int y = y1 + chunk.getY() * Chunk.SIZE;
				x *= -1;
				y *= -1;
				
				double BASE = height.getSmoothVoronoiAt(x, 0, 30, 30);
				
				double BUMP = -height.getSmoothVoronoiAt(x, y, 30, 15);
				
				double OVERHANG = height.getVoronoiAt(x, y, 15, 15);
				
				double HEIGHT = BASE + BUMP + OVERHANG;
				int ground_height = (int)(getGroundLevel() + HEIGHT);
				
				if (y <= ground_height) {
					chunk.setTile(x1, y1, Tiles.DIRT);
					if (y == ground_height) {
						chunk.setTile(x1, y1, Tiles.GRASS);						
						chunk.setTile(x1, y1-1, Tiles.GRASS);
					}
					
					if ((int)HEIGHT == 19) {
						chunk.setTile(x1, y1, Tiles.STONE);
					}
					
					if ((int)HEIGHT == 30) {
						chunk.setTile(x1, y1, Tiles.AIR);
					}
					
					if (y <= getCaveLevel() + HEIGHT) {
						chunk.setTile(x1, y1, Tiles.STONE);
						
						if ((int)HEIGHT == 5) {
							chunk.setTile(x1, y1, Tiles.DIRT);
						}
						
						if (HEIGHT >= 21) {
							chunk.setTile(x1, y1, Tiles.AIR);
						}
					} else {
						if (y <= getGroundLevel() + HEIGHT - 10)
						if (HEIGHT >= 23) {
							chunk.setTile(x1, y1, Tiles.AIR);
						}
					}
				} else {
					chunk.setTile(x1, y1, Tiles.AIR);
				}
			}
		}
		
		
		chunk.reshape();
		chunk.markForRerender();
		chunk.recalculateLights();
	}
	

	public int getGroundLevel() {
		return -64;
	}
	
	public int getSeaLevel() {
		return getGroundLevel() - 15;
	}
	
	public int getCaveLevel() {
		return getGroundLevel() - 64;
	}
}
