package kmerrill285.Inignoto.game.entity.player;

import org.joml.Vector3f;

import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.entity.Entity;
import kmerrill285.Inignoto.game.world.World;

public class PlayerEntity extends Entity {

	public PlayerEntity(Vector3f position, World world) {
		super(position, new Vector3f(0.5f, 2, 0.5f), world);
	}
	
	@Override
	public void tick() {
		super.tick();
		this.size.x = 0.5f;
		this.size.z = this.size.x;
		
		if (isSneaking) {
			this.size.y = 1.5f * 0.75f;
		} else {
			this.size.y = 1.5f;
		}
		this.eyeHeight = this.size.y * 0.9f;
	}
	
	@Override
	public void render(ShaderProgram shader) {
		
	}

}
