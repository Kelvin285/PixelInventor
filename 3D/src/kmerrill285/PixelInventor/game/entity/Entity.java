package kmerrill285.PixelInventor.game.entity;

import org.joml.Vector3f;

import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.world.World;
import kmerrill285.PixelInventor.game.world.chunk.TilePos;
import kmerrill285.PixelInventor.resources.FPSCounter;

public class Entity {
	public Vector3f position;
	public float pitch, yaw;
	public Vector3f velocity;
	public World world;
	public float width, height;
	public boolean onGround;
	protected boolean running;
	public boolean isMoving;
	public boolean isSneaking;
	
	public float moveSpeed = 0.1f;
	
	public Entity(Vector3f position, World world) {
		this.position = position;
		this.velocity = new Vector3f(0, 0, 0);
		this.world = world;
	}
	
	public void tick() {
		isMoving = (int)(velocity.x * 10) != 0 && (int)(velocity.y * 10) != 0;
		position.x += velocity.x * FPSCounter.getDelta();
		position.y += velocity.y * FPSCounter.getDelta();
		position.z += velocity.z * FPSCounter.getDelta();
	}
	
	public void render(ShaderProgram shader) {
		
	}
	
	public float getGravity() {
		return 9.8f;
	}
	
	public void dispose() {
		
	}
	
	public TilePos getTilePos() {
		return new TilePos(position.x, position.y, position.z);
	}
	
	public boolean isRunning() {
		return running;
	}
}
