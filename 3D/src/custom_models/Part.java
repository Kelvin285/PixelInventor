package custom_models;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.client.rendering.MeshRenderer;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.textures.Texture;
import kmerrill285.Inignoto.game.client.rendering.textures.Textures;
import kmerrill285.Inignoto.modelloader.Vertex;
import kmerrill285.Inignoto.resources.raytracer.Raytracer;

public class Part {
	public static final float SCALING = 1.0f / 32.0f;
	
	public Vector3f position = new Vector3f(0, 0, 0);
	public Quaternionf rotation = new Quaternionf().identity();
	public Vector3f scale = new Vector3f(0, 0, 0);
	public Vector3i size = new Vector3i(0, 0, 0);
	public Vector2f uv = new Vector2f(0, 0);
	public Vector3f origin = new Vector3f(0, 0, 0);
	
	public Part parent;
	public ArrayList<Part> children = new ArrayList<Part>();
	
	public boolean visible = true;
	public boolean locked = true;
	
	public String name = "Part";
	
	public Mesh mesh;
	public Mesh outlineMesh;
	
	private Texture texture;
	
	public Vector3f look = new Vector3f(0, 0, 1);
	
	public Vector3f axisAngles = new Vector3f(0, 0, 0);
	
	public static void duplicatePart(Part part, Part parent, Model model) {
		Part p = new Part();
		p.name = "" + part.name + " - Copy";
		p.position = new Vector3f(part.position);
		p.rotation = new Quaternionf(part.rotation);
		p.scale = new Vector3f(part.scale);
		p.size = new Vector3i(part.size);
		p.uv = new Vector2f(part.uv);
		p.origin = new Vector3f(part.origin);
		p.parent = parent;
		for (Part c : part.children) {
			duplicatePart(c, p, model);
		}
		p.buildPart(part.texture);
		model.getParts().add(p);
	}
		
	public void buildPart() {
		this.mesh = buildMesh(this, this.texture);
	}
	
	public void buildPart(Texture texture) {
		this.mesh = buildMesh(this, texture);
		this.texture = texture;
		this.outlineMesh = buildOutlineMesh(this);
	}
	
	public static Mesh buildMesh(Part part, Texture texture) {
		float size_x = part.size.x;
		float size_y = part.size.y;
		float size_z = part.size.z;
		float U = part.uv.x;
		float V = part.uv.y;
		
		part.outlineMesh = buildOutlineMesh(part);
		
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
	
	public static Mesh buildOutlineMesh(Part part) {
		Mesh mesh = null;
		Vector3f size = new Vector3f(part.size).div(4.0f);
		float[] vertices = new float[] {
				-size.x, -size.y, -size.z,
				-size.x, size.y, -size.z,
				size.x, size.y, -size.z,
				size.x, -size.y, -size.z,
				
				-size.x, -size.y, size.z,
				-size.x, size.y, size.z,
				size.x, size.y, size.z,
				size.x, -size.y, size.z
		};
		int[] indices = new int[] {
			0, 1, 1, 2, 2, 3, 3, 0,
			4, 5, 5, 6, 6, 7, 7, 4,
			0, 4, 1, 5, 2, 6, 3, 7
		};
		float[] texCoords = new float[] {
			0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0
		};
		mesh = new Mesh(vertices, texCoords, indices, Textures.OUTLINE);
		mesh.outlines = true;
		return mesh;
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
			
				
			Vector3f p5 = new Vector3f(vertexCoords.get(5)).mul(0.5f);
			
			
			
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
		rotate(rotation, new Vector3f(this.position).add(new Vector3f(this.origin).rotate(this.rotation)));
	}
	
	public void rotate(Vector3f rotation, Vector3f origin) {
		
		this.axisAngles.add(rotation);
		
        this.rotation.rotateLocalX((float)Math.toRadians(rotation.x));
        this.rotation.rotateLocalY((float)Math.toRadians(rotation.y));
        this.rotation.rotateLocalZ((float)Math.toRadians(rotation.z));
                
        Quaternionf nRot = new Quaternionf().identity();
        nRot.rotateLocalX((float)Math.toRadians(rotation.x));
        nRot.rotateLocalY((float)Math.toRadians(rotation.y));
        nRot.rotateLocalZ((float)Math.toRadians(rotation.z));
        
       
        this.position = Raytracer.rotateAround(this.position, origin, nRot);
        
        for (Part part : this.children) {
            part.rotate(rotation, origin);
        }
    }
	
	public void setRotation(Vector3f rotation) {
		rotate(new Vector3f(rotation).mul(-1));
		this.axisAngles = new Vector3f(0, 0, 0);
		this.rotation.identity();
		rotate(rotation);
	}
	
	public void changeTexture(Texture texture) {
		mesh.texture = texture;
		this.texture = texture;
	}
	
	public void render(ShaderProgram shader) {
		Vector3f rot = getEulerAngles();
		if (this.mesh != null && this.visible)
		MeshRenderer.renderMesh(mesh, new Vector3f(position).mul(SCALING), rot, new Vector3f(scale).mul(SCALING), shader);
		
	}
	
	public Vector3f getEulerAngles() {
		
		Matrix4f mat = new Matrix4f().identity().rotate(rotation);
		Vector3f euler = new Vector3f();
		mat.getEulerAnglesZYX(euler);
		euler.x = (float)Math.toDegrees(euler.x);
		euler.y = (float)Math.toDegrees(euler.y);
		euler.z = (float)Math.toDegrees(euler.z);

		return euler;
	}
	
	public void render(ShaderProgram shader, boolean outlines, Part selected) {
		if (this.mesh != null && this.visible)
		MeshRenderer.renderMesh(mesh, new Vector3f(position).mul(SCALING), rotation, new Vector3f(scale).mul(SCALING), shader);
		if (outlines) {
			if (selected == this) {
				this.outlineMesh.texture = Textures.WHITE_SQUARE;
			} else {
				this.outlineMesh.texture = Textures.OUTLINE;
			}
			if (this.mesh != null && this.visible)
				MeshRenderer.renderMesh(this.outlineMesh, new Vector3f(position).mul(SCALING), rotation, new Vector3f(scale).mul(SCALING * 2.0f), shader);
		}
	}
	
	public void renderSelected(ShaderProgram shader, Mesh selectionMesh) {
//		if (this.mesh != null && this.visible)
//		MeshRenderer.renderMesh(selectionMesh, new Vector3f(position).mul(SCALING), rotation, new Vector3f(scale).mul(SCALING * 1.1f), shader);
	}
	
	public void dispose() {
		if (this.mesh != null) {
			this.mesh.dispose();
		}
	}
}
