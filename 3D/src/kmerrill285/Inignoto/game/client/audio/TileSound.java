package kmerrill285.Inignoto.game.client.audio;

public class TileSound {

	public static int[] dirt;
	public static int[] grass;
	public static int[] stone;
	public static int[] leaves;
	public static int[] snow;
	public static int[] wood;
	
	private static int loadSound(String modId, String sound) {
		return Sounds.loadSound(modId, "tiles/"+sound);
	}
	
	public static void init() {
		dirt = new int[] {loadSound("Inignoto", "dirt_0.wav"), loadSound("Inignoto", "dirt_1.wav")};
		grass = new int[] {loadSound("Inignoto", "grass_0.wav"), loadSound("Inignoto", "grass_1.wav"), loadSound("Inignoto", "grass_2.wav")};
		snow = new int[] {loadSound("Inignoto", "snow_0.wav"), loadSound("Inignoto", "snow_1.wav"), loadSound("Inignoto", "snow_2.wav"), loadSound("Inignoto", "snow_3.wav"), loadSound("Inignoto", "snow_4.wav"), loadSound("Inignoto", "snow_5.wav")};
		stone = new int[] {loadSound("Inignoto", "stone_0.wav"), loadSound("Inignoto", "stone_1.wav"), loadSound("Inignoto", "stone_2.wav"), loadSound("Inignoto", "stone_3.wav"), loadSound("Inignoto", "stone_4.wav")};
		wood = new int[] {loadSound("Inignoto", "wood_0.wav"), loadSound("Inignoto", "wood_1.wav"), loadSound("Inignoto", "wood_2.wav"), loadSound("Inignoto", "wood_3.wav"), loadSound("Inignoto", "wood_4.wav")};
	}
}
