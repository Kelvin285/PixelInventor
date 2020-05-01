package kmerrill285.Inignoto.game.client.rendering;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL15;

import kmerrill285.Inignoto.Inignoto;
import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.shadows.ShadowRenderer;
import kmerrill285.Inignoto.game.settings.Settings;

public class MeshRenderer {
	
	public static final Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);
	public static final Vector3f rotation = new Vector3f(0, 0, 0);
	
	public static void renderMesh(Mesh mesh, Vector3f position, ShaderProgram shader) {
		renderMesh(mesh, position, scale, shader);
	}
	
	public static void renderMesh(Mesh mesh, Vector3f position, Vector3f scale, ShaderProgram shader) {
		renderMesh(mesh, position, rotation, scale, shader);
	}
	
	public static Matrix4f view = null;
	
	public static void renderMesh(Mesh mesh, Vector3f position, Vector3f rotation, Vector3f scale, ShaderProgram shader) {
		if (mesh == null) return;
		shader.setUniformInt("texture_sampler", 0);
		shader.setUniformMat4("modelMatrix", getModelMatrix(position, rotation, scale));
		shader.setUniformVec3("cameraPos", Camera.position);
		if (Settings.SHADOWS) {
//			uniform mat4 modelLightViewMatrix;
//			uniform mat4 orthoProjectionMatrix;
//			uniform sampler2D shadowMap;
			shader.setUniformMat4("modelLightViewMatrix", getLightMatrix(position, rotation, scale, Inignoto.game.shadowRenderer));
			shader.setUniformMat4("orthoProjectionMatrix", Inignoto.game.shadowRenderer.projectionMatrix);
			glActiveTexture(GL15.GL_TEXTURE1);
	    	glBindTexture(GL_TEXTURE_2D, Inignoto.game.shadowRenderer.fbo.getDepthMapTexture().getTextureId());
	    	glActiveTexture(GL15.GL_TEXTURE2);
	    	glBindTexture(GL_TEXTURE_2D, Inignoto.game.shadowRenderer.fbo1.getDepthMapTexture().getTextureId());
	    	glActiveTexture(GL15.GL_TEXTURE3);
	    	glBindTexture(GL_TEXTURE_2D, Inignoto.game.shadowRenderer.fbo2.getDepthMapTexture().getTextureId());
	    	glActiveTexture(GL15.GL_TEXTURE4);
	    	glBindTexture(GL_TEXTURE_2D, Inignoto.game.shadowRenderer.fbo3.getDepthMapTexture().getTextureId());
			shader.setUniformMat4("secondOrthoMatrix", Inignoto.game.shadowRenderer.projectionMatrix1);
			shader.setUniformMat4("thirdOrthoMatrix", Inignoto.game.shadowRenderer.projectionMatrix2);
			shader.setUniformMat4("fourthOrthoMatrix", Inignoto.game.shadowRenderer.projectionMatrix3);

			shader.setUniformInt("shadowMap", 1);
			shader.setUniformInt("shadowMap2", 2);
			shader.setUniformInt("shadowMap3", 3);
			shader.setUniformInt("shadowMap4", 4);
		}
		
		mesh.render();
	}
	
	public static void renderMesh(Mesh mesh, Vector3f position, ShaderProgram shader, ShadowRenderer renderer) {
		renderMesh(mesh, position, scale, shader, renderer);
	}
	
	public static void renderMesh(Mesh mesh, Vector3f position, Vector3f scale, ShaderProgram shader, ShadowRenderer renderer) {
		renderMesh(mesh, position, rotation, scale, shader, renderer);
	}
	
	public static void renderMesh(Mesh mesh, Vector3f position, Vector3f rotation, Vector3f scale, ShaderProgram shader, ShadowRenderer renderer) {
		
		if (mesh == null) return;
		
		shader.setUniformInt("texture_sampler", 0);
		shader.setUniformVec3("cameraPos", new Vector3f(Inignoto.game.player.lastPos).sub(Camera.getForward().mul(100)));
		shader.setUniformMat4("mvMatrix", getLightMatrix(new Vector3f(position), rotation, scale, renderer));
		mesh.render();
		Inignoto.game.world.updateLight();
		renderer.update(Inignoto.game.world.light.getPosition(), Inignoto.game.world.light.getDirection());
	}
	
	public static Matrix4f getLightMatrix(Vector3f offset, Vector3f rotation, Vector3f scale, ShadowRenderer renderer) {
		Matrix4f modelMatrix = new Matrix4f();
		modelMatrix.identity().translate(offset).
                rotateX((float)Math.toRadians(rotation.x)).
                rotateY((float)Math.toRadians(rotation.y)).
                rotateZ((float)Math.toRadians(rotation.z)).
                scale(scale.x, scale.y, scale.z);
		Matrix4f view = new Matrix4f(renderer.viewMatrix);
        return view.mul(modelMatrix);
    }
	
	public static Matrix4f getModelMatrix(Vector3f offset, Vector3f rotation, Vector3f scale) {
		Matrix4f modelMatrix = new Matrix4f();
		modelMatrix.identity().translate(offset).
                rotateX((float)Math.toRadians(rotation.x)).
                rotateY((float)Math.toRadians(rotation.y)).
                rotateZ((float)Math.toRadians(rotation.z)).
                scale(scale.x, scale.y, scale.z);
		Matrix4f view = new Matrix4f(Camera.getViewMatrix());
        return view.mul(modelMatrix);
    }
}
