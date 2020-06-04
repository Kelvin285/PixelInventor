package custom_models;

import java.util.ArrayList;

import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.textures.Texture;

public class Model {
	private ArrayList<Part> parts = new ArrayList<Part>();
	
	public ArrayList<Part> getParts() {
		return this.parts;
	}
	
	public void render(ShaderProgram shader) {
		for (Part part : parts) {
			part.render(shader);
		}
	}
	
	public void changeTexture(Texture texture) {
		for (Part part : parts) {
			part.changeTexture(texture);
		}
	}
	
	public void dispose() {
		for (Part part : parts) {
			part.dispose();
		}
	}
}
