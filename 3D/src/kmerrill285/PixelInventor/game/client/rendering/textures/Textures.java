package kmerrill285.PixelInventor.game.client.rendering.textures;

public class Textures {
	
	public static Texture TILE_SELECTION = loadTexture("PixelInventor", "gui/tile_selection");
	public static Texture FP_CURSOR = loadTexture("PixelInventor", "gui/fp_cursor");
	public static Texture VIGINETTE = loadTexture("PixelInventor", "gui/viginette");

	
	public static TextureAtlas TILES = new TextureAtlas("PixelInventor", "tiles");
	
	public static Texture loadTexture(String modId, String texture) {
		return new Texture(modId, "textures/"+texture+".png");
	}
	
	public static void load() {
		
	}
}
