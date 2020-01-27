package kelvin.pixelinventor.game.client.renderer;

import java.awt.Graphics;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.ImageIcon;

import kelvin.pixelinventor.game.tiles.Tile;
import kelvin.pixelinventor.util.Constants;

public class TileRenderer {
	public static void render(Tile tile, Graphics g, int x, int y) {
		String location = tile.getLocation();
		String file = "res/"+location.split(":")[0]+"/textures/tiles/"+location.split(":")[1]+".png";
		
		ImageIcon image = new ImageIcon(file);
		
		g.drawImage(image.getImage(), x, y, Constants.TILESIZE, Constants.TILESIZE, null);
	}
}
