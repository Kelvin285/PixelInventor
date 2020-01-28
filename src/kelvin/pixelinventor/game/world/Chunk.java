package kelvin.pixelinventor.game.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import kelvin.pixelinventor.game.PixelInventor;
import kelvin.pixelinventor.game.client.renderer.Camera;
import kelvin.pixelinventor.game.client.renderer.ChunkRenderer;
import kelvin.pixelinventor.game.tiles.Tile;
import kelvin.pixelinventor.game.tiles.Tiles;
import kelvin.pixelinventor.util.Constants;
import kelvin.pixelinventor.util.math.MathFunc;

public class Chunk {
	public static final int SIZE = 4;
	private int X, Y;
	
	private BufferedImage image;
	
	private Tile[][] tiles;
	private int[][][] states;
	private double[][] fluidDistortion;
	private double[][][] lights;
	private double[][][] renderLights;
	
	private boolean rerender = true;
	
	private World world;
	private Random random;
	
	public Chunk(int x, int y, World world, Random random) {
		this.X = x;
		this.Y = y;
		this.world = world;
		tiles = new Tile[Chunk.SIZE][Chunk.SIZE];
		states = new int[Chunk.SIZE][Chunk.SIZE][2];
		fluidDistortion = new double[Chunk.SIZE][Chunk.SIZE];
		lights = new double[Chunk.SIZE][Chunk.SIZE][4];
		renderLights = new double[Chunk.SIZE][Chunk.SIZE][4];
		image = (BufferedImage)PixelInventor.GAME.getFrame().createImage(Constants.TILESIZE * Chunk.SIZE, Constants.TILESIZE * Chunk.SIZE);
		this.random = random;
	}
	
	public void markForRerender() {
		rerender = true;
	}
	
	public void update() {
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int y = 0; y < Chunk.SIZE; y++) {
				Tile tile = tiles[x][y];
				if (random.nextDouble() < tile.getUpdatePercent()) {
					tile.update(x, y, this);
				}
			}
		}
	}
	
	public void render(Graphics g) {
		if (rerender) {
			recalculateLights();
			rerender = false;
			ChunkRenderer.render(tiles, states, renderLights, world.getSkyColor(), this, image);
		}
		g.drawImage(image, X * Chunk.SIZE * Constants.TILESIZE - (int)Camera.X, Y * Chunk.SIZE * Constants.TILESIZE - (int)Camera.Y, Chunk.SIZE * Constants.TILESIZE, Chunk.SIZE * Constants.TILESIZE, null);
	}
	
	public int getX() {
		return this.X;
	}
	
	public int getY() {
		return this.Y;
	}

	public Tile[][] getTiles() {
		return tiles;
	}
	
	public int[][][] getStates() {
		return states;
	}
	
	public double[][] getFluidDistortion() {
		return fluidDistortion;
	}
	
	public double[][][] getLights() {
		return lights;
	}
	
	public double[][][] getRenderLights() {
		return renderLights;
	}
	
	public void recalculateNextLights() {
		int step = 3;
		double density = (1.0 / 9.0) * 0.5;
		
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int y = 0; y < Chunk.SIZE; y++) {
				
				if (tiles[x][y] == Tiles.AIR) {
					renderLights[x][y][0] = 1;
					renderLights[x][y][1] = 1;
					renderLights[x][y][2] = 1;
					renderLights[x][y][3] = 1;
					continue;
				}
				
				double[] light = lights[x][y];
				double R = light[0];
				double G = light[1];
				double B = light[2];
				double A = light[3];
				
				
				
				
				for (int xx = -step; xx < step + 1; xx++) {
					for (int yy = -step; yy < step + 1; yy++) {
						double[] l2 = getLight(x + xx, y + yy);
						
						if (l2[0] > R) {
							R = MathFunc.lerp(R, l2[0], density);
						}
						
						if (l2[1] > G) {
							G = MathFunc.lerp(G, l2[1], density);
						}
						
						if (l2[2] > B) {
							B = MathFunc.lerp(R, l2[2], density);
						}
						
						if (l2[3] > A) {
							A = MathFunc.lerp(A, l2[3], density);
						}
					}
				}
				
				
				
				renderLights[x][y][0] = R;
				renderLights[x][y][1] = G;
				renderLights[x][y][2] = B;
				renderLights[x][y][3] = A;
				
			}
		}
	}
	
	public void recalculateLights() {
		int step = 2;
		for (int x = getX() - step; x < getX() + step + 1; x++) {
			for (int y = getY() - step; y < getY() + step + 1; y++) {
				if (x != getX() && y != getY()) {
					Chunk chunk = world.getChunk(x, y);
					if (chunk != null) {
						chunk.recalculateNextLights();
					}
				} else {
					recalculateNextLights();
				}
			}
		}
		
	}
	public double[] getRenderLight(int x, int y) {
		int X = 0;
		int Y = 0;
		if (x < 0) {
			x += Chunk.SIZE;
			X = -1;
		}
		if (x > Chunk.SIZE - 1) {
			x -= Chunk.SIZE;
			X = 1;
		}
		
		if (y < 0) {
			y += Chunk.SIZE;
			Y = -1;
		}
		if (y > Chunk.SIZE - 1) {
			y -= Chunk.SIZE;
			Y = 1;
		}
		
		if (X != 0 || Y != 0) {
			Chunk c2 = world.getChunk(getX() + X, getY() + Y);
			if (c2 != null) {
				return c2.getRenderLight(x, y);
			}
		}
		
		if (x < 0 || y < 0 || x > Chunk.SIZE - 1 || y > Chunk.SIZE - 1) {
			return Constants.NO_LIGHT;
		}
		
		return renderLights[x][y];
	}

	public double[] getLight(int x, int y) {
		int X = 0;
		int Y = 0;
		if (x < 0) {
			x += Chunk.SIZE;
			X = -1;
		}
		if (x > Chunk.SIZE - 1) {
			x -= Chunk.SIZE;
			X = 1;
		}
		
		if (y < 0) {
			y += Chunk.SIZE;
			Y = -1;
		}
		if (y > Chunk.SIZE - 1) {
			y -= Chunk.SIZE;
			Y = 1;
		}
		
		if (X != 0 || Y != 0) {
			Chunk c2 = world.getChunk(getX() + X, getY() + Y);
			if (c2 != null) {
				return c2.getLight(x, y);
			}
		}
		
		if (x < 0 || y < 0 || x > Chunk.SIZE - 1 || y > Chunk.SIZE - 1) {
			return Constants.NO_LIGHT;
		}
		
		return lights[x][y];
	}
	
	
	public void setLight(int x, int y, double[] light) {
		
		int X = 0;
		int Y = 0;
		if (x < 0) {
			x += Chunk.SIZE;
			X = -1;
		}
		if (x > Chunk.SIZE - 1) {
			x -= Chunk.SIZE;
			X = 1;
		}
		
		if (y < 0) {
			y += Chunk.SIZE;
			Y = -1;
		}
		if (y > Chunk.SIZE - 1) {
			y -= Chunk.SIZE;
			Y = 1;
		}
		
		if (X != 0 || Y != 0) {
			Chunk c2 = world.getChunk(getX() + X, getY() + Y);
			if (c2 != null) {
				c2.setLight(x, y, light);
				return;
			}
		}
		
		if (x < 0 || y < 0 || x > Chunk.SIZE - 1 || y > Chunk.SIZE - 1) {
			return;
		}
		lights[x][y] = light;
		markForRerender();
	}
	
	public double getDistortion(int x, int y) {
		int X = 0;
		int Y = 0;
		if (x < 0) {
			x += Chunk.SIZE;
			X = -1;
		}
		if (x > Chunk.SIZE - 1) {
			x -= Chunk.SIZE;
			X = 1;
		}
		
		if (y < 0) {
			y += Chunk.SIZE;
			Y = -1;
		}
		if (y > Chunk.SIZE - 1) {
			y -= Chunk.SIZE;
			Y = 1;
		}
		
		if (X != 0 || Y != 0) {
			Chunk c2 = world.getChunk(getX() + X, getY() + Y);
			if (c2 != null) {
				return c2.getDistortion(x, y);
			}
		}
		
		if (x < 0 || y < 0 || x > Chunk.SIZE - 1 || y > Chunk.SIZE - 1) {
			return 0;
		}
		return fluidDistortion[x][y];
	}
	
	public void setDistortion(int x, int y, double distorted) {
		
		int X = 0;
		int Y = 0;
		if (x < 0) {
			x += Chunk.SIZE;
			X = -1;
		}
		if (x > Chunk.SIZE - 1) {
			x -= Chunk.SIZE;
			X = 1;
		}
		
		if (y < 0) {
			y += Chunk.SIZE;
			Y = -1;
		}
		if (y > Chunk.SIZE - 1) {
			y -= Chunk.SIZE;
			Y = 1;
		}
		
		if (X != 0 || Y != 0) {
			Chunk c2 = world.getChunk(getX() + X, getY() + Y);
			if (c2 != null) {
				c2.setDistortion(x, y, distorted);
				return;
			}
		}
		
		if (x < 0 || y < 0 || x > Chunk.SIZE - 1 || y > Chunk.SIZE - 1) {
			return;
		}
		fluidDistortion[x][y] = distorted;
	}
	
	public int[] getState(int x, int y) {
		int X = 0;
		int Y = 0;
		if (x < 0) {
			x += Chunk.SIZE;
			X = -1;
		}
		if (x > Chunk.SIZE - 1) {
			x -= Chunk.SIZE;
			X = 1;
		}
		
		if (y < 0) {
			y += Chunk.SIZE;
			Y = -1;
		}
		if (y > Chunk.SIZE - 1) {
			y -= Chunk.SIZE;
			Y = 1;
		}
		
		if (X != 0 || Y != 0) {
			Chunk c2 = world.getChunk(getX() + X, getY() + Y);
			if (c2 != null) {
				return c2.getState(x, y);
			}
		}
		
		if (x < 0 || y < 0 || x > Chunk.SIZE - 1 || y > Chunk.SIZE - 1) {
			return Constants.DEFAULT_STATE;
		}
		return states[x][y];
	}
	
	public void setState(int x, int y, int[] state) {
		setState(x, y, state[0], state[1]);
	}
	
	public void setState(int x, int y, int sx, int sy) {
		
		int X = 0;
		int Y = 0;
		if (x < 0) {
			x += Chunk.SIZE;
			X = -1;
		}
		if (x > Chunk.SIZE - 1) {
			x -= Chunk.SIZE;
			X = 1;
		}
		
		if (y < 0) {
			y += Chunk.SIZE;
			Y = -1;
		}
		if (y > Chunk.SIZE - 1) {
			y -= Chunk.SIZE;
			Y = 1;
		}
		
		if (X != 0 || Y != 0) {
			Chunk c2 = world.getChunk(getX() + X, getY() + Y);
			if (c2 != null) {
				c2.setState(x, y, sx, sy);
				return;
			}
		}
		
		if (x < 0 || y < 0 || x > Chunk.SIZE - 1 || y > Chunk.SIZE - 1) {
			return;
		}
		states[x][y][0] = sx;
		states[x][y][1] = sy;
		markForRerender();
	}
	
	public Tile getTile(int x, int y) {
		int X = 0;
		int Y = 0;
		if (x < 0) {
			x += Chunk.SIZE;
			X = -1;
		}
		if (x > Chunk.SIZE - 1) {
			x -= Chunk.SIZE;
			X = 1;
		}
		
		if (y < 0) {
			y += Chunk.SIZE;
			Y = -1;
		}
		if (y > Chunk.SIZE - 1) {
			y -= Chunk.SIZE;
			Y = 1;
		}
		
		if (X != 0 || Y != 0) {
			Chunk c2 = world.getChunk(getX() + X, getY() + Y);
			if (c2 != null) {
				return c2.getTile(x, y);
			}
		}
		
		if (x < 0 || y < 0 || x > Chunk.SIZE - 1 || y > Chunk.SIZE - 1) {
			return Tiles.AIR;
		}
		return tiles[x][y];
	}
	
	public void setTile(int x, int y, Tile tile) {
		
		int X = 0;
		int Y = 0;
		if (x < 0) {
			x += Chunk.SIZE;
			X = -1;
		}
		if (x > Chunk.SIZE - 1) {
			x -= Chunk.SIZE;
			X = 1;
		}
		
		if (y < 0) {
			y += Chunk.SIZE;
			Y = -1;
		}
		if (y > Chunk.SIZE - 1) {
			y -= Chunk.SIZE;
			Y = 1;
		}
		
		if (X != 0 || Y != 0) {
			Chunk c2 = world.getChunk(getX() + X, getY() + Y);
			if (c2 != null) {
				c2.setTile(x, y, tile);
				return;
			}
		}
		
		if (x < 0 || y < 0 || x > Chunk.SIZE - 1 || y > Chunk.SIZE - 1) {
			return;
		}
		
		tiles[x][y] = tile;
		fluidDistortion[x][y] = tile.getDistortionFactor(random);
		lights[x][y] = tiles[x][y].getLightValue();
		reshape();
		markForRerender();
		
	}

	public void reshape() {
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int y = 0; y < Chunk.SIZE; y++) {
				if (tiles[x][y] != Tiles.AIR) {
					if (tiles[x][y] == null) tiles[x][y] = Tiles.AIR;
					int state = tiles[x][y].getShape(this, x, y);
					
					states[x][y][0] = state;
					states[x][y][1] = 0;
				}
			}
		}
	}

	
	
}
