package kelvin.pixelinventor.game.client.renderer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import kelvin.pixelinventor.game.Settings;
import kelvin.pixelinventor.game.tiles.Tile;
import kelvin.pixelinventor.game.tiles.Tiles;
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
		
		if (!Settings.dynamicLights) {
			double[][][] pixels = new double[image.getWidth()][image.getHeight()][4];
			
			for (int xx = 0; xx < image.getWidth(); xx++) {
				for (int yy = 0; yy < image.getHeight(); yy++) {
					pixels[xx][yy] = image.getRaster().getPixel(xx, yy, pixels[xx][yy]);
					double[] pixel = pixels[xx][yy];
					
					if (Settings.smoothLights) {
						
						double R = pixel[0];
						double G = pixel[1];
						double B = pixel[2];
						double A = 0;
						
						int BX = (int)(xx / 16);
						int BY = (int)(yy / 16);
						double RBX = xx / 16.0;
						double RBY = yy / 16.0;
						
						double[] light = chunk.getRenderLight(BX, BY);
						int th = 5;
						if (Math.abs(R - skyColor[0]) < th && Math.abs(G - skyColor[1]) < th && Math.abs(B - skyColor[2]) < th) {
							R = skyColor[0];
							G = skyColor[1];
							B = skyColor[2];
							pixel[0] = R;
							pixel[1] = G;
							pixel[2] = B;
							image.getRaster().setPixel(xx, yy, pixel);
							continue;
						}
						
						int lts = 0;
						for (int xx1 = -1; xx1 < 2; xx1++) {
							for (int yy1 = -1; yy1 < 2; yy1++) {
								double[] l2 = chunk.getRenderLight(BX + xx1, BY + yy1);
								if (l2[0] != 0 ||
										l2[1] != 0 || 
												l2[2] != 0) {
									lts++;
								}
							}
						}
						if (lts == 0) {
							R = MathFunc.lerp(R, R * light[0], light[3]);
							G = MathFunc.lerp(G, G * light[1], light[3]);
							B = MathFunc.lerp(B, B * light[2], light[3]);
							A = 0;
							pixel[0] = R;
							pixel[1] = G;
							pixel[2] = B;
							image.getRaster().setPixel(xx, yy, pixel);
							continue;
						}
						
						if (chunk.getTile(BX, BY) == Tiles.AIR) {
							R = MathFunc.lerp(R, R * light[0], light[3]);
							G = MathFunc.lerp(G, G * light[1], light[3]);
							B = MathFunc.lerp(B, B * light[2], light[3]);
							A = 0;
							pixel[0] = R;
							pixel[1] = G;
							pixel[2] = B;
							image.getRaster().setPixel(xx, yy, pixel);
							continue;
						}
						double LR = light[0];
						double LG = light[1];
						double LB = light[2];
						double LA = light[3];
						int size = Constants.TILESIZE;
						for (int xx1 = -2; xx1 < 2; xx1++) {
							for (int yy1 = -2; yy1 < 2; yy1++) {
								double[] l2 = chunk.getRenderLight(BX + xx1, BY + yy1);
								//(1.0 - dist)
								double dist = MathFunc.distance(RBX * size, RBY * size, (BX + xx1) * size + size / 2, (BY + yy1) * size + size / 2);
								dist /= size;
								if (dist <= 1.5) {
									dist /= 1.5;
									LR = MathFunc.lerp(LR, l2[0], 1.0 - dist);
									LG = MathFunc.lerp(LG, l2[1], 1.0 - dist);
									LB = MathFunc.lerp(LB, l2[2], 1.0 - dist);
								}
								
								
							}
						}
						
						R = MathFunc.lerp(R, R * LR, LA);
						G = MathFunc.lerp(G, G * LG, LA);
						B = MathFunc.lerp(B, B * LB, LA);
						pixel[0] = R;
						pixel[1] = G;
						pixel[2] = B;
						image.getRaster().setPixel(xx, yy, pixel);
					} else {
						int th = 5;
						if (Math.abs(pixel[0] - skyColor[0]) < th && Math.abs(pixel[1] - skyColor[1]) < th && Math.abs(pixel[2] - skyColor[2]) < th) {
							continue;
						}
						
						int x = xx / Constants.TILESIZE;
						int y = yy / Constants.TILESIZE;
						if (lights[x][y][0] == 0 && lights[x][y][1] == 0 && lights[x][y][2] == 0) {
							pixel[0] = 0;
							pixel[1] = 0;
							pixel[2] = 0;
							image.getRaster().setPixel(xx, yy, pixel);
							continue;
						}
						pixel[0] = MathFunc.lerp(pixel[0], pixel[0] * lights[x][y][0], lights[x][y][3]);
						pixel[1] = MathFunc.lerp(pixel[1], pixel[1] * lights[x][y][1], lights[x][y][3]);
						pixel[2] = MathFunc.lerp(pixel[2], pixel[2] * lights[x][y][2], lights[x][y][3]);
						image.getRaster().setPixel(xx, yy, pixel);
					}
					
					
				}
			}
		}
		
		
	}
}
