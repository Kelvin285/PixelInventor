package kmerrill285.PixelInventor.game.world.chunk;

import org.joml.Vector3f;

import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.MeshRenderer;
import kmerrill285.PixelInventor.game.client.rendering.chunk.ChunkMeshBuilder;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.tile.Tiles;

public class Chunk {
	public static final int SIZE = 16;
	private Tile[][][] tiles = new Tile[SIZE][SIZE][SIZE];
	
	private int x, y, z;
	
	private boolean rerender = true;
	
	private Mesh mesh;
	
	private ChunkManager manager;
	
	public int air = 0;
	
	public Chunk(int x, int y, int z, ChunkManager manager) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.manager = manager;
	}
	
	public Tile getTile(int x, int y, int z) {
		if (x >= 0 && x < SIZE && y >= 0 && y < SIZE && z >= 0 && z < SIZE) {
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
	
	public void setTile(int x, int y, int z, Tile tile) {
		if (x >= 0 && x < SIZE && y >= 0 && y < SIZE && z >= 0 && z < SIZE) {
			if (tiles[x][y][z] == Tiles.AIR && tile != Tiles.AIR) air--;
			if (tiles[x][y][z] != Tiles.AIR && tile == Tiles.AIR) air++;
			tiles[x][y][z] = tile;
			markForRerender();
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
			chunk.setTile(x, y, z, tile);
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
		manager.rebuild.add(this);
	}
	
	public void rebuild() {
		mesh = ChunkMeshBuilder.buildMesh(this);
		rerender = false;
	}
	
	public boolean isSurrounded() {
		if (air > 0) return false;
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
				if (getTile(SIZE - 1, z, z).isFullCube() == false) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean fullLeft() {
		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				if (getTile(0, z, z).isFullCube() == false) {
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
		if (mesh == null) return false;
		if (mesh.getVertexCount() == 0) return false;
		return true;
	}
	
	public boolean shouldRebuild() {
		return this.rerender;
	}
	
	public void render(ShaderProgram shader) {
		if (shouldRender())
		MeshRenderer.renderMesh(mesh, new Vector3f(getX() * Chunk.SIZE, getY() * Chunk.SIZE, getZ() * Chunk.SIZE), shader);
	}
	
	public void dispose() {
		if (mesh != null)
		mesh.dispose();
	}

	
}
