package kmerrill285.Inignoto.game.client.rendering.effects.lights;

import org.joml.Vector3f;

public class PointLight {
	public class Attenuation {

		private float constant, linear, exponent;
		
		public Attenuation(float constant, float linear, float exponent) {
			this.constant = constant;
			this.linear = linear;
			this.exponent = exponent;
		}
		
		public Attenuation setConstant(float constant) {
			this.constant = constant;
			return this;
		}
		
		public Attenuation setLinear(float linear) {
			this.linear = linear;
			return this;
		}
		
		public Attenuation setExponent(float exponent) {
			this.exponent = exponent;
			return this;
		}
		
		
		public float getConstant() {
			return constant;
		}

		public float getLinear() {
			return linear;
		}

		public float getExponent() {
			return exponent;
		}
		
	}
	
	private Attenuation attenuation;
	private Vector3f color;
	private Vector3f position;
	private float intensity;
	
	
	public PointLight() {
		attenuation = new Attenuation(1, 0, 0);
		color = new Vector3f(0, 0, 0);
		position = new Vector3f(0, 0, 0);
		intensity = 0;
	}
	
	public Attenuation getAttenuation() 
	{
		return this.attenuation;
	}
	
	public PointLight setColor(Vector3f color) {
		this.color = color;
		return this;
	}
	
	public PointLight setPosition(Vector3f position) {
		this.position = position;
		return this;
	}
	
	public PointLight setIntensity(float intensity) {
		this.intensity = intensity;
		return this;
	}

	public Vector3f getColor() {
		return color;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getIntensity() {
		return intensity;
	}
}
