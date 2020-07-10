package kmerrill285.Inignoto.game.foliage;

import java.awt.Rectangle;
import java.util.HashMap;

import org.joml.Vector3f;

import custom_models.CustomModelLoader;
import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.entity.Entity;
import kmerrill285.Inignoto.game.tile.Tile.TileRayTraceType;
import kmerrill285.Inignoto.game.world.chunk.Chunk;

public class GroundFoliage extends Foliage {

	private static HashMap<String, Vector3f> bends = new HashMap<String, Vector3f>();

	
	public GroundFoliage(String name) {
		super(name);
	}

	@Override
	public void tick(int x, int y, int z, Chunk chunk) {
		if (chunk.getTileState(x, y - 1, z).getRayTraceType() == TileRayTraceType.SOLID) {
			chunk.setFoliage(x, y, z, null);
		}
	}

	private Vector3f getBend(float x, float y, float z) {
		return bends.get(x+","+y+","+z);
	}
	
	private void removeBend(float x, float y, float z) {
		bends.remove(x+","+y+","+z);
	}
	
	private void setBend(float x, float y, float z, Vector3f bend) {
		bends.put(x+","+y+","+z, bend);
	}

	@Override
	public void render(float x, float y, float z, Chunk chunk, ShaderProgram shader) {
		if (Camera.position.distance(x, y, z) >= 100) {
			return;
		}
		
		Rectangle r1 = new Rectangle((int)x, (int)y, 1, 1);
		Rectangle r2 = new Rectangle((int)z, (int)y, 1, 1);
		double rot = Math.cos(Math.toRadians(System.nanoTime() / 10000000.0f) + x) * 0.1f;
		boolean bend = false;
		for (Entity e : chunk.getWorld().entities) {
			if (r1.intersects(e.position.x, e.position.y, e.size.x, e.size.y)) {
				if (r2.intersects(e.position.z, e.position.y, e.size.z, e.size.y)) {
					bend = true;
					if (getBend(x, y, z) == null) {
						setBend(x, y, z, new Vector3f(0, 0, 0));
					}
					getBend(x, y, z).lerp(new Vector3f(e.velocity).normalize().mul(10), 0.01f);
					break;
				}
			}
		}
		if (bend == false) {
			if (getBend(x, y, z) != null)
			getBend(x, y, z).lerp(new Vector3f(0), 0.1f);
		}
		
		
		
		model.origin.set(0, -0.5f, 0);
		model.translation.set(x + 0.5f + Math.cos(x) * 0.25f, y, z + 0.5f + Math.sin(z) * 0.25f);
		model.rotation.set(rot, 0, 0);
		
		if (getBend(x, y, z) != null)
		if (getBend(x, y, z).length() > 0.01f) {
			Vector3f b = getBend(x, y, z);
			model.rotation.set(b.z, 0, -b.x);
		} else {
			if (bend == false) {
				removeBend(x, y, z);
			}
		}
		
		model.rotation.add(0, (float)Math.sin(x + z) * 360, 0);
		
		if (model.rotation.x > 1) model.rotation.x = 1;
		if (model.rotation.x < -1) model.rotation.x = -1;
		if (model.rotation.z > 1) model.rotation.z = 1;
		if (model.rotation.z < -1) model.rotation.z = -1;
		
		model.scale.set(1, 1, 1);
		model.render(shader, false);
	}

}
