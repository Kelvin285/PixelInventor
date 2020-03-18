package kmerrill285.PixelInventor.game.client.rendering;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL13;

import kmerrill285.PixelInventor.PixelInventor;
import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.rendering.effects.shadows.SecondShadowRenderer;
import kmerrill285.PixelInventor.game.client.rendering.effects.shadows.ShadowRenderer;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;

public class MeshRenderer {
	
	public static final Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);
	public static final Vector3f rotation = new Vector3f(0, 0, 0);
	
	public static void renderMesh(Mesh mesh, Vector3f position, ShaderProgram shader) {
		renderMesh(mesh, position, scale, shader);
	}
	
	public static void renderMesh(Mesh mesh, Vector3f position, Vector3f scale, ShaderProgram shader) {
		renderMesh(mesh, position, rotation, scale, shader);
	}
	
	public static void renderMesh(Mesh mesh, Vector3f position, Vector3f rotation, Vector3f scale, ShaderProgram shader) {
		shader.setUniformInt("texture_sampler", 0);
		shader.setUniformInt("shadowMap", 1);
		shader.setUniformInt("secondShadowMap", 2);
		shader.setUniformMat4("modelMatrix", getModelMatrix(position, rotation, scale));
		
		
		shader.setUniformMat4("orthoProjectionMatrix", ShadowRenderer.getOrthoProjectionMatrix());
		shader.setUniformMat4("secondOrthoMatrix", SecondShadowRenderer.getOrthoProjectionMatrix());
		shader.setUniformMat4("modelLightViewMatrix", getLightMatrix(position, rotation, scale, ShadowRenderer.getLightViewMatrix(PixelInventor.game.world)));
		
		if (mesh.isSetup()) {
			glActiveTexture(GL13.GL_TEXTURE1);
	    	glBindTexture(GL_TEXTURE_2D, PixelInventor.game.shadowMap.getDepthMapTexture().getTextureId());
	    	
	    	glActiveTexture(GL13.GL_TEXTURE2);
	    	glBindTexture(GL_TEXTURE_2D, PixelInventor.game.secondShadowMap.getDepthMapTexture().getTextureId());
		}
		mesh.render();
	}
	
	public static void renderShadowMesh(Mesh mesh, Vector3f position, ShaderProgram shader, Matrix4f viewMatrix) {
		renderShadowMesh(mesh, position, scale, shader, viewMatrix);
	}
	
	public static void renderShadowMesh(Mesh mesh, Vector3f position, Vector3f scale, ShaderProgram shader, Matrix4f viewMatrix) {
		renderShadowMesh(mesh, position, rotation, scale, shader, viewMatrix);
	}
		
	public static void renderShadowMesh(Mesh mesh, Vector3f position, Vector3f rotation, Vector3f scale, ShaderProgram shader, Matrix4f viewMatrix) {
		shader.setUniformMat4("modelMatrix", getLightMatrix(position, rotation, scale, viewMatrix));
		mesh.render();
	}
	
	public static Matrix4f getLightMatrix(Vector3f offset, Vector3f rotation, Vector3f scale, Matrix4f viewMatrix) {
		Matrix4f modelMatrix = new Matrix4f();
		modelMatrix.identity().translate(offset).
                rotateX((float)Math.toRadians(rotation.x)).
                rotateY((float)Math.toRadians(rotation.y)).
                rotateZ((float)Math.toRadians(rotation.z)).
                scale(scale.x, scale.y, scale.z);
		Matrix4f view = new Matrix4f(viewMatrix);
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
