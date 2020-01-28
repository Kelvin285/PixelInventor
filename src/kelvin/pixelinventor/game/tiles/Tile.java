package kelvin.pixelinventor.game.tiles;

import java.util.Random;

import kelvin.pixelinventor.game.world.Chunk;
import kelvin.pixelinventor.util.Constants;

public class Tile {
	
	private final String location;
	private double[] light_value = Constants.NO_LIGHT;
	
	public Tile(String location) {
		this.location = location;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void update(int x, int y, Chunk chunk) {
		
	}
	
	public double getUpdatePercent() {
		return 0.0;
	}
	
	public double getDistortionFactor(Random random) {
		return 0.0;
	}
	
	public double[] getLightValue() {
		return light_value;
	}
	
	public Tile setLightValue(double r, double g, double b, double a) {
		light_value[0] = r;
		light_value[1] = g;
		light_value[2] = b;
		light_value[3] = a;
		return this;
	}
	
	public int getShape(Chunk chunk, int x, int y) {
		return chunk.getStates()[x][y][0];
	}
}
