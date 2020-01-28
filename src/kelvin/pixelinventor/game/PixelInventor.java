package kelvin.pixelinventor.game;

import java.awt.Color;
import java.awt.Graphics;
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
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void update() {
		updateKeys();
		render();
		world.update();
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
			}
		}
		if (Settings.isKeyDown(Settings.ZOOM_OUT)) {
			if (Camera.zoom > 0) {
				Camera.zoom -= 0.1;
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
				chunk.setLight(x, y, Constants.NO_LIGHT);
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
				chunk.setLight(x, y, Constants.WHITE_LIGHT);
				world.reshapeChunk(CX, CY);
				
			}
		}
	}
	
	public void render() {
		
		
		
		Graphics g = image.getGraphics();
		//draw stuff
		g.clearRect(0, 0, image.getWidth(), image.getHeight());
		world.render(g);
		g.setColor(Color.WHITE);
		
		
		g.fillRect(Mouse.getBlockX(), Mouse.getBlockY(), Constants.TILESIZE, 1);
		g.fillRect(Mouse.getBlockX(), Mouse.getBlockY(), 1, Constants.TILESIZE);
		g.fillRect(Mouse.getBlockX(), Mouse.getBlockY()+Constants.TILESIZE, Constants.TILESIZE+1, 1);
		g.fillRect(Mouse.getBlockX()+Constants.TILESIZE, Mouse.getBlockY(), 1, Constants.TILESIZE);

		Random random = new Random();
		double[][][] pixels = new double[image.getWidth()][image.getHeight()][4];
		
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				pixels[x][y] = image.getRaster().getPixel(x, y, pixels[x][y]);
			}
		}
		
		long nanoTime = System.nanoTime();
		
		int border = 2;
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
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
					
					Chunk chunk = world.getChunk(CX, CY);
					if (chunk == null) continue;
					
					//tile distortion
					double distortion = chunk.getDistortion(BX, BY);
					if (distortion > 0)
					{
						
						
						float r = (float)((nanoTime / 50000000.0) * distortion);
						
						double simplex = Constants.noise.GetSimplex((float)X, (float)Y * 2, r);
						double simpley = Constants.noise.GetSimplex((float)X * 2, (float)Y, r);
						
						int xx = (int)(Math.round(simplex));
						int yy = (int)(Math.round(simpley));
						
						if (x + xx > 0 && y + yy > 0 && x + xx < image.getWidth() && y + yy < image.getHeight()) {
							R = MathFunc.lerp(R, pixels[x + xx][y + yy][0], 1);
							G = MathFunc.lerp(G, pixels[x + xx][y + yy][1], 1);
							B = MathFunc.lerp(B, pixels[x + xx][y + yy][2], 1);
							A = MathFunc.lerp(A, pixels[x + xx][y + yy][3], 1);
						}
					}
					
					double[] d2 = new double[]{R, G, B, A};
					image.getRaster().setPixel(x, y, d2);
				}
			}
		}
		
		
		
		
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
