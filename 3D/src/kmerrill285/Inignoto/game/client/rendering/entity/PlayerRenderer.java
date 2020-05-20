package kmerrill285.Inignoto.game.client.rendering.entity;

import org.joml.Vector3f;

import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.textures.Texture;
import kmerrill285.Inignoto.game.client.rendering.textures.Textures;
import kmerrill285.Inignoto.game.entity.player.ClientPlayerEntity;
import kmerrill285.Inignoto.game.entity.player.PlayerEntity;
import kmerrill285.Inignoto.game.settings.Settings;
import kmerrill285.Inignoto.modelloader.AnimModel;
import kmerrill285.Inignoto.modelloader.CustomModel;
import kmerrill285.Inignoto.modelloader.ModelLoader;
import kmerrill285.Inignoto.modelloader.animation.Animation;
import kmerrill285.Inignoto.resources.FPSCounter;
import kmerrill285.Inignoto.resources.MathHelper;

public class PlayerRenderer extends EntityRenderer<PlayerEntity> {

	private AnimModel body;
	private AnimModel hair;
	private AnimModel pants;
	private AnimModel shirt;
	private AnimModel shoes;
	private AnimModel eyes;
	
	private CustomModel body_model;
	private CustomModel eyes_model;
	private CustomModel hair_model;
	private CustomModel pants_model;
	private CustomModel shirt_model;
	private CustomModel shoes_model;
	private Texture skin_texture;
	private Texture eyes_texture;
	private Texture hair_texture;
	private Texture pants_texture;
	private Texture shirt_texture;
	private Texture shoes_texture;
	
	private Animation WALKING;
	private Animation IDLE;
	private Animation JUMP;
	private Animation RUNNING;
	private Animation SNEAK_IDLE;
	private Animation SNEAK_WALK;
	private Animation MINING;

	
	public boolean step = false;


	public PlayerRenderer() {
		try {
			body = ModelLoader.loadModelFromFile("Inignoto", "player/PlayerModel");
			hair = ModelLoader.loadModelFromFile("Inignoto", "player/hair/hair1");
			pants = ModelLoader.loadModelFromFile("Inignoto", "player/pants/pants1");
			shirt = ModelLoader.loadModelFromFile("Inignoto", "player/shirt/shirt1");
			shoes = ModelLoader.loadModelFromFile("Inignoto", "player/shoes/shoes1");
			eyes = ModelLoader.loadModelFromFile("Inignoto", "player/eyes");
			skin_texture = Textures.loadTexture("Inignoto", "entity/player/skin/skin1");
			eyes_texture = Textures.loadTexture("Inignoto", "entity/player/eyes/eyes1");
			hair_texture = Textures.loadTexture("Inignoto", "entity/player/hair/hair1");
			pants_texture = Textures.loadTexture("Inignoto", "entity/player/pants/pants1");
			shirt_texture = Textures.loadTexture("Inignoto", "entity/player/shirt/shirt1");
			shoes_texture = Textures.loadTexture("Inignoto", "entity/player/shoes/shoes1");
			
			WALKING = ModelLoader.loadAnimationFromFile("Inignoto", "player/Walking");
			JUMP = ModelLoader.loadAnimationFromFile("Inignoto", "player/Jump");
			RUNNING = ModelLoader.loadAnimationFromFile("Inignoto", "player/Running");
			SNEAK_IDLE = ModelLoader.loadAnimationFromFile("Inignoto", "player/Sneak_Idle");
			SNEAK_WALK = ModelLoader.loadAnimationFromFile("Inignoto", "player/Sneak_Walk");
			IDLE = ModelLoader.loadAnimationFromFile("Inignoto", "player/Idle");
			MINING = ModelLoader.loadAnimationFromFile("Inignoto", "player/Mining");
			
			
			body_model = new CustomModel(body, skin_texture);
			eyes_model = new CustomModel(eyes, eyes_texture);
			hair_model = new CustomModel(hair, hair_texture);
			pants_model = new CustomModel(pants, pants_texture);
			shirt_model = new CustomModel(shirt, shirt_texture);
			shoes_model = new CustomModel(shoes, shoes_texture);
			body_model.controller.currentAnimation = IDLE;
			
		}catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	@Override
	public void render(PlayerEntity entity, ShaderProgram shader) {

		
		if (entity.ZOOM == 0) {
			 return;
		}
		Animation currentAnimation = WALKING;
		
		float animationSpeed = 0.1f;
		
		if (entity.isMoving) {
			if (entity.isRunning()) {
				currentAnimation = RUNNING;
				animationSpeed = 0.5f;
			} else {
				currentAnimation = WALKING;
				animationSpeed = 0.4f;
				if (entity.isSneaking) {
					currentAnimation = SNEAK_WALK;
					animationSpeed = 0.2f;
				}
				
				
			}
			int frame = (int)(body_model.controller.currentAnimation.currentFrame);
			if (frame == 10 || frame == 40) {
				step = true;
			}
			if (Settings.BACKWARD.isPressed()) {
				animationSpeed *= -1;
			}
		} else {
			currentAnimation = IDLE;
			if (entity.isSneaking) {
				currentAnimation = SNEAK_IDLE;
			}
			animationSpeed = 0.1f;
		}
		if (!entity.onGround) {
			currentAnimation = JUMP;
		}
		
		renderModel(body_model, currentAnimation, animationSpeed * (float)FPSCounter.getDelta(), -1, entity, shader);
		renderModel(eyes_model, currentAnimation, 0, body_model.controller.time, entity, shader);
		renderModel(hair_model, currentAnimation, 0, body_model.controller.time, entity, shader);
		renderModel(shirt_model, currentAnimation, 0, body_model.controller.time, entity, shader);
		renderModel(pants_model, currentAnimation, 0, body_model.controller.time, entity, shader);
		renderModel(shoes_model, currentAnimation, 0, body_model.controller.time, entity, shader);
		
		
		double px = entity.headPitch;
		double pz = 0;
		
		body_model.extraRotations.put("Neck", new Vector3f((float)px, entity.headYaw, (float)pz));
		eyes_model.extraRotations.put("Neck", new Vector3f((float)px, entity.headYaw, (float)pz));
		hair_model.extraRotations.put("Neck", new Vector3f((float)px, entity.headYaw, (float)pz));
	}
	

	
	private float runAdd = 0;
	private float slant = 0;
	
	public void renderModel(CustomModel model, Animation animation, float updateSpeed, float frame, PlayerEntity entity, ShaderProgram shader) {
		model.controller.currentAnimation = animation;
		model.controller.update(updateSpeed);
		if (frame != -1) {
			model.controller.time = frame;
		}
		
		if (entity.isRunning()) {
			float mul = 1.0f;
			if (entity.ZOOM == 2) mul = -1.0f;
			if (Settings.FORWARD.isPressed()) {
				runAdd = MathHelper.lerp(runAdd, 10 * mul, 0.02f);
			}else
			if (Settings.BACKWARD.isPressed()) {
				runAdd = MathHelper.lerp(runAdd, -5 * mul, 0.02f);
			}else {
				runAdd = MathHelper.lerp(runAdd, 0, 0.2f);
			}
		} else {
			runAdd = MathHelper.lerp(runAdd, 0, 0.2f);
		}
		
		if (entity instanceof ClientPlayerEntity)
		
		if (Settings.RIGHT.isPressed()) {
			slant = MathHelper.lerp(slant, entity.velocity.length() * 25 + Camera.rotation.y - entity.lastRotation, 0.01f);
		}
		else if (Settings.LEFT.isPressed()) {
			slant = MathHelper.lerp(slant, -entity.velocity.length() * 25 + Camera.rotation.y - entity.lastRotation, 0.01f);
		} else {
			slant = MathHelper.lerp(slant, Camera.rotation.y - entity.lastRotation, 0.01f);
		}
		
		if (entity.ZOOM != 2) 
		{
			model.render(new Vector3f(entity.lastPos).add(entity.size.x / 2.0f, 0, entity.size.z / 2.0f), new Vector3f(1, 1, 1), new Vector3f(runAdd, entity.rotY, slant), shader);
		} else {
			model.render(new Vector3f(entity.lastPos).add(entity.size.x / 2.0f, 0, entity.size.z / 2.0f), new Vector3f(1, 1, 1), new Vector3f(-runAdd, entity.rotY + 180, slant), shader);
		}

	}
	
	public void dispose() {
		body_model.dispose();
	}

}
