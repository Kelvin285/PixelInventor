package kmerrill285.Inignoto.game.tile;

import org.joml.Vector3f;

import kmerrill285.Inignoto.game.entity.player.PlayerEntity;
import kmerrill285.Inignoto.game.tile.data.TileState;
import kmerrill285.Inignoto.game.tile.data.TileStateHolder;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.resources.RayTraceResult;
import kmerrill285.Inignoto.resources.raytracer.RayBox;

public class StairTile extends Tile {

	
	
	public static final int FORWARD = 0, LEFT = 1, RIGHT = 2, BACK = 3, TOP_FORWARD = 4, TOP_LEFT = 5, TOP_RIGHT = 6, TOP_BACK = 7;
	
	public StairTile(String name, int[] sound) {
		super(name, sound);		
		
		collisionBoxes = new RayBox[] {
				new RayBox() {
					{
						min = new Vector3f(0, 0, 0);
						max = new Vector3f(1, 0.5f, 1);
					}
				},
				new RayBox() {
					{
						min = new Vector3f(0, 0.5f, 0.5f);
						max = new Vector3f(1, 1, 1);
					}
				}
		};
	}
	
	public TileState getStateWhenPlaced(int x, int y, int z, RayTraceResult result, PlayerEntity placer, World world) {
		final int VALUE = 360 / 4;
		int rotation = (int)Math.round(placer.rotY / VALUE);
		
		
		rotation %= 4;
		
		if (rotation < 0) {
			rotation += 4;
		}
		
		int RX = 0;
		
		System.out.println(rotation);
		
		if (rotation == 1) {
			RX = 1;
		}
		if (rotation == 0) {
			RX = 3;
		}
		if (rotation == 3) {
			RX = 2;
		}
		if (result.getHit().y >= 0) {
			if (Math.abs((int)(result.getHit().y * 2) % 2) == 1)
				RX += 4;
		} else {
			if (Math.abs((int)(result.getHit().y * 2) % 2) == 0)
				RX += 4;
		}
		
		
		return this.getStateHolder().getStateFor(RX);
	}

}
