package kmerrill285.Inignoto.game.tile;

import java.util.Random;

import kmerrill285.Inignoto.game.entity.FallingTileEntity;
import kmerrill285.Inignoto.game.tile.data.TileState;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.game.world.chunk.TilePos;

public class FallingTile extends Tile {

	public FallingTile(String name, int[] sound) {
		super(name, sound);
	}

	public void tick(World world, TilePos pos, Random random, TileState state) {
		pos.y -= 1;
		if (world.getTileState(pos, false).isReplaceable() == true) {
			pos.y += 1;
			world.setTile(pos, Tiles.AIR);
			FallingTileEntity entity = new FallingTileEntity(pos, world, state);
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
