package kmerrill285.PixelInventor.game.client.rendering.heightmap;

import java.util.ArrayList;

import org.joml.Vector3f;

import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.rendering.BlockFace;
import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.MeshRenderer;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.client.rendering.textures.Textures;
import kmerrill285.PixelInventor.game.settings.Settings;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.world.World;
import kmerrill285.PixelInventor.game.world.chunk.Chunk;

public class Heightmap {
	public World world;
			
	public Vector3f position = new Vector3f(0, 0, 0);
	public int lcx = 0;
	public int lcz = 0;
	
	public Mesh mesh;
	public Mesh newMesh;
	
	public float[] verts;
	public float[] tex;
	public int[] inds;
	
	public Heightmap(World world) {
		this.world = world;
	}
	
	public void update() {
		int size = Settings.FAR_PLANE_VIEW;

		int cx = (int)(Camera.position.x / Chunk.SIZE) - (size / 2);
		int cz = (int)(Camera.position.z / Chunk.SIZE) - (size / 2);
		
		if (lcx == cx && lcz == cz) return;
		lcx = cx;
		lcz = cz;
		
		ArrayList<Float> verts = new ArrayList<Float>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<Float> texCoords = new ArrayList<Float>();
		
		int i = 0;
		for (int x = 0; x < size; x++) {
			for (int z = 0; z < size; z++) {
				
				if (world.getChunk(cx + x, 0, cz + z) != null) continue;
				
				float height = world.getChunkGenerator().getHeight(cx * Chunk.SIZE + x * Chunk.SIZE, cz * Chunk.SIZE + z * Chunk.SIZE);
				float height2 = world.getChunkGenerator().getHeight(cx * Chunk.SIZE + x * Chunk.SIZE, cz * Chunk.SIZE + z * Chunk.SIZE + Chunk.SIZE);
				float height3 = world.getChunkGenerator().getHeight(cx * Chunk.SIZE + x * Chunk.SIZE + Chunk.SIZE, cz * Chunk.SIZE + z * Chunk.SIZE + Chunk.SIZE);
				float height4 = world.getChunkGenerator().getHeight(cx * Chunk.SIZE + x * Chunk.SIZE + Chunk.SIZE, cz * Chunk.SIZE + z * Chunk.SIZE);
								
				verts.add((float) (cx * Chunk.SIZE + x * Chunk.SIZE));
				verts.add(height);
				verts.add((float) (cz * Chunk.SIZE + z * Chunk.SIZE));
				
				verts.add((float) (cx * Chunk.SIZE + x * Chunk.SIZE));
				verts.add(height2);
				verts.add((float) (cz * Chunk.SIZE + z * Chunk.SIZE + Chunk.SIZE));
				
				verts.add((float) (cx * Chunk.SIZE + x * Chunk.SIZE + Chunk.SIZE));
				verts.add(height3);
				verts.add((float) (cz * Chunk.SIZE + z * Chunk.SIZE + Chunk.SIZE));
				
				verts.add((float) (cx * Chunk.SIZE + x * Chunk.SIZE + Chunk.SIZE));
				verts.add(height4);
				verts.add((float) (cz * Chunk.SIZE + z * Chunk.SIZE));
				
				addFace(cx * Chunk.SIZE + x * Chunk.SIZE, cz * Chunk.SIZE + z * Chunk.SIZE, texCoords);
				
				indices.add(i);
				indices.add(i + 1);
				indices.add(i + 2);
				indices.add(i + 2);
				indices.add(i + 3);
				indices.add(i);
				i+=4;
			}
		}
		
		float[] v = new float[verts.size()];
		int[] in = new int[indices.size()];
		float[] t = new float[texCoords.size()];
		
		for (int x = 0; x < v.length; x++) {
			v[x] = verts.get(x);
		}
		for (int x = 0; x < in.length; x++) {
			in[x] = indices.get(x);
		}

		for (int x = 0; x < t.length; x++) {
			t[x] = texCoords.get(x);
		}
		
		newMesh = new Mesh(v, t, in, Textures.TILES.texture);
		
		position.x = cx * Chunk.SIZE;
		position.z = cz * Chunk.SIZE;
	}
	
	private void addFace(int x, int z, ArrayList<Float> tex) {
		Tile tile = world.getChunkGenerator().getTopTile(x, z);
		BlockFace face = BlockFace.UP;
		
		float[] texCoords = new float[] {
				1.0f, 1.0f,
				1.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 1.0f
		};
		
		int scrollX = 0;
		int scrollY = 0;
		
		for (int i = 0; i < texCoords.length; i+=2) {
			texCoords[i] /= (float)tile.getWidth();
			texCoords[i + 1] /= (float)tile.getHeight();
			texCoords[i] += (1.0f / (float)tile.getWidth()) * ((scrollX) % tile.getWidth());
			texCoords[i + 1] += (1.0f / (float)tile.getHeight()) * ((scrollY) % tile.getHeight());
		}
		texCoords = Textures.TILES.convertToUV(texCoords, tile.getTextureFor(face));
		
		for (float f : texCoords) {
			tex.add(f);
		}
	}
	
	public void render(ShaderProgram shader) {
		Settings.FAR_PLANE_VIEW = 64;
		
		if (newMesh != null) {
			if (mesh != null) mesh.dispose();
			mesh = newMesh;
			newMesh = null;
		}
		
		if (mesh != null)
		{
			mesh.texture = Textures.TILES.texture;
			MeshRenderer.renderMesh(mesh, new Vector3f(0, -16, 0), shader);
			MeshRenderer.renderMesh(mesh, new Vector3f(0, 0, 0), shader);
		}
	}
	
	public void dispose() {
		if (newMesh != null) newMesh.dispose();
		if (mesh != null) mesh.dispose();
	}
	
	
}
