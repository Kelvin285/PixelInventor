package kmerrill285.PixelInventor.game.client.rendering.chunk;

import java.util.ArrayList;

import kmerrill285.PixelInventor.game.client.rendering.BlockFace;
import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.textures.Textures;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.world.chunk.Chunk;

public class ChunkMeshBuilder {
	
	
	
	private static ArrayList<Float> vertices;
	private static ArrayList<Integer> indices;
	private static ArrayList<Float> texCoords;
	
	private static int index = 0;
	
	public static Mesh buildMesh(Chunk chunk) {
		vertices = new ArrayList<Float>();
		indices = new ArrayList<Integer>();
		texCoords = new ArrayList<Float>();
		index = 0;
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int y = 0; y < Chunk.SIZE; y++) {
				for (int z = 0; z < Chunk.SIZE; z++) {
					Tile tile = chunk.getTile(x, y, z);
					
					if (tile.isFullCube() && tile.isVisible()) {
						String name = chunk.getTile(x, y, z).getName();
						if (!chunk.getTile(x - 1, y, z).isFullCube()) addFace(x, y, z, BlockFace.LEFT, tile);
						if (!chunk.getTile(x + 1, y, z).isFullCube()) addFace(x, y, z, BlockFace.RIGHT, tile);
						if (!chunk.getTile(x, y, z + 1).isFullCube()) addFace(x, y, z, BlockFace.BACK, tile);
						if (!chunk.getTile(x, y, z - 1).isFullCube()) addFace(x, y, z, BlockFace.FRONT, tile);
						if (!chunk.getTile(x, y - 1, z).isFullCube()) addFace(x, y, z, BlockFace.DOWN, tile);
						if (!chunk.getTile(x, y + 1, z).isFullCube()) addFace(x, y, z, BlockFace.UP, tile);
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
	
	private static void addFace(int x, int y, int z, BlockFace face, Tile tile) {
		
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
		
		int size = ChunkMeshBuilder.vertices.size();
		for (float f : vertices) {
			ChunkMeshBuilder.vertices.add(f);
		}
		
		for (int i : indices) {
			ChunkMeshBuilder.indices.add(i + index);
		}
		index += vertices.length / 3;
		
		for (float f : texCoords) {
			ChunkMeshBuilder.texCoords.add(f);
		}
	}
}
