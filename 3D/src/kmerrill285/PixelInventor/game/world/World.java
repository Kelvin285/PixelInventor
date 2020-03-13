package kmerrill285.PixelInventor.game.world;

import java.util.Random;

import org.joml.Vector3f;
import org.joml.Vector4f;

import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.rendering.lights.PointLight;
import kmerrill285.PixelInventor.game.client.rendering.materials.Material;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.world.chunk.ChunkGenerator;
import kmerrill285.PixelInventor.game.world.chunk.ChunkManager;

public class World {
	
	private ChunkGenerator generator;
	private Random random;
		
	private ChunkManager chunkManager;
	
	public World(long seed) {
		random = new Random(seed);
		generator = new ChunkGenerator(seed);
		
		chunkManager = new ChunkManager(generator);
	}
	
	public void update() {
		chunkManager.update();
	}
	
	public void render(ShaderProgram shader) {
		chunkManager.render(shader);
	}
	
	public void dispose() {
		chunkManager.dispose();
	}
	
	public Vector3f getSkyColor() {
		return new Vector3f(91.0f / 255.0f, 198.0f / 255.0f, 208.0f / 255.0f);
	}
	
	public ChunkManager getChunkManager() {
		return this.chunkManager;
	}
}
