package kelvin.pixelinventor.game;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;

import kelvin.pixelinventor.game.client.renderer.Camera;
import kelvin.pixelinventor.game.world.World;
import kelvin.pixelinventor.game.world.generator.ChunkGenerator;
import kelvin.pixelinventor.util.Registry;
import kelvin.pixelinventor.util.events.Events;
import kelvin.pixelinventor.util.events.RegisterTileEvent;

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
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
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
		
		image = (BufferedImage) frame.createImage(FRAME_WIDTH * 2, FRAME_HEIGHT * 2);
		
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
	}
	
	public void render() {
		Graphics g = image.getGraphics();
		//draw stuff
		g.clearRect(0, 0, image.getWidth(), image.getHeight());
		
		world.render(g);
		
		g = frame.getGraphics();
		g.drawImage(image, 0, 0, frame.getWidth(), frame.getHeight(), null);
		g.dispose();
	}
	
	public JFrame getFrame() {
		return this.frame;
	}
}
