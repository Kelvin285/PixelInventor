package kmerrill285.PixelInventor.game.entity;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.MeshRenderer;
import kmerrill285.PixelInventor.game.client.rendering.chunk.BlockBuilder;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.world.World;
import kmerrill285.PixelInventor.game.world.chunk.TilePos;
import kmerrill285.PixelInventor.resources.FPSCounter;

public class FallingTileEntity extends Entity {

	private Tile tile;
	private Mesh mesh;
	public FallingTileEntity(TilePos position, World world, Tile tile) {
		super(new Vector3f(position.x, position.y-0.5f, position.z), new Vector3f(1, 1, 1), world);
		this.tile = tile;
		mesh = BlockBuilder.buildMesh(tile, 0, 0, 0);
	}
	
	public void tick() {
		super.tick();
		if (!this.onGround) {
			this.position.y -= 0.1f * FPSCounter.getDelta();
			for (Entity e : world.entities) {
				if (e instanceof FallingTileEntity) {
					if (e != this)
					if (e.position.distance(position) <= 1.5f) {
						if (e.ticksExisted > ticksExisted || e.position.y < position.y) {
							position.y++;
							world.setTile(getTilePos(), tile);
							this.isDead = true;
							return;
						}
					}
				}
			}
		}
		this.size.x = 0.01f;
		this.size.z = this.size.x;
		if (ticksExisted > 3)
		if (onGround) {
			if (!world.getTile(getTilePos()).isReplaceable()) {
				world.setTile(getTilePos().add(0, 1, 0), tile);
			} else {
				int i = 0;
				while(!world.getTile(getTilePos().add(0, i, 0)).isReplaceable()) {
					i++;
				}
				world.setTile(getTilePos().add(0, i, 0), tile);
			}
			this.isDead = true;
		}
	}
	
	public float getGravity() {
		return 0;
	}
	
	public void render(ShaderProgram shader) {
		MeshRenderer.renderMesh(mesh, position, shader);
	}
	
	public void renderShadow(ShaderProgram shader, Matrix4f view) {
		MeshRenderer.renderShadowMesh(mesh, position, shader, view);
	}

	public void dispose() {
		mesh.dispose();
	}
}
