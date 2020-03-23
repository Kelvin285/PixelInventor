package kmerrill285.PixelInventor.game.client.rendering.raytracing;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_WORK_GROUP_SIZE;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.HashMap;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

import kmerrill285.PixelInventor.resources.Utils;

public class RayShader {
	
	private int computeProgram;
	
	private HashMap<String, Integer> uniformLocations = new HashMap<String, Integer>();

	public int workGroupSizeX;
	public int workGroupSizeY;
	
	
	public RayShader() throws Exception {
		computeProgram = createComputeProgram();
		initComputeProgram();
	}
	
	private int createShader(String resource, int type) throws IOException {
		int shader = glCreateShader(type);
		glShaderSource(shader, Utils.loadResource("PixelInventor/shaders/raytracing", resource));
		glCompileShader(shader);
		int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);
		String shaderLog = glGetShaderInfoLog(shader);
		if (shaderLog.trim().length() > 0) {
			System.err.println(shaderLog);
		}
		if (compiled == 0) {
			throw new AssertionError("Could not compile shader");
		}
		return shader;
	}

	
	private int createComputeProgram() throws IOException {
		int program = glCreateProgram();
		int cshader = createShader("raytracing.glslcs", GL_COMPUTE_SHADER);
		glAttachShader(program, cshader);
		glLinkProgram(program);
		int linked = glGetProgrami(program, GL_LINK_STATUS);
		String programLog = glGetProgramInfoLog(program);
		if (programLog.trim().length() > 0) {
			System.err.println(programLog);
		}
		if (linked == 0) {
			throw new AssertionError("Could not link program");
		}
		return program;
	}

	
	private void initComputeProgram() {
		glUseProgram(computeProgram);
		IntBuffer workGroupSize = BufferUtils.createIntBuffer(16);
		GL20.glGetProgramiv(computeProgram, GL_COMPUTE_WORK_GROUP_SIZE, workGroupSize);
		workGroupSizeX = workGroupSize.get(0);
		workGroupSizeY = workGroupSize.get(1);
		createUniform("eye");
		createUniform("ray00");
		createUniform("ray10");
		createUniform("ray01");
		createUniform("ray11");
		createUniform("skyColor");
		createUniform("sunColor");
		createUniform("sunPosition");
		createUniform("tile_texture");
		createUniform("width");
		createUniform("length");
		createUniform("height");
		createUniform("chunk_size");
		createUniform("oct_size");
		createUniform("shadows");
		createUniform("reflections");
		createUniform("world_position");

		glUseProgram(0);
	}
	
	public void createUniform(String location) {
		uniformLocations.put(location, glGetUniformLocation(computeProgram, location));
	}
	
	public void setVec3i(String location, int x, int y, int z) {
		GL43.glUniform3i(uniformLocations.get(location), x, y, z);
	}
	
	public void setVec3(String location, float x, float y, float z) {
		glUniform3f(uniformLocations.get(location), x, y, z);
	}
	
	public void setVec3(String location, Vector3f pos) {
		setVec3(location, pos.x, pos.y, pos.z);
	}
	
	public void setInt(String location, int i) {
		GL30.glUniform1i(uniformLocations.get(location), i);
	}
	
	public void dispose() {
		GL20.glDeleteProgram(computeProgram);
	}
	
	public void bind() {
		glUseProgram(computeProgram);
	}
	
	public void unbind() {
		glUseProgram(0);
	}
	
}
