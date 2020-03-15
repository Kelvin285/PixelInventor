package kmerrill285.PixelInventor.game.client.rendering.gui;

import org.joml.Vector4f;

import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.client.rendering.textures.Textures;

public class InventoryScreen extends GuiScreen {

	public InventoryScreen(GuiRenderer renderer) {
		super(renderer);
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render(ShaderProgram shader) {
		int width = Textures.INVENTORY.getWidth() * 3;
		int height = Textures.INVENTORY.getHeight() * 3;
		renderer.drawTexture(Textures.INVENTORY, 1920 / 2 - width / 2, 1080 / 2 - height / 2, width, height, 0, new Vector4f(1, 1, 1, 1));
	}

	@Override
	public void close() {
		
	}

}
