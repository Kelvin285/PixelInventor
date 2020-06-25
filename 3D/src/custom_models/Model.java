package custom_models;

import java.util.ArrayList;

import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.textures.Texture;

public class Model {
	private ArrayList<Part> parts = new ArrayList<Part>();
	
	private ArrayList<Keyframe> keyframes = new ArrayList<Keyframe>();
	
	public float currentTime = 0.0f;
	public float animationSpeed = 1.0f;
	
	public enum EditMode {
		MODEL, ANIMATION
	}
	
	public EditMode editMode = EditMode.MODEL;
	
	public ArrayList<Part> getParts() {
		return this.parts;
	}
	
	public ArrayList<Keyframe> getKeyframes() {
		return this.keyframes;
	}
	
	public int getAnimationLength() {
		return this.keyframes.size();
	}
	
	public Model copyModel() {
		Model model = new Model();
		for (Part part : parts) {
			if (part.parent == null) {
				Part.copyModelPart(part, null, model);
			}
		}
		for (Keyframe frame : keyframes) {
			model.keyframes.add(frame.copy());
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
