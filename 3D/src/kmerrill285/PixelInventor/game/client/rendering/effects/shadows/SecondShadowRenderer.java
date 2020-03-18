package kmerrill285.PixelInventor.game.client.rendering.effects.shadows;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import kmerrill285.PixelInventor.PixelInventor;
import kmerrill285.PixelInventor.game.client.rendering.effects.lights.DirectionalLight;
import kmerrill285.PixelInventor.game.world.World;
import kmerrill285.PixelInventor.resources.Constants;
import kmerrill285.PixelInventor.resources.Utils;

public class SecondShadowRenderer {
	
	
	public static void renderDepthMap(ShadowMap shadowMap, World world) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
		GL11.glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
		
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

		Utils.depth_shader.bind();
		
		Matrix4f lightViewMatrix = getLightViewMatrix(world);
		
		Matrix4f orthoProjMatrix = getOrthoProjectionMatrix();
	
		Utils.depth_shader.setUniformMat4("orthoProjectionMatrix", orthoProjMatrix);
		
		world.renderShadow(Utils.depth_shader, lightViewMatrix);
		
		Utils.depth_shader.unbind();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	public static Matrix4f getLightViewMatrix(World world) {
		return updateLightViewMatrix(world.light.getPosition(), world.light.getDirection());
	}
	
	public static Matrix4f getOrthoProjectionMatrix() {
		float mul = 15f;
		Constants.shadow_far = 100;
		return getOrthoProjectionMatrix(-10 * mul, 10 * mul, -10 * mul, 10 * mul, -1 * mul, 20 * mul);
	}
	
	private static final Matrix4f lightViewMatrix = new Matrix4f();
	
	 public static  Matrix4f updateLightViewMatrix(Vector3f position, Vector3f rotation) {

		 	Vector3f vel = PixelInventor.game.player.velocity;
		 	float vx = 0;
		 	float vy = 0;
		 	float vz = 0;
		 	if (vel != null) {
		 		vx = 0f;
		 		vy = 0;
		 		vz = 0;
		 	}
		 
		 	Matrix4f viewMatrix = lightViewMatrix;
		 	
		    viewMatrix.identity();

		    viewMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
		        .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
		        .rotate((float)Math.toRadians(rotation.z), new Vector3f(0, 0, 1))
		        ;
		    
		    viewMatrix.translate(-(position.x + vx), -(position.y + vy), -(position.z + vz));
		    return viewMatrix;

	    }
	
	public static final Matrix4f getOrthoProjectionMatrix(float left, float right, float bottom, float top, float near, float far) {
		Matrix4f orthoMatrix = new Matrix4f();
	    orthoMatrix.identity();
	    orthoMatrix.setOrtho(left, right, bottom, top, near, far);
	    return orthoMatrix;
	}
}
