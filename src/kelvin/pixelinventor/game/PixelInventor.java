package kelvin.pixelinventor.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
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
	
	private int FRAME_WIDTH = ((1920 / 2) / Constants.TILESIZE) * Constants.TILESIZE, FRAME_HEIGHT = ((1080 / 2) / Constants.TILESIZE) * Constants.TILESIZE;
	private final String FRAME_NAME = "PixelInventor";
	
	public World world;
	
	public boolean processing = false;
	
	
		
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
	
	private Random random = new Random();
	private double[][][] pixels;
	private double[] d2 = new double[4];
	private int postprocessing_border = 2;
	private double[] PIXEL = new double[4];
	private int zx;
	private int zy;
	private int wx;
	private int wy;
	WritableRaster raster;
	
	public void resetPixels(int x1, int y1, int w, int h) {
		if (pixels == null)
		pixels = new double[image.getWidth()][image.getHeight()][4];
		
		for (int x = x1; x < x1 + w; x++) {
			for (int y = y1; y < y1 + h; y++) {
				if (x >= 0 && y >= 0 && x < image.getWidth() - 1 && y < image.getHeight() - 1)
				pixels[x][y] = image.getRaster().getPixel(x, y, pixels[x][y]);
			}
		}
		PIXEL[0] = 0;
		PIXEL[1] = 0;
		PIXEL[2] = 0;
		PIXEL[3] = 0;
		zx = MathFunc.toZoomedCoordsX(0);
		zy = MathFunc.toZoomedCoordsY(0);
		
		wx = MathFunc.toZoomedCoordsX(getFrameWidth());
		wy = MathFunc.toZoomedCoordsY(getFrameHeight());
		
		raster = image.getRaster();
		RGBA[0] = 0;
		RGBA[1] = 0;
		RGBA[2] = 0;
		RGBA[3] = 0;
	}
	
	private double[] RGBA = new double[4];
	public void doTileDistortion(int x, int y, double X, double Y, int BX, int BY, double RBX, double RBY,  double R, double G, double B, double A, long nanoTime, Chunk chunk) {
		double aa = RBX - BX;
		double bb = RBY - BY;
		int AA = (int)(aa * Constants.TILESIZE);
		int BB = (int)(bb * Constants.TILESIZE);

		if (AA == 0 || BB == 0 || AA == Constants.TILESIZE - 1 || BB == Constants.TILESIZE - 1) return;
		//tile distortion
		double distortion = chunk.getDistortion(BX, BY);
		if (distortion > 0 && Settings.distortion == true)
		{
			float r = (float)((nanoTime / 50000000.0) * distortion);

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
		RGBA[0] = R;
		RGBA[1] = G;
		RGBA[2] = B;
		RGBA[3] = A;
	}
	
	public void calculateLights(double R, double G, double B, double A, double RX, double CX, double RY, double CY, Chunk chunk) {
		{
			
			int size = Constants.TILESIZE;
			
			int th = 5;
			int[] skyColor = world.getSkyColor();
			
			if (Math.abs(R - skyColor[0]) < th && Math.abs(G - skyColor[1]) < th && Math.abs(B - skyColor[2]) < th) {
				R = skyColor[0];
				G = skyColor[1];
				B = skyColor[2];
				RGBA[0] = R;
				RGBA[1] = G;
				RGBA[2] = B;
				RGBA[3] = 0;
				return;
			}
			BufferedImage lightImage = chunk.getLightImage();
			int xx = (int)(RX - CX * Chunk.SIZE * Constants.TILESIZE);
			int yy = (int)(RY - CY * Chunk.SIZE * Constants.TILESIZE);
			
			if (xx < 0) {
				xx += chunk.getLightImage().getWidth();
			}
			
			if (yy < 0) {
				yy += chunk.getLightImage().getHeight();
			}
			
			if (xx >= 0 && yy >= 0 && xx < lightImage.getWidth() && yy < lightImage.getHeight()) {
				PIXEL = lightImage.getRaster().getPixel(xx, yy, PIXEL);
				PIXEL[0] = ((int)PIXEL[0] / (256 - Settings.lightCelValues)) * (256 - Settings.lightCelValues);
				PIXEL[1] = ((int)PIXEL[1] / (256 - Settings.lightCelValues)) * (256 - Settings.lightCelValues);
				PIXEL[2] = ((int)PIXEL[2] / (256 - Settings.lightCelValues)) * (256 - Settings.lightCelValues);
				R *= PIXEL[0] / 255.0;
				G *= PIXEL[1] / 255.0;
				B *= PIXEL[2] / 255.0;
			}
		}
		RGBA[0] = R;
		RGBA[1] = G;
		RGBA[2] = B;
		RGBA[3] = 0;
	}
	
	public void doSkyEffects(double R, double G, double B, double A, long nanoTime, double RX, double RY, double X, double Y) {
		int th = 5;
		int[] skyColor = world.getSkyColor();
		
		if (MathFunc.abs(R - skyColor[0]) < th && MathFunc.abs(G - skyColor[1]) < th && MathFunc.abs(B - skyColor[2]) < th) {

			float sr = (float)((nanoTime / 50000000.0));
			
			int height = 5000;
			int space_height = 10000;
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
			double xx = RX + sr * 0.00000025;
			random.setSeed((int)(xx + RY * xx));
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
				double clouds = Constants.noise.GetSimplexFractal((float)X / 25.0f + sr / 10.0f, (float)Y / 10.0f, sr * 0.01f);
				
				
				
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
		RGBA[0] = R;
		RGBA[1] = G;
		RGBA[2] = B;
		RGBA[3] = 0;
	}
	
	public void postProcessing(Chunk chunk, int x1, int y1, int w, int h) {
		
		if (x1 + w < 0 || x1 > image.getWidth()) {
			return;
		}
		
		if (y1 + h < 0 || y1 > image.getHeight()) {
			return;
		}
		
		
		long nanoTime = System.nanoTime();
		resetPixels(x1, y1, w, h);
		
		for (int x = x1; x < x1 + w; x++) {
			for (int y = y1; y < y1 + h; y++) {
				if (x < 0 || y < 0 || x > image.getWidth() - 1 || y > image.getHeight() - 1) continue;
				
				if (x > zx && y > zy && x < wx && y < wy)
				if (x > postprocessing_border && y > postprocessing_border && x < image.getWidth() - (postprocessing_border + 1) && y < image.getHeight() - (postprocessing_border + 1)) {
					
					double R = pixels[x][y][0];
					double G = pixels[x][y][1];
					double B = pixels[x][y][2];
					double A = pixels[x][y][3];
					
					double RX = (x + Camera.X);
					double RY = (y + Camera.Y);
					
					double X = RX * 2;
					double Y = RY * 2;
					
					int CX = MathFunc.getChunkPosFor(RX);
					int CY = MathFunc.getChunkPosFor(RY);
					
					int BX = MathFunc.getTilePosFor(RX);
					int BY = MathFunc.getTilePosFor(RY);
					
					double RBX = RX / 16 - CX * Chunk.SIZE;
					double RBY = RY / 16 - CY * Chunk.SIZE;
					
					BX -= CX * Chunk.SIZE;
					BY -= CY * Chunk.SIZE;

					
//					if (BX == 0 || BY == 0)
////					chunk = world.getChunk(CX, CY);
//					if (chunk == null) continue;
					
					RGBA[0] = R;
					RGBA[1] = G;
					RGBA[2] = B;
					RGBA[3] = A;
					
					this.doTileDistortion(x, y, X, Y, BX, BY, RBX, RBY, R, G, B, A, nanoTime, chunk);
					R = RGBA[0];
					G = RGBA[1];
					B = RGBA[2];
					A = RGBA[3];
					this.calculateLights(R, G, B, A, RX, CX, RY, CY, chunk);
					R = RGBA[0];
					G = RGBA[1];
					B = RGBA[2];
					A = RGBA[3];
					this.doSkyEffects(R, G, B, A, nanoTime, RX, RY, X, Y);
					
					R = RGBA[0];
					G = RGBA[1];
					B = RGBA[2];
					A = RGBA[3];
					
					d2[0] = R;
					d2[1] = G;
					d2[2] = B;
					d2[3] = A;
					
					raster.setSample(x, y, 0, R);
					raster.setSample(x, y, 1, G);
					raster.setSample(x, y, 2, B);
				}
			}
		}
	}
	
	
	public void updateKeys() {
		double moveSpeed = 16;
		
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
				chunk.setTile(x, y, Tiles.ANTIMATTER);
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
		
		resetPixels(0, 0, image.getWidth(), image.getHeight());
		
		Graphics g = image.getGraphics();
		//draw stuff
		g.clearRect(0, 0, image.getWidth(), image.getHeight());
		world.render(g);
		g.setColor(Color.WHITE);
		
		
		g.fillRect(Mouse.getBlockX(), Mouse.getBlockY(), Constants.TILESIZE, 1);
		g.fillRect(Mouse.getBlockX(), Mouse.getBlockY(), 1, Constants.TILESIZE);
		g.fillRect(Mouse.getBlockX(), Mouse.getBlockY()+Constants.TILESIZE, Constants.TILESIZE+1, 1);
		g.fillRect(Mouse.getBlockX()+Constants.TILESIZE, Mouse.getBlockY(), 1, Constants.TILESIZE);
		
//		postProcessing();
		
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
