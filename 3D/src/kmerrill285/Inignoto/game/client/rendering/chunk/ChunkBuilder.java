package kmerrill285.Inignoto.game.client.rendering.chunk;

import java.util.ArrayList;
import java.util.Arrays;

import org.joml.Vector3f;

import custom_models.CustomModelLoader;
import custom_models.Model;
import custom_models.Part;
import kmerrill285.Inignoto.game.client.rendering.BlockFace;
import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.client.rendering.textures.Textures;
import kmerrill285.Inignoto.game.tile.Tile.TileRayTraceType;
import kmerrill285.Inignoto.game.tile.data.TileState;
import kmerrill285.Inignoto.game.world.chunk.Chunk;

public class ChunkBuilder {

	public static Mesh buildChunk(Chunk chunk, boolean cascade) {
		return buildChunk(chunk, cascade, cascade, cascade, cascade, cascade, cascade);
	}
	public static Mesh buildChunk(Chunk chunk, boolean cl, boolean cr, boolean cu, boolean cd, boolean cf, boolean cb) {
//		chunk.recalculateLights();
		int x = chunk.getX();
		int y = chunk.getY();
		int z = chunk.getZ();
		if (chunk.getWorld().getChunk(x - 1, y, z) == null) return null;
		if (chunk.getWorld().getChunk(x + 1, y, z) == null) return null;
		if (chunk.getWorld().getChunk(x, y - 1, z) == null) return null;
		if (chunk.getWorld().getChunk(x, y + 1, z) == null) return null;
		if (chunk.getWorld().getChunk(x, y, z - 1) == null) return null;
		if (chunk.getWorld().getChunk(x, y, z + 1) == null) return null;
		
		ArrayList<Float> vertices = new ArrayList<Float>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<Float> texCoords = new ArrayList<Float>();
		ArrayList<Float> colors = new ArrayList<Float>();

		int index = 0;
		index = buildChunk(chunk, vertices, indices, texCoords, colors, index);
		
		float[] v = new float[vertices.size()];
		float[] t = new float[texCoords.size()];
		int[] i = new int[indices.size()];
		float[] c = new float[colors.size()];
		for (int j = 0; j < vertices.size(); j++) {
			v[j] = vertices.get(j);
		}
		for (int j = 0; j < texCoords.size(); j++) {
			t[j] = texCoords.get(j);
		}
		for (int j = 0; j < indices.size(); j++) {
			i[j] = indices.get(j);
		}
		for (int j = 0; j < colors.size(); j++) {
			c[j] = colors.get(j);
		}
		
		
		
		Mesh mesh = new Mesh(v, t, i,c, Textures.TILES.texture);
		
		boolean cascade = cl | cr | cu | cd | cf | cb;
		if (cascade) {
			Chunk ch = chunk.getWorld().getChunk(chunk.getX() - 1, chunk.getY(), chunk.getZ());
			if (cl) {
				ch.mesh = buildChunk(ch, false);
				ch.waterMesh = buildLiquidChunk(ch);
			}
			ch = chunk.getWorld().getChunk(chunk.getX() + 1, chunk.getY(), chunk.getZ());
			if (cr) {
				ch.mesh = buildChunk(ch, false);
				ch.waterMesh = buildLiquidChunk(ch);
			}
			ch = chunk.getWorld().getChunk(chunk.getX(), chunk.getY() + 1, chunk.getZ());
			if (cu) {
				ch.mesh = buildChunk(ch, false);
				ch.waterMesh = buildLiquidChunk(ch);
			}
			ch = chunk.getWorld().getChunk(chunk.getX(), chunk.getY() - 1, chunk.getZ());
			if (cd) {
				ch.mesh = buildChunk(ch, false);
				ch.waterMesh = buildLiquidChunk(ch);
			}
			
			ch = chunk.getWorld().getChunk(chunk.getX(), chunk.getY(), chunk.getZ() + 1);
			if (cf) {
				ch.mesh = buildChunk(ch, false);
				ch.waterMesh = buildLiquidChunk(ch);
			}
			ch = chunk.getWorld().getChunk(chunk.getX(), chunk.getY(), chunk.getZ() - 1);
			if (cb) {
				ch.mesh = buildChunk(ch, false);
				ch.waterMesh = buildLiquidChunk(ch);
			}
		}
		
		return mesh;
	}
	final int FULL = 16 * 16 * 16;
	
	public static int buildChunk(Chunk chunk, ArrayList<Float> vertices, ArrayList<Integer> indices, ArrayList<Float> texCoords,ArrayList<Float> colors, int index) {
		
		Vector3f vec = new Vector3f(0);
		for (int i = 0; i < Chunk.SIZE_Y / Chunk.SIZE; i++) {
				for (int x = 0; x < Chunk.SIZE; x++) {
					for (int y = i * Chunk.SIZE; y < i * Chunk.SIZE + Chunk.SIZE; y++) {
						for (int z = 0; z < Chunk.SIZE; z++) {
							TileState data = chunk.getTileState(x, y, z, false);	
							float mining_time = chunk.getMiningTime(x, y, z);
							Mesh mesh = null;
							if (!data.getModel().isEmpty()) {
								
								if (mining_time > 0) {
									if (chunk.isLocalTileNotFull(x - 1, y, z)) {
										index = TileBuilder.addMiningFace(x, y, z, BlockFace.LEFT, mining_time, vertices, indices, texCoords, index);
										for (int f = 0; f < 4; f++) {
											colors.add(1f);
											colors.add(1f);
											colors.add(1f);
										}
									}
									if (chunk.isLocalTileNotFull(x + 1, y, z)) {
										index = TileBuilder.addMiningFace(x, y, z, BlockFace.RIGHT, mining_time, vertices, indices, texCoords, index);
										for (int f = 0; f < 4; f++) {
											colors.add(1f);
											colors.add(1f);
											colors.add(1f);
										}
									}
									if (chunk.isLocalTileNotFull(x, y, z - 1)) {
										index = TileBuilder.addMiningFace(x, y, z, BlockFace.FRONT, mining_time, vertices, indices, texCoords, index);
										for (int f = 0; f < 4; f++) {
											colors.add(1f);
											colors.add(1f);
											colors.add(1f);
										}
									}
									if (chunk.isLocalTileNotFull(x, y, z + 1)) {
										index = TileBuilder.addMiningFace(x, y, z, BlockFace.BACK, mining_time, vertices, indices, texCoords, index);
										for (int f = 0; f < 4; f++) {
											colors.add(1f);
											colors.add(1f);
											colors.add(1f);
										}
									}
									if (chunk.isLocalTileNotFull(x, y + 1, z)) {
										index = TileBuilder.addMiningFace(x, y, z, BlockFace.UP, mining_time, vertices, indices, texCoords, index);
										for (int f = 0; f < 4; f++) {
											colors.add(1f);
											colors.add(1f);
											colors.add(1f);
										}
									}
									if (chunk.isLocalTileNotFull(x, y - 1, z)) {
										index = TileBuilder.addMiningFace(x, y, z, BlockFace.DOWN, mining_time, vertices, indices, texCoords, index);
										for (int f = 0; f < 4; f++) {
											colors.add(1f);
											colors.add(1f);
											colors.add(1f);
										}
									}
								
								}
								
								Model model = CustomModelLoader.getOrLoadModel(data.getModel().split(":")[0], data.getModel().split(":")[1], Textures.TILES.texture);
								model.combine(Textures.TILES.texture);

								
								mesh = model.getParts().get(0).mesh;
								
								float[] tc = Arrays.copyOf(mesh.texCoords, mesh.texCoords.length);
								
								float f[] = {0, 0, 0, 1, 1, 1, 1, 0};
								
								
								Textures.TILES.convertToUV(
										f,
										data.getTextureFor(BlockFace.FRONT));
								
								for (int i1 = 0; i1 < tc.length / 2; i1++) {
									int u = i1 * 2;
									int v = i1 * 2 + 1;
									texCoords.add(f[0] + tc[u]);
									texCoords.add(f[1] + tc[v]);
								}
								
								Vector3f light = chunk.getLight(x + 0.5f, y + 0.5f, z + 0.5f);
								
								int verts = vertices.size();
								for (int i1 = 0; i1 < mesh.positions.length / 3; i1++) {
									float X = mesh.positions[i1 * 3] * Part.SCALING;
									float Y = mesh.positions[i1 * 3 + 1] * Part.SCALING;
									float Z = mesh.positions[i1 * 3 + 2] * Part.SCALING;
									
									vec.set(X, Y, Z);
									vec.add(data.offset_x, data.offset_y, data.offset_z);
									vec.sub(0.5f, 0.5f, 0.5f);
									
									vec.rotateX((float)Math.toRadians(data.getPitch()));
									vec.rotateY((float)Math.toRadians(data.getYaw()));

									vec.add(0.5f, 0.5f, 0.5f);

									
									X = vec.x;
									Y = vec.y;
									Z = vec.z;
									
									X += x;
									Y += y;
									Z += z;
									vertices.add(X);
									vertices.add(Y);
									vertices.add(Z);
									
									colors.add(light.x);
									colors.add(light.y);
									colors.add(light.z);
								}
								
								for (int i1 = 0; i1 < mesh.indices.length; i1++) {
									indices.add(mesh.indices[i1] + verts / 3);
								}
								
								index += mesh.positions.length / 3;
								continue;
							}
							
							if (data.isFullCube() && data.isVisible() && data.getRayTraceType() == TileRayTraceType.SOLID) {

								if (chunk.isLocalTileNotFull(x - 1, y, z)) {
									Vector3f light = chunk.getLight(x - 1, y, z);

									int len = vertices.size() / 3;
									index = TileBuilder.addFace(x, y, z, BlockFace.LEFT, data, vertices, texCoords, indices, index, mining_time); 
									for (int l = len; l < vertices.size() / 3; l++) {
										int I = l * 3;
										colors.add(light.x);
										colors.add(light.y);
										colors.add(light.z);
									}
								}
								if (chunk.isLocalTileNotFull(x + 1, y, z)) {
									Vector3f light = chunk.getLight(x + 1, y, z);

									int len = vertices.size() / 3;
									index = TileBuilder.addFace(x, y, z, BlockFace.RIGHT, data, vertices, texCoords, indices, index, mining_time);
									for (int l = len; l < vertices.size() / 3; l++) {
										int I = l * 3;
										colors.add(light.x);
										colors.add(light.y);
										colors.add(light.z);
									}
								}
								if (chunk.isLocalTileNotFull(x, y, z - 1)) {
									Vector3f light = chunk.getLight(x, y, z - 1);

									int len = vertices.size() / 3;
									index = TileBuilder.addFace(x, y, z, BlockFace.FRONT, data, vertices, texCoords, indices, index, mining_time);
									for (int l = len; l < vertices.size() / 3; l++) {
										int I = l * 3;
										colors.add(light.x);
										colors.add(light.y);
										colors.add(light.z);
									}
								}
								if (chunk.isLocalTileNotFull(x, y, z + 1)) {
									Vector3f light = chunk.getLight(x, y, z + 1);

									int len = vertices.size() / 3;
									index = TileBuilder.addFace(x, y, z, BlockFace.BACK, data, vertices, texCoords, indices, index, mining_time);
									for (int l = len; l < vertices.size() / 3; l++) {
										int I = l * 3;
										colors.add(light.x);
										colors.add(light.y);
										colors.add(light.z);
									}
								}
								if (chunk.isLocalTileNotFull(x, y + 1, z)) {
									Vector3f light = chunk.getLight(x, y + 1, z);

									int len = vertices.size() / 3;
									index = TileBuilder.addFace(x, y, z, BlockFace.UP, data, vertices, texCoords, indices, index, mining_time);
									for (int l = len; l < vertices.size() / 3; l++) {
										int I = l * 3;
										colors.add(light.x);
										colors.add(light.y);
										colors.add(light.z);
									}
								}
								if (chunk.isLocalTileNotFull(x, y - 1, z)) {
									Vector3f light = chunk.getLight(x, y - 1, z);

									int len = vertices.size() / 3;
									index = TileBuilder.addFace(x, y, z, BlockFace.DOWN, data, vertices, texCoords, indices, index, mining_time);
									for (int l = len; l < vertices.size() / 3; l++) {
										int I = l * 3;
										colors.add(light.x);
										colors.add(light.y);
										colors.add(light.z);
									}
								}
							}
						}
					}
				}
		}
		
		return index;
	}
	
	
	public static Mesh buildLiquidChunk(Chunk chunk) {
		
		int x = chunk.getX();
		int y = chunk.getY();
		int z = chunk.getZ();
		if (chunk.getWorld().getChunk(x - 1, y, z) == null) return null;
		if (chunk.getWorld().getChunk(x + 1, y, z) == null) return null;
		if (chunk.getWorld().getChunk(x, y - 1, z) == null) return null;
		if (chunk.getWorld().getChunk(x, y + 1, z) == null) return null;
		if (chunk.getWorld().getChunk(x, y, z - 1) == null) return null;
		if (chunk.getWorld().getChunk(x, y, z + 1) == null) return null;
		
		ArrayList<Float> vertices = new ArrayList<Float>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<Float> texCoords = new ArrayList<Float>();
		int index = 0;
		index = buildLiquidChunk(chunk, vertices, indices, texCoords, index);
		
		float[] v = new float[vertices.size()];
		float[] t = new float[texCoords.size()];
		int[] i = new int[indices.size()];
		for (int j = 0; j < vertices.size(); j++) {
			v[j] = vertices.get(j);
		}
		for (int j = 0; j < texCoords.size(); j++) {
			t[j] = texCoords.get(j);
		}
		for (int j = 0; j < indices.size(); j++) {
			i[j] = indices.get(j);
		}
		
		Mesh mesh = new Mesh(v, t, i, Textures.TILES.texture);
		
		
		
		return mesh;
	}
	
	public static int buildLiquidChunk(Chunk chunk, ArrayList<Float> vertices, ArrayList<Integer> indices, ArrayList<Float> texCoords, int index) {
//		if (chunk.voxels <= 0) return index;
		for (int i = 0; i < Chunk.SIZE_Y / Chunk.SIZE; i++) {
				for (int x = 0; x < Chunk.SIZE; x++) {
					for (int y = i * Chunk.SIZE; y < i * Chunk.SIZE + Chunk.SIZE; y++) {
						for (int z = 0; z < Chunk.SIZE; z++) {
							TileState data = chunk.getTileState(x, y, z, false);
							if (data.getRayTraceType() == TileRayTraceType.LIQUID) {
								if (chunk.isLocalTileAir(x - 1, y, z)) index = TileBuilder.addFace(x, y, z, BlockFace.LEFT, data, vertices, texCoords, indices, index, 0); 
								if (chunk.isLocalTileAir(x + 1, y, z)) index = TileBuilder.addFace(x, y, z, BlockFace.RIGHT, data, vertices, texCoords, indices, index, 0);
								if (chunk.isLocalTileAir(x, y, z - 1)) index = TileBuilder.addFace(x, y, z, BlockFace.FRONT, data, vertices, texCoords, indices, index, 0);
								if (chunk.isLocalTileAir(x, y, z + 1)) index = TileBuilder.addFace(x, y, z, BlockFace.BACK, data, vertices, texCoords, indices, index, 0);
								if (chunk.isLocalTileAir(x, y + 1, z)) index = TileBuilder.addFace(x, y, z, BlockFace.UP, data, vertices, texCoords, indices, index, 0);
								if (chunk.isLocalTileAir(x, y - 1, z)) index = TileBuilder.addFace(x, y, z, BlockFace.DOWN, data, vertices, texCoords, indices, index, 0);
							}
						}
					}
				}
		}
		
		return index;
	}
	
	
}
