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
				
				double BASE = height.getSmoothVoronoiAt(x / 2, y / 2, 32, 15);
				
				double H1 = height.getVoronoiAt(x / 2, y, 25, 20.0);
				double H2 = height.getVoronoiAt(x / 2, y, 8, 30.0);
				
				double H3 = height.getVoronoiAt(x, y, 4, 2.0);
				
				
				double HEIGHT = BASE + (H1 + H2) * 0.7 + H3;
				int ground_height = (int)(getGroundLevel() + HEIGHT);
				
				int STONEHEIGHT = (int)(getCaveLevel() + HEIGHT);
				
				if (y <= ground_height) {
					chunk.setTile(x1, y1, Tiles.DIRT);
					
					
					if (y <= STONEHEIGHT) {
						chunk.setTile(x1, y1, Tiles.STONE);
						if ((int)HEIGHT == 19) {
							chunk.setTile(x1, y1, Tiles.DIRT);
						}
						
						
					} else {
						if ((int)HEIGHT == 19) {
							chunk.setTile(x1, y1, Tiles.STONE);
						}
						
						
					}
					
					
					
					if (y <= ground_height - 30) {
						
						
						if (y <= this.getCaveLevel()) {
							if ((int)HEIGHT >= 30) {
								chunk.setTile(x1, y1, Tiles.AIR);
							}
						} else {
							if ((int)HEIGHT >= 40) {
								chunk.setTile(x1, y1, Tiles.AIR);
							}
						}
					} else {
						if (y <= ground_height - 15)
						if ((int)HEIGHT == 30) {
							chunk.setTile(x1, y1, Tiles.AIR);
						}
					}
				} else {
					chunk.setTile(x1, y1, Tiles.AIR);
				}
			}
		}
		for (int x1 = 0; x1 < Chunk.SIZE; x1++) {
			for (int y1 = 0; y1 < Chunk.SIZE; y1++) {
				
				int x = x1 + chunk.getX() * Chunk.SIZE;
				int y = y1 + chunk.getY() * Chunk.SIZE;
				x *= -1;
				y *= -1;
				
				int airCount = 0;
				A:
				for (int xx = -1; xx < 2; xx++) {
					for (int yy = -1; yy < 2; yy++) {
						int X = x1 + xx;
						int Y = y1 + yy;
						if (X >= 0 && Y >= 0 && X <= Chunk.SIZE - 1 && Y <= Chunk.SIZE - 1) {
							if (chunk.getTile(X, Y) == Tiles.AIR) {
								airCount++;
								if (airCount > 5) {
									break A;
								}
							}
						}
					}
				}
				
				if (airCount > 1 && y1 > 0 && chunk.getTile(x1, y1) == Tiles.DIRT && y >= getGroundLevel()) {
					chunk.setTile(x1, y1, Tiles.GRASS);
				}
				if (airCount > 5) {
					chunk.setTile(x1, y1, Tiles.AIR);
				}
				
				
				
			}
		}
		
		
		chunk.markForRerender();
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
