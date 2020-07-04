package kmerrill285.Inignoto.game.foliage;

import java.awt.Rectangle;
import java.util.HashMap;

import org.joml.Vector3f;

import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.entity.Entity;
import kmerrill285.Inignoto.game.world.chunk.Chunk;

public class TallGrassFoliage extends GroundFoliage {

	
	public TallGrassFoliage(String name) {
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
