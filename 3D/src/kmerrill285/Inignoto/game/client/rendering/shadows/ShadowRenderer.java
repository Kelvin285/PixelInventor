package kmerrill285.Inignoto.game.client.rendering.shadows;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.rendering.postprocessing.FrameBuffer;
import kmerrill285.Inignoto.resources.Utils;

public class ShadowRenderer {
	public FrameBuffer fbo;
	public FrameBuffer fbo1;
	public FrameBuffer fbo2;
	public FrameBuffer fbo3;

	public ShadowRenderer() {
		projectionMatrix = new Matrix4f();
		projectionMatrix1 = new Matrix4f();
		projectionMatrix2 = new Matrix4f();
		projectionMatrix3 = new Matrix4f();

		viewMatrix = new Matrix4f();
		try {
			fbo = new FrameBuffer(1024, 1024);
			fbo1 = new FrameBuffer(1024, 1024);
			fbo2 = new FrameBuffer(1024, 1024);
			fbo3 = new FrameBuffer(1024, 1024);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Matrix4f projectionMatrix;
	public Matrix4f projectionMatrix1;
	public Matrix4f projectionMatrix2;
	public Matrix4f projectionMatrix3;

	public Matrix4f viewMatrix;
	
	public void bind(int cascade) {
		if (cascade == 0) {
			fbo.bind();

		}
		if (cascade == 1) {
			fbo1.bind();
		}
		if (cascade == 2) {
			fbo2.bind();
		}
		if (cascade == 3) {
			fbo3.bind();
		}
	}
	
	public void unbind(int cascade) {
		if (cascade == 0) {
			fbo.unbind();
		}
		if (cascade == 1) {
			fbo1.unbind();
		}
		if (cascade == 2) {
			fbo2.unbind();
		}
		if (cascade == 3) {
			fbo3.unbind();
		}
	}
	
	public void update(Vector3f position, Vector3f rotation) {
		updateProjection();
		updateViewMatrix(position, rotation);
	}
	
	private void updateProjection() {
		projectionMatrix.identity();
		projectionMatrix.setOrtho(-10, 10, -10, 10, -10, 10);
		
		projectionMatrix1.identity();
		projectionMatrix1.setOrtho(-20, 20, -20, 20, -20, 20);
		
		projectionMatrix2.identity();
		projectionMatrix2.setOrtho(-40, 40, -40, 40, -40, 40);
		
		projectionMatrix3.identity();
		projectionMatrix3.setOrtho(-80, 80, -80, 80, -80, 80);
	}
	
	private void updateViewMatrix(Vector3f position, Vector3f rotation) {
		viewMatrix.identity();
		
	    viewMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
	        .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
	        .rotate((float)Math.toRadians(rotation.z), new Vector3f(0, 0, 1))
	        ;
	    
	    viewMatrix.translate(-position.x, -position.y, -position.z);
	}
	
	
	
	
	public void dispose() {
		unbind(0);
		unbind(1);
		unbind(2);
		unbind(3);
		fbo.dispose();
		fbo1.dispose();
		fbo2.dispose();
		fbo3.dispose();
	}
}
