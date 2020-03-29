package kmerrill285.PixelInventor.game.client;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import imported.RayCamera;
import kmerrill285.PixelInventor.PixelInventor;
import kmerrill285.PixelInventor.game.tile.Tile.TileRayTraceType;
import kmerrill285.PixelInventor.resources.MathHelper;
import kmerrill285.PixelInventor.resources.RayTraceResult;

public class Camera {
	public static Vector3f position = new Vector3f(0, 0, 0);
	public static Vector3f rotation = new Vector3f(0, 0, 0);
	
	public static Vector3f shadowPosition = new Vector3f(0, 0, 0);
	public static Vector3f shadowRotation = new Vector3f(0, 0, 0);
	
	public static RayTraceResult currentTile;
	
	public static final float BASE_REACH = 6.0f;
	public static float REACH_DISTANCE = BASE_REACH;
	
	public static void update() {
		if (rotation.x < -89) rotation.x = -89;
		if (rotation.x > 89) rotation.x = 89;
		
		PixelInventor game = PixelInventor.game;
		if (game.world != null) {
			currentTile = game.world.rayTraceTiles(position, getForward(rotation.x * -1, rotation.y).mul(REACH_DISTANCE).add(position), TileRayTraceType.SOLID);
		}
		
		RayCamera camera = PixelInventor.game.raytracer.getCamera();
		
		camera.setPosition(MathHelper.toJavaxVector(position));
		camera.setLookAt(camera.getPosition(), MathHelper.toJavaxVector(getForward().add(position)), MathHelper.toJavaxVector(new Vector3f(0, 1, 0)));
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
	
	public static Vector3f getForward() {
		return getForward(-rotation.x, rotation.y);
	}
	
	public static Vector3f getForward(float PITCH, float YAW) {
		float pitch = PITCH;
		float yaw = YAW - 90;
		return new Vector3f((float)Math.cos(Math.toRadians(yaw)) * (float)Math.cos(Math.toRadians(pitch)), (float)Math.sin(Math.toRadians(pitch)), (float)Math.sin(Math.toRadians(yaw)) * (float)Math.cos(Math.toRadians(pitch)));
	}
}
