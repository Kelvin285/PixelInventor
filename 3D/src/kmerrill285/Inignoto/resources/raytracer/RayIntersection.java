package kmerrill285.Inignoto.resources.raytracer;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class RayIntersection {
	public RayIntersection(Vector2f lambda, Vector3f normal, Vector2f texStart, Vector2f texEnd,
			Vector2f texCurrent) {
		this.lambda = lambda;
		this.normal = normal;
		this.texStart = texStart;
		this.texEnd = texEnd;
		this.texCurrent = texCurrent;
	}
	public Vector2f lambda = new Vector2f();
	public Vector3f normal = new Vector3f();
	public Vector2f texStart = new Vector2f();
	public Vector2f texEnd = new Vector2f();
	public Vector2f texCurrent = new Vector2f();
}