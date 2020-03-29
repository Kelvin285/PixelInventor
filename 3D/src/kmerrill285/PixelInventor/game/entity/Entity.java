package kmerrill285.PixelInventor.game.entity;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.tile.Tile.TileRayTraceType;
import kmerrill285.PixelInventor.game.world.World;
import kmerrill285.PixelInventor.game.world.chunk.TilePos;
import kmerrill285.PixelInventor.resources.FPSCounter;
import kmerrill285.PixelInventor.resources.MathHelper;
import kmerrill285.PixelInventor.resources.RayTraceResult;
import kmerrill285.PixelInventor.resources.RayTraceResult.RayTraceType;

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
	
	public Entity(Vector3f position, Vector3f size, World world) {
		this.position = position;
		this.velocity = new Vector3f(0, 0, 0);
		this.lastPos = new Vector3f(position);
		this.world = world;
		this.size = size;
		this.eyeHeight = this.size.y * 0.9f;
	}
	
	public void tick() {
		
		lastOnGround = onGround;
		onGround = false;
		
		collideWithTiles();
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
			velocity.y = MathHelper.lerp((float)velocity.y, (float)-getTerminalVelocity(), (float)(getGravity() * FPSCounter.getDelta()) / 45.0f);
		}
		
		if (onGround == false) {
			RayTraceResult result = world.rayTraceTiles(position, new Vector3f(position).add(0, -1, 0), Tile.TileRayTraceType.SOLID);
			double dist = result.getHit().distance(position);
			if (dist <= 0.15f) {
				onGround = true;
				touchedGround = true;
				if (velocity.y < 0)
				velocity.y = 0;
			}
		}
		
		if (velocity.y < -getTerminalVelocity()) {
			velocity.y = -getTerminalVelocity();
		}
				
		lastPos.x = position.x;
		lastPos.y = position.y;
		lastPos.z = position.z;
		
		isMoving = (int)(velocity.x * 10) != 0 && (int)(velocity.y * 10) != 0;
		
		position.x += velocity.x * FPSCounter.getDelta();
		position.y += velocity.y * FPSCounter.getDelta();
		position.z += velocity.z * FPSCounter.getDelta();
		
		ticksExisted++;
		if (touchedGround == false) {
			if (world.rayTraceTiles(position, new Vector3f(position).add(0, -50, 0), TileRayTraceType.SOLID).getType() != RayTraceType.EMPTY) {
				touchedGround=  true;
			}
		}
	}
	
	public void render(ShaderProgram shader) {
		
	}
	
	public void renderShadow(ShaderProgram shader, Matrix4f view) {
		
	}
	
	public float getGravity() {
		return 9.81f * 0.2f * 0.4f;
	}
	
	public float getTerminalVelocity() {
		return 0.65f;
	}
	
	public void jump() {
		velocity.y = 4.52f * 0.1f * 0.4f;
	}
	
	public void dispose() {
		
	}
	
	public TilePos getTilePos() {
		return new TilePos(position.x, position.y, position.z);
	}
	
	public void collideWithTiles() {
		
		float inc = 0.1f;
		float velInc = 0.1f;
		float offsX = 0;
		float offsY = 0;
		float offsZ = 0;
		float vx = velocity.x;
		float vy = velocity.y;
		float vz = velocity.z;
		if (vy > 0) vy += size.y;
		if (vx < 0) vx -= size.x / 2.0f;
		if (vx > 0) vx += size.x / 2.0f;
		if (vz < 0) vz -= size.z / 2.0f;
		if (vz > 0) vz += size.z / 2.0f;
		if (vy > 0) offsY = -size.y;
		if (vx > 0) offsX = -size.x / 2;
		if (vx < 0) offsX = size.x / 2;
		if (vz > 0) offsZ = -size.z / 2;
		if (vz < 0) offsZ = size.z / 2;
		int dirX = 0;
		int dirY = 0;
		int dirZ = 0;
		if (vx > 0) dirX = 1;
		if (vx < 0) dirX = -1;
		if (vy > 0) dirY = 1;
		if (vy < 0) dirY = -1;
		if (vz > 0) dirZ = 1;
		if (vz < 0) dirZ = -1;
		
		TilePos pos = new TilePos(0, 0, 0);
		//check for collision along the y-axis (iterate through x and z-axis)
		Y:
		for (float xx = -size.x / 2.0f + inc * 2; xx < size.x / 2.0f - inc * 2; xx+=inc) {
			for (float zz = -size.x / 2.0f + inc * 2; zz < size.x / 2.0f - inc * 2; zz+=inc) {
				for (float yy = -1; yy < Math.abs(velocity.y); yy += velInc) {
					float nx = position.x + xx;
					float ny = position.y + yy * dirY - offsY;
					float nz = position.z + zz;
					pos.setPosition(nx, ny, nz);
					if (world.getTile(pos).blocksMovement()) {
						int y = pos.y;
						if (dirY > 0) {
							position.y = lastPos.y;
						} else {
							position.y = y+1;
							onGround = true;
						}
						velocity.y = 0;
						break Y;
						
					}
				}
				
			}
		}
		//check for collision along the z-axis (iterate through x and y-axis)
		Z:
		for (float xx = -size.x / 2.0f + inc * 2; xx < size.x / 2.0f - inc * 2; xx+=inc) {
			for (float yy = 0 + inc; yy < size.y - inc; yy++) {
				for (float zz = 0; zz < Math.abs(velocity.z); zz += velInc) {
					float nx = position.x + xx;
					float ny = position.y + yy;
					float nz = position.z + zz * dirZ - offsZ;
					pos.setPosition(nx, ny, nz);
					if (world.getTile(pos).blocksMovement()) {
						velocity.z = 0;
						position.z = lastPos.z;
						break Z;
					}
				}
				
			}
		}
		
		//check for collision along the x-axis (iterate through x and y-axis)
		X:
		for (float zz = -size.z / 2.0f + inc * 2; zz < size.z / 2.0f - inc * 2; zz+=inc) {
			for (float yy = 0 + inc; yy < size.y - inc; yy++) {
				for (float xx = 0; xx < Math.abs(velocity.x); xx += velInc) {
					float nx = position.x + xx * dirX - offsX;
					float ny = position.y + yy;
					float nz = position.z + zz;
					pos.setPosition(nx, ny, nz);
					if (world.getTile(pos) != null)
					if (world.getTile(pos).blocksMovement()) {
						velocity.x = 0;
						position.x = lastPos.x;
						break X;
					}
				}
				
			}
		}
		
	}
	
	public boolean isRunning() {
		return running;
	}

}
