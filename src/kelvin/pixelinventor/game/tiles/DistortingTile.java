package kelvin.pixelinventor.game.tiles;

import java.util.Random;

import kelvin.pixelinventor.game.world.Chunk;

public class DistortingTile extends GroundTile {

	private double distortionFactor;
	public DistortingTile(String location, double distortionFactor) {
		super(location);
		this.distortionFactor = distortionFactor;
	}
	
	public double getDistortionFactor(Random random) {
		return distortionFactor;
	}
	
}
