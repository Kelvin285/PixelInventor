package kmerrill285.Inignoto.game.world.chunk.generator.biome.properties;

public enum BiomeTemperature {
	EXTREME_HEAT(2), HOT(1), WARM(0), COOL(-1), COLD(-2), FREEZING(-3);
	
	private final int value;
	BiomeTemperature(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static BiomeTemperature getTemperature(float temp) {
		if (temp <= 0.2f && temp >= -0.2f) return WARM;
		if (temp <= -0.75f) return FREEZING;
		if (temp <= -0.5f) return COLD;
		if (temp <= -0.2f) return COOL;
		if (temp >= 0.5f) return EXTREME_HEAT;
		if (temp >= 0.2f) return HOT;
		return WARM;
	}
}
