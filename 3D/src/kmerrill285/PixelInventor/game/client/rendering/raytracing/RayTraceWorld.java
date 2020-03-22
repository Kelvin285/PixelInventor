package kmerrill285.PixelInventor.game.client.rendering.raytracing;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryUtil;

import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.game.world.chunk.Chunk;
import kmerrill285.PixelInventor.game.world.chunk.generator.ChunkGenerator;

public class RayTraceWorld {
	public int VBUFFER = -1;
	public int CBUFFER = -1;
	public int OBUFFER = -1;
	
	public boolean built = false;
	
	public int OCTDIV = 4;
	public int CHUNK_SIZE = Chunk.SIZE;
	public int OCT_SIZE = CHUNK_SIZE / OCTDIV;
	public int NUM_CHUNKS = 16;
	public int NUM_OCTS = NUM_CHUNKS * OCTDIV;
	public int SIZE = CHUNK_SIZE * NUM_CHUNKS;
	
	public Tile[] tiles = new Tile[SIZE * SIZE * SIZE];
	public int[] DATA = new int[SIZE * SIZE * SIZE];
	public int[] CHUNKS = new int[NUM_CHUNKS * NUM_CHUNKS * NUM_CHUNKS];
	public int[] OCTS = new int[NUM_OCTS * NUM_OCTS * NUM_OCTS];
	
	public ChunkGenerator generator;
	
	public boolean needsRebuilding = true;
	
	public RayTraceWorld() {
		generator = new ChunkGenerator(null, new Random().nextLong());
		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				float height = generator.getHeight(x, z) + 20;
				for (int y = 0; y < SIZE; y++) {
					setTile(x, y, z, Tiles.AIR);
					if (y < height) {
						setTile(x, y, z, Tiles.STONE);
					}
				}
			}
		}
		
	}
	
	public void setTile(int x, int y, int z, Tile tile) {
		if (x >= 0 && y >= 0 && z >= 0) {
			Tile t = tiles[x + y * SIZE + z * SIZE * SIZE];
			if (t == Tiles.AIR) {
				if (tile != Tiles.AIR) {
					int cx = x / CHUNK_SIZE;
					int cy = y / CHUNK_SIZE;
					int cz = z / CHUNK_SIZE;
					CHUNKS[cx + cy * NUM_CHUNKS + cz * NUM_CHUNKS * NUM_CHUNKS] ++;
					int ox = x / OCT_SIZE;
					int oy = y / OCT_SIZE;
					int oz = z / OCT_SIZE;
					OCTS[ox + oy * NUM_OCTS + oz * NUM_OCTS * NUM_OCTS]++;
					needsRebuilding = true;
				}
			}
			else if (t != null) {
				if (tile == Tiles.AIR) {
					int cx = x / CHUNK_SIZE;
					int cy = y / CHUNK_SIZE;
					int cz = z / CHUNK_SIZE;
					CHUNKS[cx + cy * NUM_CHUNKS + cz * NUM_CHUNKS * NUM_CHUNKS] --;
					int ox = x / OCT_SIZE;
					int oy = y / OCT_SIZE;
					int oz = z / OCT_SIZE;
					OCTS[ox + oy * NUM_OCTS + oz * NUM_OCTS * NUM_OCTS]--;
					needsRebuilding = true;
				}
			}
			tiles[x + y * SIZE + z * SIZE * SIZE] = tile;
			DATA[x + y * SIZE + z * SIZE * SIZE] = tile.getID();
			
		}
	}
	
	public Tile getTile(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0) {
			return tiles[x + y * SIZE + z * SIZE * SIZE];
		}
		return Tiles.AIR;
	}
	
	public void build(RayShader shader) {
		if (!needsRebuilding) return;
		needsRebuilding = false;
		if (!built) {
			VBUFFER = GL43.glGenBuffers();
			CBUFFER = GL43.glGenBuffers();
			OBUFFER = GL43.glGenBuffers();
			built = true;
		}
		
		GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, VBUFFER);
		GL45.glNamedBufferStorage(VBUFFER, DATA, GL45.GL_DYNAMIC_STORAGE_BIT);
		GL45.glBindBufferBase(GL45.GL_SHADER_STORAGE_BUFFER, 2, VBUFFER);
		GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
		
		GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, CBUFFER);
		GL45.glNamedBufferStorage(CBUFFER, CHUNKS, GL45.GL_DYNAMIC_STORAGE_BIT);
		GL45.glBindBufferBase(GL45.GL_SHADER_STORAGE_BUFFER, 3, CBUFFER);
		GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
		
		GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, OBUFFER);
		GL45.glNamedBufferStorage(OBUFFER, OCTS, GL45.GL_DYNAMIC_STORAGE_BIT);
		GL45.glBindBufferBase(GL45.GL_SHADER_STORAGE_BUFFER, 4, OBUFFER);
		GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
		
		shader.setInt("width", SIZE);
		shader.setInt("height", SIZE);
		shader.setInt("length", SIZE);
		shader.setInt("chunk_size", CHUNK_SIZE);
		shader.setInt("oct_size", OCT_SIZE);

	}
	
	public void dispose() {
		if (built == false) {
			GL43.glDeleteBuffers(VBUFFER);
			GL43.glDeleteBuffers(CBUFFER);
			GL43.glDeleteBuffers(OBUFFER);
		}
	}
}
