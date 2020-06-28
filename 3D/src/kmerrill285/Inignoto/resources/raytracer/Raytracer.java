package kmerrill285.Inignoto.resources.raytracer;

import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Raytracer {

	public static Vector3f rotateAroundY(Vector3f a, Vector3f b, float theta) {
		float x = (float) (Math.cos(theta) * (a.x - b.x) + Math.sin(theta) * (a.z - b.z) + b.x);
		float z = (float) (Math.sin(theta) * (a.x - b.x) - Math.cos(theta) * (a.z - b.z) + b.z);
		
		return new Vector3f(x, a.y, z);
	}

	public static Vector3f rotateAroundX(Vector3f a, Vector3f b, float theta) {
		float y = (float) (Math.cos(theta) * (a.y - b.y) + Math.sin(theta) * (a.z - b.z) + b.y);
		float z = (float) (Math.sin(theta) * (a.y - b.y) - Math.cos(theta) * (a.z - b.z) + b.z);
		
		return new Vector3f(a.x, y, z);
	}

	public static Vector3f rotateAroundZ(Vector3f a, Vector3f b, float theta) {
		float y = (float) (Math.cos(theta) * (a.y - b.y) + Math.sin(theta) * (a.x - b.x) + b.y);
		float x = (float) (Math.sin(theta) * (a.y - b.y) - Math.cos(theta) * (a.x - b.x) + b.x);
		
		return new Vector3f(x, y, a.z);
	}

	public static Vector3f rotateAround(Vector3f a, Vector3f b, Vector3f theta) {
		Vector3f r1 = rotateAroundX(a, b, theta.x);
		Vector3f r2 = rotateAroundY(r1, b, theta.y);
		Vector3f r3 = rotateAroundZ(r2, b, theta.z);
		return r3;
	}
	
	public static Vector3f rotateAround(Vector3f a, Vector3f b, Quaternionf theta) {
		float x = a.x - b.x;
		float y = a.y - b.y;
		float z = a.z - b.z;
		Vector3f vec = new Vector3f(x, y, z);
		vec.rotate(theta);
		vec.x += b.x;
		vec.y += b.y;
		vec.z += b.z;
		return vec;
	}
	
	public static Vector3f rotateDir(Vector3f a, Quaternionf theta) {
		Vector3f b = new Vector3f(0, 0, 0);
		float x = a.x - b.x;
		float y = a.y - b.y;
		float z = a.z - b.z;
		Vector3f vec = new Vector3f(x, y, z);
		vec.rotate(theta);
		vec.x += b.x;
		vec.y += b.y;
		vec.z += b.z;
		return vec;
	}

	public static Vector3f rotateDirY(Vector3f dir, float theta) {
		return rotateAroundY(dir, new Vector3f(0.0f, 0.0f, 0.0f), theta);
	}

	public static Vector3f rotateDirX(Vector3f dir, float theta) {
		return rotateAroundX(dir, new Vector3f(0.0f, 0.0f, 0.0f), theta);
	}

	public static Vector3f rotateDirZ(Vector3f dir, float theta) {
		return rotateAroundZ(dir, new Vector3f(0.0f, 0.0f, 0.0f), theta);
	}

	public static Vector3f rotateDir(Vector3f dir, Vector3f theta) {
		return rotateAround(dir, new Vector3f(0.0f, 0.0f, 0.0f), theta);
	}

	public static RayIntersection intersectBox(Vector3f origin, Vector3f dir, final RayBox b, Quaternionf rotation) {
		  Vector3f center = new Vector3f(b.min).add(b.max).div(2.0f);
		  
		  Vector3f o2 = rotateAround(origin, center, rotation);
		  Vector3f d2 = rotateDir(dir, rotation);
		  RayIntersection i2 = intersectBox(o2, d2, b);
		  
		  return i2;
		}
	
	public static boolean doesCollisionOccur(Vector3f origin, Vector3f dir, final RayBox b, Quaternionf rotation) {
		RayIntersection i = intersectBox(origin, dir, b, rotation);
		Vector2f l = i.lambda;
		  if (l.x > 0.0 && l.x < l.y) {
			  return true;
		  }
		return false;
	}
	
	public static RayIntersection intersectBox(Vector3f origin, Vector3f dir, final RayBox b, Vector3f rotation) {
	  Vector3f center = new Vector3f(b.min).add(b.max).div(2.0f);
	  
	  Vector3f o2 = rotateAround(origin, center, rotation);
	  Vector3f d2 = rotateDir(dir, rotation);
	  RayIntersection i2 = intersectBox(o2, d2, b);
	  
	  return i2;
	}
	
	public static boolean doesCollisionOccur(Vector3f origin, Vector3f dir, final RayBox b, Vector3f rotation) {
		RayIntersection i = intersectBox(origin, dir, b, rotation);
		Vector2f l = i.lambda;
		  if (l.x > 0.0 && l.x < l.y) {
			  return true;
		  }
		return false;
	}
	
	public static Vector3f min(Vector3f a, Vector3f b) {
		return new Vector3f(a.x < b.x ? a.x : b.x, a.y < b.y ? a.y : b.y, a.z < b.z ? a.z : b.z);
	}
	
	public static Vector3f max(Vector3f a, Vector3f b) {
		return new Vector3f(a.x > b.x ? a.x : b.x, a.y > b.y ? a.y : b.y, a.z > b.z ? a.z : b.z);
	}
	
	public static Vector2f min(Vector2f a, Vector2f b) {
		return new Vector2f(a.x < b.x ? a.x : b.x, a.y < b.y ? a.y : b.y);
	}
	
	public static Vector2f max(Vector2f a, Vector2f b) {
		return new Vector2f(a.x > b.x ? a.x : b.x, a.y > b.y ? a.y : b.y);
	}
	
	public static float max(float a, float b) {
		return a > b ? a : b;
	}
	
	public static float min(float a, float b) {
		return a < b ? a : b;
	}
	
	public static float bias = 0.005f;

	public static RayIntersection intersectBox(Vector3f origin, Vector3f dir, final RayBox b) {
	  Vector3f tMin = new Vector3f(b.min).sub(origin).div(dir);
	  Vector3f tMax = new Vector3f(b.max).sub(origin).div(dir);
	  Vector3f t1 = min(tMin, tMax);
	  Vector3f t2 = max(tMin, tMax);
	  float tNear = max(max(t1.x, t1.y), t1.z);
	  float tFar = min(min(t2.x, t2.y), t2.z);
	  
	  Vector3f hitnear = new Vector3f(origin).add(new Vector3f(dir).mul(tNear));
	  Vector3f normal = new Vector3f(0.0f, 0.0f, 0.0f);
		  	//left
		  	if (hitnear.x >= b.min.x - bias && hitnear.x <= b.min.x + bias) {
		  		normal = new Vector3f(-1, 0, 0);
		  	}
		  	//bottom
		  	if (hitnear.y >= b.min.y - bias && hitnear.y <= b.min.y + bias) {
		  		normal = new Vector3f(0, -1, 0);
		  	}
		  	//front
		  	if (hitnear.z >= b.min.z - bias && hitnear.z <= b.min.z + bias) {
		  		normal = new Vector3f(0, 0, -1);
		  	}
		  	//right
		  	if (hitnear.x >= b.max.x - bias && hitnear.x <= b.max.x + bias) {
		  		normal = new Vector3f(1, 0, 0);
		  	}
		  	//top
		  	if (hitnear.y >= b.max.y - bias && hitnear.y <= b.max.y + bias) {
		  		normal = new Vector3f(0, 1, 0);
		  	}
		  	//back
		  	if (hitnear.z >= b.max.z - bias && hitnear.z <= b.max.z + bias) {
		  		normal = new Vector3f(0, 0, 1);
		  	}
		  	
	   //top, bottom, left, right, front, back
		    Vector2f texStart = new Vector2f(0, 0);
		    Vector2f texEnd = new Vector2f(0, 0);
		    Vector2f texCurrent = new Vector2f(0, 0);
		  	//left
		  	if (normal.x == -1) {
		  		texStart = new Vector2f(b.min.z, b.min.y);
		  		texEnd = new Vector2f(b.max.z, b.max.y);
		  		texCurrent = new Vector2f(hitnear.z, hitnear.y);
		  	}
		  	//bottom
		  	if (normal.y == -1) {
		  		texStart = new Vector2f(b.min.x, b.min.z);
		  		texEnd = new Vector2f(b.max.x, b.max.z);
		  		texCurrent = new Vector2f(hitnear.x, hitnear.z);
		  	}
		  	//front
		  	if (normal.z == -1) {
		  		texStart = new Vector2f(b.min.x, b.min.y);
		  		texEnd = new Vector2f(b.max.x, b.max.y);
		  		texCurrent = new Vector2f(hitnear.x, hitnear.y);
		  	}
		  	//right
		  	if (normal.x == 1) {
		  		texStart = new Vector2f(b.min.z, b.min.y);
		  		texEnd = new Vector2f(b.max.z, b.max.y);
		  		texCurrent = new Vector2f(hitnear.z, hitnear.y);
		  	}
		  	//top
		  	if (normal.y == 1) {
		  		texStart = new Vector2f(b.min.x, b.min.z);
		  		texEnd = new Vector2f(b.max.x, b.max.z);
		  		texCurrent = new Vector2f(hitnear.x, hitnear.z);
		  	}
		  	//back
		  	if (normal.z == 1) {
		  		texStart = new Vector2f(b.min.x, b.min.y);
		  		texEnd = new Vector2f(b.max.x, b.max.y);
		  		texCurrent = new Vector2f(hitnear.x, hitnear.y);
		  	}
	  
	  RayIntersection i = new RayIntersection(new Vector2f(tNear, tFar), normal, texStart, texEnd, texCurrent);
	  return i;
	}
}
