package kmerrill285.Inignoto.game.tile;

import java.util.Random;

import kmerrill285.Inignoto.game.entity.FallingTileEntity;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.game.world.chunk.TilePos;

public class FallingTile extends Tile {

	public FallingTile(String name, int[] sound) {
		super(name, sound);
	}

	public void tick(World world, TilePos pos, Random random) {
		pos.y -= 1;
		if (world.getTile(pos).isReplaceable() == true) {
			pos.y += 1;
			world.setTile(pos, Tiles.AIR);
			FallingTileEntity entity = new FallingTileEntity(pos, world, this);
			entity.onGround = false;
			world.entities.add(entity);
			
		}
		else {
			pos.y += 1;
		}
	}
	
	public double getTickPercent() {
		return 0.00f;
	}
}
