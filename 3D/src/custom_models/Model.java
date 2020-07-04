package custom_models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.joml.Vector3f;

import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.textures.Texture;

public class Model {

	private ArrayList<Part> parts = new ArrayList<Part>();
	
	private ArrayList<Keyframe> timeline = new ArrayList<Keyframe>();
	
	public float currentTime = 0.0f;
	public float animationSpeed = 1.0f / 30.0f;
	
	public Vector3f rotation = new Vector3f(0, 0, 0);
	public Vector3f translation = new Vector3f(0, 0, 0);
	public Vector3f scale = new Vector3f(1, 1, 1);
	public Vector3f origin = new Vector3f(0.5f, 0.5f, 0.5f);
	
	private boolean playing = false;
	
	private boolean merged = false;
	
	public Model combine(Texture texture) {
		if (merged) return this;
		if (parts.size() > 1) {
			Mesh mesh = parts.get(0).mesh;
			
			if (mesh == null) {
				
				parts.get(0).buildPart(texture);
				mesh = parts.get(0).mesh;
			}
			
			Vector3f offset = new Vector3f(parts.get(0).getPosition()).mul(-1);
			
			for (int i = 1; i < parts.size(); i++) {
				Part part = parts.get(i);
				if (part.mesh == null) {
					part.buildPart(texture);
				}
				
				mesh.combineWith(part.mesh, part.getPosition(), part.getScale(), part.getRotation(), offset);
				
			}
			Part p = parts.get(0);
			parts.clear();
			parts.add(p);
			p.locked = true;
			merged = true;
		}
		
		return this;
	}
	
	public boolean isPlaying() {
		return playing;
	}
	
	public void play(float time) {
		playing = true;
		currentTime = time;
	}
	
	public void stop() {
		playing = false;
	}
	
	public int getCurrentFrame() {
		return (int)currentTime;
	}
	
	public int getNextFrame() {
		int next = (int)currentTime + 1;
		if (next > timeline.size() - 1) {
			next = 0;
		}
		return next;
	}
	
	public float timeUntilNextFrame() {
		return (int)currentTime + 1 - currentTime;
	}
	
	public enum EditMode {
		MODEL, ANIMATION
	}
	
	public EditMode editMode = EditMode.MODEL;
	
	public Model() {
		changeTimelineLength(1);
	}
	
	public ArrayList<Part> getParts() {
		return this.parts;
	}
	
	public ArrayList<Keyframe> getKeyframes() {
		return this.timeline;
	}
	
	public int getAnimationLength() {
		return this.timeline.size();
	}
	
	public void changeTimelineLength(int newValue) {
		if (newValue >= 0) {
			while(getAnimationLength() < newValue) {
				getKeyframes().add(new Keyframe(timeline.size()));
			}
			while (getAnimationLength() > newValue && getAnimationLength() > 0) {
				getKeyframes().remove(getKeyframes().size() - 1);
			}
		}
	}
	
	public static void saveAnimation(ArrayList<Keyframe> timeline, File file) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(timeline);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<Keyframe> loadAnimation(File file) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			@SuppressWarnings("unchecked")
			ArrayList<Keyframe> timeline = (ArrayList<Keyframe>)ois.readObject();
			ois.close();
			return timeline;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<Keyframe>();
	}
	
	public void increaseTimelineLength() {
		
	}
	
	public Model copyModel() {
		Model model = new Model();
		for (Part part : parts) {
			if (part.parent == null) {
				Part.copyModelPart(part, null, model);
			}
		}
		model.timeline.clear();
		for (int i = 0; i < timeline.size(); i++) {
			model.timeline.add(timeline.get(i).copy());
		}
		while(model.timeline.size() > timeline.size()) {
			model.timeline.remove(model.timeline.size() - 1);
		}
		model.editMode = this.editMode;
		
		return model;
	}
	
	public void render(ShaderProgram shader, boolean outlines, Part selected) {
		if (this.currentTime >= this.getKeyframes().size()) {
			this.currentTime -= this.getKeyframes().size();
		}
		if (this.currentTime < 0) {
			this.currentTime += this.getKeyframes().size();
		}
		if (this.isPlaying()) {
			currentTime += this.animationSpeed * this.timeline.get(this.getCurrentFrame()).speed;
		}
		if (this.currentTime >= this.getKeyframes().size()) {
			this.currentTime -= this.getKeyframes().size();
		}
		if (this.currentTime < 0) {
			this.currentTime += this.getKeyframes().size();
		}
		for (Part part : parts) {
			part.renderInverted(shader, outlines, selected);
		}
	}
	
	public void render(ShaderProgram shader, boolean outlines) {
		render(shader, outlines, null);
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
