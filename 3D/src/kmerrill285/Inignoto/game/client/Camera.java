package kmerrill285.Inignoto.game.client;

import java.awt.Polygon;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import kmerrill285.Inignoto.Inignoto;
import kmerrill285.Inignoto.game.tile.Tile.TileRayTraceType;
import kmerrill285.Inignoto.resources.RayTraceResult;
import kmerrill285.Inignoto.resources.Utils;

public class Camera {
	public static Vector3f position = new Vector3f(0, 0, 0);
	public static Vector3f rotation = new Vector3f(0, 0, 0);
	
	public static Vector3f shadowPosition = new Vector3f(0, 0, 0);
	public static Vector3f shadowRotation = new Vector3f(0, 0, 0);
	
	public static RayTraceResult currentTile;
	
	public static final float BASE_REACH = 6.0f;
	public static float REACH_DISTANCE = BASE_REACH;
	
	public static Polygon frustum = new Polygon();
	public static Polygon downFrustum = new Polygon();
	
	public static void update() {
//		System.out.println(position.x + ", " + position.y + ", " + position.z);
		if (rotation.x < -89) rotation.x = -89;
		if (rotation.x > 89) rotation.x = 89;
		
		Inignoto game = Inignoto.game;
		if (game.world != null) {
			currentTile = game.world.rayTraceTiles(position, getForward(rotation.x * -1, rotation.y).mul(REACH_DISTANCE).add(position), TileRayTraceType.SOLID);
		}
		
		if (!position.isFinite()) {
			position = new Vector3f(0.0f);
		}
		if (!rotation.isFinite()) {
			rotation = new Vector3f(0.0f);
		}
		frustum.reset();
		downFrustum.reset();
		Vector3f far = getForward().mul(Utils.Z_FAR);
		Vector3f near = getForward().mul(Utils.Z_NEAR);
		Vector3f right = getRight().mul(Utils.FRAME_WIDTH);
		Vector3f fl = new Vector3f(far).sub(right);
		Vector3f fr = new Vector3f(far).add(right);
		Vector3f nl = new Vector3f(near).sub(right);
		Vector3f nr = new Vector3f(near).add(right);
		
		
		Vector3f nright = getRight().mul(Utils.Z_FAR);
		Vector3f ul = getUp().mul(Utils.Z_FAR).sub(nright);
		Vector3f ur = getUp().mul(Utils.Z_FAR).add(nright);
		Vector3f dl = getDown().mul(Utils.Z_FAR).sub(nright);
		Vector3f dr = getDown().mul(Utils.Z_FAR).add(nright);
		
		frustum.addPoint((int)position.x + (int)nl.x, (int)position.z + (int)nl.z);
		frustum.addPoint((int)position.x + (int)fl.x, (int)position.z + (int)fl.z);
		frustum.addPoint((int)position.x + (int)fr.x, (int)position.z + (int)fr.z);
		frustum.addPoint((int)position.x + (int)nr.x, (int)position.z + (int)nr.z);
		
		downFrustum.addPoint((int)position.x + (int)ul.x, (int)position.z + (int)ul.z);
		downFrustum.addPoint((int)position.x + (int)ur.x, (int)position.z + (int)ur.z);
		downFrustum.addPoint((int)position.x + (int)dr.x, (int)position.z + (int)dr.z);
		downFrustum.addPoint((int)position.x + (int)dl.x, (int)position.z + (int)dl.z);
	}
	
	public static Matrix4f getViewMatrix() {
		Matrix4f viewMatrix = new Matrix4f();

	    viewMatrix.identity();

	    viewMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
	        .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
	        .rotate((float)Math.toRadians(rotation.z), new Vector3f(0, 0, 1))
	        ;
	    
	    viewMatrix.translate(-position.x, -position.y, -position.z);
	    return viewMatrix;
	}
	
	public static Vector3f getUp() {
		return getForward(-(rotation.x + 90), rotation.y);
	}
	
	public static Vector3f getDown() {
		return getForward(-(rotation.x - 90), rotation.y);
	}
	
	public static Vector3f getForward() {
		return getForward(-rotation.x, rotation.y);
	}
	
	public static Vector3f getRight() {
		return getForward(0, rotation.y + 90);
	}
	
	public static Vector3f getForward(float PITCH, float YAW) {
		float pitch = PITCH;
		float yaw = YAW - 90;
		return new Vector3f((float)Math.cos(Math.toRadians(yaw)) * (float)Math.cos(Math.toRadians(pitch)), (float)Math.sin(Math.toRadians(pitch)), (float)Math.sin(Math.toRadians(yaw)) * (float)Math.cos(Math.toRadians(pitch)));
	}
}
