package custom_models;

import java.util.ArrayList;

import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.textures.Texture;

public class Model {
	private ArrayList<Part> parts = new ArrayList<Part>();
		
	public ArrayList<Part> getParts() {
		return this.parts;
	}
	
	public Model copyModel() {
		Model model = new Model();
		for (Part part : parts) {
			if (part.parent == null) {
				Part.copyModelPart(part, null, model);
			}
		}
		return model;
	}
	
	public void render(ShaderProgram shader, boolean outlines, Part selected) {
		for (Part part : parts) {
			part.renderInverted(shader, outlines, selected);
		}
	}
	
	public void render(ShaderProgram shader) {
		for (Part part : parts) {
			part.renderInverted(shader);
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
