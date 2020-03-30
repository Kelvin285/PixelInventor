package kmerrill285.PixelInventor.game.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.joml.Vector2i;
import org.joml.Vector3f;

import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.MeshRenderer;
import kmerrill285.PixelInventor.game.client.rendering.chunk.MegachunkBuilder;
import kmerrill285.PixelInventor.game.client.rendering.effects.lights.DirectionalLight;
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
import kmerrill285.PixelInventor.game.world.chunk.Megachunk;
import kmerrill285.PixelInventor.game.world.chunk.TileData;
import kmerrill285.PixelInventor.game.world.chunk.TilePos;
import kmerrill285.PixelInventor.game.world.chunk.generator.ChunkGenerator;
import kmerrill285.PixelInventor.game.world.chunk.generator.FlatChunkGenerator;
import kmerrill285.PixelInventor.resources.Constants;
import kmerrill285.PixelInventor.resources.RayTraceResult;
import kmerrill285.PixelInventor.resources.RayTraceResult.RayTraceType;

public class World {
	
	public static ArrayList<Chunk> saveQueue = new ArrayList<Chunk>();
	private ChunkGenerator generator;
	private Random random;
			
	private long seed;
	
	private WorldSaver worldSaver;
	
	public ArrayList<Entity> entities = new ArrayList<Entity>();
	
	private Fog fog;
	private Fog shadowBlendFog;
	
	private HashMap<String, Megachunk> megachunks = new HashMap<String, Megachunk>();
	
	public DirectionalLight light = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0, -1, 0), 1.0f);
	
	public int megaview = 5;
	
	public ArrayList<Megachunk> unloadedChunks = new ArrayList<Megachunk>();
	public ArrayList<Megachunk> activeChunks = new ArrayList<Megachunk>();
	public ArrayList<Megachunk> rendering = new ArrayList<Megachunk>();
	
	public World(String worldName, long s) {
		worldSaver = new WorldSaver(worldName, this, s);
		worldSaver.loadWorld();
		
		random = new Random(seed);
		generator = new FlatChunkGenerator(this, seed);
		
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
	
	public Megachunk getMegachunk(int x, int y, int z) {
		return megachunks.get(x+","+y+","+z);
	}
	
	public void removeMegachunk(int x, int y, int z) {
		megachunks.remove(x+","+y+","+z);
	}
	
	public void addMegachunk(int x, int y, int z) {
		if (getMegachunk(x, y, z) == null) {
			megachunks.put(x+","+y+","+z,new Megachunk(x, y, z, this));
		}
		activeChunks.add(getMegachunk(x, y, z));
		unloadedChunks.remove(getMegachunk(x, y, z));
	}
	
	public void saveChunks() {
		for (int i = 0; i < saveQueue.size(); i++) {
			saveQueue.get(i).save();
		}
		saveQueue.clear();
	}
	
	public void buildMegachunks() {
		
		double distance[] = {Double.MAX_VALUE};
		Megachunk[] m = {null};
		Vector2i closest = new Vector2i(0, 0);
		
		for (String str : megachunks.keySet()) {
			megachunks.get(str).updateAndBuild(distance, m, closest, Camera.position);
		}
		
		if (distance[0] != Double.MAX_VALUE) {
			if (m[0] == null) {
				return;
			}
			
			Megachunk.pseudochunk.setPos(closest.x, closest.y);
			Megachunk.pseudochunk.setParent(m[0]);
			Megachunk.pseudochunk.setSavefile(null);
			this.getChunkGenerator().generateChunk(Megachunk.pseudochunk, true);
			Chunk c = m[0].getLocalChunk(closest.x, closest.y);
			if (c == null) {
				c = new Chunk(closest.x, closest.y, m[0]);
				m[0].setLocalChunk(closest.x, closest.y, c);
			}
			c.mesh = MegachunkBuilder.buildChunk(Megachunk.pseudochunk);
		}
	}

	private int mx, my, mz;
	public void updateChunkManager() {
		if (!adding) return;
		
		for (int i = 0; i < unloadedChunks.size(); i++ ) {
			if (!activeChunks.contains(unloadedChunks.get(i))) {
				Megachunk m = unloadedChunks.get(i);
				this.removeMegachunk(m.getX(), m.getY(), m.getZ());
			}
		}
		unloadedChunks.clear();
		
		activeChunks.clear();
		
		
		for (String str : megachunks.keySet()) {
			unloadedChunks.add(megachunks.get(str));
		}
		if (Settings.VIEW_DISTANCE / (Chunk.SIZE * Megachunk.SIZE) > 2) {
			megaview = Settings.VIEW_DISTANCE / (Chunk.SIZE * Megachunk.SIZE);
		} else {
			megaview = 2;
		}
		mx = (int)Math.floor(Camera.position.x / (Megachunk.SIZE * Chunk.SIZE));
		my = (int)Math.floor(Camera.position.y / Chunk.SIZE_Y);
		mz = (int)Math.floor(Camera.position.z / (Megachunk.SIZE * Chunk.SIZE));
		for (int x = -megaview / 2; x < megaview / 2 + 1; x++) {
			for (int z = -megaview / 2; z < megaview / 2 + 1; z++) {
				for (int y = -megaview / 2; y < megaview / 2 + 1; y++) {
					addMegachunk(x + mx, y + my, z + mz);
				}
			}
			
		}
		adding = false;
	}
	
	public void tickMegachunks() {
		for (String str : megachunks.keySet()) {
			megachunks.get(str).tick();
		}
	}
	
	public void tick() {
		
		updateLight();
		
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			
			int mx = (int)Math.floor((float)e.position.x / (Megachunk.SIZE * Chunk.SIZE));
			int my = (int)Math.floor((float)e.position.y / Chunk.SIZE_Y);
			int mz = (int)Math.floor((float)e.position.x / (Megachunk.SIZE * Chunk.SIZE));
			
			if (this.getMegachunk(mx, my, mz) != null || e.ticksExisted > 10) {
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
	private boolean adding = false;
	public void renderMegachunks(ShaderProgram shader) {
		if (activeChunks.size() > 0) {
			rendering.clear();
			for (int i = 0; i < activeChunks.size(); i++) {
				rendering.add(activeChunks.get(i));
			}
		}
		for (int i = 0; i < rendering.size(); i++) {
			if (rendering.get(i) != null)
			rendering.get(i).render(shader);
		}
		adding = true;
		renderTileHover(shader);
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
		int mx = (int)Math.floor((float)pos.x / (Chunk.SIZE * Megachunk.SIZE));
		int my = (int)Math.floor((float)pos.y / Chunk.SIZE_Y);
		int mz = (int)Math.floor((float)pos.z / (Chunk.SIZE * Megachunk.SIZE));
		Megachunk chunk = getMegachunk(mx, my, mz);
		int x = pos.x;
		int y = pos.y;
		int z = pos.z;
		x -= mx * (Chunk.SIZE * Megachunk.SIZE);
		y -= my * Chunk.SIZE_Y;
		z -= mz * (Chunk.SIZE * Megachunk.SIZE);
		if (chunk == null) return Tiles.AIR;
		return chunk.getTileAt(x, y, z);
	}
	
	public TileData getTileData(TilePos pos, boolean modifying) {
		int mx = (int)Math.floor((float)pos.x / (Chunk.SIZE * Megachunk.SIZE));
		int my = (int)Math.floor((float)pos.y / Chunk.SIZE_Y);
		int mz = (int)Math.floor((float)pos.z / (Chunk.SIZE * Megachunk.SIZE));
		Megachunk chunk = getMegachunk(mx, my, mz);
		int x = pos.x;
		int y = pos.y;
		int z = pos.z;
		x -= mx * (Chunk.SIZE * Megachunk.SIZE);
		y -= my * Chunk.SIZE_Y;
		z -= mz * (Chunk.SIZE * Megachunk.SIZE);
		if (chunk == null) return new TileData(Tiles.AIR.getID());
		return chunk.getTileDataAt(x, y, z, modifying);
	}
	
	public boolean setTileData(TilePos pos, TileData data) {
		return setTileData(pos.x, pos.y, pos.z, data);
	}
	

	
	private boolean setTileData(int x1, int y1, int z1, TileData data) {
		int mx = (int)Math.floor((float)x1 / (Chunk.SIZE * Megachunk.SIZE));
		int my = (int)Math.floor((float)y1 / Chunk.SIZE_Y);
		int mz = (int)Math.floor((float)z1 / (Chunk.SIZE * Megachunk.SIZE));
		Megachunk chunk = getMegachunk(mx, my, mz);
		int x = x1;
		int y = y1;
		int z = z1;
		x -= mx * (Chunk.SIZE * Megachunk.SIZE);
		y -= my * Chunk.SIZE_Y;
		z -= mz * (Chunk.SIZE * Megachunk.SIZE);
		if (chunk == null) return false;
		chunk.setTileData(x, y, z, data);
		return true;
	}
	
	public Chunk getChunkAt(TilePos pos) {
		int mx = (int)Math.floor((float)pos.x / (Chunk.SIZE * Megachunk.SIZE));
		int my = (int)Math.floor((float)pos.y / Chunk.SIZE_Y);
		int mz = (int)Math.floor((float)pos.z / (Chunk.SIZE * Megachunk.SIZE));
		Megachunk chunk = getMegachunk(mx, my, mz);
		int x = pos.x;
		int z = pos.z;
		x -= mx * (Chunk.SIZE * Megachunk.SIZE);
		z -= mz * (Chunk.SIZE * Megachunk.SIZE);
		x /= Chunk.SIZE;
		z /= Chunk.SIZE;
		if (chunk == null) return null;
		return chunk.getLocalChunk(x, z);
	}
	
	public boolean setTile(TilePos pos, Tile tile) {
		return setTile(pos.x, pos.y, pos.z, tile);
	}
	


	public boolean setTile(int x, int y, int z, Tile tile) {
		int mx = (int)Math.floor((float)x / (Chunk.SIZE * Megachunk.SIZE));
		int my = (int)Math.floor((float)y / Chunk.SIZE_Y);
		int mz = (int)Math.floor((float)z / (Chunk.SIZE * Megachunk.SIZE));
		Megachunk chunk = getMegachunk(mx, my, mz);
		x -= mx * (Chunk.SIZE * Megachunk.SIZE);
		y -= my * Chunk.SIZE_Y;
		z -= mz * (Chunk.SIZE * Megachunk.SIZE);
		if (chunk != null) {
			chunk.setTileAt(x, y, z, tile);
			if (chunk.getLocalChunk(x / Megachunk.SIZE, z / Megachunk.SIZE) != null) {
				chunk.getLocalChunk(x / Megachunk.SIZE, z / Megachunk.SIZE).markForRerender();
				chunk.getLocalChunk(x / Megachunk.SIZE, z / Megachunk.SIZE).markForSave();
			}
			return true;
		}
		return false;
	}
	
	
	public void mineTile(TilePos pos, float strength) {
		int mx = (int)Math.floor((float)pos.x / (Chunk.SIZE * Megachunk.SIZE));
		int my = (int)Math.floor((float)pos.y / Chunk.SIZE_Y);
		int mz = (int)Math.floor((float)pos.z / (Chunk.SIZE * Megachunk.SIZE));
		Megachunk chunk = getMegachunk(mx, my, mz);
		int x = pos.x;
		int y = pos.y;
		int z = pos.z;
		x -= mx * (Chunk.SIZE * Megachunk.SIZE);
		y -= my * Chunk.SIZE_Y;
		z -= mz * (Chunk.SIZE * Megachunk.SIZE);
		if (chunk != null) {
			TileData data = chunk.getTileDataAt(x, y, z, false);
			data.setMiningTime(data.getMiningTime() + strength / Tiles.getTile(data.getTile()).getHardness());
			int current = (int)(data.getMiningTime() / 20);
			int last = (int)(data.getLastMiningTime() / 20);
			if (data.getMiningTime() > 100.0) {
				data.setMiningTime(0.0f);
				if (chunk.getLocalChunk(x / Megachunk.SIZE, z / Megachunk.SIZE) != null)
					chunk.getLocalChunk(x / Megachunk.SIZE, z / Megachunk.SIZE).markForRerender();
				chunk.setTileAt(x, y, z, Tiles.AIR);
				chunk.getLocalChunk(x / Megachunk.SIZE, z / Megachunk.SIZE).markForSave();
			}
			else
			if (current != last) {
				if (chunk.getLocalChunk(x / Megachunk.SIZE, z / Megachunk.SIZE) != null)
					chunk.getLocalChunk(x / Megachunk.SIZE, z / Megachunk.SIZE).markForRerender();
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
