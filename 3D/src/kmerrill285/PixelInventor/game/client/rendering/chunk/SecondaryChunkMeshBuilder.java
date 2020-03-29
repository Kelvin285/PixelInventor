package kmerrill285.PixelInventor.game.client.rendering.chunk;

import java.util.ArrayList;

import kmerrill285.PixelInventor.PixelInventor;
import kmerrill285.PixelInventor.game.client.rendering.BlockFace;
import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.textures.Textures;
import kmerrill285.PixelInventor.game.settings.Settings;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.world.chunk.Chunk;
import kmerrill285.PixelInventor.game.world.chunk.TileData;

public class SecondaryChunkMeshBuilder {
	
	
	private static ArrayList<Chunk> queue = new ArrayList<Chunk>();
	private static ArrayList<Float> vertices;
	private static ArrayList<Integer> indices;
	private static ArrayList<Float> texCoords;
	
	private static int index = 0;
	
	public static Mesh buildMesh(Chunk chunk) {
		vertices = new ArrayList<Float>();
		indices = new ArrayList<Integer>();
		texCoords = new ArrayList<Float>();
		index = 0;
		chunk.rayMesh.clear();
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int y = 0; y < Chunk.SIZE; y++) {
				for (int z = 0; z < Chunk.SIZE; z++) {
					Tile tile = chunk.getTile(x, y, z);
					
					if (tile.isFullCube() && tile.isVisible()) {
						if (!chunk.getTile(x - 1, y, z).isFullCube()) addFace(x, y, z, BlockFace.LEFT, tile, chunk);
						if (!chunk.getTile(x + 1, y, z).isFullCube()) addFace(x, y, z, BlockFace.RIGHT, tile, chunk);
						if (!chunk.getTile(x, y, z + 1).isFullCube()) addFace(x, y, z, BlockFace.BACK, tile, chunk);
						if (!chunk.getTile(x, y, z - 1).isFullCube()) addFace(x, y, z, BlockFace.FRONT, tile, chunk);
						if (!chunk.getTile(x, y - 1, z).isFullCube()) addFace(x, y, z, BlockFace.DOWN, tile, chunk);
						if (!chunk.getTile(x, y + 1, z).isFullCube()) addFace(x, y, z, BlockFace.UP, tile, chunk);
						
					}
					
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
	
	private static void addFace(int x, int y, int z, BlockFace face, Tile tile, Chunk chunk) {
		float mining_progress = chunk.getMiningProgress(x, y, z);
		if (mining_progress > 0) {
			addMiningFace(x, y, z, face, mining_progress);
		}
		float[] vertices = new float[] {
				x + 0.0f, y + 0.0f, z + 0.0f,
				x + 0.0f, y + 1.0f, z + 0.0f,
				x + 1.0f, y + 1.0f, z + 0.0f,
				x + 1.0f, y + 0.0f, z + 0.0f
		};
		int[] indices = {0, 1, 2, 2, 3, 0};
		float[] texCoords = new float[] {
				1.0f, 1.0f,
				1.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 1.0f
		};
		
		int scrollX = x;
		int scrollY = y;
		
		switch (face) {
		case FRONT:
			vertices = new float[] {
					x + 0.0f, y + 0.0f, z + 0.0f,
					x + 0.0f, y + 1.0f, z + 0.0f,
					x + 1.0f, y + 1.0f, z + 0.0f,
					x + 1.0f, y + 0.0f, z + 0.0f
			};
			break;
		case BACK:
			vertices = new float[] {
					x + 0.0f, y + 0.0f, z + 1.0f,
					x + 0.0f, y + 1.0f, z + 1.0f,
					x + 1.0f, y + 1.0f, z + 1.0f,
					x + 1.0f, y + 0.0f, z + 1.0f
			};
			break;
		case LEFT:
			vertices = new float[] {
					x + 0.0f, y + 0.0f, z + 0.0f,
					x + 0.0f, y + 1.0f, z + 0.0f,
					x + 0.0f, y + 1.0f, z + 1.0f,
					x + 0.0f, y + 0.0f, z + 1.0f
			};
			scrollX = z;
			break;
		case RIGHT:
			vertices = new float[] {
					x + 1.0f, y + 0.0f, z + 0.0f,
					x + 1.0f, y + 1.0f, z + 0.0f,
					x + 1.0f, y + 1.0f, z + 1.0f,
					x + 1.0f, y + 0.0f, z + 1.0f
			};
			scrollX = z;
			break;
		case DOWN:
			vertices = new float[] {
					x + 0.0f, y + 0.0f, z + 0.0f,
					x + 0.0f, y + 0.0f, z + 1.0f,
					x + 1.0f, y + 0.0f, z + 1.0f,
					x + 1.0f, y + 0.0f, z + 0.0f
			};
			scrollY = z;
			break;
		case UP:
			vertices = new float[] {
					x + 0.0f, y + 1.0f, z + 0.0f,
					x + 0.0f, y + 1.0f, z + 1.0f,
					x + 1.0f, y + 1.0f, z + 1.0f,
					x + 1.0f, y + 1.0f, z + 0.0f
			};
			scrollY = z;
			break;
		}
		
		
		for (int i = 0; i < texCoords.length; i+=2) {
			texCoords[i] /= (float)tile.getWidth();
			texCoords[i + 1] /= (float)tile.getHeight();
			texCoords[i] += (1.0f / (float)tile.getWidth()) * ((scrollX) % tile.getWidth());
			texCoords[i + 1] += (1.0f / (float)tile.getHeight()) * ((scrollY) % tile.getHeight());
		}
		texCoords = Textures.TILES.convertToUV(texCoords, tile.getTextureFor(face));
		
		for (float f : vertices) {
			SecondaryChunkMeshBuilder.vertices.add(f);
		}
		
		for (int i : indices) {
			SecondaryChunkMeshBuilder.indices.add(i + index);
		}
		index += vertices.length / 3;
		
		for (float f : texCoords) {
			SecondaryChunkMeshBuilder.texCoords.add(f);
		}
	}
	
	private static void addMiningFace(int x, int y, int z, BlockFace face, float progress) {
		if (progress <= 0) return;
		float[] vertices = new float[] {
				x + 0.0f, y + 0.0f, z + 0.0f,
				x + 0.0f, y + 1.0f, z + 0.0f,
				x + 1.0f, y + 1.0f, z + 0.0f,
				x + 1.0f, y + 0.0f, z + 0.0f
		};
		int[] indices = {0, 1, 2, 2, 3, 0};
		float[] texCoords = new float[] {
				1.0f, 1.0f,
				1.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 1.0f
		};
		
		int scrollX = (int)(5 * (progress / 100.0f));
		int scrollY = 0;
		
		switch (face) {
		case FRONT:
			vertices = new float[] {
					x + 0.0f, y + 0.0f, z + 0.0f - 0.01f,
					x + 0.0f, y + 1.0f, z + 0.0f - 0.01f,
					x + 1.0f, y + 1.0f, z + 0.0f - 0.01f,
					x + 1.0f, y + 0.0f, z + 0.0f - 0.01f
			};
			break;
		case BACK:
			vertices = new float[] {
					x + 0.0f, y + 0.0f, z + 1.01f,
					x + 0.0f, y + 1.0f, z + 1.01f,
					x + 1.0f, y + 1.0f, z + 1.01f,
					x + 1.0f, y + 0.0f, z + 1.01f
			};
			break;
		case LEFT:
			vertices = new float[] {
					x + 0.0f - 0.01f, y + 0.0f, z + 0.0f,
					x + 0.0f - 0.01f, y + 1.0f, z + 0.0f,
					x + 0.0f - 0.01f, y + 1.0f, z + 1.0f,
					x + 0.0f - 0.01f, y + 0.0f, z + 1.0f
			};
			break;
		case RIGHT:
			vertices = new float[] {
					x + 1.0f + 0.01f, y + 0.0f, z + 0.0f,
					x + 1.0f + 0.01f, y + 1.0f, z + 0.0f,
					x + 1.0f + 0.01f, y + 1.0f, z + 1.0f,
					x + 1.0f + 0.01f, y + 0.0f, z + 1.0f
			};
			break;
		case DOWN:
			vertices = new float[] {
					x + 0.0f, y + 0.0f - 0.01f, z + 0.0f,
					x + 0.0f, y + 0.0f - 0.01f, z + 1.0f,
					x + 1.0f, y + 0.0f - 0.01f, z + 1.0f,
					x + 1.0f, y + 0.0f - 0.01f, z + 0.0f
			};
			break;
		case UP:
			vertices = new float[] {
					x + 0.0f, y + 1.0f + 0.01f, z + 0.0f,
					x + 0.0f, y + 1.0f + 0.01f, z + 1.0f,
					x + 1.0f, y + 1.0f + 0.01f, z + 1.0f,
					x + 1.0f, y + 1.0f + 0.01f, z + 0.0f
			};
			break;
		}
		
		float width = 5;
		float height = 1;
		
		for (int i = 0; i < texCoords.length; i+=2) {
			texCoords[i] /= width;
			texCoords[i + 1] /= height;
			texCoords[i] += (1.0f / width) * ((scrollX) % width);
			texCoords[i + 1] += (1.0f / height) * ((scrollY) % height);
		}
		texCoords = Textures.TILES.convertToUV(texCoords, Textures.MINING_LOCATION);
		
		for (float f : vertices) {
			SecondaryChunkMeshBuilder.vertices.add(f);
		}
		
		for (int i : indices) {
			SecondaryChunkMeshBuilder.indices.add(i + index);
		}
		index += vertices.length / 3;
		
		for (float f : texCoords) {
			SecondaryChunkMeshBuilder.texCoords.add(f);
		}
	}

	public static void queueChunk(Chunk chunk) {
		queue.add(chunk);
	}
	
	public static void update() {
		ArrayList<Chunk> alreadyBuilt = new ArrayList<Chunk>();
		for (int i = 0; i < queue.size(); i++) {
			if (alreadyBuilt.contains(queue.get(i)))
				continue;
			queue.get(i).setMesh(buildMesh(queue.get(i)));
			alreadyBuilt.add(queue.get(i));
		}
		queue.clear();
	}
}

