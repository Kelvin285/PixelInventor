package kmerrill285.PixelInventor.game.client.rendering.chunk;

import java.util.ArrayList;

import kmerrill285.PixelInventor.game.client.rendering.BlockFace;
import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.textures.Textures;
import kmerrill285.PixelInventor.game.tile.Tile;

public class BlockBuilder {
	
	
	
	private static ArrayList<Float> vertices;
	private static ArrayList<Integer> indices;
	private static ArrayList<Float> texCoords;
	
	public static Mesh buildMesh(Tile tile, float x, float y, float z) {
		if (vertices == null) {
			vertices = new ArrayList<Float>();
			indices = new ArrayList<Integer>();
			texCoords = new ArrayList<Float>();
		} else {
			vertices.clear();
			indices.clear();
			texCoords.clear();
		}
		int index = 0;
		if (tile.isFullCube() && tile.isVisible()) {
			index = addFace(x, y, z, BlockFace.LEFT, tile, vertices, texCoords, indices, index);
			index = addFace(x, y, z, BlockFace.RIGHT, tile, vertices, texCoords, indices, index);
			index = addFace(x, y, z, BlockFace.BACK, tile, vertices, texCoords, indices, index);
			index = addFace(x, y, z, BlockFace.FRONT, tile, vertices, texCoords, indices, index);
			index = addFace(x, y, z, BlockFace.DOWN, tile, vertices, texCoords, indices, index);
			index = addFace(x, y, z, BlockFace.UP, tile, vertices, texCoords, indices, index);
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
	
	public static int addFace(float x, float y, float z, BlockFace face, Tile tile, ArrayList<Float> vertices, ArrayList<Float> texCoords, ArrayList<Integer> indices, int index) {
		
		float[] vertices1 = new float[] {
				x + 0.0f, y + 0.0f, z + 0.0f,
				x + 0.0f, y + 1.0f, z + 0.0f,
				x + 1.0f, y + 1.0f, z + 0.0f,
				x + 1.0f, y + 0.0f, z + 0.0f
		};
		int[] indices1 = {0, 1, 2, 2, 3, 0};
		float[] texCoords1 = new float[] {
				1.0f, 1.0f,
				1.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 1.0f
		};
		
		int scrollX = 0;
		int scrollY = 0;
		
		switch (face) {
		case FRONT:
			vertices1 = new float[] {
					x + 0.0f, y + 0.0f, z + 0.0f,
					x + 0.0f, y + 1.0f, z + 0.0f,
					x + 1.0f, y + 1.0f, z + 0.0f,
					x + 1.0f, y + 0.0f, z + 0.0f
			};
			break;
		case BACK:
			vertices1 = new float[] {
					x + 0.0f, y + 0.0f, z + 1.0f,
					x + 0.0f, y + 1.0f, z + 1.0f,
					x + 1.0f, y + 1.0f, z + 1.0f,
					x + 1.0f, y + 0.0f, z + 1.0f
			};
			break;
		case LEFT:
			vertices1 = new float[] {
					x + 0.0f, y + 0.0f, z + 0.0f,
					x + 0.0f, y + 1.0f, z + 0.0f,
					x + 0.0f, y + 1.0f, z + 1.0f,
					x + 0.0f, y + 0.0f, z + 1.0f
			};
			break;
		case RIGHT:
			vertices1 = new float[] {
					x + 1.0f, y + 0.0f, z + 0.0f,
					x + 1.0f, y + 1.0f, z + 0.0f,
					x + 1.0f, y + 1.0f, z + 1.0f,
					x + 1.0f, y + 0.0f, z + 1.0f
			};
			break;
		case DOWN:
			vertices1 = new float[] {
					x + 0.0f, y + 0.0f, z + 0.0f,
					x + 0.0f, y + 0.0f, z + 1.0f,
					x + 1.0f, y + 0.0f, z + 1.0f,
					x + 1.0f, y + 0.0f, z + 0.0f
			};
			break;
		case UP:
			vertices1 = new float[] {
					x + 0.0f, y + 1.0f, z + 0.0f,
					x + 0.0f, y + 1.0f, z + 1.0f,
					x + 1.0f, y + 1.0f, z + 1.0f,
					x + 1.0f, y + 1.0f, z + 0.0f
			};
			break;
		}
		
		
		for (int i = 0; i < texCoords1.length; i+=2) {
			texCoords1[i] /= (float)tile.getWidth();
			texCoords1[i + 1] /= (float)tile.getHeight();
			texCoords1[i] += (1.0f / (float)tile.getWidth()) * ((scrollX) % tile.getWidth());
			texCoords1[i + 1] += (1.0f / (float)tile.getHeight()) * ((scrollY) % tile.getHeight());
		}
		texCoords1 = Textures.TILES.convertToUV(texCoords1, tile.getTextureFor(face));
		for (float f : vertices1) {
			vertices.add(f);
		}
		for (int i : indices1) {
			indices.add(i + index);
		}
		
		for (float f : texCoords1) {
			texCoords.add(f);
		}
		return index + vertices1.length / 3;
	}
	
}
