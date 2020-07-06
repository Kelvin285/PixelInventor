package kmerrill285.Inignoto.game.tile;

import kmerrill285.Inignoto.game.entity.player.PlayerEntity;
import kmerrill285.Inignoto.game.world.World;

public class StairTile extends Tile {

	public static final int FORWARD = 0, LEFT = 1, RIGHT = 2, BACK = 3, TOP_FORWARD = 4, TOP_LEFT = 5, TOP_RIGHT = 6, TOP_BACK = 7;
	
	public StairTile(String name, int[] sound) {
		super(name, sound);
		this.setFullCube(false);
	}
	
	public float getPitchForState(int state) {
		if (state / 4 == 1) return 180;
		return 0;
	}
	
	public float getYawForState(int state) {
		switch (state % 4) {
		case FORWARD: return 0 + getPitchForState(state);
		case LEFT: return -90 + getPitchForState(state);
		case RIGHT: return 90 + getPitchForState(state);
		case BACK: return 180 + getPitchForState(state);
	}
		return 0;
	}
	
	public int getStateNumberWhenPlaced(int x, int y, int z, PlayerEntity placer, World world) {
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
		
		if (placer.headPitch < 0) {
			RX += 4;
		}
		
		return RX;
	}

}
