package kmerrill285.Inignoto.game.entity.player;

import org.joml.Vector2f;
import org.joml.Vector3f;

import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.rendering.entity.PlayerRenderer;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.settings.Settings;
import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.tile.Tile.TileRayTraceType;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.resources.FPSCounter;
import kmerrill285.Inignoto.resources.MathHelper;
import kmerrill285.Inignoto.resources.RayTraceResult;
import kmerrill285.Inignoto.resources.RayTraceResult.RayTraceType;

public class ClientPlayerEntity extends PlayerEntity {
	
	private int crawlTap = 0;
	private int hopTap = 0;
	
	

	private PlayerRenderer renderer;
	
	public ClientPlayerEntity(Vector3f position, World world) {
		super(position, world);
		world.entities.add(this);
		renderer = new PlayerRenderer();
	}
	
	
	private float headBob = 0;
	private float headBobX = 0;
	
	private Vector3f moveVel = new Vector3f(0, 0, 0);
	
	public Vector3f getOffsVel() {
		return moveVel;
	}
	
	private float useTimer = 0;
	
	private Vector2f moveDir = new Vector2f(0, 0);
	
	private boolean moved = false;
	
	@Override
	public void tick() {
		super.tick();
		
		if (useTimer > 0) {
			useTimer-=FPSCounter.getDelta()*0.5f;
		} else {
			useTimer = 0;
		}
		isMoving = false;
		
		
		float XP = 0;
		float ZP = 0;
		
		int extra = 0;
		
		if (!rolling)
		if (Settings.RUN.isPressed()) {
			this.running = true;
		} else {
			this.running = false;
		}
		float bobSpeed = 0.0f;
		float bobSpeedX = 0.0f;
		
		
		
		
		Vector2f dir = new Vector2f(0);
		

		if (hopTap > 0) hopTap --;
		
		if (rollTap > 0) rollTap--;
		else
			rolling = false;
		
		if (onGround && !isSneaking && !crawling) {
			if (Settings.ALTERNATE_MOVEMENT.isJustPressed() || moved == false && Settings.ALTERNATE_MOVEMENT.isPressed()) {
				
				if (Settings.FORWARD.isPressed() && rolling == false &&
						!Settings.LEFT.isPressed() && !Settings.RIGHT.isPressed()) {
					rolling = true;
					rollTap = 40;
				}
				else if (rolling == false) {
					if (Settings.BACKWARD.isPressed()) {
						float mdir = 0;
						if (Settings.LEFT.isPressed()) {
							mdir = -1f;
						} 
						if (Settings.RIGHT.isPressed()) {
							mdir = 1f;
						}
						float move = 0.3f;
						
						if (hopTap == 0) {
							velocity.y = 0.12f;
							velocity.x += (float)Math.cos(Math.toRadians(yaw)) * mdir * move * 0.75f;
							velocity.z += (float)Math.sin(Math.toRadians(yaw)) * mdir * move * 0.75f;
							if (mdir == 0) {
								velocity.x += (float)Math.cos(Math.toRadians(yaw + 90)) * move;
								velocity.z += (float)Math.sin(Math.toRadians(yaw + 90)) * move;
							} else {
								velocity.x += (float)Math.cos(Math.toRadians(yaw + 90)) * move * 0.75f;
								velocity.z += (float)Math.sin(Math.toRadians(yaw + 90)) * move * 0.75f;
							}
							hopTap = 20;
						}
					} else 
					if (Settings.LEFT.isPressed() || Settings.RIGHT.isPressed()){
						float mdir = 0;
						if (Settings.LEFT.isPressed()) {
							mdir = -1f;
						} 
						if (Settings.RIGHT.isPressed()) {
							mdir = 1f;
						}
						if (hopTap == 0) {
							float move = 0.15f;
							velocity.y = 0.12f;
							velocity.x += (float)Math.cos(Math.toRadians(yaw)) * mdir * move;
							velocity.z += (float)Math.sin(Math.toRadians(yaw)) * mdir * move;
							hopTap = 15;
						}
						
					}
				}
				
			}
		}
		
		
		moved = false;
		
		if (Settings.FORWARD.isPressed()) {
			moved = true;
			if (!Settings.BACKWARD.isPressed()) {
				if (Settings.LEFT.isPressed() || Settings.RIGHT.isPressed()) {
					dir.add(0, 0.75f);
				} else {
					dir.add(0, 1);
				}
				extra = -45;
				bobSpeed = 18f;
				bobSpeedX = 18f;
				isMoving = true;
			}
		}
		
		float backMul = 1.0f;
		if (Settings.BACKWARD.isPressed()) {
			moved = true;
			if (!Settings.FORWARD.isPressed()) {
				backMul = 0.85f;
				
				if (Settings.LEFT.isPressed() || Settings.RIGHT.isPressed()) {
					dir.add(0, -0.75f);
				} else {
					dir.add(0, -1);
				}
				
				extra = 45;
				
				bobSpeed = 18f * backMul;
				bobSpeedX = 18f * backMul;
				isMoving = true;
				if (running) {
					dir.mul(1.0f, 0.75f);
				}
			}
		}
		if (Settings.LEFT.isPressed()) {
			moved = true;
			if (Settings.FORWARD.isPressed() || Settings.BACKWARD.isPressed()) {
				dir.add(-0.75f, 0);
			} else {
				dir.add(-1, 0);
			}
			isMoving = true;
			bobSpeed = 18f * backMul;
			if (dir.y <= 0 && running) {
				dir.mul(0.75f);
			}
		}
		
		if (Settings.RIGHT.isPressed()) {
			moved = true;
			if (Settings.FORWARD.isPressed() || Settings.BACKWARD.isPressed()) {
				dir.add(0.75f, 0);
			} else {
				dir.add(1, 0);
			}
			isMoving = true;
			bobSpeed = 18f * backMul;
			if (dir.y <= 0 && running) {
				dir.mul(0.75f);
			}
		}
		
		
		
		moveDir.lerp(dir, 0.5f);
		
		if (rollTap < 30) {
			if (ZOOM != 2) {
				XP = (float)Math.cos(Math.toRadians(yaw - 90)) * moveDir.y * backMul;
				ZP = (float)Math.sin(Math.toRadians(yaw - 90)) * moveDir.y * backMul;
				
				XP += (float)Math.cos(Math.toRadians(yaw)) * moveDir.x * backMul;
				ZP += (float)Math.sin(Math.toRadians(yaw)) * moveDir.x * backMul;
			} else {
				XP = -(float)Math.cos(Math.toRadians(yaw - 90)) * moveDir.y * backMul;
				ZP = -(float)Math.sin(Math.toRadians(yaw - 90)) * moveDir.y * backMul;
				
				XP -= (float)Math.cos(Math.toRadians(yaw)) * moveDir.x * backMul;
				ZP -= (float)Math.sin(Math.toRadians(yaw)) * moveDir.x * backMul;
			}
		}
		
		
		this.isSneaking = Settings.SNEAK.isPressed();
		
		if (crawlTap > 0) {
			crawlTap--;
		}
		
		if (Settings.CRAWLING.isJustPressed()) {
			crawling = !crawling;
		}
		
		
		
		
		if (running)
		{
			bobSpeed *= 1.2f;
			bobSpeedX *= 1.2f;
		}
		
		if (isSneaking || crawling) {
			bobSpeedX *= 0.5f;
			bobSpeed *= 0.5f;
		}
		
		if (onGround == false) {
			bobSpeed = 0;
			bobSpeedX = 0;
		}
		
		
		
		headBob += bobSpeed * FPSCounter.getDelta() * 0.5f;
		headBobX += bobSpeedX * FPSCounter.getDelta() * 0.5f;
		if (headBob > 360) headBob -= 360;
		if (headBobX > 360) headBobX -= 360;
		if (headBob < 0) headBob += 360;
		if (headBobX < 0) headBobX += 360;
		
		if (isMoving == false || onGround == false) {
			float resetSpeed = 0.18f;
			if (headBob > 90 && headBob < 270) {
				headBob = MathHelper.lerp(headBob, 180, resetSpeed);
			} else {
				if (headBob <= 90) {
					headBob = MathHelper.lerp(headBob, 0, resetSpeed);
				} else {
					headBob = MathHelper.lerp(headBob, 360, resetSpeed);
				}
			}
			
			if (headBobX > 90 && headBobX < 270) {
				headBobX = MathHelper.lerp(headBobX, 180, resetSpeed);
			} else {
				if (headBobX <= 90) {
					headBobX = MathHelper.lerp(headBobX, 0, resetSpeed);
				} else {
					headBobX = MathHelper.lerp(headBobX, 360, resetSpeed);
				}
			}
		}
		
		float mul = 0.75f;
		if (running && onGround) mul = 1.7f;
		if (isSneaking && onGround) mul = 0.3f;
		if (crawling && onGround) mul = 0.3f;
		
		mul *= 0.7f;
		
		if (Settings.JUMP.isPressed()) {
			if (onGround)
			jump();
			else if (Settings.JUMP.isJustPressed()) {
				Vector3f forward = Camera.getForward();
				float x = forward.x;
				float y = forward.y;
				float z = forward.z;
				
				boolean col = false;
				
				if (!doesCollisionOccur(position.x + x, position.y + size.y, position.z + z)) {
					if (doesCollisionOccur(position.x + x, position.y + size.y - 1f, position.z + z)) {
						jump();
						onGround = true;
						col = true;
					}
				}
				if (position.y + size.y + y > position.y + 1.0f)
				if (!col)
				if (!doesCollisionOccur(position.x + x, position.y + size.y + y, position.z + z)) {
					if (doesCollisionOccur(position.x + x, position.y + size.y - 1f + y, position.z + z)) {
						jump();
						onGround = true;
					}
				}
			}
		}
		if (Settings.SNEAK.isPressed()) {
			Vector3f forward = Camera.getForward();
			float x = forward.x;
			float y = forward.y;
			float z = forward.z;
			if (!doesCollisionOccur(position.x + x, position.y + size.y, position.z + z)) {
				if (doesCollisionOccur(position.x + x, position.y + size.y - 1f, position.z + z)) {
					if (velocity.y < 0) {
						velocity.y = 0f;
						onGround = true;
					}
				}
			}
			if (position.y + size.y + y > position.y + 1.0f)
			if (!doesCollisionOccur(position.x + x, position.y + size.y + y, position.z + z)) {
				if (doesCollisionOccur(position.x + x, position.y + size.y - 1f + y, position.z + z)) {
					if (velocity.y < 0) {
						velocity.y = 0f;
						onGround = true;
					}
				}
			}
		}
		
		float moveLerp = 1.0f;
		if (onGround == false) {
			moveLerp = 0.1f;
		}
		if (onGround) {
			velocity.x = MathHelper.lerp(velocity.x, XP * this.moveSpeed * mul, 0.45f);
			velocity.z = MathHelper.lerp(velocity.z, ZP * this.moveSpeed * mul, 0.45f);
		} else {
			velocity.x = MathHelper.lerp(velocity.x, XP * this.moveSpeed * mul, 0.05f);
			velocity.z = MathHelper.lerp(velocity.z, ZP * this.moveSpeed * mul, 0.05f);
		}
		
		
		moveVel = moveVel.lerp(velocity, 0.1f);
		if (!isSneaking) moveVel.mul(0.1f);
		else
			moveVel.mul(0.9f);
		
		if (isSneaking && onGround) {
			if (velocity.x > 0) {
				if (this.doesCollisionOccur(position.x, position.y - 0.5f, position.z + size.z / 2.0f))
				if (!this.doesCollisionOccur(position.x + size.x / 8.0f, position.y - 0.5f, position.z + size.z / 2.0f)) {
					position.x = lastPos.x;
					velocity.x = 0;
				}
			}
			if (velocity.x < 0) {
				if (this.doesCollisionOccur(position.x + size.x, position.y - 0.5f, position.z + size.z / 2.0f))
				if (!this.doesCollisionOccur(position.x + size.x - size.x / 8.0f, position.y - 0.5f, position.z + size.z / 2.0f)) {
					position.x = lastPos.x;
					velocity.x = 0;
				}
			}
			
			if (velocity.z < 0) {
				if (this.doesCollisionOccur(position.x + size.x / 2.0f, position.y - 0.5f, position.z + size.z)) {
					if (!this.doesCollisionOccur(position.x + size.x / 2.0f, position.y - 0.5f, position.z + size.z - size.z / 8.0f)) {
						position.z = lastPos.z;
						velocity.z = 0;
					}
				}
			}
			if (velocity.z > 0) {
				if (this.doesCollisionOccur(position.x + size.x / 2.0f, position.y - 0.5f, position.z)) {
					if (!this.doesCollisionOccur(position.x + size.x / 2.0f, position.y - 0.5f, position.z + size.z / 8.0f)) {
						position.z = lastPos.z;
						velocity.z = 0;
					}
				}
			}
			if (velocity.x < 0 && velocity.z < 0) {
				if (this.doesCollisionOccur(position.x + size.x, position.y - 0.5f, position.z + size.z)) {
					if (!this.doesCollisionOccur(position.x + size.x - size.x / 8.0f, position.y - 0.5f, position.z + size.z - size.z / 8.0f)) {
						position.z = lastPos.z;
						velocity.z = 0;
						position.x = lastPos.x;
						velocity.x = 0;
					}
				}
			}
			if (velocity.x < 0 && velocity.z > 0) {
				if (this.doesCollisionOccur(position.x + size.x, position.y - 0.5f, position.z)) {
					if (!this.doesCollisionOccur(position.x + size.x - size.x / 8.0f, position.y - 0.5f, position.z + size.z / 8.0f)) {
						position.z = lastPos.z;
						velocity.z = 0;
						position.x = lastPos.x;
						velocity.x = 0;
					}
				}
			}
			if (velocity.x > 0 && velocity.z < 0) {
				if (this.doesCollisionOccur(position.x, position.y - 0.5f, position.z + size.z)) {
					if (!this.doesCollisionOccur(position.x + size.x / 8.0f, position.y - 0.5f, position.z + size.z - size.z / 8.0f)) {
						position.z = lastPos.z;
						velocity.z = 0;
						position.x = lastPos.x;
						velocity.x = 0;
					}
				}
			}
			if (velocity.x > 0 && velocity.z > 0) {
				if (this.doesCollisionOccur(position.x, position.y - 0.5f, position.z)) {
					if (!this.doesCollisionOccur(position.x + size.x / 8.0f, position.y - 0.5f, position.z + size.z / 8.0f)) {
						position.z = lastPos.z;
						velocity.z = 0;
						position.x = lastPos.x;
						velocity.x = 0;
					}
				}
			}
			
		}
		
		
	}
	public float getUseTime() {
		return useTimer;
	}
	public void setUseTime(float useTime) {
		useTimer = useTime;
	}
	
	float fm = 0;
	
	private int soundCounter = 0;
	private float zoom_dist = 0;

	@Override
	public void render(ShaderProgram shader) {
		super.render(shader);
		

		if (Camera.rotation.y > lastRotation) {
			rotDir = 1;
		}
		else if (Camera.rotation.y < lastRotation) {
			rotDir = -1;
		} else {
			rotDir = 0;
		}
		
		float bm = 0.75f;
		if (isSneaking) bm = 0.25f;
		float bob = (float)Math.sin(Math.toRadians(headBobX)) * 0.1f * bm;
		float bobX = bob * (float)Math.cos(Math.toRadians(Camera.rotation.y)) * bm;
		float bobZ = bob * (float)Math.sin(Math.toRadians(Camera.rotation.y)) * bm;
		float bobY = (float)Math.abs(Math.sin(Math.toRadians(headBob)) * 0.15f) * bm;
		
		if (soundCounter > 0) soundCounter--;

		if (this.onGround) {
			if ((moved && this.renderer.step) || lastOnGround == false && moved) {
				if (soundCounter == 0 && !isSneaking && !crawling && !rolling && lastOnGround == true || lastOnGround == false && moved) {
					Tile tile = world.getTile(getTilePos().add(0, -1, 0));
					if (tile.sound != null) {
						Camera.soundSource.play(tile.sound[world.getRandom().nextInt(tile.sound.length)]);
					}
					soundCounter = 5;
					this.renderer.step = false;
				}
			}
		}
		
		if (Float.isFinite(bobX) == false) bobX = 0;
		if (Float.isFinite(bobY) == false) bobY = 0;
		if (Float.isFinite(bobZ) == false) bobZ = 0;
		if (Float.isFinite(headBobX) == false) headBobX = 0;
		if (Float.isFinite(headBob) == false) headBob = 0;
		if (Float.isFinite(bob) == false) bob = 0;
		Vector3f vel = new Vector3f(moveVel);
		vel.mul(0);
		
		double zoomDist = 4;
		float dist = 0;

		if (ZOOM == 1) {
			
			Vector3f vec = Camera.getForward().mul(dist);
			Vector3f position = new Vector3f(lastPos).add(size.x / 2.0f, 0, size.z / 2.0f).sub(vec).add(0, this.eyeHeight, 0);
			int i = 0;
			while (!this.doesCollisionOccur(position.x, position.y, position.z)) {
				if (dist < zoomDist) {
					dist += 0.001f;
				}
				vec = Camera.getForward().mul(dist);
				position = new Vector3f(lastPos).add(size.x / 2.0f, 0, size.z / 2.0f).sub(vec).add(0, this.eyeHeight, 0);
				i++;
				if (i > 10000) {
					break;
				}
			}
			dist -= 0.1f;
			vec = Camera.getForward().mul(dist);
			position = new Vector3f(lastPos).add(size.x / 2.0f, 0, size.z / 2.0f).sub(vec).add(0, this.eyeHeight, 0);
			cameraDist = dist;
			
		} else
			if (ZOOM == 2) {
				Vector3f vec = Camera.getForward().mul(dist);
				Vector3f position = new Vector3f(lastPos).add(size.x / 2.0f, 0, size.z / 2.0f).sub(vec).add(0, this.eyeHeight, 0);
				int i = 0;
				while (!this.doesCollisionOccur(position.x, position.y, position.z)) {
					if (dist < zoomDist) {
						dist += 0.001f;
					}
					vec = Camera.getForward().mul(dist);
					position = new Vector3f(lastPos).add(size.x / 2.0f, 0, size.z / 2.0f).sub(vec).add(0, this.eyeHeight, 0);
					i++;
					if (i > 10000) {
						break;
					}
				}
				dist -= 0.1f;
				vec = Camera.getForward().mul(dist);
				position = new Vector3f(lastPos).add(size.x / 2.0f, 0, size.z / 2.0f).sub(vec).add(0, this.eyeHeight, 0);
				cameraDist = dist;
				
			
		}
		
		zoom_dist = MathHelper.lerp(zoom_dist, dist, 0.7f);
		Vector3f vec = Camera.getForward().mul(zoom_dist);
		Camera.position = new Vector3f(lastPos).add(size.x / 2.0f, 0, size.z / 2.0f).sub(vec).add(0, this.eyeHeight, 0);
		
		if (rollTap > 30) {
		
			if (rotDir > 0) {
				Camera.rotation.z = MathHelper.lerp(Camera.rotation.z, 10, 0.2f);
			} 
			if (rotDir < 0){
				Camera.rotation.z = MathHelper.lerp(Camera.rotation.z, -10, 0.2f);
			}
		} else {
			Camera.rotation.z = MathHelper.lerp(Camera.rotation.z, 0, 0.2f);
		}
		
		
		Camera.update();
		
		float fRot = -Camera.rotation.y + 180;
		if (Math.abs(rotY - fRot) > 30) {
			rotY = MathHelper.lerp(rotY, fRot, 0.1f);
		}
		if (this.isMoving) {
			rotY = MathHelper.lerp(rotY, fRot, 0.15f);
		}
		this.headYaw = fRot - rotY;
		
		if (ZOOM != 2) {
			this.headPitch = Camera.rotation.x;
		} else {
			this.headPitch = -Camera.rotation.x;
		}
		
		renderer.render(this, shader);

		
		this.yaw = Camera.rotation.y;
		
		lastRotation = Camera.rotation.y;
		
		if (Settings.ZOOM_OUT.isJustPressed()) {
			ZOOM++;
			if (ZOOM == 2) {
				Camera.rotation.y += 180;
				Camera.rotation.x *= -1;
			}
			if (ZOOM > 2) {
				ZOOM = 0;
				Camera.rotation.y += 180;
				Camera.rotation.x *= -1;
			}
		}
		
	}
	
}
