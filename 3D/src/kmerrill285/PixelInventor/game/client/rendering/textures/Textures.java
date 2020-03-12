package kmerrill285.PixelInventor.game.client.rendering.textures;

public class Textures {
	
	public static Texture DIRT = loadTexture("PixelInventor", "tiles/dirt");
	public static Texture STONE = loadTexture("PixelInventor", "tiles/stone");
	public static Texture ANTIMATTER = loadTexture("PixelInventor", "tiles/antimatter");

	public static TextureAtlas TILES = new TextureAtlas("PixelInventor", "tiles");
	
	public static Texture loadTexture(String modId, String texture) {
		return new Texture(modId, "textures/"+texture+".png");
	}
	
	public static void load() {
		
	}
}
