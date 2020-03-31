package kmerrill285.Inignoto.game.client.rendering.materials;

import org.joml.Vector4f;

public class Material {
	private float reflectance;
	private Vector4f ambientColor;
	private Vector4f diffuseColor;
	private Vector4f specularColor;
	private boolean isTextured;
	public Material() {
		this.ambientColor = new Vector4f(0, 0, 0, 0);
		this.diffuseColor = new Vector4f(0, 0, 0, 0);
		this.specularColor = new Vector4f(0, 0, 0, 0);
	}

	public Material setAmbientColor(Vector4f ambientColor) {
		this.ambientColor = ambientColor;
		return this;
	}
	
	public Material setDiffuseColor(Vector4f diffuseColor) {
		this.diffuseColor = diffuseColor;
		return this;
	}
	
	public Material setSpecularColor(Vector4f specularColor) {
		this.specularColor = specularColor;
		return this;
	}
	
	public Material setTextured(boolean isTextured) {
		this.isTextured = isTextured;
		return this;
	}
	
	public Material setReflectance(float reflectance) {
		this.reflectance = reflectance;
		return this;
	}
	
	public Vector4f getAmbientColor() {
		return this.ambientColor;
	}

	public Vector4f getDiffuseColor() {
		return this.diffuseColor;
	}

	public Vector4f getSpecularColor() {
		return this.specularColor;
	}

	public boolean isTextured() {
		return this.isTextured;
	}

	public float getReflectance() {
		return this.reflectance;
	}
}
