package kmerrill285.PixelInventor.game.tile;

import java.util.Random;

import kmerrill285.PixelInventor.game.entity.FallingTileEntity;
import kmerrill285.PixelInventor.game.world.World;
import kmerrill285.PixelInventor.game.world.chunk.TilePos;

public class FallingTile extends Tile {

	public FallingTile(String name) {
		super(name);
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
