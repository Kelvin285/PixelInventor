package kmerrill285.Inignoto.game.world.chunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector3f;
import org.joml.Vector3i;

import kmerrill285.Inignoto.Inignoto;
import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.client.rendering.MeshRenderer;
import kmerrill285.Inignoto.game.client.rendering.chunk.ChunkBuilder;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.shadows.ShadowRenderer;
import kmerrill285.Inignoto.game.foliage.Foliage;
import kmerrill285.Inignoto.game.settings.Settings;
import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.tile.Tile.TileRayTraceType;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.tile.data.TileState;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.resources.Constants;
import kmerrill285.Inignoto.resources.FPSCounter;

public class Chunk {
	

	public static final int SIZE = 16;
	public static final int SIZE_Y = 16;
		
	private static final int NUM_TILES = Chunk.SIZE * Chunk.SIZE * Chunk.SIZE_Y;
		
	private World world;
	
	private int x, y, z;
	private Vector3i pos;
	
	public int voxels = 0;
	
	private TileState[] tiles;
	
	public Mesh mesh;
	public Mesh waterMesh;
	
	public Mesh setMesh;
	public Mesh setWaterMesh;
	
	public boolean needsToSave = false;
	
	public float loadValue = 1.0f;
	
	public boolean generated = false;
	public boolean isGenerating = false;
	
	public HashMap<Short, Foliage> foliage = new HashMap<Short, Foliage>();
	public HashMap<Short, Float> mining_time = new HashMap<Short, Float>();
	public HashMap<Short, Float> last_mining_time = new HashMap<Short, Float>();
	public short[][][] lightMap = new short[Chunk.SIZE_Y][Chunk.SIZE][Chunk.SIZE];
	
	public ArrayList<Short> lightBfsQueue;
	public ArrayList<Short> lightRemovalBfsQueue;
	public ArrayList<Short> sunlightBfsQueue;
	public ArrayList<Short> sunlightRemovalBfsQueue;
	public ArrayList<Short> redRemovalBfsQueue;
	public ArrayList<Short> greenRemovalBfsQueue;
	public ArrayList<Short> blueRemovalBfsQueue;
	public ArrayList<Short> redBfsQueue;
	public ArrayList<Short> greenBfsQueue;
	public ArrayList<Short> blueBfsQueue;
	public Chunk(int x, int y, int z, World world) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		int X = x * SIZE;
		int Y = y * SIZE_Y;
		int Z = z * SIZE;
		pos = new Vector3i(X, Y, Z);
		
		this.world = world;
		this.lightBfsQueue = new ArrayList<Short>();
		this.lightRemovalBfsQueue = new ArrayList<Short>();
		this.sunlightBfsQueue = new ArrayList<Short>();
		this.sunlightRemovalBfsQueue = new ArrayList<Short>();
		this.redBfsQueue = new ArrayList<Short>();
		this.greenBfsQueue = new ArrayList<Short>();
		this.blueBfsQueue = new ArrayList<Short>();
		this.redRemovalBfsQueue = new ArrayList<Short>();
		this.greenRemovalBfsQueue = new ArrayList<Short>();
		this.blueRemovalBfsQueue = new ArrayList<Short>();
	}
	
	public void setPos(int x, int y, int z) {
		int X = x * SIZE;
		int Y = y * SIZE_Y;
		int Z = z * SIZE;
		pos.x = X;
		pos.y = Y;
		pos.z = Z;
		this.x = x;
		this.y = y;
		this.z = z;
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
	
	
	public Vector3i getWorldPos() {
		return this.pos;
	}
	
	public boolean isEmpty() {
		return voxels <= 0;
	}

	public TileState[] getTiles() {
		return tiles;
	}

	public void setTiles(TileState[] tiles) {
		this.tiles = tiles;
	}
	
	public boolean isWithinChunk(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			return true;
		}
		return false;
	}
	
	// bits XXXX0000
	public int getSunlightValue(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			return (lightMap[y][z][x] >> 4) & 0xF;
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			return world.getChunk(X, Y, Z).getSunlightValue(x, y, z);
		}
		return 1;
	}


	public void updateSunlight(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			short index = (short) (y * Chunk.SIZE * Chunk.SIZE_Y + z * Chunk.SIZE + x);
			sunlightBfsQueue.add(index);
		}
	}
	
	
	public void setSunlightValue(int x, int y, int z, int light) {
		this.setSunlightValue(x, y, z, light, true);
	}
	// bits XXXX0000
	public void setSunlightValue(int x, int y, int z, int light, boolean cascade) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			try {
				lightMap[y][z][x] = (short) ((lightMap[y][z][x] & 0xF) | (light << 4));
				short index = (short) (y * Chunk.SIZE * Chunk.SIZE_Y + z * Chunk.SIZE + x);
				sunlightBfsQueue.add(index);
			} catch (Exception e) {
				
			}
			
			return;
		}
		if (!cascade) return;
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).setSunlightValue(x, y, z, light, true);
			}
		}
	}
	
	public void removeSunlight(int x, int y, int z) {
		this.removeSunlight(x, y, z, true);
	}
	// bits XXXX0000
	public void removeSunlight(int x, int y, int z, boolean cascade) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			short index = (short) (y * Chunk.SIZE * Chunk.SIZE_Y + z * Chunk.SIZE + x);
			sunlightRemovalBfsQueue.add(index);
			return;
		}
		if (!cascade) return;
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).removeSunlight(x, y, z, true);
			}
		}
	}
	
	//bits 0000XXXX
	public int getTorchlight(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			return lightMap[y][z][x] & 0xF;
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			return world.getChunk(X, Y, Z).getTorchlight(x, y, z);
		}
		return 0;
	}
	
	public void setTorchlight(int x, int y, int z, int light) {
		setTorchlight(x, y, z, light, true);
	}
	//bits 0000XXXX;
	public void setTorchlight(int x, int y, int z, int light, boolean cascade) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			lightMap[y][z][x] = (short) ((lightMap[y][z][x] & 0xF0) | light);
			short index = (short) (y * Chunk.SIZE * Chunk.SIZE_Y + z * Chunk.SIZE + x);
			lightBfsQueue.add(index);
			return;
		}
		if (!cascade) return;
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).setTorchlight(x, y, z, light, true);
			}
		}
	}
	
	public void removeTorchlight(int x, int y, int z) {
		removeTorchlight(x, y, z, true);
	}
	//bits 0000XXXX;
	public void removeTorchlight(int x, int y, int z, boolean cascade) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			short index = (short) (y * Chunk.SIZE * Chunk.SIZE_Y + z * Chunk.SIZE + x);
			lightRemovalBfsQueue.add(index);
			return;
		}
		if (!cascade) return;
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).removeTorchlight(x, y, z, true);
			}
		}
	}
	
	public int getRed(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			return (lightMap[y][z][x] >> 8) & 0xF;
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			return world.getChunk(X, Y, Z).getRed(x, y, z);
		}
		return 0;
	}
	
	public int getGreen(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			return (lightMap[y][z][x] >> 4) & 0xF;
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			return world.getChunk(X, Y, Z).getGreen(x, y, z);
		}
		return 0;
	}
	
	public int getBlue(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			return lightMap[y][z][x] & 0xF;
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			return world.getChunk(X, Y, Z).getBlue(x, y, z);
		}
		return 0;
	}
	
	public void removeRed(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			short index = (short) (y * Chunk.SIZE * Chunk.SIZE_Y + z * Chunk.SIZE + x);
			redRemovalBfsQueue.add(index);
			return;
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).removeRed(x, y, z);
			}
		}
	}
	
	public void removeGreen(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			short index = (short) (y * Chunk.SIZE * Chunk.SIZE_Y + z * Chunk.SIZE + x);
			greenRemovalBfsQueue.add(index);
			return;
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).removeGreen(x, y, z);
			}
		}
	}
	
	public void removeBlue(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			short index = (short) (y * Chunk.SIZE * Chunk.SIZE_Y + z * Chunk.SIZE + x);
			blueRemovalBfsQueue.add(index);
			return;
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).removeBlue(x, y, z);
			}
		}
	}
		
	public void setRed(int x, int y, int z, int val) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			this.lightMap[y][z][x] = (short) ((lightMap[y][z][x] & 0xF0FF) | (val << 8));
			short index = (short) (y * Chunk.SIZE * Chunk.SIZE_Y + z * Chunk.SIZE + x);
			redBfsQueue.add(index);
			return;
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).setRed(x, y, z, val);
			}
		}
	}
	
	public void setGreen(int x, int y, int z, int val) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			this.lightMap[y][z][x] = (short) ((lightMap[y][z][x] & 0xFF0F) | (val << 4));
			short index = (short) (y * Chunk.SIZE * Chunk.SIZE_Y + z * Chunk.SIZE + x);
			greenBfsQueue.add(index);
			return;
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).setGreen(x, y, z, val);
			}
		}
	}
	
	public void setBlue(int x, int y, int z, int val) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			this.lightMap[y][z][x] = (short) ((lightMap[y][z][x] & 0xFFF0) | (val));
			short index = (short) (y * Chunk.SIZE * Chunk.SIZE_Y + z * Chunk.SIZE + x);
			blueBfsQueue.add(index);
			return;
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).setBlue(x, y, z, val);
			}
		}
	}
	
	
		
	public Vector3f getLight(float x, float y, float z) {
		int torchlight = getTorchlight((int)x, (int)y, (int)z);
		int sunlight = getSunlightValue((int)x, (int)y, (int)z);
		int light = torchlight + sunlight;
		if (light > 15) light = 15;
		
		float r = getRed((int)x, (int)y, (int)z);
		float g = getGreen((int)x, (int)y, (int)z);
		float b = getBlue((int)x, (int)y, (int)z);
		
		return new Vector3f(light / 15.0f);
	}
	
	public Tile getLocalTile(int x, int y, int z) {
		return getLocalTile(x, y, z, true);
	}
	
	public Tile getLocalTile(int x, int y, int z, boolean cascade) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			if (tiles == null) {
				return Tiles.AIR;
			}
			if (tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] == null) return Tiles.AIR;
			return Tiles.getTile(tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y].getTile());
		}
		if (!cascade) return Tiles.AIR;
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) return Tiles.AIR;
			if (world.getChunk(X, Y, Z).generated) {
				return world.getChunk(X, Y, Z).getLocalTile(x, y, z);
			}
		}
		return Tiles.AIR;
	}
	
	public TileState getTileState(int x, int y, int z) {
		return getTileState(x, y, z, false);
	}
	
	public TileState getTileState(int x, int y, int z, boolean modifying) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			if (tiles == null) {
				return Tiles.AIR.getDefaultState();
			}
			if (tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] == null) return Tiles.AIR.getDefaultState();
			return tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y];
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) Tiles.AIR.getDefaultState();
			if (world.getChunk(X, Y, Z).generated) {
				return world.getChunk(X, Y, Z).getTileState(x, y, z, modifying);
			}
		}
		return Tiles.AIR.getDefaultState();
	}
	
	public void setTileState(int x, int y, int z, TileState data) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			
			if (tiles == null) {
				return;
			}
			getLocalTile(x, y, z).updateLightWhenRemovedFromWorld(x, y, z, this, getTileState(x, y, z));
			tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] = data;
			Tiles.getTile(data.getTile()).updateLightWhenAddedToWorld(x, y, z, this, data);
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) return;
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).setTileState(x, y, z, data);
			}
		}
	}
	
	public void setFoliage(int x, int y, int z, Foliage foliage) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			if (foliage == null) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + x);
				this.foliage.remove(index);
			} else {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + x);
				this.foliage.put(index, foliage);
			}
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) return;
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).setFoliage(x, y, z, foliage);
			}
		}
	}
	
	public Foliage getFoliage(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + x);
			return this.foliage.get(index);
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) return null;
			if (world.getChunk(X, Y, Z).generated) {
				return world.getChunk(X, Y, Z).getFoliage(x, y, z);
			}
		}
		return null;
	}
	
	public void setLocalTile(int x, int y, int z, Tile tile) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			if (tiles == null) {
				return;
			}
			
			Tile local = getLocalTile(x, y, z);
			if (tile == Tiles.AIR && local != Tiles.AIR) {
				if (voxels > 0) {
					voxels--;
				}
			}
			if (tile != Tiles.AIR && local == Tiles.AIR) {
				voxels++;
			}
			if (tiles == null) 
			{
				tiles = new TileState[Chunk.SIZE * Chunk.SIZE_Y * Chunk.SIZE];
			}
			

			if (tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] == null) {
				tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] = tile.getDefaultState();
			} else {
				getLocalTile(x, y, z).updateLightWhenRemovedFromWorld(x, y, z, this, getTileState(x, y, z));
				tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] = tile.getDefaultState();
			}
			tile.updateLightWhenAddedToWorld(x, y, z, this, tile.getDefaultState());
		}
		
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) return;
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).setLocalTile(x, y, z, tile);
			}
		}
	}
	
	public float getMiningTime(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + x);
			Float mining_time = this.mining_time.get(index);
			return mining_time != null ? mining_time : 0;
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) return 0;
			if (world.getChunk(X, Y, Z).generated) {
				return world.getChunk(X, Y, Z).getMiningTime(x, y, z);
			}
		}
		return 0;
	}
	
	public float getLastMiningTime(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + x);
			Float mining_time = this.last_mining_time.get(index);
			return mining_time != null ? mining_time : 0;
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) return 0;
			if (world.getChunk(X, Y, Z).generated) {
				return world.getChunk(X, Y, Z).getLastMiningTime(x, y, z);
			}
		}
		return 0;
	}
	
	public void setLastMiningTime(int x, int y, int z, float mining_time) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + x);
			this.last_mining_time.put(index, mining_time);
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) return;
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).setLastMiningTime(x, y, z, mining_time);
			}
		}
	}
	
	public void setMiningTime(int x, int y, int z, float mining_time) {
		setLastMiningTime(x, y, z, mining_time);
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + x);
			this.mining_time.put(index, mining_time);
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) return;
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).setMiningTime(x, y, z, mining_time);
			}
		}
	}

	public boolean isLocalTileNotSame(int x, int y, int z, Tile tile) {
		Tile local = getLocalTile(x, y, z);
		return local.getID() != tile.getID();
	}
	
	public boolean isLocalTileAir(int x, int y, int z) {
		Tile local = getLocalTile(x, y, z);
		return local == Tiles.AIR;
	}
	
	public boolean isLocalTileNotFull(int x, int y, int z) {
		TileState local = this.getTileState(x, y, z, false);
		return !local.isFullCube() || !local.isVisible() || local.getRayTraceType() != TileRayTraceType.SOLID;
	}

	public World getWorld() {
		return world;
	}
	private File savefile;
	
	public void save() {
		if (!this.needsToSave) return;
		if (tiles == null) return;
		saving = true;
		String DIR = "Inignoto/saves/"+Inignoto.game.world.getWorldSaver().getWorldName()+"/";
		File dir = new File(DIR);
		dir.mkdirs();
		this.savefile = new File(DIR+"chunk"+getX()+","+getY()+","+getZ()+".chnk");
		try {
			savefile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		try {
			FileOutputStream fos = new FileOutputStream(savefile);
			int i = 0;
			for (int x = 0; x < Chunk.SIZE; x++) {
				for (int y = 0; y < Chunk.SIZE_Y; y++) {
					for (int z = 0; z < Chunk.SIZE; z++) {
						if (tiles[i] == null) {
							tiles[i] = Tiles.AIR.getDefaultState();
						}
						fos.write(tiles[i].getTile());
						fos.write(tiles[i].getState());
						fos.write(getTorchlight(x, y, z));
						fos.write(getSunlightValue(x, y, z));
						i++;
					}
				}
			}
			fos.close();
			this.needsToSave = false;
		} catch (Exception e) {
			e.printStackTrace();
			this.savefile.delete();
			System.out.println("Failed to save chunk!");
		}
		saving = false;
		
	}
	
	public boolean saving = false;
	
	public boolean load() {
		if (saving) {
			return true;
		}
		if (savefile == null) {
			String DIR = "Inignoto/saves/"+Inignoto.game.world.getWorldSaver().getWorldName()+"/";
			setSavefile(new File(DIR+"chunk"+getX()+","+getY()+","+getZ()+".chnk"));
		}
		if (!savefile.exists()) {return false;}
		
		boolean setTile = false;
		if (savefile != null) {
			try {
				FileInputStream fis = new FileInputStream(savefile);
				this.generated = false;
				tiles = new TileState[SIZE * SIZE_Y * SIZE];
				
				int i = 0;
				for (int x = 0; x < Chunk.SIZE; x++) {
					for (int y = 0; y < Chunk.SIZE_Y; y++) {
						for (int z = 0; z < Chunk.SIZE; z++) {
							tiles[i] = Tiles.getTile(fis.read()).getStateHolder().getStateFor(fis.read());
							setTorchlight(x, y, z, fis.read());
							setSunlightValue(x, y, z, fis.read());
							i++;
						}
					}
				}
				
				fis.close();
				this.generated = true;
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return setTile;
	}
	
	public boolean canTick() {
		int x = getX() * SIZE + SIZE / 2;
		int z = getZ() * SIZE + SIZE / 2;
		return Camera.position.distance(x, 0, z) <= SIZE * Constants.ACTIVE_CHUNK_DISTANCE;
	}
	
	public boolean isActive() {
		return isInActiveRange();
	}
	
	public boolean isInActiveRange() {
		int x = getX() * SIZE + SIZE / 2;
		int y = getY() * SIZE_Y + SIZE_Y / 2;
		int z = getZ() * SIZE + SIZE / 2;
		return Camera.position.distance(x, y, z) <= SIZE * SIZE * 1.5f;
	}
	
	public boolean isWithinViewingRange() {
		int x = getX() * SIZE + SIZE / 2;
		int z = getZ() * SIZE + SIZE / 2;
		return Camera.position.distance(x, 0, z) <= SIZE * Settings.VIEW_DISTANCE;
	}
	
	public boolean canRender() {
		int x = getX() * SIZE + SIZE / 2;
		int y = getY() * SIZE_Y + SIZE_Y / 2;
		int z = getZ() * SIZE + SIZE / 2;
		if (Camera.frustum.intersects(pos.x, pos.y, SIZE, SIZE) || Camera.downFrustum.intersects(pos.x, pos.y, SIZE, SIZE)) return true;
		return Camera.position.distance(x, 0, z) <= SIZE * Settings.VIEW_DISTANCE || new Vector3f(Camera.position).mul(0, 1, 0).distance(0, y, 0) <= SIZE_Y * Settings.VERTICAL_VIEW_DISTANCE * 1.5;
	}
	
	public void tick() {

		if (this.canTick()) {
			TilePos pos = new TilePos(0, 0, 0);
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE_Y; y++) {
					for (int z = 0; z < SIZE; z++) {
						
						pos.x = x + getX() * SIZE;
						pos.y = y + getY() * SIZE_Y;
						pos.z = z + getZ() * SIZE;
						
						TileState data = getTileState(x, y, z, false);
						
						
						if (data != null) {
							Tile tile = Tiles.getTile(data.getTile());
							
							if (data.getTile() == Tiles.AIR.getID())
							{
								continue;
							}
							
							
							if (data.getRayTraceType() == TileRayTraceType.LIQUID) {
								tile.tick(getWorld(), pos, getWorld().getRandom(), data);
							}
							
							if (tile.getTickPercent() > 0)
							if (getWorld().getRandom().nextDouble() * 100 <= tile.getTickPercent()) {
								tile.tick(getWorld(), pos, getWorld().getRandom(), data);
							}
							if (getMiningTime(x, y, z) > 0) {
								if (getWorld().getRandom().nextInt(100) <= 75) {
									setMiningTime(x, y, z, getMiningTime(x, y, z) - 0.1f);
									this.markForRerender();
								}
								if (getMiningTime(x, y, z) < 0) {
									short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + x);

									this.mining_time.remove(index);
									this.last_mining_time.remove(index);
									if ((int)getMiningTime(x, y, z) / 20 != (int)getLastMiningTime(x, y, z) / 20) {
										this.markForRerender();
									}
									
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void updateLights() {
		try {
		boolean cu = false, cd = false, cl = false, cr = false, cb = false, cf = false;
		boolean rerender = false;
		while (sunlightBfsQueue.isEmpty() == false) {
			rerender = true;
			short node = sunlightBfsQueue.get(0);
			sunlightBfsQueue.remove(0);
			
			int x = node % Chunk.SIZE;
			int y = node / (Chunk.SIZE * Chunk.SIZE_Y);
			int z = (node % (Chunk.SIZE * Chunk.SIZE_Y)) / Chunk.SIZE;
			
			if (x - 1 <= 0 || x + 1 >= Chunk.SIZE - 1) {
				if (x - 1 <= 0) cl = true;
				else cr = true;
			}
			if (y - 1 <= 0 || y + 1 >= Chunk.SIZE - 1) {
				if (y - 1 <= 0) cd = true;
				else
					cu = true;
			}
			if (z - 1 <= 0 || z + 1 >= Chunk.SIZE - 1) {
				if (z - 1 <= 0) cb = true;
				else cf = true;
			}
			
			int lightLevel = this.getSunlightValue(x, y, z);

			if (getTileState(x - 1, y, z).isOpaque() == false && 
					getSunlightValue(x - 1, y, z) + 2 <= lightLevel) {
				setSunlightValue(x - 1, y, z, lightLevel - 1);
			}
			if (getTileState(x + 1, y, z).isOpaque() == false && 
					getSunlightValue(x + 1, y, z) + 2 <= lightLevel) {
				setSunlightValue(x + 1, y, z, lightLevel - 1);
			}
			if (getTileState(x, y + 1, z).isOpaque() == false && 
					getSunlightValue(x, y + 1, z) + 2 <= lightLevel) {
				setSunlightValue(x, y + 1, z, lightLevel - 1);
			}
			if (getTileState(x, y - 1, z).isOpaque() == false && 
					getSunlightValue(x, y - 1, z) + 2 <= lightLevel) {
				setSunlightValue(x, y - 1, z, lightLevel == 15 ? lightLevel : lightLevel - 1);
			}
			if (getTileState(x, y, z - 1).isOpaque() == false && 
					getSunlightValue(x, y, z - 1) + 2 <= lightLevel) {
				setSunlightValue(x, y, z - 1, lightLevel - 1);
			}
			if (getTileState(x, y, z + 1).isOpaque() == false && 
					getSunlightValue(x, y, z + 1) + 2 <= lightLevel) {
				setSunlightValue(x, y, z + 1, lightLevel - 1);
			}
		}
		while (sunlightRemovalBfsQueue.isEmpty() == false) {
			rerender = true;
			short node = sunlightRemovalBfsQueue.get(0);
			sunlightRemovalBfsQueue.remove(0);
			
			int x = node % Chunk.SIZE;
			int y = node / (Chunk.SIZE * Chunk.SIZE_Y);
			int z = (node % (Chunk.SIZE * Chunk.SIZE_Y)) / Chunk.SIZE;
			int lightLevel = this.getSunlightValue(x, y, z);
			setSunlightValue(x, y, z, 0);
			
			if (x - 1 <= 0 || x + 1 >= Chunk.SIZE - 1) {
				if (x - 1 <= 0) cl = true;
				else cr = true;
			}
			if (y - 1 <= 0 || y + 1 >= Chunk.SIZE - 1) {
				if (y - 1 <= 0) cd = true;
				else
					cu = true;
			}
			if (z - 1 <= 0 || z + 1 >= Chunk.SIZE - 1) {
				if (z - 1 <= 0) cb = true;
				else cf = true;
			}
			
			
			int neighborLevel = getSunlightValue(x - 1, y, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeSunlight(x - 1, y, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + (x - 1));
				sunlightBfsQueue.add(index);
			}
			
			neighborLevel = getSunlightValue(x + 1, y, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeSunlight(x + 1, y, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + (x + 1));
				sunlightBfsQueue.add(index);
			}
			
			neighborLevel = getSunlightValue(x, y + 1, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeSunlight(x, y + 1, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) ((y + 1) * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + x);
				sunlightBfsQueue.add(index);
			}
			
			neighborLevel = getSunlightValue(x, y - 1, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel || lightLevel == 15) {
				removeSunlight(x, y - 1, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) ((y - 1) * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + x);
				sunlightBfsQueue.add(index);
			}
			
			neighborLevel = getSunlightValue(x, y, z - 1);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeSunlight(x, y, z - 1);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + (z - 1) * Chunk.SIZE + x);
				sunlightBfsQueue.add(index);
			}
			
			neighborLevel = getSunlightValue(x, y, z + 1);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeSunlight(x, y, z + 1);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + (z + 1) * Chunk.SIZE + x);
				sunlightBfsQueue.add(index);
			}
		}
		
		while (blueBfsQueue.isEmpty() == false) {
			rerender = true;
			short node = blueBfsQueue.get(0);
			blueBfsQueue.remove(0);
			
			int x = node % Chunk.SIZE;
			int y = node / (Chunk.SIZE * Chunk.SIZE_Y);
			int z = (node % (Chunk.SIZE * Chunk.SIZE_Y)) / Chunk.SIZE;
			
			if (x - 1 <= 0 || x + 1 >= Chunk.SIZE - 1) {
				if (x - 1 <= 0) cl = true;
				else cr = true;
			}
			if (y - 1 <= 0 || y + 1 >= Chunk.SIZE - 1) {
				if (y - 1 <= 0) cd = true;
				else
					cu = true;
			}
			if (z - 1 <= 0 || z + 1 >= Chunk.SIZE - 1) {
				if (z - 1 <= 0) cb = true;
				else cf = true;
			}
			
			int lightLevel = this.getBlue(x, y, z);

			if (getTileState(x - 1, y, z).isOpaque() == false && 
					getBlue(x - 1, y, z) + 2 <= lightLevel) {
				setBlue(x - 1, y, z, lightLevel - 1);
			}
			if (getTileState(x + 1, y, z).isOpaque() == false && 
					getBlue(x + 1, y, z) + 2 <= lightLevel) {
				setBlue(x + 1, y, z, lightLevel - 1);
			}
			if (getTileState(x, y + 1, z).isOpaque() == false && 
					getBlue(x, y + 1, z) + 2 <= lightLevel) {
				setBlue(x, y + 1, z, lightLevel - 1);
			}
			if (getTileState(x, y - 1, z).isOpaque() == false && 
					getBlue(x, y - 1, z) + 2 <= lightLevel) {
				setBlue(x, y - 1, z, lightLevel - 1);
			}
			if (getTileState(x, y, z - 1).isOpaque() == false && 
					getBlue(x, y, z - 1) + 2 <= lightLevel) {
				setBlue(x, y, z - 1, lightLevel - 1);
			}
			if (getTileState(x, y, z + 1).isOpaque() == false && 
					getBlue(x, y, z + 1) + 2 <= lightLevel) {
				setBlue(x, y, z + 1, lightLevel - 1);
			}
		}
		
		
		while (greenBfsQueue.isEmpty() == false) {
			rerender = true;
			short node = greenBfsQueue.get(0);
			greenBfsQueue.remove(0);
			
			int x = node % Chunk.SIZE;
			int y = node / (Chunk.SIZE * Chunk.SIZE_Y);
			int z = (node % (Chunk.SIZE * Chunk.SIZE_Y)) / Chunk.SIZE;
			
			if (x - 1 <= 0 || x + 1 >= Chunk.SIZE - 1) {
				if (x - 1 <= 0) cl = true;
				else cr = true;
			}
			if (y - 1 <= 0 || y + 1 >= Chunk.SIZE - 1) {
				if (y - 1 <= 0) cd = true;
				else
					cu = true;
			}
			if (z - 1 <= 0 || z + 1 >= Chunk.SIZE - 1) {
				if (z - 1 <= 0) cb = true;
				else cf = true;
			}
			
			int lightLevel = this.getGreen(x, y, z);

			if (getTileState(x - 1, y, z).isOpaque() == false && 
					getGreen(x - 1, y, z) + 2 <= lightLevel) {
				setGreen(x - 1, y, z, lightLevel - 1);
			}
			if (getTileState(x + 1, y, z).isOpaque() == false && 
					getGreen(x + 1, y, z) + 2 <= lightLevel) {
				setGreen(x + 1, y, z, lightLevel - 1);
			}
			if (getTileState(x, y + 1, z).isOpaque() == false && 
					getGreen(x, y + 1, z) + 2 <= lightLevel) {
				setGreen(x, y + 1, z, lightLevel - 1);
			}
			if (getTileState(x, y - 1, z).isOpaque() == false && 
					getGreen(x, y - 1, z) + 2 <= lightLevel) {
				setGreen(x, y - 1, z, lightLevel - 1);
			}
			if (getTileState(x, y, z - 1).isOpaque() == false && 
					getGreen(x, y, z - 1) + 2 <= lightLevel) {
				setGreen(x, y, z - 1, lightLevel - 1);
			}
			if (getTileState(x, y, z + 1).isOpaque() == false && 
					getGreen(x, y, z + 1) + 2 <= lightLevel) {
				setGreen(x, y, z + 1, lightLevel - 1);
			}
		}
		
		while (redBfsQueue.isEmpty() == false) {
			rerender = true;
			short node = redBfsQueue.get(0);
			redBfsQueue.remove(0);
			
			int x = node % Chunk.SIZE;
			int y = node / (Chunk.SIZE * Chunk.SIZE_Y);
			int z = (node % (Chunk.SIZE * Chunk.SIZE_Y)) / Chunk.SIZE;
			
			if (x - 1 <= 0 || x + 1 >= Chunk.SIZE - 1) {
				if (x - 1 <= 0) cl = true;
				else cr = true;
			}
			if (y - 1 <= 0 || y + 1 >= Chunk.SIZE - 1) {
				if (y - 1 <= 0) cd = true;
				else
					cu = true;
			}
			if (z - 1 <= 0 || z + 1 >= Chunk.SIZE - 1) {
				if (z - 1 <= 0) cb = true;
				else cf = true;
			}
			
			int lightLevel = this.getRed(x, y, z);

			if (getTileState(x - 1, y, z).isOpaque() == false && 
					getRed(x - 1, y, z) + 2 <= lightLevel) {
				setRed(x - 1, y, z, lightLevel - 1);
			}
			if (getTileState(x + 1, y, z).isOpaque() == false && 
					getRed(x + 1, y, z) + 2 <= lightLevel) {
				setRed(x + 1, y, z, lightLevel - 1);
			}
			if (getTileState(x, y + 1, z).isOpaque() == false && 
					getRed(x, y + 1, z) + 2 <= lightLevel) {
				setRed(x, y + 1, z, lightLevel - 1);
			}
			if (getTileState(x, y - 1, z).isOpaque() == false && 
					getRed(x, y - 1, z) + 2 <= lightLevel) {
				setRed(x, y - 1, z, lightLevel - 1);
			}
			if (getTileState(x, y, z - 1).isOpaque() == false && 
					getRed(x, y, z - 1) + 2 <= lightLevel) {
				setRed(x, y, z - 1, lightLevel - 1);
			}
			if (getTileState(x, y, z + 1).isOpaque() == false && 
					getRed(x, y, z + 1) + 2 <= lightLevel) {
				setRed(x, y, z + 1, lightLevel - 1);
			}
		}
		
		while (blueRemovalBfsQueue.isEmpty() == false) {
			rerender = true;
			short node = blueRemovalBfsQueue.get(0);
			blueRemovalBfsQueue.remove(0);
			
			int x = node % Chunk.SIZE;
			int y = node / (Chunk.SIZE * Chunk.SIZE_Y);
			int z = (node % (Chunk.SIZE * Chunk.SIZE_Y)) / Chunk.SIZE;
			int lightLevel = this.getBlue(x, y, z);
			setBlue(x, y, z, 0);
			
			if (x - 1 <= 0 || x + 1 >= Chunk.SIZE - 1) {
				if (x - 1 <= 0) cl = true;
				else cr = true;
			}
			if (y - 1 <= 0 || y + 1 >= Chunk.SIZE - 1) {
				if (y - 1 <= 0) cd = true;
				else
					cu = true;
			}
			if (z - 1 <= 0 || z + 1 >= Chunk.SIZE - 1) {
				if (z - 1 <= 0) cb = true;
				else cf = true;
			}
			
			int neighborLevel = getBlue(x - 1, y, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeBlue(x - 1, y, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + (x - 1));
				blueBfsQueue.add(index);
			}
			
			neighborLevel = getBlue(x + 1, y, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeTorchlight(x + 1, y, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + (x + 1));
				blueBfsQueue.add(index);
			}
			
			neighborLevel = getBlue(x, y + 1, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeBlue(x, y + 1, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) ((y + 1) * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + x);
				blueBfsQueue.add(index);
			}
			
			neighborLevel = getBlue(x, y - 1, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeBlue(x, y - 1, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) ((y - 1) * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + x);
				blueBfsQueue.add(index);
			}
			
			neighborLevel = getBlue(x, y, z - 1);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeBlue(x, y, z - 1);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + (z - 1) * Chunk.SIZE + x);
				blueBfsQueue.add(index);
			}
			
			neighborLevel = getBlue(x, y, z + 1);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeBlue(x, y, z + 1);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + (z + 1) * Chunk.SIZE + x);
				blueBfsQueue.add(index);
			}
		}
		
		while (greenRemovalBfsQueue.isEmpty() == false) {
			rerender = true;
			short node = greenRemovalBfsQueue.get(0);
			greenRemovalBfsQueue.remove(0);
			
			int x = node % Chunk.SIZE;
			int y = node / (Chunk.SIZE * Chunk.SIZE_Y);
			int z = (node % (Chunk.SIZE * Chunk.SIZE_Y)) / Chunk.SIZE;
			int lightLevel = this.getGreen(x, y, z);
			setGreen(x, y, z, 0);
			
			if (x - 1 <= 0 || x + 1 >= Chunk.SIZE - 1) {
				if (x - 1 <= 0) cl = true;
				else cr = true;
			}
			if (y - 1 <= 0 || y + 1 >= Chunk.SIZE - 1) {
				if (y - 1 <= 0) cd = true;
				else
					cu = true;
			}
			if (z - 1 <= 0 || z + 1 >= Chunk.SIZE - 1) {
				if (z - 1 <= 0) cb = true;
				else cf = true;
			}
			
			int neighborLevel = getGreen(x - 1, y, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeGreen(x - 1, y, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + (x - 1));
				greenBfsQueue.add(index);
			}
			
			neighborLevel = getGreen(x + 1, y, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeTorchlight(x + 1, y, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + (x + 1));
				greenBfsQueue.add(index);
			}
			
			neighborLevel = getGreen(x, y + 1, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeGreen(x, y + 1, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) ((y + 1) * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + x);
				greenBfsQueue.add(index);
			}
			
			neighborLevel = getGreen(x, y - 1, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeGreen(x, y - 1, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) ((y - 1) * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + x);
				greenBfsQueue.add(index);
			}
			
			neighborLevel = getGreen(x, y, z - 1);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeGreen(x, y, z - 1);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + (z - 1) * Chunk.SIZE + x);
				greenBfsQueue.add(index);
			}
			
			neighborLevel = getGreen(x, y, z + 1);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeGreen(x, y, z + 1);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + (z + 1) * Chunk.SIZE + x);
				greenBfsQueue.add(index);
			}
		}
		
		while (redRemovalBfsQueue.isEmpty() == false) {
			rerender = true;
			short node = redRemovalBfsQueue.get(0);
			redRemovalBfsQueue.remove(0);
			
			int x = node % Chunk.SIZE;
			int y = node / (Chunk.SIZE * Chunk.SIZE_Y);
			int z = (node % (Chunk.SIZE * Chunk.SIZE_Y)) / Chunk.SIZE;
			int lightLevel = this.getRed(x, y, z);
			setRed(x, y, z, 0);
			
			if (x - 1 <= 0 || x + 1 >= Chunk.SIZE - 1) {
				if (x - 1 <= 0) cl = true;
				else cr = true;
			}
			if (y - 1 <= 0 || y + 1 >= Chunk.SIZE - 1) {
				if (y - 1 <= 0) cd = true;
				else
					cu = true;
			}
			if (z - 1 <= 0 || z + 1 >= Chunk.SIZE - 1) {
				if (z - 1 <= 0) cb = true;
				else cf = true;
			}
			
			int neighborLevel = getRed(x - 1, y, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeRed(x - 1, y, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + (x - 1));
				redBfsQueue.add(index);
			}
			
			neighborLevel = getRed(x + 1, y, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeTorchlight(x + 1, y, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + (x + 1));
				redBfsQueue.add(index);
			}
			
			neighborLevel = getRed(x, y + 1, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeRed(x, y + 1, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) ((y + 1) * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + x);
				redBfsQueue.add(index);
			}
			
			neighborLevel = getRed(x, y - 1, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeRed(x, y - 1, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) ((y - 1) * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + x);
				redBfsQueue.add(index);
			}
			
			neighborLevel = getRed(x, y, z - 1);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeRed(x, y, z - 1);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + (z - 1) * Chunk.SIZE + x);
				redBfsQueue.add(index);
			}
			
			neighborLevel = getRed(x, y, z + 1);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeRed(x, y, z + 1);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + (z + 1) * Chunk.SIZE + x);
				redBfsQueue.add(index);
			}
		}
		
		while (lightBfsQueue.isEmpty() == false) {
			rerender = true;
			short node = lightBfsQueue.get(0);
			lightBfsQueue.remove(0);
			
			int x = node % Chunk.SIZE;
			int y = node / (Chunk.SIZE * Chunk.SIZE_Y);
			int z = (node % (Chunk.SIZE * Chunk.SIZE_Y)) / Chunk.SIZE;
			
			if (x - 1 <= 0 || x + 1 >= Chunk.SIZE - 1) {
				if (x - 1 <= 0) cl = true;
				else cr = true;
			}
			if (y - 1 <= 0 || y + 1 >= Chunk.SIZE - 1) {
				if (y - 1 <= 0) cd = true;
				else
					cu = true;
			}
			if (z - 1 <= 0 || z + 1 >= Chunk.SIZE - 1) {
				if (z - 1 <= 0) cb = true;
				else cf = true;
			}
			
			int lightLevel = this.getTorchlight(x, y, z);

			if (getTileState(x - 1, y, z).isOpaque() == false && 
					getTorchlight(x - 1, y, z) + 2 <= lightLevel) {
				setTorchlight(x - 1, y, z, lightLevel - 1);
			}
			if (getTileState(x + 1, y, z).isOpaque() == false && 
					getTorchlight(x + 1, y, z) + 2 <= lightLevel) {
				setTorchlight(x + 1, y, z, lightLevel - 1);
			}
			if (getTileState(x, y + 1, z).isOpaque() == false && 
					getTorchlight(x, y + 1, z) + 2 <= lightLevel) {
				setTorchlight(x, y + 1, z, lightLevel - 1);
			}
			if (getTileState(x, y - 1, z).isOpaque() == false && 
					getTorchlight(x, y - 1, z) + 2 <= lightLevel) {
				setTorchlight(x, y - 1, z, lightLevel - 1);
			}
			if (getTileState(x, y, z - 1).isOpaque() == false && 
					getTorchlight(x, y, z - 1) + 2 <= lightLevel) {
				setTorchlight(x, y, z - 1, lightLevel - 1);
			}
			if (getTileState(x, y, z + 1).isOpaque() == false && 
					getTorchlight(x, y, z + 1) + 2 <= lightLevel) {
				setTorchlight(x, y, z + 1, lightLevel - 1);
			}
		}
		while (lightRemovalBfsQueue.isEmpty() == false) {
			rerender = true;
			short node = lightRemovalBfsQueue.get(0);
			lightRemovalBfsQueue.remove(0);
			
			int x = node % Chunk.SIZE;
			int y = node / (Chunk.SIZE * Chunk.SIZE_Y);
			int z = (node % (Chunk.SIZE * Chunk.SIZE_Y)) / Chunk.SIZE;
			int lightLevel = this.getTorchlight(x, y, z);
			setTorchlight(x, y, z, 0);
			
			if (x - 1 <= 0 || x + 1 >= Chunk.SIZE - 1) {
				if (x - 1 <= 0) cl = true;
				else cr = true;
			}
			if (y - 1 <= 0 || y + 1 >= Chunk.SIZE - 1) {
				if (y - 1 <= 0) cd = true;
				else
					cu = true;
			}
			if (z - 1 <= 0 || z + 1 >= Chunk.SIZE - 1) {
				if (z - 1 <= 0) cb = true;
				else cf = true;
			}
			
			int neighborLevel = getTorchlight(x - 1, y, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeTorchlight(x - 1, y, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + (x - 1));
				lightBfsQueue.add(index);
			}
			
			neighborLevel = getTorchlight(x + 1, y, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeTorchlight(x + 1, y, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + (x + 1));
				lightBfsQueue.add(index);
			}
			
			neighborLevel = getTorchlight(x, y + 1, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeTorchlight(x, y + 1, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) ((y + 1) * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + x);
				lightBfsQueue.add(index);
			}
			
			neighborLevel = getTorchlight(x, y - 1, z);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeTorchlight(x, y - 1, z);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) ((y - 1) * Chunk.SIZE_Y * Chunk.SIZE + z * Chunk.SIZE + x);
				lightBfsQueue.add(index);
			}
			
			neighborLevel = getTorchlight(x, y, z - 1);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeTorchlight(x, y, z - 1);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + (z - 1) * Chunk.SIZE + x);
				lightBfsQueue.add(index);
			}
			
			neighborLevel = getTorchlight(x, y, z + 1);
			if (neighborLevel != 0 && neighborLevel < lightLevel) {
				removeTorchlight(x, y, z + 1);
			} else if (neighborLevel >= lightLevel) {
				short index = (short) (y * Chunk.SIZE_Y * Chunk.SIZE + (z + 1) * Chunk.SIZE + x);
				lightBfsQueue.add(index);
			}
		}
		if (rerender) {
			if (this.mesh != null) meshesToDispose.add(this.mesh);
			if (this.waterMesh != null) meshesToDispose.add(this.waterMesh);
			mesh = ChunkBuilder.buildChunk(this, cl, cr, cu, cd, cf, cb);
			waterMesh = ChunkBuilder.buildLiquidChunk(this);
		}
		}catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	public ArrayList<Mesh> meshesToDispose = new ArrayList<Mesh>();
	private boolean needsToRebuild = false;
	boolean triedToLoad = false;
	public void render(ShaderProgram shader) {
		
		if (mesh == null) {
			markForRerender();
		}
		
		if (needsToRebuild) {
			
			if (this.mesh != null) this.meshesToDispose.add(mesh);
			if (this.waterMesh != null) this.meshesToDispose.add(waterMesh);
			needsToRebuild = false;
			mesh = ChunkBuilder.buildChunk(this, true);
			waterMesh = ChunkBuilder.buildLiquidChunk(this);
			if(setMesh != null) {
				meshesToDispose.add(setMesh);
			}
			if (setWaterMesh != null) {
				meshesToDispose.add(setWaterMesh);
			}
			setMesh = null;
			setWaterMesh = null;
		} else {
			if (setWaterMesh != null) {
				if (this.waterMesh != null) this.meshesToDispose.add(waterMesh);
				waterMesh = setWaterMesh;
				setWaterMesh = null;
			}
			if (setMesh != null) {
				if (this.mesh != null) this.meshesToDispose.add(mesh);
				mesh = setMesh;
				setMesh = null;
			}
		}
		if (generated) {
			if (loadValue > 0) {
				loadValue -= 0.2f * FPSCounter.getDelta();
			} else {
				loadValue = 0;
			}
			
			shader.setUniformFloat("loadValue", loadValue);
			MeshRenderer.renderMesh(mesh, new Vector3f(getX() * SIZE, getY() * SIZE_Y, getZ() * SIZE), shader);
			if (mesh == null && setMesh != null) {
				MeshRenderer.renderMesh(setMesh, new Vector3f(getX() * SIZE, getY() * SIZE_Y, getZ() * SIZE), shader);
			}
			
			for (short node : this.foliage.keySet()) {
				int x = node % Chunk.SIZE;
				int y = node / (Chunk.SIZE * Chunk.SIZE_Y);
				int z = (node % (Chunk.SIZE * Chunk.SIZE_Y)) / Chunk.SIZE;
				this.foliage.get(node).render(x + getX() * SIZE, y + getY() * SIZE_Y, z + getZ() * SIZE, this, shader);
			}
			
			shader.setUniformFloat("loadValue", 0);
		}
		
		
	}
	
	public void renderWater(ShaderProgram shader) {
		
		shader.setUniformFloat("loadValue", loadValue);
		MeshRenderer.renderMesh(waterMesh, new Vector3f(getX() * SIZE, getY() * SIZE_Y, getZ() * SIZE), shader);
		shader.setUniformFloat("loadValue", 0);
	}
	

	public void renderShadow(ShaderProgram shader, ShadowRenderer renderer) {
		if (mesh != null) {
			shader.setUniformFloat("loadValue", loadValue);
			MeshRenderer.renderMesh(mesh, new Vector3f(getX() * SIZE, getY() * SIZE_Y, getZ() * SIZE), shader, renderer);
			shader.setUniformFloat("loadValue", 0);
		}
	}
	
	public void performFrameUpdates() {
		
		
		for (int i = 0; i < meshesToDispose.size(); i++) {
			if (meshesToDispose.get(i) != null) {
				meshesToDispose.get(i).dispose();
			}
		}
		meshesToDispose.clear();
				
		
		
		if (isActive()) {
			if (tiles == null) {				
				this.generated = false;
				this.world.markChunkForBuilding(this);
			}
		} 
		else {
			if (mesh != null) {
				if (!needsToSave && generated) {
					tiles = null;
					
				}
			}
		}
	}
	
	public void dispose() {
		for (int i = 0; i < meshesToDispose.size(); i++) {
			if (meshesToDispose.get(i) != null) {
				meshesToDispose.get(i).dispose();
			}
		}
		meshesToDispose.clear();
		if (mesh != null) mesh.dispose();
		if (waterMesh != null) waterMesh.dispose();
		if (needsToSave) this.save();
		tiles = null;
		this.mesh = null;
		this.waterMesh = null;
		this.setSavefile(null);
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void markForRerender() {
		this.setNeedsToRebuild(true);
//		this.needsToCalculateLights = true;
	}
	
	public void markForSave() {
		this.needsToSave = true;
		if (!World.saveQueue.contains(this))
			World.saveQueue.add(this);
	}
	

	public void setSavefile(File savefile) {
		this.savefile = savefile;
	}

	public boolean needsToRebuild() {
		return needsToRebuild;
	}

	public void setNeedsToRebuild(boolean needsToRebuild) {
		this.needsToRebuild = needsToRebuild;
	}



	
}
