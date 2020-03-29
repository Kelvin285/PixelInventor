package kmerrill285.PixelInventor.game.world;

import java.util.ArrayList;
import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import kmerrill285.PixelInventor.PixelInventor;
import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.MeshRenderer;
import kmerrill285.PixelInventor.game.client.rendering.chunk.SecondaryChunkMeshBuilder;
import kmerrill285.PixelInventor.game.client.rendering.effects.lights.DirectionalLight;
import kmerrill285.PixelInventor.game.client.rendering.effects.lights.Fog;
import kmerrill285.PixelInventor.game.client.rendering.heightmap.Heightmap;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.client.rendering.textures.Texture;
import kmerrill285.PixelInventor.game.client.rendering.textures.Textures;
import kmerrill285.PixelInventor.game.entity.Entity;
import kmerrill285.PixelInventor.game.settings.Settings;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.tile.Tile.TileRayTraceType;
import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.game.world.chunk.Chunk;
import kmerrill285.PixelInventor.game.world.chunk.ChunkManager;
import kmerrill285.PixelInventor.game.world.chunk.TilePos;
import kmerrill285.PixelInventor.game.world.chunk.generator.ChunkGenerator;
import kmerrill285.PixelInventor.resources.Constants;
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
	private Fog shadowBlendFog;
	
	public DirectionalLight light = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0, -1, 0), 1.0f);
	
	public Heightmap heightmap;
	
	public World(String worldName, long s) {
		worldSaver = new WorldSaver(worldName, this, s);
		worldSaver.loadWorld();
		
		random = new Random(seed);
		generator = new ChunkGenerator(this, seed);
		
		chunkManager = new ChunkManager(this, generator);
		this.setSeed(seed);
		
		this.fog = new Fog();
		fog.active = true;
		fog.density = 0.1f;
		fog.color = new Vector3f(1.0f, 1.0f, 1.0f);
		
		this.shadowBlendFog = new Fog();
		shadowBlendFog.active = true;
		shadowBlendFog.density = 0.1f;
		shadowBlendFog.color = new Vector3f(1.0f, 1.0f, 1.0f);
		
		heightmap = new Heightmap(this);
	}
	
	public void updateChunkManager() {
		chunkManager.update();
		chunkManager.tick();
		if (Settings.FAR_PLANE_ENABLED) {
			heightmap.update();
		}
	}
	
	public void tick() {
		updateLight();
		
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			
			if (getChunk(e.getTilePos()) != null || e.ticksExisted > 0) {
				e.tick();
				if (e.isDead) {
					entities.remove(i);
				}
			}
		}
	}
	
	public boolean rebuild = false;
	public void render(ShaderProgram shader) {
		if (!Settings.RAYTRACING) {
			updateFog();
			shader.setUniformFog("fog", fog);
			shader.setUniformFog("shadowBlendFog", shadowBlendFog);
			shader.setUniformInt("hasShadows", 0);
			shader.setUniformVec3("cameraPos", Camera.position);
			shader.setUniformVec3("sunPos", this.light.getPosition());
			shader.setUniformVec3("sunColor", this.light.getColor());
			shader.setUniformInt("cascadedShadows", 0);
			
			Vector3f sunRotation = this.light.getDirection();
			//y and z
			Vector3f sunDirection = new Vector3f(0, (float)Math.sin(Math.toRadians(sunRotation.z)), (float)Math.cos(Math.toRadians(sunRotation.x)));
			
			shader.setUniformVec3("sunDirection", sunDirection);
			
			chunkManager.render(shader, false);
			renderTileHover(shader);
			
			for (int i = 0; i < entities.size(); i++) {
				entities.get(i).render(shader);
			}
			
			if (Settings.FAR_PLANE_ENABLED) {
				heightmap.render(shader);
			}
		} else {
			shader.setUniformVec3("cameraPos", Camera.position);
			if (Settings.FAR_PLANE_ENABLED) {
				heightmap.render(shader);
			}
		}
		
	}
	

	public void renderRaytracer() {
		chunkManager.render(null, true);
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).render(null);
		}
	}
	
	public void renderShadow(ShaderProgram shader, Matrix4f lightMatrix) {
		
		chunkManager.renderShadow(shader, lightMatrix);
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.renderShadow(shader, lightMatrix);
		}
	}
	
	public void updateLight() {
		Camera.shadowPosition = new Vector3f(Camera.position);
		Camera.shadowRotation = new Vector3f(Camera.rotation);
		this.light.setShadowPosMult(Constants.shadow_far / 4.0f);

		float sun_rotation = 17;
				
		this.light.setDirection(new Vector3f(sun_rotation, 0, 0));
		float py = (float)Math.asin(Math.toRadians(sun_rotation)) * light.getShadowPosMult();
		float pz = (float)Math.acos(Math.toRadians(sun_rotation)) * light.getShadowPosMult();
		
		Vector3f pos = Camera.shadowPosition;
		
		
		float x = pos.x;
		float y = pos.y + py;
		float z = pos.z + pz;
		
		
		this.light.setPosition(new Vector3f(x, y, z));
		
		if (Settings.RAYTRACING) {
			this.light.setPosition(new Vector3f(0, py, pz));
		}
	}
	
	public void updateFog() {
		fog.active = true;
		fog.density = 1.0f / 700.0f;
		fog.color = getSkyColor().mul(2.0f, 2.0f, 2.0f);
		
		shadowBlendFog.active = true;
		shadowBlendFog.density = 1.0f / 50.0f;
		shadowBlendFog.color = new Vector3f(1.0f, 1.0f, 1.0f);
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
		if (Camera.currentTile != null) {
			TilePos pos = Camera.currentTile.getPosition();
			if (pos != null) {
				float size = 0.05f;
				if (Camera.currentTile.getType() == RayTraceType.TILE) {
					MeshRenderer.renderMesh(selection, new Vector3f(pos.x - (size / 2.0f), pos.y - (size / 2.0f), pos.z - (size / 2.0f)), new Vector3f(1.0f + size, 1.0f + size, 1.0f + size), shader);
					
				}
			}
		}
		
	}
	
	public void dispose() {
		chunkManager.dispose();
		worldSaver.saveWorld();
		heightmap.dispose();
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
		
		float length = end.distance(start);
		
		float inc = 0.001f;
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
		chunk.setTile(pos2.x, pos2.y, pos2.z, tile, true, true);
		chunk.setUpdateNeeded(pos2.x, pos2.y, pos2.z, true);
	}
	
	public void mineTile(TilePos pos, float strength) {
		TilePos pos2 = new TilePos(pos.x, pos.y, pos.z);
		
		Chunk chunk = getChunk(pos2);
		if (chunk == null) return;
		pos2.x -= chunk.getX() * Chunk.SIZE;
		pos2.y -= chunk.getY() * Chunk.SIZE;
		pos2.z -= chunk.getZ() * Chunk.SIZE;
		chunk.mineTile(pos2.x, pos2.y, pos2.z, strength);
		chunk.setUpdateNeeded(pos2.x, pos2.y, pos2.z, true);
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

	public Random getRandom() {
		return this.random;
	}

	public ChunkGenerator getChunkGenerator() {
		return this.generator;
	}

}
