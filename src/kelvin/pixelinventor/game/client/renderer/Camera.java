package kelvin.pixelinventor.game.client.renderer;

import kelvin.pixelinventor.game.world.Chunk;

public class Camera {
	public static double X, Y = 628;
	public static int VIEW_X = (int)(16.0 * (4.0 / Chunk.SIZE));
	public static int VIEW_Y = (int)(8.0 * (4.0 / Chunk.SIZE));
	public static double zoom = 0;
}
