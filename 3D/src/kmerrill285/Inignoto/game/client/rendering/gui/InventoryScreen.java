package kmerrill285.Inignoto.game.client.rendering.gui;

import org.joml.Vector4f;

import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.textures.Textures;

public class InventoryScreen extends GuiScreen {

	public InventoryScreen(GuiRenderer renderer) {
		super(renderer);
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render(ShaderProgram shader) {
		int width = Textures.INVENTORY.getWidth() * 4;
		int height = Textures.INVENTORY.getHeight() * 4;
		renderer.drawNormalTexture(Textures.INVENTORY, 1920 / 2 - width / 2, 1080 / 2 - height / 2, width, height, 0, new Vector4f(1, 1, 1, 1));
	}

	@Override
	public void close() {
		
	}

}
