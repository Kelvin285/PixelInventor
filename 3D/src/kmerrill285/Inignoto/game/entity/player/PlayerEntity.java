package kmerrill285.Inignoto.game.entity.player;

import org.joml.Vector3f;

import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.entity.Entity;
import kmerrill285.Inignoto.game.world.World;

public class PlayerEntity extends Entity {

	protected boolean crawling = false;
	protected boolean rolling = false;
	protected int rollTap = 0;

	
	public PlayerEntity(Vector3f position, World world) {
		super(position, new Vector3f(0.5f, 2, 0.5f), world);
	}
	
	@Override
	public void tick() {
		super.tick();
		this.size.x = 0.35f;
		this.size.z = this.size.x;
		
		
		if (!crawling) {
			boolean up = false;
			if (this.doesCollisionOccur(position.x, position.y + 1, position.z)) up = true;
			if (this.doesCollisionOccur(position.x + size.x, position.y + 1, position.z)) up = true;
			if (this.doesCollisionOccur(position.x + size.x, position.y + 1, position.z + size.z)) up = true;
			if (this.doesCollisionOccur(position.x, position.y + 1, position.z + size.z)) up = true;
			if (up) crawling = true;
		}
		
		if (isSneaking) {
			this.size.y = 1.5f * 0.9f;
		} else {
			this.size.y = 1.5f;
		}
		
		if (crawling) {
			this.size.y = 0.7f;
		}
		
		if (onGround == false) {
			rollTap = 0;
		}
		
		
		if (rollTap > 30) {
			this.size.y = 0.7f;
			if (running) {
				velocity.x += (float)Math.cos(Math.toRadians(yaw - 90)) * 0.3f;
				velocity.z += (float)Math.sin(Math.toRadians(yaw - 90)) * 0.3f;
			} else {
				velocity.x += (float)Math.cos(Math.toRadians(yaw - 90)) * 0.2f;
				velocity.z += (float)Math.sin(Math.toRadians(yaw - 90)) * 0.2f;
			}
		} else {
			if (rollTap < 20) 
			{
				rolling = false;
			}
			if (rollTap < 15) rollTap = 0;
		}
		
		this.eyeHeight = this.size.y * 0.9f;
	}
	
	@Override
	public void render(ShaderProgram shader) {
		
	}
	
	public boolean isCrawling() {
		return crawling;
	}
	
	public boolean isRolling() 
	{
		return rolling;
	}
	
	public void jump() {
		if (!crawling && !rolling) super.jump();
		else crawling = false;
	}

}
