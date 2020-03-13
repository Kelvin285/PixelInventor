package kmerrill285.PixelInventor.game.entity.player;

import org.joml.Vector3f;

import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.entity.Entity;
import kmerrill285.PixelInventor.game.world.World;

public class PlayerEntity extends Entity {

	public PlayerEntity(Vector3f position, World world) {
		super(position, world);
	}
	
	@Override
	public void tick() {
		super.tick();
	}
	
	@Override
	public void render(ShaderProgram shader) {
		
	}

}
