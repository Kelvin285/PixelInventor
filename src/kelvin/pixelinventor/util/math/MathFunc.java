package kelvin.pixelinventor.util.math;

import kelvin.pixelinventor.game.PixelInventor;
import kelvin.pixelinventor.game.client.renderer.Camera;
import kelvin.pixelinventor.game.client.renderer.Mouse;
import kelvin.pixelinventor.game.world.Chunk;
import kelvin.pixelinventor.util.Constants;

public class MathFunc {
	
	public static final double RAD = (Math.PI / 180.0);
	public static final double DEG = (180.0 / Math.PI);
	
	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
	public static double lerp(double height, double height2, double d)
	{
	    return height + d * (height2 - height);
	}
	
	public static int getChunkPosFor(double x) {
		return (int) Math.floor(x / (Chunk.SIZE * Constants.TILESIZE));
	}
	
	public static int getTilePosFor(double x) {
		return (int) Math.floor(x / Constants.TILESIZE);
	}
	
	public static double abs(double x) {
		if (x < 0) return -x;
		return x;
	}
	
	public static int toZoomedCoordsY(int x) {
		double mx = x - PixelInventor.GAME.getFrameHeight() / 2;
		mx /= (Camera.zoom + 1);
		mx += PixelInventor.GAME.getFrameHeight() / 2;
		return (int)mx;
	}
	
	public static int toZoomedCoordsX(int x) {
		double mx = x - PixelInventor.GAME.getFrameWidth() / 2;
		mx /= (Camera.zoom + 1);
		mx += PixelInventor.GAME.getFrameWidth() / 2;
		return (int)mx;
	}
}
