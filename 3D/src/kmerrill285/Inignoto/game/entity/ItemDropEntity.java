package kmerrill285.Inignoto.game.entity;

import org.joml.Vector3f;

import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.client.rendering.MeshRenderer;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.entity.player.PlayerEntity;
import kmerrill285.Inignoto.game.inventory.InventoryItemStack;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.resources.TPSCounter;

public class ItemDropEntity extends Entity {
	
	private Mesh mesh = null;
	private Vector3f offset = new Vector3f(0, 0, 0);
	public InventoryItemStack stack;
	public float timer = 0;

	public ItemDropEntity(Vector3f position, World world, InventoryItemStack stack) {
		super(position, new Vector3f(0.25f, 0.25f, 0.25f), world, 1);
		this.stack = stack;
	}

	public void tick() {
		if (mesh == null) {
			this.mesh = stack.item.mesh;
		}
		super.tick();
		if (onGround == false) {
			this.yaw += 10f * world.getRandom().nextInt(3) - 1;
			this.pitch += 10.0f * world.getRandom().nextInt(3) - 1;
		} else {
			this.offset.y = (float)Math.abs(Math.cos(Math.toRadians((System.nanoTime() * 2) / 100000000.0d) + position.x + position.y + position.z) * 0.25f);
			this.yaw += world.getRandom().nextFloat();
			this.pitch += world.getRandom().nextFloat();
		}
		size.x = 0.25f;
		size.y = 0.25f;
		size.z = 0.25f;
		
		float distance = 2f;
		PlayerEntity closest = null;
		for (Entity entity : world.players) {
			if (entity instanceof PlayerEntity) {
				if (entity.position.distance(position) <= distance) {
					distance = entity.position.distance(position);
					closest = (PlayerEntity)entity;
				}
			}
		}
		if (closest != null && timer <= 0) {
			Vector3f a = new Vector3f(closest.position).add(0, 0.75f, 0);
			position.lerp(a, 0.25f);
			if (position.distance(a) <= 0.25f) {
				int val = closest.inventory.addStack(stack);
				if (val == 0) {
					this.isDead = true;
				} else {
					this.stack.size = val;
					this.timer = 2;
				}
			}
		}
		if (timer > 0) {
			timer -= TPSCounter.getTrueDelta();
		}
	}
	
	public float getGravity() {
		return 0.5f;
	}
	
	public void dispose() {
		mesh.dispose();
	}
	
	public void render(ShaderProgram shader) {
		if (shader == null) return;
		if (mesh != null)
		if (position.distance(Camera.position) <= renderDistance)
		MeshRenderer.renderMesh(mesh, new Vector3f(position).add(offset), new Vector3f(pitch, yaw, 0), size, shader);
	}
}
