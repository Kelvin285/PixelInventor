package kmerrill285.Inignoto.game.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.joml.Vector3f;
import org.joml.Vector3i;

import kmerrill285.Inignoto.Inignoto;
import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.client.rendering.MeshRenderer;
import kmerrill285.Inignoto.game.client.rendering.chunk.ChunkBuilder;
import kmerrill285.Inignoto.game.client.rendering.effects.lights.DirectionalLight;
import kmerrill285.Inignoto.game.client.rendering.effects.lights.Fog;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.shadows.ShadowRenderer;
import kmerrill285.Inignoto.game.client.rendering.textures.Texture;
import kmerrill285.Inignoto.game.client.rendering.textures.Textures;
import kmerrill285.Inignoto.game.entity.Entity;
import kmerrill285.Inignoto.game.settings.Settings;
import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.tile.Tile.TileRayTraceType;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.chunk.Chunk;
import kmerrill285.Inignoto.game.world.chunk.TileData;
import kmerrill285.Inignoto.game.world.chunk.TilePos;
import kmerrill285.Inignoto.game.world.chunk.generator.ChunkGenerator;
import kmerrill285.Inignoto.game.world.chunk.generator.FlatChunkGenerator;
import kmerrill285.Inignoto.resources.RayTraceResult;
import kmerrill285.Inignoto.resources.RayTraceResult.RayTraceType;

public class World {
	
	public static ArrayList<Chunk> saveQueue = new ArrayList<Chunk>();
	private ChunkGenerator generator;
	private Random random;
			
	private long seed;
	
	private WorldSaver worldSaver;
	
	public ArrayList<Entity> entities = new ArrayList<Entity>();
	
	private Fog fog;
	private Fog shadowBlendFog;
	
	private HashMap<String, Chunk> chunks = new HashMap<String, Chunk>();
	
	public DirectionalLight light = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0, -1, 0), 1.0f);
		
	public ArrayList<Chunk> unloadedChunks = new ArrayList<Chunk>();
	public ArrayList<Chunk> activeChunks = new ArrayList<Chunk>();
	public ArrayList<Chunk> rendering = new ArrayList<Chunk>();
	public ArrayList<Chunk> chunksToBuild = new ArrayList<Chunk>();
	
	public World(String worldName, long s) {
		worldSaver = new WorldSaver(worldName, this, s);
		worldSaver.loadWorld();
		
		random = new Random(seed);
		generator = new ChunkGenerator(this, seed);
		
		this.setSeed(seed);
		
		this.fog = new Fog();
		fog.active = true;
		fog.density = 0.1f;
		fog.color = new Vector3f(1.0f, 1.0f, 1.0f);
		
		this.shadowBlendFog = new Fog();
		shadowBlendFog.active = true;
		shadowBlendFog.density = 0.1f;
		shadowBlendFog.color = new Vector3f(1.0f, 1.0f, 1.0f);
		
	}
	
	public Chunk getChunk(int x, int y, int z) {
		return chunks.get(x+","+y+","+z);
	}
	
	public void removeChunk(int x, int y, int z) {
		chunks.remove(x+","+y+","+z);
	}
	
	public void addChunk(int x, int y, int z) {
		if (getChunk(x, y, z) == null) {
			chunks.put(x+","+y+","+z,new Chunk(x, y, z, this));
		}
		activeChunks.add(getChunk(x, y, z));
		unloadedChunks.remove(getChunk(x, y, z));
		chunksToBuild.add(getChunk(x, y, z));
	}
	
	public void saveChunks() {
		for (int i = 0; i < saveQueue.size(); i++) {
			saveQueue.get(i).save();
		}
		saveQueue.clear();
	}
	public static Chunk pseudochunk = new Chunk(0, 0, 0, null);
	private Vector3i cp = new Vector3i(0);
	public void buildChunks() {
		
		int cx = (int)Math.floor(Camera.position.x / Chunk.SIZE);
		int cy = (int)Math.floor(Camera.position.y / Chunk.SIZE_Y);
		int cz = (int)Math.floor(Camera.position.z / Chunk.SIZE);
		cp.x = cx;
		cp.y = cy;
		cp.z = cz;
		double distance = Double.MAX_VALUE;
		Chunk closest = null;
		int index = -1;
		for (int i = 0; i < chunksToBuild.size(); i++) {
			Chunk c = chunksToBuild.get(i);
			if (c.generated == false) {
				double dist = cp.distance(c.getX(), c.getY(), c.getZ());
				if (dist < distance) {
					distance = dist;
					closest = c;
					index = i;
				}
			}
		}
		
		if (closest != null) {
			World.pseudochunk.setPos(closest.getX(), closest.getY(), closest.getZ());
			World.pseudochunk.setWorld(this);
			World.pseudochunk.setSavefile(null);
			this.getChunkGenerator().generateChunk(World.pseudochunk, true);
			closest.mesh = ChunkBuilder.buildChunk(World.pseudochunk);
			closest.generated = true;
			chunksToBuild.remove(index);
		}
	}

	private int mx, my, mz;
	public void updateChunkManager() {
		if (!adding) return;
		
		for (int i = 0; i < unloadedChunks.size(); i++ ) {
			if (!activeChunks.contains(unloadedChunks.get(i))) {
				Chunk m = unloadedChunks.get(i);
				this.removeChunk(m.getX(), m.getY(), m.getZ());
			}
		}
		unloadedChunks.clear();
		
		activeChunks.clear();
		
		
		for (String str : chunks.keySet()) {
			unloadedChunks.add(chunks.get(str));
		}
		
		mx = (int)Math.floor(Camera.position.x / Chunk.SIZE);
		my = (int)Math.floor(Camera.position.y / Chunk.SIZE_Y);
		mz = (int)Math.floor(Camera.position.z / Chunk.SIZE);
		for (int x = -Settings.VIEW_DISTANCE / 2; x < Settings.VIEW_DISTANCE / 2 + 1; x++) {
			for (int z = -Settings.VIEW_DISTANCE / 2; z < Settings.VIEW_DISTANCE / 2 + 1; z++) {
				for (int y = -Settings.VERTICAL_VIEW_DISTANCE / 2; y < Settings.VERTICAL_VIEW_DISTANCE / 2 + 1; y++) {
					addChunk(x + mx, y + my, z + mz);
				}
			}
			
		}
		adding = false;
	}
	
	public void tickChunks() {
		for (String str : chunks.keySet()) {
			chunks.get(str).tick();
		}
	}
	
	public void tick() {
				
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			
			int mx = (int)Math.floor((float)e.position.x / Chunk.SIZE);
			int my = (int)Math.floor((float)e.position.y / Chunk.SIZE_Y);
			int mz = (int)Math.floor((float)e.position.x / Chunk.SIZE);
			
			if (this.getChunk(mx, my, mz) != null || e.ticksExisted > 10) {
				e.tick();
				if (e.isDead) {
					entities.remove(i);
				}
			} else {
				e.ticksExisted++;
				e.velocity.x = 0;
				e.velocity.y = 0;
				e.velocity.z = 0;
			}
		}
		
	}
	
	public boolean rebuild = false;
	public void render(ShaderProgram shader) {
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
		
		
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).render(shader);
		}
	}
	
	public void renderShadow(ShaderProgram shader, ShadowRenderer renderer) {
		
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).renderShadow(shader, renderer);
		}
	}
	
	private boolean adding = false;
	public void renderChunks(ShaderProgram shader) {
		if (!adding) {
			rendering.clear();
			for (String str : chunks.keySet()) {
				Chunk c = chunks.get(str);
				rendering.add(c);
			}
		}
		
		for (int i = 0; i < rendering.size(); i++) {
			if (rendering.get(i) == null) continue;
			rendering.get(i).tick();
			rendering.get(i).render(shader);
		}
		
		adding = true;
		renderTileHover(shader);
	}
	
	public void renderChunksShadow(ShaderProgram shader, ShadowRenderer renderer) {
		for (int i = 0; i < rendering.size(); i++) {
			if (rendering.get(i) != null)
			rendering.get(i).renderShadow(shader, renderer);
		}
//		updateLight();
//		Inignoto.game.shadowRenderer.update(light.getPosition(), light.getDirection());
	}
	
	
	public void updateLight() {
		this.light.setShadowPosMult(0);

		float sun_rotation = light.getDirection().x + 0.0001f;
		
		this.light.setDirection(new Vector3f(sun_rotation, 0, 0));
		
		float py = (float)Math.sin(Math.toRadians(sun_rotation)) * light.getShadowPosMult();
		float pz = (float)Math.cos(Math.toRadians(sun_rotation)) * light.getShadowPosMult();
		Vector3f pos = new Vector3f(Inignoto.game.player.lastPos).add(Camera.getForward().mul(2));
		
		
		float x = pos.x;
		float y = pos.y + py;
		float z = pos.z + pz;
		
		
		this.light.setPosition(new Vector3f(x, y, z));
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
				float size = 0.01f;
				if (Camera.currentTile.getType() == RayTraceType.TILE) {
					MeshRenderer.renderMesh(selection, new Vector3f(pos.x - (size / 2.0f), pos.y - (size / 2.0f), pos.z - (size / 2.0f)), new Vector3f(1.0f + size, 1.0f + size, 1.0f + size), shader);
					
				}
			}
		}
		
	}
	
	public void dispose() {
		
		worldSaver.saveWorld();
		for (Entity e : entities) {
			e.dispose();
		}
	}
	
	public Vector3f getSkyColor() {
		return new Vector3f(91.0f / 255.0f, 198.0f / 255.0f, 208.0f / 255.0f);
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
		int mx = (int)Math.floor((float)pos.x / Chunk.SIZE);
		int my = (int)Math.floor((float)pos.y / Chunk.SIZE_Y);
		int mz = (int)Math.floor((float)pos.z / Chunk.SIZE);
		Chunk chunk = getChunk(mx, my, mz);
		int x = pos.x;
		int y = pos.y;
		int z = pos.z;
		x -= mx * Chunk.SIZE;
		y -= my * Chunk.SIZE_Y;
		z -= mz * Chunk.SIZE;
		if (chunk == null) return Tiles.AIR;
		return chunk.getLocalTile(x, y, z);
	}
	
	public TileData getTileData(TilePos pos, boolean modifying) {
		int mx = (int)Math.floor((float)pos.x / Chunk.SIZE);
		int my = (int)Math.floor((float)pos.y / Chunk.SIZE_Y);
		int mz = (int)Math.floor((float)pos.z / Chunk.SIZE);
		Chunk chunk = getChunk(mx, my, mz);
		int x = pos.x;
		int y = pos.y;
		int z = pos.z;
		x -= mx * Chunk.SIZE;
		y -= my * Chunk.SIZE_Y;
		z -= mz * Chunk.SIZE;
		if (chunk == null) return new TileData(Tiles.AIR.getID());
		return chunk.getTileData(x, y, z, modifying);
	}
	
	public boolean setTileData(TilePos pos, TileData data) {
		return setTileData(pos.x, pos.y, pos.z, data);
	}
	

	
	private boolean setTileData(int x1, int y1, int z1, TileData data) {
		int mx = (int)Math.floor((float)x1 / Chunk.SIZE);
		int my = (int)Math.floor((float)y1 / Chunk.SIZE_Y);
		int mz = (int)Math.floor((float)z1 / Chunk.SIZE);
		Chunk chunk = getChunk(mx, my, mz);
		int x = x1;
		int y = y1;
		int z = z1;
		x -= mx * Chunk.SIZE;
		y -= my * Chunk.SIZE_Y;
		z -= mz * Chunk.SIZE;
		if (chunk == null) return false;
		chunk.setTileData(x, y, z, data);
		return true;
	}
	
	public Chunk getChunkAt(TilePos pos) {
		int mx = (int)Math.floor((float)pos.x / Chunk.SIZE);
		int my = (int)Math.floor((float)pos.y / Chunk.SIZE_Y);
		int mz = (int)Math.floor((float)pos.z / Chunk.SIZE);
		
		return getChunk(mx, my, mz);
	}
	
	public boolean setTile(TilePos pos, Tile tile) {
		return setTile(pos.x, pos.y, pos.z, tile);
	}
	


	public boolean setTile(int x, int y, int z, Tile tile) {
		int mx = (int)Math.floor((float)x / Chunk.SIZE);
		int my = (int)Math.floor((float)y / Chunk.SIZE_Y);
		int mz = (int)Math.floor((float)z / Chunk.SIZE);
		Chunk chunk = getChunk(mx, my, mz);

		x -= mx * Chunk.SIZE;
		y -= my * Chunk.SIZE_Y;
		z -= mz * Chunk.SIZE;
		if (chunk != null) {
			chunk.setLocalTile(x, y, z, tile);
			chunk.markForRerender();
			chunk.markForSave();
			return true;
		}
		return false;
	}
	
	
	public void mineTile(TilePos pos, float strength) {
		int mx = (int)Math.floor((float)pos.x / Chunk.SIZE);
		int my = (int)Math.floor((float)pos.y / Chunk.SIZE_Y);
		int mz = (int)Math.floor((float)pos.z / Chunk.SIZE);
		Chunk chunk = getChunk(mx, my, mz);
		int x = pos.x;
		int y = pos.y;
		int z = pos.z;
		x -= mx * Chunk.SIZE;
		y -= my * Chunk.SIZE_Y;
		z -= mz * Chunk.SIZE;
		if (chunk != null) {
			TileData data = chunk.getTileData(x, y, z, false);
			data.setMiningTime(data.getMiningTime() + strength / Tiles.getTile(data.getTile()).getHardness());
			int current = (int)(data.getMiningTime() / 20);
			int last = (int)(data.getLastMiningTime() / 20);
			if (data.getMiningTime() > 100.0) {
				data.setMiningTime(0.0f);
				chunk.setLocalTile(x, y, z, Tiles.AIR);
				chunk.markForRerender();
				chunk.markForSave();
			}
			else
			if (current != last) {
				chunk.markForRerender();
			}
		}
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
