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
		for (int x1 = 0; x1 < Chunk.SIZE; x1++) {
			for (int y1 = 0; y1 < Chunk.SIZE; y1++) {
				int x = x1 + chunk.getX() * Chunk.SIZE;
				int y = y1 + chunk.getY() * Chunk.SIZE;
				x *= -1;
				y *= -1;
				
				double BASE = height.getSmoothVoronoiAt(x, 0, 30, 30);
				
				double BUMP = -height.getSmoothVoronoiAt(x, y, 30, 15);
				
				double OVERHANG = height.getVoronoiAt(x, y, 15, 15);
				
				double B = height.getSmoothVoronoiAt(x, 0, 30, 30);
				double H1 = height.getVoronoiAt(x / 2, y, 25, 20.0);
				double H2 = height.getVoronoiAt(x / 2, y, 8, 30.0);
				double H3 = height.getVoronoiAt(x, y, 4, 2.0);
				
				
				double HEIGHT = BASE + BUMP + OVERHANG;
				double HEIGHT2 = B + (H1 + H2) * 0.7 + H3;
				int ground_height = (int)(getGroundLevel() + HEIGHT);
				chunk.setTile(x1, y1, Tiles.AIR);
				
				//spaaaaceee
				if (y >= 625) {
					if ((int)OVERHANG >= 5 && (int)OVERHANG <= 7) {
						chunk.setTile(x1, y1, Tiles.STONE);
						if ((int)HEIGHT2 >= 19 && (int)HEIGHT2 <= 25) {
							chunk.setTile(x1, y1, Tiles.DIRT);
						}
						
						if ((int)HEIGHT2 >= 10 && (int)HEIGHT2 <= 15) {
							chunk.setTile(x1, y1, Tiles.ANTIMATTER);
						}
						
						if ((int)HEIGHT2 >= 30 && (int)HEIGHT2 <= 32) {
							chunk.setTile(x1, y1, Tiles.ANTIMATTER);
						}
					}
				}
				
				if (y <= getCaveLevel() - HEIGHT2) {
					chunk.setTile(x1, y1, Tiles.STONE);
					
					if (HEIGHT2 >= 35) {
						chunk.setTile(x1, y1, Tiles.AIR);
					}
					else
					if ((int)HEIGHT == 19) {
						chunk.setTile(x1, y1, Tiles.DIRT);
					}
					
					
				}
				else if (y <= getGroundLevel() + HEIGHT - 25)
				{
					chunk.setTile(x1, y1, Tiles.DIRT);
					if (HEIGHT2 >= 40) {
						chunk.setTile(x1, y1, Tiles.AIR);
					}
				}
				else
				if (y <= ground_height) {
					chunk.setTile(x1, y1, Tiles.DIRT);
					if (y == ground_height) {
						chunk.setTile(x1, y1, Tiles.PURPLE_GRASS);						
						chunk.setTile(x1, y1-1, Tiles.PURPLE_GRASS);
					}
					else
					if ((int)HEIGHT2 == 19) {
						chunk.setTile(x1, y1, Tiles.STONE);
					}
					
					
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
