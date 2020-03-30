package kmerrill285.PixelInventor.game.client.rendering.chunk;

import java.util.ArrayDeque;
import java.util.ArrayList;

import org.joml.Vector3i;

import kmerrill285.PixelInventor.game.client.rendering.BlockFace;
import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.textures.Textures;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.game.world.chunk.Chunk;
import kmerrill285.PixelInventor.game.world.chunk.Megachunk;
import kmerrill285.PixelInventor.game.world.chunk.TileData;

public class MegachunkBuilder {

	
	public static Mesh buildMegachunk(Megachunk megachunk) {
		if (megachunk.getVoxels() <= 0) return null;
		ArrayList<Float> vertices = new ArrayList<Float>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<Float> texCoords = new ArrayList<Float>();
		int index = 0;
		for (int x = 0; x < Megachunk.SIZE; x++) {
			for (int z = 0; z < Megachunk.SIZE; z++) {
				Chunk chunk = megachunk.getLocalChunk(x, z);
				if (chunk != null) {
					chunk.removeMesh(chunk.mesh);
					chunk.mesh = buildChunk(chunk);
					index = buildChunk(chunk, vertices, indices, texCoords, index);
				}
			}
		}
		
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
	
	public static Mesh buildChunk(Chunk chunk) {
		if (chunk.voxels <= 0) return null;
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
		if (chunk.voxels <= 0) return index;
		Vector3i pos = chunk.getWorldPos();
		for (int i = 0; i < Chunk.SIZE_Y / Chunk.SIZE; i++) {
				for (int x = 0; x < Chunk.SIZE; x++) {
					for (int y = i * Chunk.SIZE; y < i * Chunk.SIZE + Chunk.SIZE; y++) {
						for (int z = 0; z < Chunk.SIZE; z++) {
							TileData data = chunk.getTileData(x, y, z, false);
							Tile tile = Tiles.getTile(data.getTile());
							if (tile.isFullCube() && tile.isVisible()) {
								if (chunk.isLocalTileNotFull(x - 1, y, z)) index = BlockBuilder.addFace(x + pos.x, y + pos.y, z + pos.z, BlockFace.LEFT, data, vertices, texCoords, indices, index); 
								if (chunk.isLocalTileNotFull(x + 1, y, z)) index = BlockBuilder.addFace(x + pos.x, y + pos.y, z + pos.z, BlockFace.RIGHT, data, vertices, texCoords, indices, index);
								if (chunk.isLocalTileNotFull(x, y, z - 1)) index = BlockBuilder.addFace(x + pos.x, y + pos.y, z + pos.z, BlockFace.FRONT, data, vertices, texCoords, indices, index);
								if (chunk.isLocalTileNotFull(x, y, z + 1)) index = BlockBuilder.addFace(x + pos.x, y + pos.y, z + pos.z, BlockFace.BACK, data, vertices, texCoords, indices, index);
								if (chunk.isLocalTileNotFull(x, y + 1, z)) index = BlockBuilder.addFace(x + pos.x, y + pos.y, z + pos.z, BlockFace.UP, data, vertices, texCoords, indices, index);
								if (chunk.isLocalTileNotFull(x, y - 1, z)) index = BlockBuilder.addFace(x + pos.x, y + pos.y, z + pos.z, BlockFace.DOWN, data, vertices, texCoords, indices, index);
							}
						}
					}
				}
		}
		
		return index;
	}

	private static ArrayDeque<Chunk> queue = new ArrayDeque<Chunk>();
	
	public static void updateQueue() {
		if (queue.size() <= 0) return;
		Chunk c = queue.pop();
		if (c != null) {
			c.mesh = MegachunkBuilder.buildChunk(c);
			c.setTiles(null);
		}
	}
	
	public static void queueChunk(Chunk chunk) {
		if (!queue.contains(chunk))
			queue.push(chunk);
	}
	
	
}
