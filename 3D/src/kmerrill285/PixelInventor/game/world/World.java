package kmerrill285.PixelInventor.game.world;

import java.util.ArrayList;
import java.util.Random;

import org.joml.Vector3f;

import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.MeshRenderer;
import kmerrill285.PixelInventor.game.client.rendering.effects.lights.Fog;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.client.rendering.textures.Texture;
import kmerrill285.PixelInventor.game.client.rendering.textures.Textures;
import kmerrill285.PixelInventor.game.entity.Entity;
import kmerrill285.PixelInventor.game.settings.Settings;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.tile.Tile.TileRayTraceType;
import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.game.world.chunk.Chunk;
import kmerrill285.PixelInventor.game.world.chunk.ChunkGenerator;
import kmerrill285.PixelInventor.game.world.chunk.ChunkManager;
import kmerrill285.PixelInventor.game.world.chunk.TilePos;
import kmerrill285.PixelInventor.resources.MathHelper;
import kmerrill285.PixelInventor.resources.RayTraceResult;
import kmerrill285.PixelInventor.resources.RayTraceResult.RayTraceType;

public class World {
	
	private ChunkGenerator generator;
	private Random random;
		
	private ChunkManager chunkManager;
	
	private long seed;
	
	private WorldSaver worldSaver;
	
	public ArrayList<Entity> entities = new ArrayList<Entity>();
	
	private Fog fog;
	
	public World(String worldName, long seed) {
		worldSaver = new WorldSaver(worldName, this);
		worldSaver.loadWorld();
		
		random = new Random(seed);
		generator = new ChunkGenerator(this, seed);
		
		chunkManager = new ChunkManager(this, generator);
		this.setSeed(seed);
		
		this.fog = new Fog();
		fog.active = true;
		fog.density = 0.1f;
		fog.color = new Vector3f(1.0f, 1.0f, 1.0f);
	}
	
	public void updateChunkManager() {
		chunkManager.update();
	}
	
	public void tick() {
		for (Entity e : entities) {
			if (getChunk(e.getTilePos()) != null)
			e.tick();
		}
	}
	
	public void render(ShaderProgram shader) {
		updateFog();
		shader.setUniformFog("fog", fog);
		chunkManager.render(shader);
		renderTileHover(shader);
		for (Entity e : entities) {
			e.render(shader);
		}
	}
	
	public void updateFog() {
		fog.active = true;
		fog.density = 1.0f / 200.0f;
		fog.color = getSkyColor().mul(2.0f, 2.0f, 2.0f);
	}
	
	private Mesh selection = null;
	private void renderTileHover(ShaderProgram shader) {
		if (selection == null) {
			float[] vertices = new float[] {
				0, 0, 0, //0
				0, 1, 0, //1
				1, 1, 0, //2
				1, 0, 0, //3
				
				0, 0, 1, //4
				0, 1, 1, //5
				1, 1, 1, //6
				1, 0, 1, //7
				
				0, 0, 0, //8
				0, 0, 1, //9
				1, 0, 1, //10
				1, 0, 0, //11
				
				0, 1, 0, //12
				0, 1, 1, //13
				1, 1, 1, //14
				1, 1, 0, //15
				
				0, 0, 0, //16
				0, 1, 0, //17
				0, 1, 1, //18
				0, 0, 1, //19
				
				1, 0, 0, //20
				1, 1, 0, //21
				1, 1, 1, //22
				1, 0, 1, //23
			};
			float[] texCoords = new float[] {
				0, 0,
				0, 1,
				1, 1,
				1, 0,
				
				0, 0,
				0, 1,
				1, 1,
				1, 0,
				
				0, 0,
				0, 1,
				1, 1,
				1, 0,
				
				0, 0,
				0, 1,
				1, 1,
				1, 0,
				
				0, 0,
				0, 1,
				1, 1,
				1, 0,
				
				0, 0,
				0, 1,
				1, 1,
				1, 0
				
			};
			int[] indices = new int[] {
				0, 1, 2, 2, 3, 0,
				4, 5, 6, 6, 7, 4,
				
				8, 9, 10, 10, 11, 8,
				12, 13, 14, 14, 15, 12,
				
				16, 17, 18, 18, 19, 16,
				20, 21, 22, 22, 23, 20
			};
			Texture tex = Textures.TILE_SELECTION;
			selection = new Mesh(vertices, texCoords, indices, tex);
		}
		TilePos pos = Camera.currentTile.getPosition();
		if (pos != null) {
			if (Camera.currentTile.getType() == RayTraceType.TILE) {
				MeshRenderer.renderMesh(selection, new Vector3f(pos.x - 0.005f, pos.y - 0.005f, pos.z - 0.005f), new Vector3f(1.01f, 1.01f, 1.01f), shader);
				
			}
		}
	}
	
	public void dispose() {
		chunkManager.dispose();
		worldSaver.saveWorld();
		for (Entity e : entities) {
			e.dispose();
		}
	}
	
	public Vector3f getSkyColor() {
		return new Vector3f(91.0f / 255.0f, 198.0f / 255.0f, 208.0f / 255.0f);
	}
	
	public ChunkManager getChunkManager() {
		return this.chunkManager;
	}

	public long getSeed() {
		return this.seed;
	}

	public WorldSaver getWorldSaver() {
		return this.worldSaver;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}
	
	public RayTraceResult rayTraceTiles(Vector3f start, Vector3f end, TileRayTraceType type) {
		TilePos pos = new TilePos(start.x, start.y, start.z);
		
		final int sx = pos.x, sy = pos.y, sz = pos.z;
		
		Vector3f slope = new Vector3f(end.x - start.x, end.y - start.y, end.z - start.z).normalize();
		float length = end.distance(start);
		
		float inc = 0.0001f;
		for (float i = 0; i < length; i+=inc) {
			Vector3f n = new Vector3f(start).lerp(end, i / length);
			pos.setPosition(n.x, n.y, n.z);
			Tile tile = getTile(pos);
			
			if (tile != null) {
				if (tile.isVisible()) {
					if (tile.getRayTraceType() == type) {
						return new RayTraceResult(RayTraceType.TILE, pos, new Vector3f(start).lerp(end, (i-inc) / length));
					}
				}
			}
		}
		
		return new RayTraceResult(RayTraceType.EMPTY, pos, end);
	}
	
	public Tile getTile(TilePos pos) {
		TilePos pos2 = new TilePos(pos.x, pos.y, pos.z);
		Chunk chunk = getChunk(pos2);
		if (chunk == null) return Tiles.AIR;
		pos2.x -= chunk.getX() * Chunk.SIZE;
		pos2.y -= chunk.getY() * Chunk.SIZE;
		pos2.z -= chunk.getZ() * Chunk.SIZE;
		return chunk.getTile(pos2.x, pos2.y, pos2.z);
	}
	
	public void setTile(TilePos pos, Tile tile) {
		TilePos pos2 = new TilePos(pos.x, pos.y, pos.z);

		Chunk chunk = getChunk(pos2);
		if (chunk == null) return;
		pos2.x -= chunk.getX() * Chunk.SIZE;
		pos2.y -= chunk.getY() * Chunk.SIZE;
		pos2.z -= chunk.getZ() * Chunk.SIZE;
		chunk.setTile(pos2.x, pos2.y, pos2.z, tile, true);
	}
	
	public Chunk getChunk(TilePos pos) 
	{
		return getChunkManager().getChunk((int)MathHelper.floorDiv(pos.x, Chunk.SIZE), (int)MathHelper.floorDiv(pos.y, Chunk.SIZE), (int)MathHelper.floorDiv(pos.z, Chunk.SIZE));
	}
	
	public Chunk getChunk(int x, int y, int z) 
	{
		return getChunkManager().getChunk(x, y, z);
	}
	
	public Fog getFog() {
		return this.fog;
	}
}
