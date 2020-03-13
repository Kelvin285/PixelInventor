package kmerrill285.PixelInventor.resources;

import org.joml.Vector3f;
import org.joml.Vector3i;

import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.world.chunk.TilePos;

public class RayTraceResult {
	public enum RayTraceType {
		TILE, ENTITY, EMPTY
	}
	public enum Direction {
		
		UP(0, 1, 0), DOWN(0, -1, 0), LEFT(-1, 0, 0), RIGHT(1, 0, 0), FRONT(0, 0, -1), BACK(0, 0, 1);
		public final int x, y, z;
		
		Direction(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	private RayTraceType type;
	private TilePos position;
	private Vector3f hit;
	private Direction direction;
	public RayTraceResult(RayTraceType type, TilePos position, Vector3f hit) {
		this.type = type;
		this.position = position;
		this.hit = hit;
		
		if (position != null && hit != null) {
			Vector3f p = new Vector3f(position.x, position.y, position.z);
			Vector3f n = p.sub(hit.x, hit.y, hit.z).mul(-1);
			Direction direction = getDirection(n.x, n.y, n.z);
			this.direction = direction;
		}
	}
	
	private Direction getDirection(double X, double Y, double Z) {
		int x = (int)Math.floor(X);
		int y = (int)Math.floor(Y);
		int z = (int)Math.floor(Z);
		if (Y < 1.0 && Y > 0.0) y = 0;
		if (Z < 1.0 && Z > 0.0) z = 0;
		if (X < 1.0 && X > 0.0) x = 0;
		
		if (x == -1 && y == 0 && z == 0) {
			return Direction.LEFT;
		}
		if (x == 1 && y == 0 && z == 0) {
			return Direction.RIGHT;
		}
		if (x == 0 && y == 1 && z == 0) {
			return Direction.UP;
		}
		if (x == 0 && y == -1 && z == 0) {
			return Direction.DOWN;
		}
		if (x == 0 && y == 0 && z == 1) {
			return Direction.BACK;
		}
		if (x == 0 && y == 0 && z == -1) {
			return Direction.FRONT;
		}
		return null;
	}
	
	public RayTraceType getType() {
		return this.type;
	}
	
	public TilePos getPosition() {
		return this.position;
	}
	
	public Vector3f getHit() {
		return this.hit;
	}

	public Direction getDirection() {
		return this.direction;
	}
}
