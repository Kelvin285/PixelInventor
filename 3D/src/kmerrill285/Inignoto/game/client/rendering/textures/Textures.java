package kmerrill285.Inignoto.game.client.rendering.textures;

public class Textures {
	
	public static Texture TILE_SELECTION = loadTexture("Inignoto", "gui/tile_selection");
	public static Texture FP_CURSOR = loadTexture("Inignoto", "gui/fp_cursor");
	public static Texture VIGINETTE = loadTexture("Inignoto", "gui/viginette");
	public static Texture HOTBAR = loadTexture("Inignoto", "gui/hotbar");
	public static Texture INVENTORY = loadTexture("Inignoto", "gui/inventory");
	public static Texture HOTBAR_SELECTED = loadTexture("Inignoto", "gui/hotbar_selected");
	public static Texture INVENTORY_SELECTED = loadTexture("Inignoto", "gui/inventory_selected");
	public static Texture WHITE_SQUARE = loadTexture("Inignoto", "gui/white_square");

	
	public static TextureAtlas TILES = new TextureAtlas("Inignoto", "tiles");
	
	public static String MINING_LOCATION = "Inignoto:mining";
	
	public static Texture loadTexture(String modId, String texture) {
		return new Texture(modId, "textures/"+texture+".png");
	}
	
	public static void load() {
//		TILES.addToImage("Inignoto","gui","mining");
	}
}
