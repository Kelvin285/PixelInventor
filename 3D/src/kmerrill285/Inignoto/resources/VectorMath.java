package kmerrill285.Inignoto.resources;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class VectorMath {
	
	
	public static Vector3f min(Vector3f a, Vector3f b) {
		return new Vector3f((float)Math.min(a.x, b.x), (float)Math.min(a.y, b.y), (float)Math.min(a.z, b.z));
	}
	
	public static Vector3f max(Vector3f a, Vector3f b) {
		return new Vector3f((float)Math.max(a.x, b.x), (float)Math.max(a.y, b.y), (float)Math.max(a.z, b.z));
	}
	
	public static Vector2f intersectBox(Vector3f origin, Vector3f dir, Vector3f b_min, Vector3f b_max) {
		Vector3f tMin = new Vector3f((b_min.x - origin.x) / dir.x, (b_min.y - origin.y) / dir.y, (b_min.z - origin.z) / dir.z);
		Vector3f tMax = new Vector3f((b_max.x - origin.x) / dir.x, (b_max.y - origin.y) / dir.y, (b_max.z - origin.z) / dir.z);
		Vector3f t1 = min(tMin, tMax);
		Vector3f t2 = max(tMin, tMax);
		float tNear = (float)Math.max(Math.max(t1.x, t1.y), t1.z);
		float tFar = (float)Math.min(Math.min(t2.x, t2.y), t2.z);
		return new Vector2f(tNear, tFar);
	}
}
