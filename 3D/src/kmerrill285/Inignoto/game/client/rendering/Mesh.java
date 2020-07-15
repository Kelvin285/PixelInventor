package kmerrill285.Inignoto.game.client.rendering;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import kmerrill285.Inignoto.game.client.rendering.textures.Texture;

public class Mesh {
	private int vaoID;

    private int vboID;
    private int texID;
    private int indexID;
    private int normalID;
    private int colorID;

    private int vertexCount;
    
    public Texture texture;
    
    public boolean empty = true;
    
    public boolean outlines = false;
    
    private boolean disposed = false;
    private boolean setup = false;
    
    public float[] positions;
    public float[] texCoords;
    public int[] indices;
    public float[] normals;
    public float[] colors;
    
    public boolean locked = false;
    
    public Mesh(float[] positions, float[] texCoords, int[] indices, Texture texture) {
        this.texture = texture;
        this.vertexCount = indices.length;
        
        float[] normals = {0, 1, 0};
        
        this.positions = positions;
        this.texCoords = texCoords;
        this.indices = indices;
        this.normals = normals;
        this.colors = new float[positions.length];
        for (int i = 0; i < colors.length; i++) {
        	colors[i] = 1;
        }
    }
    
    public Mesh(float[] positions, float[] texCoords, int[] indices, float[] colors, Texture texture) {
        this.texture = texture;
        this.vertexCount = indices.length;
        
        float[] normals = new float[] {0, 1, 0};
        
        this.positions = positions;
        this.texCoords = texCoords;
        this.indices = indices;
        this.normals = normals;
        this.colors = colors;
    }
    
    public Mesh(float[] positions, float[] texCoords, int[] indices, float[] normals, float[] colors, Texture texture) {
        this.texture = texture;
        this.vertexCount = indices.length;
        
        this.positions = positions;
        this.texCoords = texCoords;
        this.indices = indices;
        this.normals = normals;
        this.colors = new float[positions.length];
        for (int i = 0; i < colors.length; i++) {
        	colors[i] = 1;
        }
    }
    

	public void combineWith(Mesh mesh, Vector3f position, Vector3f scale, Quaternionf rotation, Vector3f offset) {
		

		if (!mesh.isSetup()) {
			int verts = positions.length / 3;
			ArrayList<Float> pos = new ArrayList<Float>();
			for (int i = 0; i < mesh.positions.length / 3; i++) {
				int I = i * 3;
				int x = I;
				int y = I + 1;
				int z = I + 2;
				Vector3f vec = new Vector3f(mesh.positions[x], mesh.positions[y], mesh.positions[z]);

				vec.rotate(rotation);
				vec.mul(scale);
				vec.add(position);
				vec.add(offset);
				
				pos.add(vec.x);
				pos.add(vec.y);
				pos.add(vec.z);
			}
			for (int i = 0; i < positions.length; i++) {
				pos.add(positions[i]);
			}
			
			positions = new float[pos.size()];
			for (int i = 0; i < pos.size(); i++) {
				positions[i] = pos.get(i);
			}
			
			mesh.positions = null;
			
			ArrayList<Float> tex = new ArrayList<Float>();
			for (int i = 0; i < mesh.getTexCoords().length; i++) {
				tex.add(mesh.getTexCoords()[i]);
			}
			for (int i = 0; i < getTexCoords().length; i++) {
				tex.add(getTexCoords()[i]);
			}
			texCoords = new float[tex.size()];
			for (int i = 0; i < tex.size(); i++) {
				getTexCoords()[i] = tex.get(i);
			}
			mesh.texCoords = null;
			
			ArrayList<Float> col = new ArrayList<Float>();
			for (int i = 0; i < mesh.getColors().length; i++) {
				col.add(mesh.getColors()[i]);
			}
			for (int i = 0; i < getColors().length; i++) {
				col.add(getColors()[i]);
			}
			colors = new float[col.size()];
			for (int i = 0; i < col.size(); i++) {
				getColors()[i] = col.get(i);
			}
			mesh.colors = null;
			
			ArrayList<Float> norm = new ArrayList<Float>();
			for (int i = 0; i < mesh.normals.length; i++) {
				norm.add(mesh.normals[i]);
			}
			for (int i = 0; i < normals.length; i++) {
				norm.add(normals[i]);
			}
			normals = new float[norm.size()];
			for (int i = 0; i < norm.size(); i++) {
				normals[i] = norm.get(i);
			}
			mesh.normals = null;
			
			ArrayList<Integer> ind = new ArrayList<Integer>();
			for (int i = 0; i < mesh.indices.length; i++) {
				ind.add(mesh.indices[i] + verts);
			}
			for (int i = 0; i < indices.length; i++) {
				ind.add(indices[i]);
			}
			indices = new int[ind.size()];
			for (int i = 0; i < ind.size(); i++) {
				indices[i] = ind.get(i);
			}
			mesh.indices = null;
			vertexCount += mesh.vertexCount;
			mesh.vertexCount = 0;
		}
	}
    
    public void setup() {
    	if (locked) return;
        
    	
    	FloatBuffer verticesBuffer = null;
    	FloatBuffer texBuffer = null;
        IntBuffer indicesBuffer = null;
        FloatBuffer normalBuffer = null;
        FloatBuffer colorBuffer = null;

        verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
        vertexCount = indices.length;
        verticesBuffer.put(positions).flip();
        
        texBuffer = MemoryUtil.memAllocFloat(getTexCoords().length);
        texBuffer.put(getTexCoords()).flip();
        indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();
        
        colorBuffer = MemoryUtil.memAllocFloat(getColors().length);
        colorBuffer.put(getColors()).flip();
        
        normalBuffer = MemoryUtil.memAllocFloat(normals.length);
        normalBuffer.put(normals).flip();
        
        positions = null;
        indices = null;
        texCoords = null;
        normals = null;
        
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        texID = glGenBuffers();
        normalID = glGenBuffers();
        colorID = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);            
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        
        glBindBuffer(GL_ARRAY_BUFFER, texID);
        glBufferData(GL_ARRAY_BUFFER, texBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        
        glBindBuffer(GL_ARRAY_BUFFER, normalID);
        glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
        
        glBindBuffer(GL_ARRAY_BUFFER, colorID);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);

        indexID = glGenBuffers();
        
        glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, indexID);
        glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);         
        
        setup = true;
        
        if (verticesBuffer  != null) {
            MemoryUtil.memFree(verticesBuffer);
        }
        if (texBuffer != null) {
        	MemoryUtil.memFree(texBuffer);
        }
        if (indicesBuffer != null) {
        	MemoryUtil.memFree(indicesBuffer);
        }
        if (normalBuffer != null) {
        	MemoryUtil.memFree(normalBuffer);
        }
        if (colorBuffer != null) {
        	MemoryUtil.memFree(colorBuffer);
        }
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void dispose() {
    	if (!setup) return;
    	if (empty) return;
        glDisableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboID);
        glDeleteBuffers(texID);
        glDeleteBuffers(indexID);
        glDeleteBuffers(normalID);
        glDeleteBuffers(colorID);

        glBindVertexArray(0);
        glDeleteVertexArrays(vaoID);
        disposed = true;
        
    }
    
    public static int BUILT;
    public static final int MAX_BUILD = 10;
    
    public void render() {
    	if (disposed) return;
    	if (empty) {
    		if (BUILT > MAX_BUILD) return;
    		setup();
    		BUILT++;
    		empty = false;
    	}
    	if (!setup) return;
    	glActiveTexture(GL_TEXTURE0);
    	glBindTexture(GL_TEXTURE_2D, texture.getTextureId());
    	
    	glBindVertexArray(getVaoID());
    	glEnableVertexAttribArray(0);
    	glEnableVertexAttribArray(1);
    	glEnableVertexAttribArray(2);
    	glEnableVertexAttribArray(3);

        glDrawElements(outlines ? GL_LINES : GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
        
        glDisableVertexAttribArray(3);
    	glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(1);
    	glDisableVertexAttribArray(0);
    	glBindVertexArray(0);
    }
    
    
	public boolean isSetup() {
		return setup;
	}

	public float[] getTexCoords() {
		return texCoords;
	}
	public float[] getColors() {
		return colors;
	}
}
