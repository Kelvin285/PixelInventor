package kmerrill285.PixelInventor.game.client.rendering;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
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

import kmerrill285.PixelInventor.game.client.rendering.textures.Texture;

public class Mesh {
	private int vaoID;

    private int vboID;
    private int texID;
    private int indexID;

    private int vertexCount;
    
    public Texture texture;
    
    public boolean empty = true;
    
    private float[] positions;
    private float[] texCoords;
    private int[] indices;
    
    private boolean disposed = false;
    private boolean setup = false;
    
    public Mesh(float[] positions, float[] texCoords, int[] indices, Texture texture) {
    	
        this.positions = positions;
        this.texCoords = texCoords;
        this.indices = indices;
        this.texture = texture;
        this.vertexCount = indices.length;
    }
    
    public void setup() {
    	FloatBuffer verticesBuffer = null;
        FloatBuffer texBuffer = null;
        IntBuffer indicesBuffer = null;
        try {
            verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
            vertexCount = indices.length;
            verticesBuffer.put(positions).flip();
            
            texBuffer = MemoryUtil.memAllocFloat(texCoords.length);
            texBuffer.put(texCoords).flip();
            
           
            vaoID = glGenVertexArrays();
            glBindVertexArray(vaoID);

            vboID = glGenBuffers();
            texID = glGenBuffers();
            
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);            
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            
            glBindBuffer(GL_ARRAY_BUFFER, texID);
            glBufferData(GL_ARRAY_BUFFER, texBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            indexID = glGenBuffers();
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, indexID);
            glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
            
            glBindBuffer(GL_ARRAY_BUFFER, 0);

            glBindVertexArray(0);         
        } finally {
            if (verticesBuffer  != null) {
                MemoryUtil.memFree(verticesBuffer);
            }
            if (texBuffer != null) {
            	MemoryUtil.memFree(texBuffer);
            }
            if (indicesBuffer != null) {
            	MemoryUtil.memFree(indicesBuffer);
            }
        }
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

        glBindVertexArray(0);
        glDeleteVertexArrays(vaoID);
        disposed = true;
    }
    
    public void render() {
    	if (disposed) return;
    	if (empty) {
    		setup();
    		empty = false;
    	}
    	if (!setup) return;
    	glActiveTexture(GL_TEXTURE0);
    	glBindTexture(GL_TEXTURE_2D, texture.getTextureId());
    	
    	glBindVertexArray(getVaoID());
    	glEnableVertexAttribArray(0);
    	glEnableVertexAttribArray(1);
        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(1);
    	glDisableVertexAttribArray(0);
    	glBindVertexArray(0);
    }

	public boolean isSetup() {
		return setup;
	}
}
