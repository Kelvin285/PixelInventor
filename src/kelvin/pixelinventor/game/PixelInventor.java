package kelvin.pixelinventor.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;

import kelvin.pixelinventor.game.client.renderer.Camera;
import kelvin.pixelinventor.game.client.renderer.Mouse;
import kelvin.pixelinventor.game.tiles.Tiles;
import kelvin.pixelinventor.game.world.Chunk;
import kelvin.pixelinventor.game.world.World;
import kelvin.pixelinventor.game.world.generator.ChunkGenerator;
import kelvin.pixelinventor.util.Constants;
import kelvin.pixelinventor.util.Registry;
import kelvin.pixelinventor.util.events.Events;
import kelvin.pixelinventor.util.events.RegisterTileEvent;
import kelvin.pixelinventor.util.math.MathFunc;



public class PixelInventor {
	
	public static PixelInventor GAME;
	
	
	private JFrame frame;
	private BufferedImage image;
	
	private int FRAME_WIDTH = 1920 / 2, FRAME_HEIGHT = 1080 / 2;
	private final String FRAME_NAME = "PixelInventor";
	
	public World world;
	
	public boolean processing = false;
	
	public Thread thread = new Thread() {
		public void run() {
			processing = true;
			postProcessing();
			processing = false;
		}
	};
		
	public PixelInventor() {
		PixelInventor.GAME = this;
		setup();
	}
	
	public void start() {
		
		frame = new JFrame(FRAME_NAME);
		frame.setSize(getFrameWidth(), getFrameHeight());
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				Settings.keys[e.getKeyCode()] = true;
			}

			@Override
			public void keyReleased(KeyEvent e) {
				Settings.keys[e.getKeyCode()] = false;
			}

			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			
		});
				
		frame.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				Settings.buttons[e.getButton()] = true;
				Mouse.X = (int)(e.getX() * (960.0 / (frame.getWidth())));
				Mouse.Y = (int)(e.getY() * (540.0 / (frame.getHeight())));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				Settings.buttons[e.getButton()] = false;
				Mouse.X = (int)(e.getX() * (960.0 / (frame.getWidth())));
				Mouse.Y = (int)(e.getY() * (540.0 / (frame.getHeight())));
			}
			
		});
		
		frame.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				Mouse.X = (int)(e.getX() * (960.0 / (frame.getWidth())));
				Mouse.Y = (int)(e.getY() * (540.0 / (frame.getHeight())));
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				Mouse.X = (int)(e.getX() * (960.0 / (frame.getWidth())));
				Mouse.Y = (int)(e.getY() * (540.0 / (frame.getHeight())));
			}
			
		});
		
		image = (BufferedImage) frame.createImage((int)(getFrameWidth()), (int)(getFrameHeight()));
		
	}
	
	
	public void setup() {
		start();
		Events.runEvent(RegisterTileEvent.class, Registry.TILES);
		
		world = new World(new ChunkGenerator(new Random().nextLong()));
		
		while (true) {
			update();
			render();
		}
	}
	
	public void update() {
		updateKeys();
		world.update();
	}
	
	public void postProcessing() {
		Random random = new Random();
		double[][][] pixels = new double[image.getWidth()][image.getHeight()][4];
		
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				pixels[x][y] = image.getRaster().getPixel(x, y, pixels[x][y]);
			}
		}
		
		long nanoTime = System.nanoTime();
		
		
		
		int border = 2;
		
		int zx = MathFunc.toZoomedCoordsX(0);
		int zy = MathFunc.toZoomedCoordsY(0);
		
		int wx = MathFunc.toZoomedCoordsX(getFrameWidth());
		int wy = MathFunc.toZoomedCoordsY(getFrameHeight());
		
		P:
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				if (x > zx && y > zy && x < wx && y < wy)
				if (x > border && y > border && x < image.getWidth() - (border + 1) && y < image.getHeight() - (border + 1)) {
					double R = pixels[x][y][0];
					double G = pixels[x][y][1];
					double B = pixels[x][y][2];
					double A = pixels[x][y][3];
					
					double X = (x + Camera.X) * 2;
					double Y = (y + Camera.Y) * 2;
					
					double RX = (x + Camera.X);
					double RY = (y + Camera.Y);
					
					int CX = MathFunc.getChunkPosFor(RX);
					int CY = MathFunc.getChunkPosFor(RY);
					
					int BX = MathFunc.getTilePosFor(RX);
					int BY = MathFunc.getTilePosFor(RY);
					
					BX -= CX * Chunk.SIZE;
					BY -= CY * Chunk.SIZE;
					
					double RBX = RX / Constants.TILESIZE - CX * Chunk.SIZE;
					double RBY = RY / Constants.TILESIZE - CY * Chunk.SIZE;
					
					if (RX < 0) BX-= 1;
					
					Chunk chunk = world.getChunk(CX, CY);
					if (chunk == null) break P;

					
					//tile distortion
					double distortion = chunk.getDistortion(BX, BY);
					
					
					float r = (float)((nanoTime / 50000000.0) * distortion);
					if (distortion > 0 && Settings.distortion == true)
					{
						

						double simplex = Constants.noise.GetSimplex((float)X, (float)Y * 2, r);
						double simpley = Constants.noise.GetSimplex((float)X * 2, (float)Y, r);
						
						
						
						int xx = (int)(Math.round(simplex));
						int yy = (int)(Math.round(simpley));
						
						double t = ((simplex + simpley) * 0.5 + 1) * 0.5;
						if (t > 0.1) t = 1;
						
						if (x + xx > 0 && y + yy > 0 && x + xx < image.getWidth() && y + yy < image.getHeight()) {
							R = MathFunc.lerp(R, pixels[x + xx][y + yy][0], t);
							G = MathFunc.lerp(G, pixels[x + xx][y + yy][1], t);
							B = MathFunc.lerp(B, pixels[x + xx][y + yy][2], t);
							A = MathFunc.lerp(A, pixels[x + xx][y + yy][3], t);
						}
					}
					
					
					A: {
						
						int size = Constants.TILESIZE;
						
						int th = 5;
						int[] skyColor = world.getSkyColor();
						
						if (Math.abs(R - skyColor[0]) < th && Math.abs(G - skyColor[1]) < th && Math.abs(B - skyColor[2]) < th) {
							R = skyColor[0];
							G = skyColor[1];
							B = skyColor[2];
							break A;
						}
						if (Settings.dynamicLights)
						if (Settings.smoothLights) {
							
							
							
							double[] light = chunk.getRenderLight(BX, BY);
							
							int lights = 0;
							for (int xx = -1; xx < 2; xx++) {
								for (int yy = -1; yy < 2; yy++) {
									double[] l2 = chunk.getRenderLight(BX + xx, BY + yy);
									if (l2[0] != 0 ||
											l2[1] != 0 || 
													l2[2] != 0) {
										lights++;
									}
								}
							}
							if (lights == 0) {
								R = MathFunc.lerp(R, R * light[0], light[3]);
								G = MathFunc.lerp(G, G * light[1], light[3]);
								B = MathFunc.lerp(B, B * light[2], light[3]);
								A = 0;
								break A;
							}
							if (chunk.getTile(BX, BY) == Tiles.AIR) {
								R = MathFunc.lerp(R, R * light[0], light[3]);
								G = MathFunc.lerp(G, G * light[1], light[3]);
								B = MathFunc.lerp(B, B * light[2], light[3]);
								A = 0;
								break A;
							}
							double LR = light[0];
							double LG = light[1];
							double LB = light[2];
							double LA = light[3];
							
							for (int xx = -1; xx < 2; xx++) {
								for (int yy = -1; yy < 2; yy++) {
									double[] l2 = chunk.getRenderLight(BX + xx, BY + yy);
									//(1.0 - dist)
									double dist = MathFunc.distance(RBX * size, RBY * size, (BX + xx) * size + size / 2, (BY + yy) * size + size / 2);
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
							A = 0;
						} else {
						
							double[] light = chunk.getRenderLight(BX, BY);
							
							if (light[0] == 0 &&
									light[1] == 0 && 
											light[2] == 0) {
								
							}
							
							R = MathFunc.lerp(R, R * light[0], light[3]);
							G = MathFunc.lerp(G, G * light[1], light[3]);
							B = MathFunc.lerp(B, B * light[2], light[3]);
							A = 0;
						}
						
					}
					
					int th = 5;
					int[] skyColor = world.getSkyColor();
					
					float sr = (float)((nanoTime / 50000000.0));
					
					int height = 5000;
					int space_height = 10000;
					
					
					
					if (Math.abs(R - skyColor[0]) < th && Math.abs(G - skyColor[1]) < th && Math.abs(B - skyColor[2]) < th) {
						int RR = skyColor[0];
						int GG = skyColor[1];
						int BB = skyColor[2];
						if (RY < -space_height) {
							for (int ii = 0; ii < skyColor.length; ii++) {
								skyColor[ii] += (RY + space_height) * 0.01;
								if (skyColor[ii] < 0) skyColor[ii] = 0;
							}
							
						}
						
						R = skyColor[0];
						G = skyColor[1];
						B = skyColor[2];
						
						random.setSeed((int)(RX + RY * RX));
						if (random.nextDouble() > 0.9995) {
							R = RR - (RY + space_height) * 0.01;
							G = GG - (RY + space_height) * 0.01;
							B = BB - (RY + space_height) * 0.01;
							int SR = random.nextInt(255);
							int SG = random.nextInt(255);
							int SB = random.nextInt(255);
							if (R > SR) R = SR;
							if (G > SG) G = SG;
							if (B > SB) B = SB;
							if (R < skyColor[0]) R = skyColor[0];
							if (G < skyColor[1]) G = skyColor[1];
							if (B < skyColor[2]) B = skyColor[2];
						}
						
						
						if (Settings.clouds) {
							double clouds = Constants.noise.GetSimplexFractal((float)X / 25.0f + sr / 10.0f, (float)Y / 10.0f);
							
							
							
							clouds += 1;
							clouds *= 0.5;
							
							if (RY > 0) {
								clouds -= RY * 0.001;
								if (clouds < 0)
									clouds = 0;
							}
							
							
							if (RY < -height) {
								clouds += (RY + height) * 0.001;
								if (clouds < 0)
									clouds = 0;
							}
							
							
							if (clouds > 0.5) {
								R = MathFunc.lerp(R, 255, (clouds - 0.5) * 2);
								G = MathFunc.lerp(G, 255, (clouds - 0.5) * 2);
								B = MathFunc.lerp(B, 255, (clouds - 0.5) * 2);
							}
								
						}
						}
						
					
					double[] d2 = new double[]{R, G, B, A};
					image.getRaster().setPixel(x, y, d2);
				}
			}
		}
	}
	
	public void updateKeys() {
		double moveSpeed = 4.0;
		
		if (Settings.isKeyDown(Settings.UP)) {
			Camera.Y-=moveSpeed;
		}
		if (Settings.isKeyDown(Settings.DOWN)) {
			Camera.Y+=moveSpeed;
		}
		if (Settings.isKeyDown(Settings.LEFT)) {
			Camera.X-=moveSpeed;
		}
		if (Settings.isKeyDown(Settings.RIGHT)) {
			Camera.X+=moveSpeed;
		}
		if (Settings.isKeyDown(Settings.ZOOM_IN)) {
			if (Camera.zoom < 3) {
				Camera.zoom += 0.1;
			} else {
				Camera.zoom = 3;
			}
		}
		if (Settings.isKeyDown(Settings.ZOOM_OUT)) {
			if (Camera.zoom > 0) {
				Camera.zoom -= 0.1;
			} else {
				Camera.zoom = 0;
			}
		}
		
		if (Settings.isMouseButtonDown(Settings.USE)) {
			int BX = Mouse.getBlockX() / Constants.TILESIZE;
			int BY = Mouse.getBlockY() / Constants.TILESIZE;
			
			BX += Math.ceil(Camera.X / Constants.TILESIZE);
			BY += Math.ceil(Camera.Y / Constants.TILESIZE);
			
			int CX = BX / Chunk.SIZE;
			int CY = BY / Chunk.SIZE;

			int x = BX - CX * Chunk.SIZE;
			int y = BY - CY * Chunk.SIZE;

			Chunk chunk = world.getChunk(CX, CY);
			if (chunk != null) {
				chunk.setTile(x, y, Tiles.GRASS);
				world.reshapeChunk(CX, CY);
			}
		}
		
		if (Settings.isMouseButtonDown(Settings.ATTACK)) {
			int BX = Mouse.getBlockX() / Constants.TILESIZE;
			int BY = Mouse.getBlockY() / Constants.TILESIZE;
			
			BX += Math.ceil(Camera.X / Constants.TILESIZE);
			BY += Math.ceil(Camera.Y / Constants.TILESIZE);
			
			int CX = BX / Chunk.SIZE;
			int CY = BY / Chunk.SIZE;

			int x = BX - CX * Chunk.SIZE;
			int y = BY - CY * Chunk.SIZE;

			Chunk chunk = world.getChunk(CX, CY);
			if (chunk != null) {
				chunk.setTile(x, y, Tiles.AIR);
				world.reshapeChunk(CX, CY);
				
			}
		}
	}
	
	int i = 0;
	public void render() {
		i++;
		if (i % Settings.frameSkip > 0) return;
		Graphics g = image.getGraphics();
		//draw stuff
		g.clearRect(0, 0, image.getWidth(), image.getHeight());
		world.render(g);
		g.setColor(Color.WHITE);
		
		
		g.fillRect(Mouse.getBlockX(), Mouse.getBlockY(), Constants.TILESIZE, 1);
		g.fillRect(Mouse.getBlockX(), Mouse.getBlockY(), 1, Constants.TILESIZE);
		g.fillRect(Mouse.getBlockX(), Mouse.getBlockY()+Constants.TILESIZE, Constants.TILESIZE+1, 1);
		g.fillRect(Mouse.getBlockX()+Constants.TILESIZE, Mouse.getBlockY(), 1, Constants.TILESIZE);
		
		postProcessing();
		
		g = frame.getGraphics();
		g.drawImage(image, (int)-(Camera.zoom * frame.getWidth()) / 2, (int)-(Camera.zoom * frame.getHeight()) / 2, frame.getWidth() + (int)(Camera.zoom * frame.getWidth()), frame.getHeight() + (int)(Camera.zoom * frame.getHeight()), null);
		g.dispose();
		
		
	}
	
	public JFrame getFrame() {
		return this.frame;
	}

	public int getFrameWidth() {
		return FRAME_WIDTH;
	}

	public int getFrameHeight() {
		return FRAME_HEIGHT;
	}
}
