import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {
		new Main();
	}
	
	float radius = 125;
	
	public Main() {
		JFrame frame = new JFrame("Polar map");
		frame.setSize(1000, 1000);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		FastNoise noise = new FastNoise();
		
		float circumference = (float)Math.PI * 2 * radius;
		float diameter = 2 * radius;
		
		BufferedImage image = new BufferedImage((int)(diameter * 2), (int)(diameter * 2), BufferedImage.TYPE_INT_RGB);
		
		
		Graphics g = image.getGraphics();
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		
		int length = image.getWidth();
		for (float x1 = 0; x1 < length; x1+=0.5f) {
			for (float y1 = 0; y1 < length; y1+=0.5f) {
				
				int x = (int)x1;
				int y = (int)y1;
				
				float lat = (x1 / (float)length) * 360.0f;
				float lon = (y1 / (float)length) * 360.0f;
				
				float NOISE = noise.GetPerlin(lat * 4, lon * 3);
				
				int col = (int)(NOISE * 255);
				col = Math.max(0, Math.min(255, col));
				
				float NOISE2 = noise.GetPerlin(lat, lon * 5);
				
				int col2 = (int)(NOISE2 * 255);
				col2 = Math.max(0, Math.min(255, col2));
				
				Color color = new Color(col2, Math.min(255, col + col2), Math.min(255, 255 - col + col2));
				
				float third = 360.0f / 3.0f;
				if (lon <= third) {
					float dist = (lon / third);
					float rad = (float)Math.toRadians(lat);
					
					image.setRGB((int)(radius + Math.cos(rad) * dist * radius), (int)(diameter * 1.5f + Math.sin(rad) * dist * radius), color.getRGB());
				}
				else
				if (lon >= third * 2) {
					float dist = 1.0f - (lon - third * 2) / third;
					float rad = (float)Math.toRadians(lat + 180);
					
					image.setRGB((int)(radius + diameter + Math.cos(rad) * dist * radius), (int)(diameter * 1.5f + Math.sin(rad) * dist * radius), color.getRGB());
				} else {
					float dist = (lon - third) / third;
					float rad = (float)Math.toRadians(lat);
					int X = (int)(length - (x + length * 0.25f) % length);
					X = Math.max(0, Math.min(length - 1, X));
					image.setRGB(X, (int)(dist * diameter), color.getRGB());
				}
			}
		}
		
		g = frame.getGraphics();
		g.drawImage(image, 9, 9, frame.getWidth() - 18, frame.getHeight() - 18, null);
		g.dispose();
		
		try {
			ImageIO.write(image, "png", new File("out.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
