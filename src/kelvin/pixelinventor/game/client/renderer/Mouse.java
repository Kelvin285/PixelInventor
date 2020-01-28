package kelvin.pixelinventor.game.client.renderer;

import kelvin.pixelinventor.game.PixelInventor;
import kelvin.pixelinventor.util.Constants;

public class Mouse {
	public static int X, Y;
	
	public static int getX() {
		double mx = Mouse.X - PixelInventor.GAME.getFrameWidth() / 2;
		mx /= (Camera.zoom + 1);
		mx += PixelInventor.GAME.getFrameWidth() / 2;
		return (int)mx;
	}
	
	public static int getY() {
		double my = Mouse.Y - PixelInventor.GAME.getFrameHeight() / 2;
		my /= (Camera.zoom + 1);
		my += PixelInventor.GAME.getFrameHeight() / 2;
		return (int)my;
	}
	
	public static int getBlockX() {
		int offsX = 0;
		
		return (int) ((int)((Mouse.getX() + Camera.X) / 16) * 16 - Camera.X) + offsX;
	}
	
	public static int getBlockY() {
		return (int) ((int)((Mouse.getY() + Camera.Y) / 16) * 16 - Camera.Y);
	}
}
