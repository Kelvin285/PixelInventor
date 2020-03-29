package kmerrill285.PixelInventor.game.entity;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.MeshRenderer;
import kmerrill285.PixelInventor.game.client.rendering.chunk.BlockBuilder;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.entity.player.PlayerEntity;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.world.World;

public class ItemDropEntity extends Entity {
	
	private Mesh mesh = null;
	private Vector3f offset = new Vector3f(0, 0, 0);
	public Tile tile;

	public ItemDropEntity(Vector3f position, World world, Tile tile) {
		super(position, new Vector3f(0.25f, 0.25f, 0.25f), world);
		this.tile = tile;
	}

	public void tick() {
		if (mesh == null) {
			this.mesh = BlockBuilder.buildMesh(tile, -0.5f, -0.5f, -0.5f);
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
		for (Entity entity : world.entities) {
			if (entity instanceof PlayerEntity) {
				if (entity.position.distance(position) <= distance) {
					distance = entity.position.distance(position);
					closest = (PlayerEntity)entity;
				}
			}
		}
		if (closest != null) {
			Vector3f a = new Vector3f(closest.position).add(0, 0.75f, 0);
			position.lerp(a, 0.25f);
			if (position.distance(a) <= 0.25f) {
				this.isDead = true;
			}
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
	
	public void renderShadow(ShaderProgram shader, Matrix4f view) {
		if (mesh != null)
		if (position.distance(Camera.position) <= renderDistance)
		MeshRenderer.renderShadowMesh(mesh, new Vector3f(position).add(offset), new Vector3f(pitch, yaw, 0), size, shader, view);
	}
}
