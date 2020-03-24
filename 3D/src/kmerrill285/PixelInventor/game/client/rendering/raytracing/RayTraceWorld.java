package kmerrill285.PixelInventor.game.client.rendering.raytracing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL45;

import kmerrill285.PixelInventor.PixelInventor;
import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.settings.Settings;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.game.world.chunk.Chunk;
import kmerrill285.PixelInventor.game.world.chunk.generator.ChunkGenerator;
import kmerrill285.PixelInventor.game.world.chunk.TileData;

public class RayTraceWorld {
	public int VBUFFER = -1;
	public int CBUFFER = -1;
	public int OBUFFER = -1;
	public int TBUFFER = -1;
	
	public boolean built = false;
	
	public int OCTDIV = 4;
	public int CHUNK_SIZE = Chunk.SIZE;
	public int OCT_SIZE = CHUNK_SIZE / OCTDIV;
	public int NUM_CHUNKS = Settings.RAYTRACE_VIEW;
	public int NUM_OCTS = NUM_CHUNKS * OCTDIV;
	public int SIZE = CHUNK_SIZE * NUM_CHUNKS;
	
	public Tile[] tiles = new Tile[SIZE * SIZE * SIZE];
	public int[] DATA = new int[SIZE * SIZE * SIZE];

	public int[] CHUNKS = new int[NUM_CHUNKS * NUM_CHUNKS * NUM_CHUNKS];
	public int[] OCTS = new int[NUM_OCTS * NUM_OCTS * NUM_OCTS];
	public static ArrayList<Float> TEX_COORDS = new ArrayList<Float>();
	
	public ChunkGenerator generator;
	
	public boolean needsRebuilding = true;
	public boolean building = false;
	
	public int CX, CY, CZ;
	public int LCX, LCY, LCZ;
	
	private boolean RUNNING = true;
	
	
	public RayTraceWorld() {
		generator = new ChunkGenerator(this, new Random().nextLong());
		updatePosition();
		clearVoxels();
		if (!built) {
			VBUFFER = GL43.glGenBuffers();
			CBUFFER = GL43.glGenBuffers();
			OBUFFER = GL43.glGenBuffers();
			TBUFFER = GL43.glGenBuffers();
			built = true;
			
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
			
			if (tex_coords == null) {
				tex_coords = new float[RayTraceWorld.TEX_COORDS.size()];
				for (int i = 0; i < RayTraceWorld.TEX_COORDS.size(); i++) {
					tex_coords[i] = RayTraceWorld.TEX_COORDS.get(i);
				}
			}
			
			GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, TBUFFER);
			GL45.glNamedBufferStorage(TBUFFER, tex_coords, GL45.GL_DYNAMIC_STORAGE_BIT);
			GL45.glBindBufferBase(GL45.GL_SHADER_STORAGE_BUFFER, 5, TBUFFER);
			GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
		}
	}
	
	public void clearVoxels() {
		Arrays.fill(tiles, 0, SIZE * SIZE * SIZE, Tiles.AIR);
		Arrays.fill(DATA, 0, SIZE * SIZE * SIZE, Tiles.AIR.getID());
		Arrays.fill(CHUNKS, 0, NUM_CHUNKS * NUM_CHUNKS * NUM_CHUNKS, 0);
		Arrays.fill(OCTS, 0, NUM_OCTS * NUM_OCTS * NUM_OCTS, 0);
	}
	
	public void updateView() {
		if (!building) return;
//		this.clearVoxels();
		updateRenderer();
		Vector3f forwards = Camera.getForward();
		int steps = NUM_CHUNKS / 2;
		int size = 3;
		ArrayDeque<Chunk> alreadyChecked = new ArrayDeque<Chunk>();
		for (int i = 1; i < steps; ++i) {
			int xx = (int)Math.floor((Camera.position.x + forwards.x * (CHUNK_SIZE / 2) * steps) / CHUNK_SIZE);
			int yy = (int)Math.floor((Camera.position.y + forwards.y * (CHUNK_SIZE / 2) * steps) / CHUNK_SIZE);
			int zz = (int)Math.floor((Camera.position.z + forwards.z * (CHUNK_SIZE / 2) * steps) / CHUNK_SIZE);
						
			for (int x = -size; x < size; ++x) {
				for (int y = -size; y < size; ++y) {
					for (int z = -size; z < size; ++z) {
						int cx = xx + x;
						int cy = yy + y;
						int cz = zz + z;
						Chunk chunk = PixelInventor.game.world.getChunk(cx, cy, cz);
						if (!alreadyChecked.contains(chunk))
							if (chunk != null) {
							updateChunk(cx, cy, cz);
							alreadyChecked.push(chunk);
							
						}
					}
				}
			}
			size++;
		}
		building = false;
	}
	
	public void loadChunk(int cx, int cy, int cz) {
		updatePosition();
		generator.generateChunk(cx, cy, cz);
	}
	
	public void updatePosition() {
		CX = (int)(Camera.position.x) / CHUNK_SIZE - (NUM_CHUNKS / 2);
		CY = (int)(Camera.position.y) / CHUNK_SIZE - (NUM_CHUNKS / 2);
		CZ = (int)(Camera.position.z) / CHUNK_SIZE - (NUM_CHUNKS / 2);
	}
	
	public void setTile(int x, int y, int z, Tile tl) {
		Tile tile = tl;
		if (tile == null) tile = Tiles.AIR;
		if (x >= 0 && y >= 0 && z >= 0 && x < SIZE && y < SIZE && z < SIZE) {
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
		
		if (x >= 0 && y >= 0 && z >= 0 && x < SIZE && y < SIZE && z < SIZE) {
			return tiles[x + y * SIZE + z * SIZE * SIZE];
		}
		return Tiles.AIR;
	}
	
	public void updateRenderer() {
		for (int cx = NUM_CHUNKS / 2 - 2; cx < NUM_CHUNKS / 2 + 2; cx++) {
			for (int cy = NUM_CHUNKS / 2 - 2; cy < NUM_CHUNKS / 2 + 2; cy++) {
				for (int cz = NUM_CHUNKS / 2 - 2; cz < NUM_CHUNKS / 2 + 2; cz++) {
					updateChunk(cx + CX, cy + CY, cz + CZ);
				}
			}
		}
	}
	
	public void updateChunk(int xx, int yy, int zz) {
		Chunk chunk = PixelInventor.game.world.getChunk(xx, yy, zz);
		if (chunk == null) return;
		int cx = chunk.getX() - CX;
		int cy = chunk.getY() - CY;
		int cz = chunk.getZ() - CZ;
		
		
		if (cx >= 0 && cy >= 0 && cz >= 0 && cx < NUM_CHUNKS && cy < NUM_CHUNKS && cz < NUM_CHUNKS) {
//			for (int i = 0; i < chunk.rayMesh.size(); i++) {
//				TileData data = chunk.rayMesh.get(i);
//				int X = data.x + cx * CHUNK_SIZE;
//				int Y = data.y + cy * CHUNK_SIZE;
//				int Z = data.z + cz * CHUNK_SIZE;
//				setTile(X, Y, Z, data.tile);
//			}
			for (int x = 0; x < CHUNK_SIZE; x++) {
				for (int y = 0; y < CHUNK_SIZE; y++) {
					for (int z = 0; z < CHUNK_SIZE; z++) {
						int X = x + cx * CHUNK_SIZE;
						int Y = y + cy * CHUNK_SIZE;
						int Z = z + cz * CHUNK_SIZE;
						setTile(X, Y, Z, chunk.getTile(x, y, z));
					}
				}
			}
		}
		needsRebuilding = true;
	}
	
	private float[] tex_coords;
	public boolean shouldRerender = true;
	public boolean threadedRender = false;
	public void build(RayShader shader) {
		if (building) return;
		building = true;
		updatePosition();
		if (LCX != CX || LCY != CY || LCZ != CZ) {
			shouldRerender = true;
			LCX = CX;
			LCY = CY;
			LCZ = CZ;
			
			Vector3f forwards = Camera.getForward();
			int steps = 1;
			int size = 3;
			for (int i = 0; i < steps; ++i) {
				int xx = (int)Math.floor((Camera.position.x + forwards.x * (CHUNK_SIZE / 2) * steps) / CHUNK_SIZE);
				int yy = (int)Math.floor((Camera.position.y + forwards.y * (CHUNK_SIZE / 2) * steps) / CHUNK_SIZE);
				int zz = (int)Math.floor((Camera.position.z + forwards.z * (CHUNK_SIZE / 2) * steps) / CHUNK_SIZE);
				
				for (int x = -size; x < size; ++x) {
					for (int y = -size; y < size; ++y) {
						for (int z = -size; z < size; ++z) {
							int cx = xx + x;
							int cy = yy + y;
							int cz = zz + z;
							updateChunk(cx, cy, cz);
						}
					}
				}
			}
			
		} else {
			shouldRerender = false;
		}
		
		
		
		GL45.glBindBuffer(GL45.GL_SHADER_STORAGE_BUFFER, VBUFFER);
	    GL45.glBufferSubData(GL45.GL_SHADER_STORAGE_BUFFER, 0, DATA);
	    GL45.glBindBuffer(GL45.GL_SHADER_STORAGE_BUFFER, 0);
	    
	    GL45.glBindBuffer(GL45.GL_SHADER_STORAGE_BUFFER, CBUFFER);
	    GL45.glBufferSubData(GL45.GL_SHADER_STORAGE_BUFFER, 0, CHUNKS);
	    GL45.glBindBuffer(GL45.GL_SHADER_STORAGE_BUFFER, 0);
	    
	    GL45.glBindBuffer(GL45.GL_SHADER_STORAGE_BUFFER, OBUFFER);
	    GL45.glBufferSubData(GL45.GL_SHADER_STORAGE_BUFFER, 0, OCTS);
	    GL45.glBindBuffer(GL45.GL_SHADER_STORAGE_BUFFER, 0);
		
		shader.setInt("width", SIZE);
		shader.setInt("height", SIZE);
		shader.setInt("length", SIZE);
		shader.setInt("chunk_size", CHUNK_SIZE);
		shader.setInt("oct_size", OCT_SIZE);
		shader.setVec3i("world_position", CX, CY, CZ);
		
	}
	
	public void dispose() {
		if (built == false) {
			GL43.glDeleteBuffers(VBUFFER);
			GL43.glDeleteBuffers(CBUFFER);
			GL43.glDeleteBuffers(OBUFFER);
			built = false;
		}
		RUNNING = false;
	}
	
}
