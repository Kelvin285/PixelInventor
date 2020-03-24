package kmerrill285.PixelInventor.game.world.chunk;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import kmerrill285.PixelInventor.PixelInventor;
import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.MeshRenderer;
import kmerrill285.PixelInventor.game.client.rendering.chunk.ChunkMeshBuilder;
import kmerrill285.PixelInventor.game.client.rendering.chunk.SecondaryChunkMeshBuilder;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.entity.StaticEntity;
import kmerrill285.PixelInventor.game.settings.Settings;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.resources.FPSCounter;

public class Chunk {
	public static final int SIZE = 16;
	private Tile[][][] tiles = new Tile[SIZE][SIZE][SIZE];
	private float[][][] miningProgress = new float[SIZE][SIZE][SIZE];
	private float[][][] lastMiningProgress = new float[SIZE][SIZE][SIZE];
	private boolean[][][] updateNeeded = new boolean[SIZE][SIZE][SIZE];
	private int x, y, z;
	
	public ArrayList<TileData> rayMesh = new ArrayList<TileData>();
	
	private boolean rerender = true;
	
	private Mesh mesh;
	
	private ChunkManager manager;
	
	public int voxels = 0;
	
	private boolean needsToSave = false;
	
	public ArrayList<StaticEntity> staticEntities = new ArrayList<StaticEntity>();
	
	public Chunk(int x, int y, int z, ChunkManager manager) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.manager = manager;
	}
	
	
	public Tile getTile(int x, int y, int z) {
		if (x >= 0 && x < SIZE && y >= 0 && y < SIZE && z >= 0 && z < SIZE) {
			if(tiles[x][y][z] == null) return Tiles.AIR;
			return tiles[x][y][z];
		}
		int X = 0;
		int Y = 0; 
		int Z = 0;
		if (x < 0) {
			X = -1;
			x += SIZE;
		}
		if (x >= SIZE) {
			X += 1;
			x -= SIZE;
		}
		if (y < 0) {
			Y = -1;
			y += SIZE;
		}
		if (y >= SIZE) {
			Y += 1;
			y -= SIZE;
		}
		if (z < 0) {
			Z = -1;
			z += SIZE;
		}
		if (z >= SIZE) {
			Z += 1;
			z -= SIZE;
		}
		Chunk chunk = manager.getChunk(getX() + X, getY() + Y, getZ() + Z);
		if (chunk != null) {
			return chunk.getTile(x, y, z);
		}
		return Tiles.AIR;
	}
	
	public float getMiningProgress(int x, int y, int z) {
		if (x >= 0 && x < SIZE && y >= 0 && y < SIZE && z >= 0 && z < SIZE) {
			return miningProgress[x][y][z];
		}
		int X = 0;
		int Y = 0; 
		int Z = 0;
		if (x < 0) {
			X = -1;
			x += SIZE;
		}
		if (x >= SIZE) {
			X += 1;
			x -= SIZE;
		}
		if (y < 0) {
			Y = -1;
			y += SIZE;
		}
		if (y >= SIZE) {
			Y += 1;
			y -= SIZE;
		}
		if (z < 0) {
			Z = -1;
			z += SIZE;
		}
		if (z >= SIZE) {
			Z += 1;
			z -= SIZE;
		}
		Chunk chunk = manager.getChunk(getX() + X, getY() + Y, getZ() + Z);
		if (chunk != null) {
			return chunk.getMiningProgress(x, y, z);
		}
		return 0;
	}
	
	public boolean getUpdateNeeded(int x, int y, int z) {
		if (x >= 0 && x < SIZE && y >= 0 && y < SIZE && z >= 0 && z < SIZE) {
			return updateNeeded[x][y][z];
		}
		int X = 0;
		int Y = 0; 
		int Z = 0;
		if (x < 0) {
			X = -1;
			x += SIZE;
		}
		if (x >= SIZE) {
			X += 1;
			x -= SIZE;
		}
		if (y < 0) {
			Y = -1;
			y += SIZE;
		}
		if (y >= SIZE) {
			Y += 1;
			y -= SIZE;
		}
		if (z < 0) {
			Z = -1;
			z += SIZE;
		}
		if (z >= SIZE) {
			Z += 1;
			z -= SIZE;
		}
		Chunk chunk = manager.getChunk(getX() + X, getY() + Y, getZ() + Z);
		if (chunk != null) {
			return chunk.getUpdateNeeded(x, y, z);
		}
		return false;
	}
	
	public void setTile(int x, int y, int z, Tile tile, boolean rerender, boolean updateSurroundings) {
		if (x >= 0 && x < SIZE && y >= 0 && y < SIZE && z >= 0 && z < SIZE) {
			
			if (tiles[x][y][z] != tile) {
				if (tiles[x][y][z] != Tiles.AIR) {
					if (tile == Tiles.AIR) {
						this.voxels--;
						}
				}
				if (tiles[x][y][z] == Tiles.AIR) {
					if (tile != Tiles.AIR) {
						this.voxels++;
					}
				}
				if (voxels < 0) voxels = 0;
				tiles[x][y][z] = tile;
				if (updateSurroundings)
				{
					this.setUpdateNeeded(x - 1, y, z, true);
					this.setUpdateNeeded(x + 1, y, z, true);
					this.setUpdateNeeded(x, y - 1, z, true);
					this.setUpdateNeeded(x, y + 1, z, true);
					this.setUpdateNeeded(x, y, z - 1, true);
					this.setUpdateNeeded(x, y, z + 1, true);
				}
				
				if (rerender) {
					markForRerender();
				}
			}
			
			return;
		}
		int X = 0;
		int Y = 0; 
		int Z = 0;
		if (x < 0) {
			X = -1;
			x += SIZE;
		}
		if (x >= SIZE) {
			X += 1;
			x -= SIZE;
		}
		if (y < 0) {
			Y = -1;
			y += SIZE;
		}
		if (y >= SIZE) {
			Y += 1;
			y -= SIZE;
		}
		if (z < 0) {
			Z = -1;
			z += SIZE;
		}
		if (z >= SIZE) {
			Z += 1;
			z -= SIZE;
		}
		Chunk chunk = manager.getChunk(getX() + X, getY() + Y, getZ() + Z);
		if (chunk != null) {
			chunk.setTile(x, y, z, tile, rerender, updateSurroundings);
		} else {
			chunk = new Chunk(x, y, z, manager);
			manager.setup.add(chunk);
		}
	}
	
	public void setMiningProgress(int x, int y, int z, float progress, boolean rerender) {
		if (x >= 0 && x < SIZE && y >= 0 && y < SIZE && z >= 0 && z < SIZE) {
			miningProgress[x][y][z] = progress;
			if (rerender)
			{
				int last = (int)(5 * (progress / 100.0f));
				int current = (int)(5 * (lastMiningProgress[x][y][z] / 100.0f));
				if (last != current || miningProgress[x][y][z] == 0) {
					this.markForRerender();
					lastMiningProgress[x][y][z] = miningProgress[x][y][z];
				}
			}
			return;
		}
		int X = 0;
		int Y = 0; 
		int Z = 0;
		if (x < 0) {
			X = -1;
			x += SIZE;
		}
		if (x >= SIZE) {
			X += 1;
			x -= SIZE;
		}
		if (y < 0) {
			Y = -1;
			y += SIZE;
		}
		if (y >= SIZE) {
			Y += 1;
			y -= SIZE;
		}
		if (z < 0) {
			Z = -1;
			z += SIZE;
		}
		if (z >= SIZE) {
			Z += 1;
			z -= SIZE;
		}
		Chunk chunk = manager.getChunk(getX() + X, getY() + Y, getZ() + Z);
		if (chunk != null) {
			chunk.setMiningProgress(x, y, z, progress, rerender);
		}
	}
	
	public void setUpdateNeeded(int x, int y, int z, boolean updateNeeded) {
		if (x >= 0 && x < SIZE && y >= 0 && y < SIZE && z >= 0 && z < SIZE) {
			this.updateNeeded[x][y][z] = updateNeeded;
			return;
		}
		int X = 0;
		int Y = 0; 
		int Z = 0;
		if (x < 0) {
			X = -1;
			x += SIZE;
		}
		if (x >= SIZE) {
			X += 1;
			x -= SIZE;
		}
		if (y < 0) {
			Y = -1;
			y += SIZE;
		}
		if (y >= SIZE) {
			Y += 1;
			y -= SIZE;
		}
		if (z < 0) {
			Z = -1;
			z += SIZE;
		}
		if (z >= SIZE) {
			Z += 1;
			z -= SIZE;
		}
		Chunk chunk = manager.getChunk(getX() + X, getY() + Y, getZ() + Z);
		if (chunk != null) {
			chunk.setUpdateNeeded(x, y, z, updateNeeded);
		}
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
	
	public void markForRerender() {
		
		rerender = true;
		for (int xx = -1; xx < 2; xx++) {
			for (int yy = -1; yy < 2; yy++) {
				for (int zz = -1; zz < 2; zz++) {
					Chunk chunk = manager.getChunk(getX() + xx, getY() + yy, getZ() + zz);
					if (chunk != null) {
						chunk.rebuildNow();
					}
				}
			}
		}
		this.needsToSave = true;
	}
	
	public void mineTile(int x, int y, int z, float strength) {
		float hardness = getTile(x, y, z).getHardness();
		float progress = getMiningProgress(x, y, z);
		if (hardness == 0) {
			breakTile(x, y, z, false);
			setMiningProgress(x, y, z, 0, true);
			return;
		}
		if (hardness == -1) {
			if (progress > 0) {
				setMiningProgress(x, y, z, 0, true);
			}
			return;
		}
		float newStrength = (float) ((strength / hardness) * FPSCounter.getDelta());
		if (progress + newStrength > 100) {
			breakTile(x, y, z, false);
			setMiningProgress(x, y, z, 0, true);
		} else {
			setMiningProgress(x, y, z, progress + newStrength, true);
		}
	}
	
	public void breakTile(int x, int y, int z, boolean rerender) {
		getTile(x, y, z).dropAsItem(manager.getWorld(), x + getX() * Chunk.SIZE, y + getY() * Chunk.SIZE, z + getZ() * Chunk.SIZE);
		setTile(x, y, z, Tiles.AIR, rerender, true);
	}

	public boolean needsToSave() {
		return this.needsToSave;
	}
	
	public void rebuild() {
		mesh = ChunkMeshBuilder.buildMesh(this);
		rerender = false;
	}
	

	private void rebuildNow() {
		SecondaryChunkMeshBuilder.queueChunk(this);
		rerender = false;
	}
	
	public boolean isSurrounded() {
		if (voxels < 16 * 16 * 16) return false;
		int x = getX();
		int y = getY();
		int z = getZ();
		{
			Chunk chunk = manager.getChunk(x, y + 1, z);
			if (chunk == null) return false; 
			if (chunk.shouldRender() == false) return false;
			if (chunk.fullBottom() == false) return false;
		}
		{
			Chunk chunk = manager.getChunk(x, y - 1, z);
			if (chunk == null) return false; 
			if (chunk.shouldRender() == false) return false;
			if (chunk.fullTop() == false) return false;
		}
		{
			Chunk chunk = manager.getChunk(x + 1, y, z);
			if (chunk == null) return false; 
			if (chunk.shouldRender() == false) return false;
			if (chunk.fullLeft() == false) return false;
		}
		{
			Chunk chunk = manager.getChunk(x - 1, y, z);
			if (chunk == null) return false; 
			if (chunk.shouldRender() == false) return false;
			if (chunk.fullRight() == false) return false;
		}
		{
			Chunk chunk = manager.getChunk(x, y, z - 1);
			if (chunk == null) return false; 
			if (chunk.shouldRender() == false) return false;
			if (chunk.fullBack() == false) return false;
		}
		{
			Chunk chunk = manager.getChunk(x, y, z + 1);
			if (chunk == null) return false; 
			if (chunk.shouldRender() == false) return false;
			if (chunk.fullFront() == false) return false;
		}
		return true;
	}
	
	public boolean fullBack() {
		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				if (getTile(x, z, SIZE - 1).isFullCube() == false) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean fullFront() {
		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				if (getTile(x, z, 0).isFullCube() == false) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean fullRight() {
		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				if (getTile(SIZE - 1, x, z).isFullCube() == false) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean fullLeft() {
		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				if (getTile(0, x, z).isFullCube() == false) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean fullTop() {
		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				if (getTile(x, SIZE - 1, z).isFullCube() == false) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean fullBottom() {
		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				if (getTile(x, 0, z).isFullCube() == false) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean shouldRender() {
		if (voxels == 0) return false;
		if (Settings.RAYTRACING == true) {
			return true;
		}
		if (mesh == null) return false;
		if (mesh.getVertexCount() == 0) return false;
		return true;
	}
	
	public boolean shouldRebuild() {
		return this.rerender;
	}
	
	public void render(ShaderProgram shader) {
		if (shouldRender())
		{
			if (Settings.RAYTRACING) {
				this.rebuildNow();
			}
			else {
				MeshRenderer.renderMesh(mesh, new Vector3f(getX() * Chunk.SIZE, getY() * Chunk.SIZE, getZ() * Chunk.SIZE), shader);
				for (StaticEntity e : staticEntities) {
					e.render(shader);
				}
			}
		}
		
	}
	
	public void renderShadow(ShaderProgram shader, Matrix4f lightMatrix) {
		if (shouldRender())
		MeshRenderer.renderShadowMesh(mesh, new Vector3f(getX() * Chunk.SIZE, getY() * Chunk.SIZE, getZ() * Chunk.SIZE), shader, lightMatrix);
		for (int i = 0; i < staticEntities.size(); i++) {
			StaticEntity e = staticEntities.get(i);
			e.renderShadow(shader, lightMatrix);
		}
	}
	
	public void dispose() {
		manager.getWorld().getWorldSaver().saveChunk(this);
		if (mesh != null)
		mesh.dispose();
		for (int i = 0; i < staticEntities.size(); i++) {
			StaticEntity e = staticEntities.get(i);
			e.dispose();
		}
		staticEntities.clear();
	}

	public void tick() {
//		PixelInventor.game.raytracer.getWorld().updateChunk(this);
//		PixelInventor.game.raytracer.getWorld().updatePosition();
		TilePos pos = new TilePos(0, 0, 0);
		
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					if (tiles[x][y][z] == null) continue;
					pos.setPosition(x + getX() * Chunk.SIZE, y + getY() * Chunk.SIZE, z + getZ() * Chunk.SIZE);
					if (getMiningProgress(x, y, z) > 0) {
						setMiningProgress(x, y, z, getMiningProgress(x, y, z) - 0.5f, true);
					}
					if (getMiningProgress(x, y, z) < 0) {
						setMiningProgress(x, y, z, 0, true);
					}
					if (tiles[x][y][z].getTickPercent() > 0 || updateNeeded[x][y][z]) {
						if (this.manager.getWorld().getRandom().nextDouble() * 100.0 <= tiles[x][y][z].getTickPercent() || updateNeeded[x][y][z]) {
							tiles[x][y][z].tick(this.manager.getWorld(), pos, this.manager.getWorld().getRandom());
						}
						updateNeeded[x][y][z] = false;
					}
				}
			}
		}
		for (int i = 0; i < staticEntities.size(); i++) {
			StaticEntity e = staticEntities.get(i);
			e.tick();
			if (e.isDead) {
				staticEntities.get(i).dispose();
				staticEntities.remove(i);
			}
		}
	}

	public void setMesh(Mesh buildMesh) {
		this.mesh = buildMesh;
	}

	public void addStaticEntity(StaticEntity base, float x, float y, float z) {
		base.position.x = x + getX() * Chunk.SIZE;
		base.position.y = y + getY() * Chunk.SIZE;
		base.position.z = z + getZ() * Chunk.SIZE;
		staticEntities.add(base.create(this));
	}
}
