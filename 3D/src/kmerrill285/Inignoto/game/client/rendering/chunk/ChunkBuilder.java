package kmerrill285.Inignoto.game.client.rendering.chunk;

import java.util.ArrayList;

import kmerrill285.Inignoto.game.client.rendering.BlockFace;
import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.client.rendering.textures.Textures;
import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.tile.Tile.TileRayTraceType;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.game.world.chunk.Chunk;
import kmerrill285.Inignoto.game.world.chunk.TileData;

public class ChunkBuilder {

	
	public static Mesh buildChunk(Chunk chunk, boolean cascade) {
		
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
		index = buildChunk(chunk, vertices, indices, texCoords, index);
		
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
	final int FULL = 16 * 16 * 16;
	
	public static int buildChunk(Chunk chunk, ArrayList<Float> vertices, ArrayList<Integer> indices, ArrayList<Float> texCoords, int index) {
		for (int i = 0; i < Chunk.SIZE_Y / Chunk.SIZE; i++) {
				for (int x = 0; x < Chunk.SIZE; x++) {
					for (int y = i * Chunk.SIZE; y < i * Chunk.SIZE + Chunk.SIZE; y++) {
						for (int z = 0; z < Chunk.SIZE; z++) {
							TileData data = chunk.getTileData(x, y, z, false);
							Tile tile = Tiles.getTile(data.getTile());
							if (tile.isFullCube() && tile.isVisible() && tile.getRayTraceType() == TileRayTraceType.SOLID) {
								if (chunk.isLocalTileNotFull(x - 1, y, z)) index = BlockBuilder.addFace(x, y, z, BlockFace.LEFT, data, vertices, texCoords, indices, index); 
								if (chunk.isLocalTileNotFull(x + 1, y, z)) index = BlockBuilder.addFace(x, y, z, BlockFace.RIGHT, data, vertices, texCoords, indices, index);
								if (chunk.isLocalTileNotFull(x, y, z - 1)) index = BlockBuilder.addFace(x, y, z, BlockFace.FRONT, data, vertices, texCoords, indices, index);
								if (chunk.isLocalTileNotFull(x, y, z + 1)) index = BlockBuilder.addFace(x, y, z, BlockFace.BACK, data, vertices, texCoords, indices, index);
								if (chunk.isLocalTileNotFull(x, y + 1, z)) index = BlockBuilder.addFace(x, y, z, BlockFace.UP, data, vertices, texCoords, indices, index);
								if (chunk.isLocalTileNotFull(x, y - 1, z)) index = BlockBuilder.addFace(x, y, z, BlockFace.DOWN, data, vertices, texCoords, indices, index);
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
	
	private static void cascade(int x, int y, int z, World world) {
		Chunk c = world.getChunk(x, y, z);
		if (c != null) {
			if (c.generated) {
				c.mesh = buildChunk(c, false);
				c.waterMesh = buildLiquidChunk(c);
				c.generated = true;
			}
		}
	}
	
	public static int buildLiquidChunk(Chunk chunk, ArrayList<Float> vertices, ArrayList<Integer> indices, ArrayList<Float> texCoords, int index) {
//		if (chunk.voxels <= 0) return index;
		for (int i = 0; i < Chunk.SIZE_Y / Chunk.SIZE; i++) {
				for (int x = 0; x < Chunk.SIZE; x++) {
					for (int y = i * Chunk.SIZE; y < i * Chunk.SIZE + Chunk.SIZE; y++) {
						for (int z = 0; z < Chunk.SIZE; z++) {
							TileData data = chunk.getTileData(x, y, z, false);
							Tile tile = Tiles.getTile(data.getTile());
							if (tile.getRayTraceType() == TileRayTraceType.LIQUID) {
								if (chunk.isLocalTileAir(x - 1, y, z)) index = BlockBuilder.addFace(x, y, z, BlockFace.LEFT, data, vertices, texCoords, indices, index); 
								if (chunk.isLocalTileAir(x + 1, y, z)) index = BlockBuilder.addFace(x, y, z, BlockFace.RIGHT, data, vertices, texCoords, indices, index);
								if (chunk.isLocalTileAir(x, y, z - 1)) index = BlockBuilder.addFace(x, y, z, BlockFace.FRONT, data, vertices, texCoords, indices, index);
								if (chunk.isLocalTileAir(x, y, z + 1)) index = BlockBuilder.addFace(x, y, z, BlockFace.BACK, data, vertices, texCoords, indices, index);
								if (chunk.isLocalTileAir(x, y + 1, z)) index = BlockBuilder.addFace(x, y, z, BlockFace.UP, data, vertices, texCoords, indices, index);
								if (chunk.isLocalTileAir(x, y - 1, z)) index = BlockBuilder.addFace(x, y, z, BlockFace.DOWN, data, vertices, texCoords, indices, index);
							}
						}
					}
				}
		}
		
		return index;
	}
	
	
}
