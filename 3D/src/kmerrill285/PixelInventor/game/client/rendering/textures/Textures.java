package kmerrill285.PixelInventor.game.client.rendering.textures;

public class Textures {
	
	public static Texture TILE_SELECTION = loadTexture("PixelInventor", "gui/tile_selection");
	public static Texture FP_CURSOR = loadTexture("PixelInventor", "gui/fp_cursor");
	public static Texture VIGINETTE = loadTexture("PixelInventor", "gui/viginette");
	public static Texture HOTBAR = loadTexture("PixelInventor", "gui/hotbar");
	public static Texture INVENTORY = loadTexture("PixelInventor", "gui/inventory");
	public static Texture HOTBAR_SELECTED = loadTexture("PixelInventor", "gui/hotbar_selected");
	public static Texture INVENTORY_SELECTED = loadTexture("PixelInventor", "gui/inventory_selected");
	public static Texture WHITE_SQUARE = loadTexture("PixelInventor", "gui/white_square");

	
	public static TextureAtlas TILES = new TextureAtlas("PixelInventor", "tiles");
	
	public static String MINING_LOCATION = "PixelInventor:mining";
	
	public static Texture loadTexture(String modId, String texture) {
		return new Texture(modId, "textures/"+texture+".png");
	}
	
	public static void load() {
//		TILES.addToImage("PixelInventor","gui","mining");
	}
}
