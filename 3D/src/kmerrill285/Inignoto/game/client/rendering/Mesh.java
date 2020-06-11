package kmerrill285.Inignoto.game.client.rendering;

import static org.lwjgl.opengl.GL11.*;
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

import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import kmerrill285.Inignoto.game.client.rendering.textures.Texture;

public class Mesh {
	private int vaoID;

    private int vboID;
    private int texID;
    private int indexID;
    private int normalID;

    private int vertexCount;
    
    public Texture texture;
    
    public boolean empty = true;
    
    public boolean outlines = false;
    
    private boolean disposed = false;
    private boolean setup = false;
    private FloatBuffer verticesBuffer = null;
    private FloatBuffer texBuffer = null;
    private IntBuffer indicesBuffer = null;
    private FloatBuffer normalBuffer = null;
    public Mesh(float[] positions, float[] texCoords, int[] indices, Texture texture) {
        this.texture = texture;
        this.vertexCount = indices.length;
        
        float[] normals = {0, 1, 0};
        
        verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
        vertexCount = indices.length;
        verticesBuffer.put(positions).flip();
        
        texBuffer = MemoryUtil.memAllocFloat(texCoords.length);
        texBuffer.put(texCoords).flip();
        indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();
        
        normalBuffer = MemoryUtil.memAllocFloat(normals.length);
        normalBuffer.put(normals).flip();
    }
    
    public Mesh(float[] positions, float[] texCoords, int[] indices, float[] normals, Texture texture) {
        this.texture = texture;
        this.vertexCount = indices.length;
        
        verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
        vertexCount = indices.length;
        verticesBuffer.put(positions).flip();
        
        texBuffer = MemoryUtil.memAllocFloat(texCoords.length);
        texBuffer.put(texCoords).flip();
        indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();
        
        normalBuffer = MemoryUtil.memAllocFloat(normals.length);
        normalBuffer.put(normals).flip();
    }
    
    public void setup() {
    	
        
           
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        texID = glGenBuffers();
        normalID = glGenBuffers();
        
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);            
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        
        glBindBuffer(GL_ARRAY_BUFFER, texID);
        glBufferData(GL_ARRAY_BUFFER, texBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        
        glBindBuffer(GL_ARRAY_BUFFER, normalID);
        glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
        

        indexID = glGenBuffers();
        
        glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, indexID);
        glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);         
        
        setup = true;
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

        glBindVertexArray(0);
        glDeleteVertexArrays(vaoID);
        disposed = true;
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
        glDrawElements(outlines ? GL_LINES : GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
    	glEnableVertexAttribArray(2);
        glDisableVertexAttribArray(1);
    	glDisableVertexAttribArray(0);
    	glBindVertexArray(0);
    }
    
    
	public boolean isSetup() {
		return setup;
	}
}
