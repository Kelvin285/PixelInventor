package kmerrill285.Inignoto.game.client.rendering.textures;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Fonts {
	public static Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 48);
	public static Font BOLD = new Font(Font.MONOSPACED, Font.BOLD, 48);

	public static int fontHeight = 0;
	
	public static Map<Character, Texture> chars = new HashMap<>();
	public static Map<Character, Texture> bold = new HashMap<>();

	public static void loadFonts() {
		int height = 0;
		
		for (int i = 32; i < 256; i++) {
			if (i == 127) continue;
			char c = (char)i;
			createImageFromChar(FONT, c, true, chars);
			createImageFromChar(BOLD, c, true, bold);
		}
		fontHeight = height;
	}
	
	private static void createImageFromChar(Font font, char c, boolean antiAliasing, Map<Character, Texture> chars) {
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		if (antiAliasing) {
		    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics();
		g.dispose();
		
		int charWidth = metrics.charWidth(c);
		int charHeight = metrics.getHeight();
		
		image = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
		if (antiAliasing) {
		    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.setFont(font);
		g.setPaint(Color.WHITE);
		g.drawString(String.valueOf(c), 0, metrics.getAscent());
		g.dispose();
		FontTexture img = new FontTexture(image);
		chars.put(c, img.texture);
	}
	
	public static void dispose() {
		for (Character c : chars.keySet()) {
			chars.get(c).dispose();
		}
	}
}
