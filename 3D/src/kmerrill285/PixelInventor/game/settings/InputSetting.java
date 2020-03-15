package kmerrill285.PixelInventor.game.settings;

public class InputSetting {
	private int id;
	private boolean mouse;
	private String name;
	public InputSetting(int id, boolean mouse, String name) {
		this.id = id;
		this.mouse = mouse;
		this.name = name;
	}
	
	public void set(int id, boolean mouse) {
		this.id = id;
		this.mouse = mouse;
	}
	
	public boolean isPressed() {
		if (mouse) return Settings.isMouseButtonDown(id);
		return Settings.isKeyDown(id);
	}
	
	public boolean isJustPressed() {
		if (mouse) return Settings.isMouseButtonJustDown(id);
		return Settings.isKeyJustDown(id);
	}
	
	public String getName() {
		return name;
	}
	
	public String getTranslatedName() {
		return Translation.translateText(this.name);
	}
}
