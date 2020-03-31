package kmerrill285.Inignoto.game.client.rendering.shader;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import kmerrill285.Inignoto.game.client.rendering.effects.lights.DirectionalLight;
import kmerrill285.Inignoto.game.client.rendering.effects.lights.Fog;
import kmerrill285.Inignoto.game.client.rendering.effects.lights.PointLight;
import kmerrill285.Inignoto.game.client.rendering.materials.Material;

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
    		System.err.println("Could not find uniform " + uniformName);
    	}
    	uniforms.put(uniformName, uniformLocation);
    }
    
    public void createPointLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".att.constant");
        createUniform(uniformName + ".att.linear");
        createUniform(uniformName + ".att.exponent");
    }

    public void createMaterialUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".ambient");
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".reflectance");
    }
    

    public void createDirectionalLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".intensity");
    }
    
    public void createFogUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".activeFog");
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".density");
    }

    public void setUniformFog(String uniformName, Fog fog) {
        setUniformInt(uniformName + ".activeFog", fog.active ? 1 : 0);
        setUniformVec3(uniformName + ".color", fog.color );
        setUniformFloat(uniformName + ".density", fog.density);
    }

    public void setUniformDirectionalLight(String uniformName, DirectionalLight dirLight) {
        setUniformVec3(uniformName + ".color", dirLight.getColor());
        setUniformVec3(uniformName + ".direction", dirLight.getDirection());
        setUniformFloat(uniformName + ".intensity", dirLight.getIntensity());
    }
    
    public void setUniformPointLight(String uniformName, PointLight pointLight) {
        setUniformVec3(uniformName + ".color", pointLight.getColor() );
        setUniformVec3(uniformName + ".position", pointLight.getPosition());
        setUniformFloat(uniformName + ".intensity", pointLight.getIntensity());
        PointLight.Attenuation att = pointLight.getAttenuation();
        setUniformFloat(uniformName + ".att.constant", att.getConstant());
        setUniformFloat(uniformName + ".att.linear", att.getLinear());
        setUniformFloat(uniformName + ".att.exponent", att.getExponent());
    }

    public void setUniformMaterial(String uniformName, Material material) {
    	setUniformVec4(uniformName + ".ambient", material.getAmbientColor());
    	setUniformVec4(uniformName + ".diffuse", material.getDiffuseColor());
    	setUniformVec4(uniformName + ".specular", material.getSpecularColor());
        setUniformInt(uniformName + ".hasTexture", material.isTextured() ? 1 : 0);
        setUniformFloat(uniformName + ".reflectance", material.getReflectance());
    }
    
    public void setUniformMat4(String uniformName, Matrix4f mat) {
    	try (MemoryStack stack = MemoryStack.stackPush()) {
    		FloatBuffer fb = stack.mallocFloat(16);
    		mat.get(fb);
    		GL30.glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
    	}
    }
    
    public void setUniformVec4(String uniformName, Vector4f mat) {
    	GL30.glUniform4f(uniforms.get(uniformName), mat.x, mat.y, mat.z, mat.w);
    }
    
    public void setUniformVec3(String uniformName, Vector3f mat) {
    	GL30.glUniform3f(uniforms.get(uniformName), mat.x, mat.y, mat.z);
    }
    
    public void setUniformVec2(String uniformName, Vector2f mat) {
    	GL30.glUniform2f(uniforms.get(uniformName), mat.x, mat.y);
    }
    
    public void setUniformBoolean(String uniformName, boolean value) {
        GL30.glUniform1i(uniforms.get(uniformName), value ? 1 : 0);
    }
    
    public void setUniformFloat(String uniformName, float value) {
        GL30.glUniform1f(uniforms.get(uniformName), value);
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
