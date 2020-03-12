package kmerrill285.PixelInventor.game.tile;

import kmerrill285.PixelInventor.game.settings.Translation;

public class Tile {
	private String name;
	
	private boolean fullCube = true;
	
	public Tile(String name) {
		this.name = name;
		Tiles.REGISTRY.put(this.name, this);
	}
	
	public String getName() {
		return name;
	}
	
	public String getTranslatedName() {
		return Translation.translateText(getName());
	}
	
	public Tile setFullCube(boolean fullCube) {
		this.fullCube = fullCube;
		return this;
	}
	
	public boolean isFullCube() {
		return this.fullCube;
	}
}
