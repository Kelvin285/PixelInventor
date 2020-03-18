package kmerrill285.PixelInventor.game.client.rendering.chunk;

import java.util.ArrayList;

import kmerrill285.PixelInventor.game.client.rendering.BlockFace;
import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.textures.Textures;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.world.chunk.Chunk;

public class BlockBuilder {
	
	
	
	private static ArrayList<Float> vertices;
	private static ArrayList<Integer> indices;
	private static ArrayList<Float> texCoords;
	
	private static int index = 0;
	
	public static Mesh buildMesh(Tile tile, float x, float y, float z) {
		vertices = new ArrayList<Float>();
		indices = new ArrayList<Integer>();
		texCoords = new ArrayList<Float>();
		index = 0;
		
					
		if (tile.isFullCube() && tile.isVisible()) {
			addFace(x, y, z, BlockFace.LEFT, tile);
			addFace(x, y, z, BlockFace.RIGHT, tile);
			addFace(x, y, z, BlockFace.BACK, tile);
			addFace(x, y, z, BlockFace.FRONT, tile);
			addFace(x, y, z, BlockFace.DOWN, tile);
			addFace(x, y, z, BlockFace.UP, tile);
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
	
	private static void addFace(float x, float y, float z, BlockFace face, Tile tile) {
		
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
		
		int scrollX = 0;
		int scrollY = 0;
		
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
			break;
		case RIGHT:
			vertices = new float[] {
					x + 1.0f, y + 0.0f, z + 0.0f,
					x + 1.0f, y + 1.0f, z + 0.0f,
					x + 1.0f, y + 1.0f, z + 1.0f,
					x + 1.0f, y + 0.0f, z + 1.0f
			};
			break;
		case DOWN:
			vertices = new float[] {
					x + 0.0f, y + 0.0f, z + 0.0f,
					x + 0.0f, y + 0.0f, z + 1.0f,
					x + 1.0f, y + 0.0f, z + 1.0f,
					x + 1.0f, y + 0.0f, z + 0.0f
			};
			break;
		case UP:
			vertices = new float[] {
					x + 0.0f, y + 1.0f, z + 0.0f,
					x + 0.0f, y + 1.0f, z + 1.0f,
					x + 1.0f, y + 1.0f, z + 1.0f,
					x + 1.0f, y + 1.0f, z + 0.0f
			};
			break;
		}
		
		
		for (int i = 0; i < texCoords.length; i+=2) {
			texCoords[i] /= (float)tile.getWidth();
			texCoords[i + 1] /= (float)tile.getHeight();
			texCoords[i] += (1.0f / (float)tile.getWidth()) * ((scrollX) % tile.getWidth());
			texCoords[i + 1] += (1.0f / (float)tile.getHeight()) * ((scrollY) % tile.getHeight());
		}
		texCoords = Textures.TILES.convertToUV(texCoords, tile.getTextureFor(face));
		
		int size = BlockBuilder.vertices.size();
		for (float f : vertices) {
			BlockBuilder.vertices.add(f);
		}
		
		for (int i : indices) {
			BlockBuilder.indices.add(i + index);
		}
		index += vertices.length / 3;
		
		for (float f : texCoords) {
			BlockBuilder.texCoords.add(f);
		}
	}
	
}
