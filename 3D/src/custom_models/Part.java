package custom_models;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;

import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.client.rendering.MeshRenderer;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.textures.Texture;
import kmerrill285.Inignoto.modelloader.Vertex;
import kmerrill285.Inignoto.resources.raytracer.Raytracer;

public class Part {
	public static final float SCALING = 1.0f / 32.0f;
	
	public Vector3f position = new Vector3f(0, 0, 0);
	public Vector3f rotation = new Vector3f(0, 0, 0);
	public Vector3f scale = new Vector3f(0, 0, 0);
	public Vector3f size = new Vector3f(0, 0, 0);
	public Vector2f uv = new Vector2f(0, 0);
	public Vector3f origin = new Vector3f(0, 0, 0);
	
	public Part parent;
	public ArrayList<Part> children = new ArrayList<Part>();
	
	public boolean visible = true;
	public boolean locked = true;
	
	public String name = "Part";
	
	public Mesh mesh;
	
	public void buildPart(Texture texture) {
		this.mesh = buildMesh(this, texture);
	}
	
	public static Mesh buildMesh(Part part, Texture texture) {
		float size_x = part.size.x;
		float size_y = part.size.y;
		float size_z = part.size.z;
		float U = part.uv.x;
		float V = part.uv.y;
		
		float[] texCoords = {
				//front
				U + size_z, V + size_z + size_y,
				U + size_z, V + size_z,
				U + size_z + size_x, V + size_z,
				U + size_z + size_x, V + size_z + size_y,
				//back
				U + size_z + size_x + size_z, V + size_z + size_y,
				U + size_z + size_x + size_z, V + size_z,
				U + size_z + size_x + size_x + size_z, V + size_z,
				U + size_z + size_x + size_x + size_z, V + size_z + size_y,
				//left
				U, V + size_z + size_y,
				U, V + size_z,
				U + size_z, V + size_z,
				U + size_z, V + size_z + size_y,
				//right
				U + size_z + size_x, V + size_z + size_y,
				U + size_z + size_x, V + size_z,
				U + size_z * 2 + size_x, V + size_z,
				U + size_z * 2 + size_x, V + size_z + size_y,
				//top
				U + size_z, V + size_z,
				U + size_z, V,
				U + size_z + size_x, V,
				U + size_z + size_x, V + size_z,
				//bottom
				U + size_z + size_x, V + size_z,
				U + size_z + size_x, V,
				U + size_z + size_x + size_x, V,
				U + size_z + size_x + size_x, V + size_z,
				
			};
			
			for (int i = 0; i < texCoords.length; i++) {
				texCoords[i] /= (float)texture.getWidth();
				i++;
				texCoords[i] /= (float)texture.getHeight();
			}
			return buildMesh(part, texture, texCoords);
	}
	
	public static Mesh buildMesh(Part part, Texture texture, float[] texCoords) {
		float size_x = part.size.x;
		float size_y = part.size.y;
		float size_z = part.size.z;
		
		ArrayList<Vector3f> vertexCoords = new ArrayList<Vector3f>();
				
		vertexCoords.add(new Vector3f(0, size_y, 0));
		vertexCoords.add(new Vector3f(size_x, size_y, 0));
		vertexCoords.add(new Vector3f(size_x, 0, 0));
		vertexCoords.add(new Vector3f(0, 0, 0));
		
		vertexCoords.add(new Vector3f(0, size_y, size_z));
		vertexCoords.add(new Vector3f(size_x, size_y, size_z));
		vertexCoords.add(new Vector3f(size_x, 0, size_z));
		vertexCoords.add(new Vector3f(0, 0, size_z));
		
		ArrayList<Vertex> verts = new ArrayList<Vertex>();
		
		verts.add(new Vertex(0, 0, 0));
		verts.add(new Vertex(0, 1, 0));
		verts.add(new Vertex(1, 1, 0));
		verts.add(new Vertex(1, 0, 0));
		
		verts.add(new Vertex(0, 0, 1));
		verts.add(new Vertex(0, 1, 1));
		verts.add(new Vertex(1, 1, 1));
		verts.add(new Vertex(1, 0, 1));
		
		verts.add(new Vertex(0, 0, 1));
		verts.add(new Vertex(0, 1, 1));
		verts.add(new Vertex(0, 1, 0));
		verts.add(new Vertex(0, 0, 0));
		
		verts.add(new Vertex(1, 0, 0));
		verts.add(new Vertex(1, 1, 0));
		verts.add(new Vertex(1, 1, 1));
		verts.add(new Vertex(1, 0, 1));
		
		verts.add(new Vertex(0, 1, 0));
		verts.add(new Vertex(0, 1, 1));
		verts.add(new Vertex(1, 1, 1));
		verts.add(new Vertex(1, 1, 0));
		
		verts.add(new Vertex(0, 0, 1));
		verts.add(new Vertex(0, 0, 0));
		verts.add(new Vertex(1, 0, 0));
		verts.add(new Vertex(1, 0, 1));
		
		int[] indices = {
			0, 1, 2, 2, 3, 0,
			4, 5, 6, 6, 7, 4,
			8, 9, 10, 10, 11, 8,
			12, 13, 14, 14, 15, 12,
			16, 17, 18, 18, 19, 16,
			20, 21, 22, 22, 23, 20
		};
		
		float[] vertices = new float[verts.size() * 3];

		//5
		for (int i = 0; i < verts.size(); i++) {
			int J = i * 3;
			
				
			Vector3f p5 = vertexCoords.get(5);
			
			
			Vector3f v = new Vector3f(verts.get(i).x, verts.get(i).y, verts.get(i).z).sub(0.5f, 0.5f, 0.5f).mul(p5.x * 2, p5.y * 2, p5.z * 2).mul(part.scale);
			vertices[J] = v.x;
			vertices[J + 1] = v.y;
			vertices[J + 2] = v.z;
			
			
		}
		
		
		
		return new Mesh(vertices, texCoords, indices, texture);
	}
	
	public void translate(Vector3f translation) {
		this.position.add(translation);
		for (Part part : this.children) {
			part.translate(translation);
		}
	}
	
	public void rotate(Vector3f rotation) {
		rotate(rotation, new Vector3f(this.position).add(this.origin));
	}
	
	public void rotate(Vector3f rotation, Vector3f origin) {
		this.rotation.add(rotation);
		Raytracer.rotateAround(this.position, origin, rotation);
		for (Part part : this.children) {
			part.rotate(rotation, origin);
		}
	}
	
	public void changeTexture(Texture texture) {
		mesh.texture = texture;
	}
	
	public void render(ShaderProgram shader) {
		if (this.mesh != null && this.visible)
		MeshRenderer.renderMesh(mesh, new Vector3f(position).mul(SCALING), rotation, new Vector3f(scale).mul(SCALING), shader);
	}
	
	public void renderSelected(ShaderProgram shader, Mesh selectionMesh) {
		if (this.mesh != null && this.visible)
		MeshRenderer.renderMesh(selectionMesh, new Vector3f(position).mul(SCALING), rotation, new Vector3f(scale).mul(SCALING * 1.1f), shader);
	}
	
	public void dispose() {
		if (this.mesh != null) {
			this.mesh.dispose();
		}
	}
}
