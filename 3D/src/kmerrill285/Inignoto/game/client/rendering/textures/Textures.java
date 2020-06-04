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
	public static Texture HEALTHBAR = loadTexture("Inignoto", "gui/healthbar");
	public static Texture HEALTHBAR_BACK = loadTexture("Inignoto", "gui/healthbar_back");
	public static Texture HEALTH_ICON = loadTexture("Inignoto", "gui/health_icon");
	public static Texture[] TITLE_BACKGROUND = new Texture[] {
			loadTexture("Inignoto", "gui/title_background/sprite_0"),
			loadTexture("Inignoto", "gui/title_background/sprite_1"),
			loadTexture("Inignoto", "gui/title_background/sprite_2"),
			loadTexture("Inignoto", "gui/title_background/sprite_3")
	};
	public static Texture INIGNOTO = loadTexture("Inignoto", "gui/title_background/inignoto");
	public static Texture MOON = loadTexture("Inignoto", "gui/title_background/moon");
	public static Texture MOON_CLIP = loadTexture("Inignoto", "gui/title_background/moon_clip");

	public static Texture MODELER = loadTexture("Inignoto", "gui/modeler");
	public static Texture BACK_ARROW = loadTexture("Inignoto", "gui/back_arrow");
	public static Texture COLOR_PICKER = loadTexture("Inignoto", "modelmaker/color_picker");
	public static Texture DUPLICATE = loadTexture("Inignoto", "modelmaker/duplicate");
	public static Texture FILL = loadTexture("Inignoto", "modelmaker/fill");
	public static Texture GRID = loadTexture("Inignoto", "modelmaker/grid");
	public static Texture MOVE = loadTexture("Inignoto", "modelmaker/move");
	public static Texture PAINTBRUSH = loadTexture("Inignoto", "modelmaker/paintbrush");
	public static Texture POINTER = loadTexture("Inignoto", "modelmaker/pointer");
	public static Texture ROTATE = loadTexture("Inignoto", "modelmaker/rotate");
	public static Texture SCALE = loadTexture("Inignoto", "modelmaker/scale");
	public static Texture TRASH = loadTexture("Inignoto", "modelmaker/trash");
	public static Texture POSITION_HANDLE_X = loadTexture("Inignoto", "modelmaker/position_handle_x");
	public static Texture POSITION_HANDLE_Y = loadTexture("Inignoto", "modelmaker/position_handle_y");
	public static Texture POSITION_HANDLE_Z = loadTexture("Inignoto", "modelmaker/position_handle_z");
	public static Texture GRAY_MATERIAL = loadTexture("Inignoto", "modelmaker/gray_material");
	
	public static TextureAtlas TILES = new TextureAtlas("Inignoto", "tiles");
	
	public static String MINING_LOCATION = "Inignoto:mining";
	
	public static Texture loadTexture(String modId, String texture) {
		return new Texture(modId, "textures/"+texture+".png");
	}
	
	public static void load() {
//		TILES.addToImage("Inignoto","gui","mining");
	}
}
