package kmerrill285.PixelInventor.game.entity.player;

import org.joml.Vector2f;
import org.joml.Vector3f;

import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.settings.Settings;
import kmerrill285.PixelInventor.game.world.World;
import kmerrill285.PixelInventor.resources.FPSCounter;
import kmerrill285.PixelInventor.resources.MathHelper;

public class ClientPlayerEntity extends PlayerEntity {
	public ClientPlayerEntity(Vector3f position, World world) {
		super(position, world);
		world.entities.add(this);
	}
	
	
	private float headBob = 0;
	private float headBobX = 0;
	
	private Vector3f moveVel = new Vector3f(0, 0, 0);
	
	public Vector3f getOffsVel() {
		return moveVel;
	}
	
	private float useTimer = 0;
	
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
		
		if (Settings.RUN.isPressed()) {
			this.running = true;
		} else {
			this.running = false;
		}
		float bobSpeed = 0.0f;
		float bobSpeedX = 0.0f;
		if (Settings.FORWARD.isPressed()) {
			if (!Settings.BACKWARD.isPressed()) {
				XP = (float)Math.cos(Math.toRadians(yaw - 90));
				ZP = (float)Math.sin(Math.toRadians(yaw - 90));
				extra = -45;
				bobSpeed = 18f;
				bobSpeedX = 18f;
				isMoving = true;
			}
		}
		
		float backMul = 1.0f;
		if (Settings.BACKWARD.isPressed()) {
			if (!Settings.FORWARD.isPressed()) {
				backMul = 0.75f;
				XP = (float)Math.cos(Math.toRadians(yaw + 90)) * backMul;
				ZP = (float)Math.sin(Math.toRadians(yaw + 90)) * backMul;
				extra = 45;
				
				bobSpeed = 18f * backMul;
				bobSpeedX = 18f * backMul;
				isMoving = true;
			}
		}
		if (Settings.LEFT.isPressed()) {
			if (!Settings.RIGHT.isPressed()) {
				XP = (float)Math.cos(Math.toRadians(yaw - 180 - extra)) * backMul;
				ZP = (float)Math.sin(Math.toRadians(yaw - 180 - extra)) * backMul;
				isMoving = true;
				bobSpeed = 18f * backMul;
			}
		}
		
		if (Settings.RIGHT.isPressed()) {
			if (!Settings.LEFT.isPressed()) {
				XP = (float)Math.cos(Math.toRadians(yaw + extra)) * backMul;
				ZP = (float)Math.sin(Math.toRadians(yaw + extra)) * backMul;
				running = false;
				isMoving = true;
				bobSpeed = 18f * backMul;
			}
		}
		
		this.isSneaking = Settings.SNEAK.isPressed();
		
		if (running)
		{
			bobSpeed *= 1.2f;
			bobSpeedX *= 1.2f;
		}
		
		if (isSneaking) {
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
		if (running) mul = 1.2f;
		if (isSneaking) mul = 0.2f;
		
		mul *= 0.75f;
		
		if (isSneaking && onGround) {
			
			float lx = position.x;
			if (world.getTile(getTilePos().add(0, -1, 0)).blocksMovement() == false) {
				position.x = lastPos.x;
			}
			if (world.getTile(getTilePos().add(0, -1, 0)).blocksMovement() == false) {
				position.x = lx;
				position.z = lastPos.z;
			}
			if (world.getTile(getTilePos().add(0, -1, 0)).blocksMovement() == false) {
				position.x = lastPos.x;
				position.z = lastPos.z;
			}
			
		}
		
		if (Settings.JUMP.isPressed() && onGround) {
			jump();
		}
		
		float moveLerp = 0.5f;
		if (onGround == false) {
			moveLerp = 0.1f;
		}
		velocity.x = MathHelper.lerp(velocity.x, XP * this.moveSpeed * mul, moveLerp);
		velocity.z = MathHelper.lerp(velocity.z, ZP * this.moveSpeed * mul, moveLerp);
		
		moveVel = moveVel.lerp(velocity, 0.1f);
		if (!isSneaking) moveVel.mul(0);
		else
			moveVel.mul(0.9f);
		
	}
	public float getUseTime() {
		return useTimer;
	}
	public void setUseTime(float useTime) {
		useTimer = useTime;
	}
	
	float fm = 0;
	@Override
	public void render(ShaderProgram shader) {
		super.render(shader);
		
		float bm = 1.0f;
		if (isSneaking) bm = 0.25f;
		float bob = (float)Math.sin(Math.toRadians(headBobX)) * 0.1f * bm;
		float bobX = bob * (float)Math.cos(Math.toRadians(Camera.rotation.y)) * bm;
		float bobZ = bob * (float)Math.sin(Math.toRadians(Camera.rotation.y)) * bm;
		float bobY = (float)Math.abs(Math.sin(Math.toRadians(headBob)) * 0.15f) * bm;
		
		
		Vector3f vel = new Vector3f(moveVel);
		vel.mul(1, 0, 1);
		Vector2f mv = new Vector2f(velocity.x, velocity.z);
		fm = MathHelper.lerp(fm, mv.length() * 150, 0.1f);
		Settings.ACTUAL_FOV = Settings.FOV + (fm);
		Camera.position = new Vector3f(lastPos).add(bobX + vel.x * 25, bobY + this.eyeHeight, bobZ + vel.z * 25);
		Camera.update();
		this.yaw = Camera.rotation.y;
	}
	
}
