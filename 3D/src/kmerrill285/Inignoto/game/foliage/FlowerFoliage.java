package kmerrill285.Inignoto.game.foliage;

import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.world.chunk.Chunk;

public class FlowerFoliage extends GroundFoliage {
	
	public FlowerFoliage(String name) {
		super(name);
	}
	
	@Override
	public void tick(int x, int y, int z, Chunk chunk) {
		super.tick(x, y, z, chunk);
	}
	
	@Override
	public void render(float x, float y, float z, Chunk chunk, ShaderProgram shader) {
		super.render(x, y, z, chunk, shader);
	}

}
