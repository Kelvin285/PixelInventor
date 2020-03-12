package kmerrill285.PixelInventor.game.client.rendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import kmerrill285.PixelInventor.game.client.Camera;
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
		shader.setUniformMat4("modelMatrix", getModelMatrix(position, rotation, scale));
		mesh.render();
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
