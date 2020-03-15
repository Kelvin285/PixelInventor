package kmerrill285.PixelInventor.game.entity.player;

import org.joml.Vector3f;

import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.entity.Entity;
import kmerrill285.PixelInventor.game.world.World;

public class PlayerEntity extends Entity {

	public PlayerEntity(Vector3f position, World world) {
		super(position, new Vector3f(0.5f, 2, 0.5f), world);
	}
	
	@Override
	public void tick() {
		super.tick();
		this.size.x = 0.75f;
		this.size.z = this.size.x;
		this.size.y = 2.0f;
	}
	
	@Override
	public void render(ShaderProgram shader) {
		
	}

}
