package kmerrill285.Inignoto.game.world.chunk.generator.noise;

import org.joml.Vector2f;
import org.joml.Vector2i;

import imported.FastNoise;

public class VoronoiMap {
	private long seed;
	private int size;
	public VoronoiMap(long seed, int size) {
		this.seed = seed;
		this.size = size;
	}
	
	public Vector2f getPointFor(int x, int z, FastNoise noise) {
		x += (noise.GetSimplex(x, z) * size);
		z += (noise.GetSimplex(z, x) * size);
		Vector2f point = new Vector2f((float)Math.floor(x / getSize()) * getSize(), (float)Math.floor(z / getSize()) * getSize());
//		point.x += (noise.GetWhiteNoiseInt((int)point.x, (int)point.y) * getSize());
//		point.y += (noise.GetWhiteNoiseInt((int)point.x, (int)point.y) * getSize());
		
		return point;
	}
	
	public boolean bordering(int x, int z, FastNoise noise, float border_size) {
		Vector2f pos = new Vector2f(x, z);
		double distance = Float.MAX_VALUE;
		Vector2f closest = null;
		for (int xx = -1; xx < 2; xx++) {
			for (int zz = -1; zz < 2; zz++) {
				int X = x + xx * getSize();
				int Z = z + zz * getSize();
				Vector2f p = getPointFor(X, Z, noise);
				if (p.distance(pos) < distance) {
					closest = new Vector2f(p);
					distance = p.distance(x, z);
				}
			}
		}
		for (int xx = -1; xx < 2; xx++) {
			for (int zz = -1; zz < 2; zz++) {
				int X = x + xx * getSize();
				int Z = z + zz * getSize();
				Vector2f p = getPointFor(X, Z, noise);
				if (!closest.equals(p))
				if (Math.abs(closest.distance(x, z) - p.distance(x, z)) <= border_size) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Vector2f getClosestPoint(int x, int z, FastNoise noise) {
		Vector2f pos = new Vector2f(x, z);
		double distance = Float.MAX_VALUE;
		Vector2f closest = null;
		for (int xx = -1; xx < 2; xx++) {
			for (int zz = -1; zz < 2; zz++) {
				int X = x + xx * getSize();
				int Z = z + zz * getSize();
				Vector2f p = getPointFor(X, Z, noise);
				if (p.distance(pos) < distance) {
					closest = new Vector2f(p);
					distance = p.distance(x, z);
				}
			}
		}
		return closest;
	}
	
	public boolean borderingValue(int x, int z, FastNoise noise, float value) {
		float height = getHeightAt(x, z, noise);
		for (int xx = -1; xx < 2; xx++) {
			for (int zz = -1; zz < 2; zz++) {
				Vector2f p = getPointFor(x + xx, z + zz, noise);
				float h2 = getHeightForPoint(p, noise);
				if (height < value && h2 > value || height > value && h2 < value) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int getSign(int x, int z, FastNoise noise) {
		float sign = 1;
		for (int xx = -1; xx < 2; xx++) {
			for (int zz = -1; zz < 2; zz++) {
				int X = x + xx * getSize();
				int Z = z + zz * getSize();
				Vector2f p = getPointFor(X, Z, noise);
				sign += getHeightForPoint(p, noise);
			}
		}
		sign = (int)sign;

		return sign == 0 ? 0 : sign > 0 ? 1 : -1;
	}
	
	public float getTrueHeightForPoint(Vector2f point, FastNoise noise) {
		float n = noise.GetCellular((int)(point.x * size), (int)(point.y * size));
		if ((int)point.x / size == 0 && (int)point.y / size == 0) {
			return Math.abs(n);
		}
		return n;
	}
	
	public float getHeightForPoint(Vector2f point, FastNoise noise) {
//		float x = point.x;
//		float z = point.y;
//		float height = 0;
//		int i = 0;
//		for (int xx = -1; xx < 2; xx++) {
//			for (int zz = -1; zz < 2; zz++) {
//				height += getTrueHeightForPoint(new Vector2f(x + xx * (getSize() / 2), z + zz * (getSize() / 2)), noise);
//				i++;
//			}
//			i++;
//		}
//		height /= i;
//				
//		return height;
		
		return getTrueHeightForPoint(point, noise);
	}
	
	public float getHeightAt(int x, int z, FastNoise noise) {
		Vector2f closest = getClosestPoint(x, z, noise);
		return getHeightForPoint(closest, noise);
	}

	public int getSize() {
		return size;
	}
}
