package kmerrill285.Inignoto.game.tile;

import java.util.Random;

import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.game.world.chunk.TilePos;

public class WaterTile extends Tile {

	public WaterTile(String name, int[] sound) {
		super(name, sound);
	}

	public void tick(World world, TilePos pos, Random random) {
		final int x = pos.x, y = pos.y, z = pos.z;

		pos.setPosition(x, y - 1, z);
		if (world.getTileState(pos, false).getRayTraceType() == TileRayTraceType.GAS) {
			if (world.getChunkAt(pos) != null) 
			{
				if (world.getChunkAt(pos).generated) {
					world.setTile(pos, this, false);
					pos.setPosition(x, y, z);
					world.setTile(pos, Tiles.AIR, false);
				}
			}
			
		}
			
		pos.setPosition(x, y, z);
	}
}
