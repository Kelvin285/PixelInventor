package kelvin.pixelinventor.game.client.renderer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import kelvin.pixelinventor.game.tiles.Tile;
import kelvin.pixelinventor.util.Constants;
import kelvin.pixelinventor.util.math.MathFunc;

public class TileRenderer {
	public static void render(Tile tile, Graphics g, int x, int y, int sx, int sy) {
		String location = tile.getLocation();
		String file = "res/"+location.split(":")[0]+"/textures/tiles/"+location.split(":")[1]+".png";
		
		ImageIcon image = new ImageIcon(file);
		
		g.drawImage(image.getImage(), x, y, x + Constants.TILESIZE, y + Constants.TILESIZE, sx * Constants.TILESIZE, sy * Constants.TILESIZE, sx * Constants.TILESIZE + Constants.TILESIZE, sy * Constants.TILESIZE + Constants.TILESIZE, null);
	}
}
