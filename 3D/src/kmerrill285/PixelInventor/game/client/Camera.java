package kmerrill285.PixelInventor.game.client;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
	public static Vector3f position = new Vector3f(0, 0, 0);
	public static Vector3f rotation = new Vector3f(0, 0, 0);
	
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
	
	public static Vector3f getForward(float PITCH, float YAW) {
		float pitch = PITCH;
		float yaw = YAW - 90;
		return new Vector3f((float)Math.cos(Math.toRadians(yaw)) * (float)Math.cos(Math.toRadians(pitch)), (float)Math.sin(Math.toRadians(pitch)), (float)Math.sin(Math.toRadians(yaw)) * (float)Math.cos(Math.toRadians(pitch)));
	}
}
