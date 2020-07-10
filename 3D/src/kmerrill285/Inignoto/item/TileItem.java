package kmerrill285.Inignoto.item;

import java.awt.Rectangle;

import kmerrill285.Inignoto.Inignoto;
import kmerrill285.Inignoto.game.client.rendering.chunk.TileBuilder;
import kmerrill285.Inignoto.game.entity.Entity;
import kmerrill285.Inignoto.game.entity.player.PlayerEntity;
import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.game.world.chunk.TilePos;
import kmerrill285.Inignoto.resources.RayTraceResult;
import kmerrill285.Inignoto.resources.RayTraceResult.Direction;
import kmerrill285.Inignoto.resources.RayTraceResult.RayTraceType;

public class TileItem extends Item {

	public final Tile tile;
	
	public TileItem(Tile tile) {
		super(tile.getName(), 64);
		this.tile = tile;
		this.mesh = TileBuilder.buildMesh(tile.getDefaultState(), -0.5f, -0.5f, -0.5f, 30, 30);
	}
	
	public boolean rightClick(World world, PlayerEntity player, RayTraceResult result) {
		if (result != null) {
			if (result.getType() == RayTraceType.TILE) {
				TilePos pos = new TilePos(result.getPosition().x, result.getPosition().y, result.getPosition().z);
				Direction direction = result.getDirection();
				if (direction != null) {
					pos.x += direction.x;
					pos.y += direction.y;
					pos.z += direction.z;
					boolean stop = false;
					for (Entity e : world.entities) {
						if (new Rectangle(pos.x, pos.z, 1, 1).intersects((e.position.x), (e.position.z), (e.size.x), e.size.z))
							if (!(e instanceof PlayerEntity)) {
								if (pos.y == e.getTilePos().y || pos.y == e.getTilePos().y + 1) {
									stop = true;
									break;
								}
							} else {
								if (((PlayerEntity)e).isCrawling()) {
									if (pos.y == e.getTilePos().y) {
										stop = true;
										break;
									}
								} else {
									if (pos.y == e.getTilePos().y || pos.y == e.getTilePos().y + 1) {
										stop = true;
										break;
									}
								}
							}
					}
					
					if (!stop)
				if (world.getTileState(pos).isReplaceable()) {					
					world.setTileState(pos, tile.getStateWhenPlaced(pos.x, pos.y, pos.z, result, player, world), true);
					player.inventory.hotbar[player.inventory.hotbarSelected].decrementStack(1);
					player.arm_swing = 1.0f;

				}
			}
				
			}
		}
		return false;
	}

}
