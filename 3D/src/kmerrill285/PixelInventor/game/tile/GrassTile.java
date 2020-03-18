package kmerrill285.PixelInventor.game.tile;

import java.util.Random;

import kmerrill285.PixelInventor.game.world.World;
import kmerrill285.PixelInventor.game.world.chunk.TilePos;

public class GrassTile extends Tile {

	public GrassTile(String name) {
		super(name);
	}

	public void tick(World world, TilePos pos, Random random) {
		final int x = pos.x, y = pos.y, z = pos.z;
		if (world.getRandom().nextInt(100) <= 25) {
			pos.setPosition(x, y + 1, z);
			if (world.getTile(pos).isFullCube()) {
				pos.setPosition(x, y, z);
				world.setTile(pos, Tiles.DIRT);
				return;
			}
			for (int xx = -1; xx < 2; xx++) {
				for (int yy = -1; yy < 2; yy++) {
					for (int zz = -1; zz < 2; zz++) {
						if (xx == 0 && zz == 0) continue;
						if (world.getRandom().nextBoolean()) {
							pos.setPosition(x + xx, y + yy + 1, z + zz);
							
							if (world.getTile(pos).isFullCube() == false) {
								pos.setPosition(x + xx, y + yy, z + zz);
								if (world.getTile(pos) == Tiles.DIRT)
								world.setTile(pos, this);
							}
						}
					}
				}
			}
			pos.setPosition(x, y, z);
		}
	}
	
	public double getTickPercent() {
		return 0.01f;
	}
}
