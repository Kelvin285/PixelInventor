package kmerrill285.PixelInventor.game.client.rendering.shader;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

public class ShaderProgram {
	private final int programId;
	private int vertShader;
	private int fragShader;
	
	private final Map<String, Integer> uniforms;
	
	public ShaderProgram() throws Exception {
		uniforms = new HashMap<String, Integer>();
		programId = GL20.glCreateProgram();
		if (programId == 0) {
			throw new Exception("Could not create shader program!");
		}
	}
	
	public void createVertexShader(String code) throws Exception {
		vertShader = createShader(code, GL20.GL_VERTEX_SHADER);
	}
	
	public void createFragmentShader(String code) throws Exception {
		fragShader = createShader(code, GL20.GL_FRAGMENT_SHADER);
	}
	
	protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = GL20.glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        GL20.glShaderSource(shaderId, shaderCode);
        GL20.glCompileShader(shaderId);

        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + GL20.glGetShaderInfoLog(shaderId, 1024));
        }

        GL20.glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
    	GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
        }

        if (vertShader != 0) {
        	GL20.glDetachShader(programId, vertShader);
        }
        if (fragShader != 0) {
        	GL20.glDetachShader(programId, fragShader);
        }

        GL20.glValidateProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
        }

    }
    
    public void createUniform(String uniformName) throws Exception {
    	int uniformLocation = GL30.glGetUniformLocation(programId, uniformName);
    	if (uniformLocation < 0) {
    		throw new Exception("Could not find uniform " + uniformName);
    	}
    	uniforms.put(uniformName, uniformLocation);
    }
    
    public void setUniformMat4(String uniformName, Matrix4f mat) {
    	try (MemoryStack stack = MemoryStack.stackPush()) {
    		FloatBuffer fb = stack.mallocFloat(16);
    		mat.get(fb);
    		GL30.glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
    	}
    }
    
    public void setUniformInt(String uniformName, int value) {
        GL30.glUniform1i(uniforms.get(uniformName), value);
    }
    
    public void bind() {
    	GL20.glUseProgram(programId);
    }

    public void unbind() {
    	GL20.glUseProgram(0);
    }

    public void dispose() {
        unbind();
        if (programId != 0) {
            GL20.glDeleteProgram(programId);
        }
    }
}
