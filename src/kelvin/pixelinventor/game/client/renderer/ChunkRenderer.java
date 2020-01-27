package kelvin.pixelinventor.game.client.renderer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import kelvin.pixelinventor.game.tiles.Tile;
import kelvin.pixelinventor.util.Constants;

public class ChunkRenderer {
	public static void render(Tile[][] tiles, BufferedImage image) {
		
		Graphics g = image.getGraphics();
		g.clearRect(0, 0, image.getWidth(), image.getHeight());
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[0].length; y++) {
				TileRenderer.render(tiles[x][y], g, x * Constants.TILESIZE, y * Constants.TILESIZE);
			}
		}
		g.dispose();
	}
}
