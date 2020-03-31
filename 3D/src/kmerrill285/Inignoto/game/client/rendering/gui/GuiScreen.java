package kmerrill285.Inignoto.game.client.rendering.gui;

import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;

public abstract class GuiScreen {
	protected GuiRenderer renderer;
	public GuiScreen(GuiRenderer renderer) {
		this.renderer = renderer;
	}
	
	public abstract void tick();
	
	public abstract void render(ShaderProgram shader);
	
	public abstract void close();
}
