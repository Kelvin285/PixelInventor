package kelvin.pixelinventor.game.client.renderer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import kelvin.pixelinventor.game.tiles.Tile;
import kelvin.pixelinventor.game.world.Chunk;
import kelvin.pixelinventor.util.Constants;
import kelvin.pixelinventor.util.math.MathFunc;

public class ChunkRenderer {
	public static void render(Tile[][] tiles, int[][][] states, double[][][] lights, int[] skyColor, Chunk chunk, BufferedImage image) {
		
		Graphics g = image.getGraphics();
		
		g.setColor(new Color(skyColor[0], skyColor[1], skyColor[2]));
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[0].length; y++) {
				int[] state = states[x][y];
				TileRenderer.render(tiles[x][y], g, x * Constants.TILESIZE, y * Constants.TILESIZE, state[0], state[1]);
			}
		}
		g.dispose();
		
		
		double[][][] pixels = new double[image.getWidth()][image.getHeight()][4];
		
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				pixels[x][y] = image.getRaster().getPixel(x, y, pixels[x][y]);
			}
		}
		
		for (int xx = 0; xx < image.getWidth(); xx++) {
			for (int yy = 0; yy < image.getHeight(); yy++) {
				double[] pixel = pixels[xx][yy];
				
				if (pixel[0] == skyColor[0] && pixel[1] == skyColor[1] && pixel[2] == skyColor[2]) continue;
				
				
				
				
				
				int x = xx / Constants.TILESIZE;
				int y = yy / Constants.TILESIZE;
				pixel[0] = MathFunc.lerp(pixel[0], pixel[0] * lights[x][y][0], lights[x][y][3]);
				pixel[1] = MathFunc.lerp(pixel[1], pixel[1] * lights[x][y][1], lights[x][y][3]);
				pixel[2] = MathFunc.lerp(pixel[2], pixel[2] * lights[x][y][2], lights[x][y][3]);
				image.getRaster().setPixel(xx, yy, pixel);
			}
		}
		
	}
}
