package kmerrill285.PixelInventor.game.world.chunk;

import java.util.Arrays;

import org.joml.Vector3f;

import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.MeshRenderer;
import kmerrill285.PixelInventor.game.client.rendering.chunk.MegachunkBuilder;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.world.World;

public class Megachunk {
	private int x, y, z;
	
	public static final int SIZE = 16;
	private static final int NUM_CHUNKS = SIZE * SIZE;

	private static Chunk[] chunks;
		
	private int voxels = 0;
	
	private final World world;
	
	private Mesh mesh;
	
	private Vector3f pos;
	
	public Megachunk(int x, int y, int z, World world) {
		this.x = x;
		this.y = y;
		this.z = z;
		pos = new Vector3f(x * SIZE * Chunk.SIZE, y * Chunk.SIZE_Y, z * SIZE * Chunk.SIZE);
		this.world = world;
		chunks = new Chunk[NUM_CHUNKS];
		generate();
	}
	
	public void generate() {
		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				setLocalChunk(x, z, world.getChunkGenerator().generateChunk(this, x, z));
			}
		}
		mesh = MegachunkBuilder.buildMegachunk(this);
	}
	
	public void setLocalChunk(int x, int z, Chunk chunk) {
		if (x >= 0 && z >= 0 && x < SIZE && z < SIZE) {
			chunks[x + z * SIZE] = chunk;
		}
	}
	
	public Chunk getLocalChunk(int x, int z) {
		if (x >= 0 && z >= 0 && x < SIZE && z < SIZE) {
			return chunks[x + z * SIZE];
		}
		return null;
	}
	
	public int getChunkValue(int x, int y, int z) {
		Chunk local = getLocalChunk(x, z);
		return local != null ? local.getChunk(y) : 0;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public boolean isEmpty() {
		return getVoxels() <= 0;
	}
	
	public void render(ShaderProgram shader) {
		if (mesh != null)
			MeshRenderer.renderMesh(mesh, pos, shader);
	}
	
	public void dispose() {
		Arrays.fill(chunks, null);
		if (mesh != null)
		mesh.dispose();
	}

	public int getVoxels() {
		return voxels;
	}

	public void setVoxels(int voxels) {
		this.voxels = voxels;
	}
}
