package kmerrill285.Inignoto.game.settings;

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

	public void read(String b) {
		String[] data = b.split(",");
		if (data.length == 2) {
			set(Integer.parseInt(data[0].replace(" ","").trim()), Boolean.parseBoolean(data[1].replace(" ", "").trim()));
		}
	}

	public String write() {
		return id+","+mouse;
	}
}
