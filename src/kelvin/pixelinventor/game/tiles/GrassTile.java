package kelvin.pixelinventor.game.tiles;

import java.util.Random;

import kelvin.pixelinventor.game.world.Chunk;

public class GrassTile extends GroundTile {

	public GrassTile(String location) {
		super(location);
	}

	public void update(int x, int y, Chunk chunk) {
		int airCount = 0;
		for (int xx = -1; xx < 2; xx++) {
			for (int yy = -1; yy < 2; yy++) {
				if (chunk.getTile(x + xx, y + yy) == Tiles.AIR) {
					airCount++;
				}
			}
		}
		if (airCount > 2) {
			for (int xx = -1; xx < 2; xx++) {
				for (int yy = -1; yy < 2; yy++) {
					if (chunk.getTile(x + xx, y + yy) == Tiles.DIRT) {
						chunk.setTile(x + xx, y + yy, Tiles.GRASS);
						return;
					}
				}
			}
		}
	}
	
	public double getUpdatePercent() {
		return 0.001;
	}
	
	public double getDistortionFactor(Random random) {
		return random.nextDouble();
	}
	
}
