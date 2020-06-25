package kmerrill285.Inignoto.game.entity;

import org.joml.Vector3f;

import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.shadows.ShadowRenderer;
import kmerrill285.Inignoto.game.tile.Tile.TileRayTraceType;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.game.world.chunk.TilePos;
import kmerrill285.Inignoto.resources.FPSCounter;
import kmerrill285.Inignoto.resources.RayTraceResult.RayTraceType;

public class Entity {
	public Vector3f position;
	public Vector3f lastPos;
	public float pitch, yaw;
	public Vector3f velocity;
	
	public World world;
	public float width, height;
	public boolean onGround = false;
	protected boolean running;
	public boolean isMoving;
	public boolean isSneaking;
	protected boolean lastOnGround;
	public int ticksExisted = 0;
	public boolean headInGround = false;
	public float eyeHeight = 0;
	
	public Vector3f size;
	
	public float moveSpeed = 0.1f;
	
	public boolean isDead = false;
	
	public float renderDistance = 200;
	public boolean touchedGround = false;
	
	private int fallTimer = 0;
	
	private int jumpDelay = 0;
	
	public float rotY;
	public float headPitch;
	public float headYaw;
	
	
	public Entity(Vector3f position, Vector3f size, World world) {
		this.position = position;
		this.velocity = new Vector3f(0, 0, 0);
		this.lastPos = new Vector3f(position);
		this.world = world;
		this.size = size;
		this.eyeHeight = this.size.y * 0.9f;
	}
	
	public void tick() {
		
		if (jumpDelay > 0) {
			jumpDelay--;
		}
//		if (this.isRunning()) {
//			if (this.onGround) {
//				velocity.x *= 0.52f;
//				velocity.z *= 0.52f;
//			} else {
//				velocity.x *= 0.62f;
//				velocity.z *= 0.62f;
//			}
//		} else {
//			if (this.isSneaking) {
//				if (this.onGround) {
//					velocity.x *= 0.5f;
//					velocity.z *= 0.5f;
//				} else {
//					velocity.x *= 0.25f;
//					velocity.z *= 0.25f;
//				}
//			} else {
//				velocity.x *= 0.5f;
//				velocity.z *= 0.5f;
//			}
//		}
		
		
		lastOnGround = onGround;
		onGround = false;
		
		
		if (position.isFinite() == false) {
			if (lastPos.isFinite() == false) {
				position.x = 0;
				position.y = 32;
				position.z = 0;
				velocity = new Vector3f(0, 0, 0);
			} else {
				position = new Vector3f(lastPos);
				velocity = new Vector3f(0, 0, 0);
			}
		}
		
		if (!onGround) {
			if (fallTimer > 0) {
				fallTimer--;
			} else {
				fallTimer = 0;
				velocity.y -= getGravity() * FPSCounter.getDelta();
			}
		} else {
			fallTimer = 25;
		}
		
		if (fallTimer > 0) onGround = true;
		
		float bias = 0.1f;
		
		
		if (velocity.x < 0) {
			boolean collision = false;
			if (doesCollisionOccur(position.x + velocity.x - bias, position.y, position.z)) collision = true;
			if (doesCollisionOccur(position.x + velocity.x - bias, position.y, position.z + size.z)) collision = true;
			if (doesCollisionOccur(position.x + velocity.x - bias, position.y + size.y, position.z)) collision = true;
			if (doesCollisionOccur(position.x + velocity.x - bias, position.y + size.y, position.z + size.z)) collision = true;
			if (collision) {
				velocity.x = 0;
				position.x = lastPos.x;
			}
		}
		if (velocity.x > 0) {
			boolean collision = false;
			if (doesCollisionOccur(position.x + velocity.x + size.x + bias, position.y, position.z)) collision = true;
			if (doesCollisionOccur(position.x + velocity.x + size.x + bias, position.y, position.z + size.z)) collision = true;
			if (doesCollisionOccur(position.x + velocity.x + size.x + bias, position.y + size.y, position.z)) collision = true;
			if (doesCollisionOccur(position.x + velocity.x + size.x + bias, position.y + size.y, position.z + size.z)) collision = true;
			if (collision) {
				velocity.x = 0;
				position.x = lastPos.x;
			}
		}
		
		if (velocity.z < 0) {
			boolean collision = false;
			if (doesCollisionOccur(position.x, position.y, position.z + velocity.z - bias)) collision = true;
			if (doesCollisionOccur(position.x + size.x, position.y, position.z + velocity.z - bias)) collision = true;
			if (doesCollisionOccur(position.x, position.y + size.y, position.z + velocity.z - bias)) collision = true;
			if (doesCollisionOccur(position.x + size.x, position.y + size.y, position.z + velocity.z - bias)) collision = true;
			if (collision) {
				velocity.z = 0;
				position.z = lastPos.z;
			}
		}
		
		if (velocity.z > 0) {
			boolean collision = false;
			if (doesCollisionOccur(position.x, position.y, position.z + velocity.z + bias + size.z)) collision = true;
			if (doesCollisionOccur(position.x + size.x, position.y, position.z + velocity.z + bias + size.z)) collision = true;
			if (doesCollisionOccur(position.x, position.y + size.y, position.z + velocity.z + bias + size.z)) collision = true;
			if (doesCollisionOccur(position.x + size.x, position.y + size.y, position.z + velocity.z + bias + size.z)) collision = true;
			if (collision) {
				velocity.z = 0;
				position.z = lastPos.z;
			}
		}
		
		if (velocity.y < 0) {
			boolean collision = false;
			if (doesCollisionOccur(position.x, position.y + velocity.y - bias, position.z)) collision = true;
			if (doesCollisionOccur(position.x + size.x, position.y + velocity.y - bias, position.z)) collision = true;
			if (doesCollisionOccur(position.x + size.x, position.y + velocity.y - bias, position.z + size.z)) collision = true;
			if (doesCollisionOccur(position.x, position.y + velocity.y - bias, position.z + size.z)) collision = true;
			if (collision) {
				if (!lastOnGround) {
					jumpDelay = 1;
				}
				onGround = true;
				velocity.y = 0;
				position.y = (float) Math.floor(lastPos.y);
			}
		}
		
		if (velocity.y > 0) {
			boolean collision = false;
			if (doesCollisionOccur(position.x, position.y + velocity.y + size.y + bias, position.z)) collision = true;
			if (doesCollisionOccur(position.x + size.x, position.y + velocity.y + size.y + bias, position.z)) collision = true;
			if (doesCollisionOccur(position.x + size.x, position.y + velocity.y + size.y + bias, position.z + size.z)) collision = true;
			if (doesCollisionOccur(position.x, position.y + velocity.y + size.y + bias, position.z + size.z)) collision = true;
			if (collision) {
				velocity.y = 0;
				position.y = lastPos.y;
			}
		}
		
		if (velocity.y < -getTerminalVelocity()) {
			velocity.y = -getTerminalVelocity();
		}
				
		lastPos.x = position.x;
		lastPos.y = position.y;
		lastPos.z = position.z;
		
		isMoving = (int)(velocity.x * 10) != 0 && (int)(velocity.y * 10) != 0;
		if (ticksExisted > 100) {
			position.x += velocity.x * FPSCounter.getDelta();
			position.y += velocity.y * FPSCounter.getDelta();
			position.z += velocity.z * FPSCounter.getDelta();
		}
		
		ticksExisted++;
		if (touchedGround == false) {
			if (world.rayTraceTiles(position, new Vector3f(position).add(0, -50, 0), TileRayTraceType.SOLID).getType() != RayTraceType.EMPTY) {
				touchedGround=  true;
			}
		}
	}
	
	public void render(ShaderProgram shader) {
		
	}
	
	public void renderShadow(ShaderProgram shader, ShadowRenderer renderer) {
		
	}
	
	public float getGravity() {
		return 1.0f / 90.0f;
	}
	
	public float getTerminalVelocity() {
		return (1.0f / 60.0f) * 43 ;
	}
	
	public void jump() {
		if (jumpDelay > 0) return;
		velocity.y = 0.15f * 1.25f;
		velocity.x *= 1.5f;
		velocity.z *= 1.5f;
	}
	
	public void dispose() {
		
	}
	
	public TilePos getTilePos() {
		return new TilePos(position.x, position.y, position.z);
	}
	
	public boolean doesCollisionOccur(float x, float y, float z) {
		TilePos pos = new TilePos(x, y, z);
		if (world.getTile(pos).blocksMovement()) {
			return true;
		}
		return false;
	}
	
	public boolean isRunning() {
		return running;
	}


}
