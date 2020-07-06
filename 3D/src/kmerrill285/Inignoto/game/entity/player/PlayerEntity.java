package kmerrill285.Inignoto.game.entity.player;

import org.joml.Vector3f;

import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.entity.Entity;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.resources.MathHelper;
import kmerrill285.Inignoto.resources.TPSCounter;

public class PlayerEntity extends Entity {

	protected boolean crawling = false;
	protected boolean rolling = false;
	public double rollTap = 0;
	
	public int ZOOM = 0;
	
	public float cameraDist = 2.0f;
	
	public int rotDir = 0;
	
	public float lastRotation = 0;
	
	public boolean rightHop = false;
	public boolean leftHop = false;
	public boolean backHop = false;
	
	
	public PlayerEntity(Vector3f position, World world) {
		super(position, new Vector3f(0.5f, 2, 0.5f), world, 70);
	}
	
	@Override
	public void tick() {
		super.tick();
		this.size.x = 0.5f;
		this.size.z = this.size.x;
		
		if (!crawling) {
			boolean up = false;
			if (this.doesCollisionOccur(position.x, position.y + 1, position.z)) up = true;
			if (this.doesCollisionOccur(position.x + size.x, position.y + 1, position.z)) up = true;
			if (this.doesCollisionOccur(position.x + size.x, position.y + 1, position.z + size.z)) up = true;
			if (this.doesCollisionOccur(position.x, position.y + 1, position.z + size.z)) up = true;
			if (up) crawling = true;
		}
		
		if (rollTap < 25) {
			if (isSneaking) {
				this.size.y = MathHelper.lerp(this.size.y, 1.25f, 0.25f * (float)TPSCounter.getDelta());
			} else {
				this.size.y = MathHelper.lerp(this.size.y, 1.65f, 0.25f * (float)TPSCounter.getDelta());
			}
			
			if (crawling) {
				this.size.y = MathHelper.lerp(this.size.y, 0.1f, 0.25f * (float)TPSCounter.getDelta());
			}
		}
		
		
		if (rollTap > 30) {
			this.size.y = MathHelper.lerp(this.size.y, 0.1f, 0.25f * (float)TPSCounter.getDelta());
			float speed = 0.25f;
			if (ZOOM != 2) {
				if (running) {
					velocity.x += (float)Math.cos(Math.toRadians(yaw - 90)) * 0.17f * TPSCounter.getDelta() * speed;
					velocity.z += (float)Math.sin(Math.toRadians(yaw - 90)) * 0.17f * TPSCounter.getDelta() * speed;
				} else {
					velocity.x += (float)Math.cos(Math.toRadians(yaw - 90)) * 0.12f * TPSCounter.getDelta() * speed;
					velocity.z += (float)Math.sin(Math.toRadians(yaw - 90)) * 0.12f * TPSCounter.getDelta() * speed;
				}
			} else {
				if (running) {
					velocity.x -= (float)Math.cos(Math.toRadians(yaw - 90)) * 0.17f * TPSCounter.getDelta() * speed;
					velocity.z -= (float)Math.sin(Math.toRadians(yaw - 90)) * 0.17f * TPSCounter.getDelta() * speed;
				} else {
					velocity.x -= (float)Math.cos(Math.toRadians(yaw - 90)) * 0.12f * TPSCounter.getDelta() * speed;
					velocity.z -= (float)Math.sin(Math.toRadians(yaw - 90)) * 0.12f * TPSCounter.getDelta() * speed;
				}
			}
		} else {
			if (rollTap < 25) 
			{
				if (rolling == true) {
					if (world.getTile(getTilePos().add(0, 1, 0)) == Tiles.AIR) {
						crawling = false;
					}
				}
				rolling = false;
				
			}
			if (rollTap < 20) rollTap = 0;
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
		if (!crawling && !rolling) {
			super.jump();
		}
		else crawling = false;
	}

}
