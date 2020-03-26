package kmerrill285.PixelInventor.game.client.rendering.chunk;

import java.util.ArrayList;

import org.joml.Vector3i;

import kmerrill285.PixelInventor.game.client.rendering.BlockFace;
import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.textures.Textures;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.game.world.chunk.Chunk;
import kmerrill285.PixelInventor.game.world.chunk.Megachunk;

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
	
	public static int buildChunk(Chunk chunk, ArrayList<Float> vertices, ArrayList<Integer> indices, ArrayList<Float> texCoords, int index) {
		Vector3i pos = chunk.getWorldPos();
		final int FULL = 16 * 16 * 16;
		
		for (int i = 0; i < Chunk.SIZE_Y / Chunk.SIZE; i++) {
			int ch = chunk.getChunk(i * Chunk.SIZE);
			if (ch >= FULL) {
				boolean render = false;
				for (int x = -1; x < 1; ++x) {
					for (int z = -1; z < 1; ++z) {
						int X = chunk.getX() + x;
						int Z = chunk.getZ() + z;
						int val = chunk.getParent().getChunkValue(X, i * Chunk.SIZE, Z);
						if (val < FULL) {
							render = true;
							break;
						}
					}
				}
				if (!render) continue;
			}
			if (ch > 0) {
				for (int x = 0; x < Chunk.SIZE; x++) {
					for (int y = i * Chunk.SIZE; y < i * Chunk.SIZE + Chunk.SIZE; y++) {
						for (int z = 0; z < Chunk.SIZE; z++) {
							Tile tile = chunk.getLocalTile(x, y, z);
							if (tile.isFullCube() && tile.isVisible()) {
								if (chunk.isLocalTileNotFull(x - 1, y, z)) index = BlockBuilder.addFace(x + pos.x, y + pos.y, z + pos.z, BlockFace.LEFT, tile, vertices, texCoords, indices, index); 
								if (chunk.isLocalTileNotFull(x + 1, y, z)) index = BlockBuilder.addFace(x + pos.x, y + pos.y, z + pos.z, BlockFace.RIGHT, tile, vertices, texCoords, indices, index);
								if (chunk.isLocalTileNotFull(x, y, z - 1)) index = BlockBuilder.addFace(x + pos.x, y + pos.y, z + pos.z, BlockFace.FRONT, tile, vertices, texCoords, indices, index);
								if (chunk.isLocalTileNotFull(x, y, z + 1)) index = BlockBuilder.addFace(x + pos.x, y + pos.y, z + pos.z, BlockFace.BACK, tile, vertices, texCoords, indices, index);
								if (chunk.isLocalTileNotFull(x, y + 1, z)) index = BlockBuilder.addFace(x + pos.x, y + pos.y, z + pos.z, BlockFace.UP, tile, vertices, texCoords, indices, index);
								if (chunk.isLocalTileNotFull(x, y - 1, z)) index = BlockBuilder.addFace(x + pos.x, y + pos.y, z + pos.z, BlockFace.DOWN, tile, vertices, texCoords, indices, index);
							}
						}
					}
				}
			}
		}
		
		return index;
	}
}
