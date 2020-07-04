package kmerrill285.Inignoto.game.client.rendering.chunk;

import java.util.ArrayList;

import custom_models.CustomModelLoader;
import custom_models.Model;
import kmerrill285.Inignoto.game.client.rendering.BlockFace;
import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.client.rendering.textures.Textures;
import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.chunk.TileData;

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
		TileData data = new TileData(tile.getID());
		if (tile.getModel().isEmpty()) {
			if (tile.isFullCube() && tile.isVisible()) {
				index = addFace(x, y, z, BlockFace.LEFT, data, vertices, texCoords, indices, index);
				index = addFace(x, y, z, BlockFace.RIGHT, data, vertices, texCoords, indices, index);
				index = addFace(x, y, z, BlockFace.BACK, data, vertices, texCoords, indices, index);
				index = addFace(x, y, z, BlockFace.FRONT, data, vertices, texCoords, indices, index);
				index = addFace(x, y, z, BlockFace.DOWN, data, vertices, texCoords, indices, index);
				index = addFace(x, y, z, BlockFace.UP, data, vertices, texCoords, indices, index);
			}
		} else {
			Model model = CustomModelLoader.getOrLoadModel(tile.getModel().split(":")[0], tile.getModel().split(":")[1], Textures.TILES.texture);
			model.combine(Textures.TILES.texture);
			float[] tc = model.getParts().get(0).mesh.getTexCoords();
			float[] f = Textures.TILES.convertToUV(new float[] {0, 0, 0, 1, 1, 0, 1, 1}, tile.getTextureFor(BlockFace.FRONT));
			for (int i = 0; i < tc.length / 2; i++) {
				int u = i;
				int v = i + 1;
				tc[u] += f[0];
				tc[v] += f[1];
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
	
	public static int addFace(float x, float y, float z, BlockFace face, TileData data, ArrayList<Float> vertices, ArrayList<Float> texCoords, ArrayList<Integer> indices, int index) {
		if (data.getMiningTime() > 0) {
			index = addMiningFace((int)x, (int)y, (int)z, face, data.getMiningTime(), vertices, indices, texCoords, index);
		}
		Tile tile = Tiles.getTile(data.getTile());
		
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
		
		int scrollX = (int)x;
		int scrollY = (int)y;
		
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
			scrollX = (int)z;
			break;
		case RIGHT:
			vertices1 = new float[] {
					x + 1.0f, y + 0.0f, z + 0.0f,
					x + 1.0f, y + 1.0f, z + 0.0f,
					x + 1.0f, y + 1.0f, z + 1.0f,
					x + 1.0f, y + 0.0f, z + 1.0f
			};
			scrollX = (int)z;
			break;
		case DOWN:
			vertices1 = new float[] {
					x + 0.0f, y + 0.0f, z + 0.0f,
					x + 0.0f, y + 0.0f, z + 1.0f,
					x + 1.0f, y + 0.0f, z + 1.0f,
					x + 1.0f, y + 0.0f, z + 0.0f
			};
			scrollY = (int)z;
			break;
		case UP:
			vertices1 = new float[] {
					x + 0.0f, y + 1.0f, z + 0.0f,
					x + 0.0f, y + 1.0f, z + 1.0f,
					x + 1.0f, y + 1.0f, z + 1.0f,
					x + 1.0f, y + 1.0f, z + 0.0f
			};
			scrollY = (int)z;
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

	private static int addMiningFace(int x, int y, int z, BlockFace face, float progress, ArrayList<Float> vertices, ArrayList<Integer> indices, ArrayList<Float> texCoords, int index) {
		if (progress <= 0) return index;
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
		
		int scrollX = (int)(5 * (progress / 100.0f));
		int scrollY = 0;
		
		switch (face) {
		case FRONT:
			vertices1 = new float[] {
					x + 0.0f, y + 0.0f, z + 0.0f - 0.01f,
					x + 0.0f, y + 1.0f, z + 0.0f - 0.01f,
					x + 1.0f, y + 1.0f, z + 0.0f - 0.01f,
					x + 1.0f, y + 0.0f, z + 0.0f - 0.01f
			};
			break;
		case BACK:
			vertices1 = new float[] {
					x + 0.0f, y + 0.0f, z + 1.01f,
					x + 0.0f, y + 1.0f, z + 1.01f,
					x + 1.0f, y + 1.0f, z + 1.01f,
					x + 1.0f, y + 0.0f, z + 1.01f
			};
			break;
		case LEFT:
			vertices1 = new float[] {
					x + 0.0f - 0.01f, y + 0.0f, z + 0.0f,
					x + 0.0f - 0.01f, y + 1.0f, z + 0.0f,
					x + 0.0f - 0.01f, y + 1.0f, z + 1.0f,
					x + 0.0f - 0.01f, y + 0.0f, z + 1.0f
			};
			break;
		case RIGHT:
			vertices1 = new float[] {
					x + 1.0f + 0.01f, y + 0.0f, z + 0.0f,
					x + 1.0f + 0.01f, y + 1.0f, z + 0.0f,
					x + 1.0f + 0.01f, y + 1.0f, z + 1.0f,
					x + 1.0f + 0.01f, y + 0.0f, z + 1.0f
			};
			break;
		case DOWN:
			vertices1 = new float[] {
					x + 0.0f, y + 0.0f - 0.01f, z + 0.0f,
					x + 0.0f, y + 0.0f - 0.01f, z + 1.0f,
					x + 1.0f, y + 0.0f - 0.01f, z + 1.0f,
					x + 1.0f, y + 0.0f - 0.01f, z + 0.0f
			};
			break;
		case UP:
			vertices1 = new float[] {
					x + 0.0f, y + 1.0f + 0.01f, z + 0.0f,
					x + 0.0f, y + 1.0f + 0.01f, z + 1.0f,
					x + 1.0f, y + 1.0f + 0.01f, z + 1.0f,
					x + 1.0f, y + 1.0f + 0.01f, z + 0.0f
			};
			break;
		}
		
		float width = 5;
		float height = 1;
		
		for (int i = 0; i < texCoords1.length; i+=2) {
			texCoords1[i] /= width;
			texCoords1[i + 1] /= height;
			texCoords1[i] += (1.0f / width) * ((scrollX) % width);
			texCoords1[i + 1] += (1.0f / height) * ((scrollY) % height);
		}
		texCoords1 = Textures.TILES.convertToUV(texCoords1, Textures.MINING_LOCATION);
		
		for (float f : vertices1) {
			vertices.add(f);
		}
		
		for (int i : indices1) {
			indices.add(i + index);
		}
		index += vertices1.length / 3;
		
		for (float f : texCoords1) {
			texCoords.add(f);
		}
		return index;
	}
}
