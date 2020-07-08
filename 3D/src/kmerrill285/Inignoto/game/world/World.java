package kmerrill285.Inignoto.game.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.joml.Vector3f;
import org.joml.Vector3i;

import custom_models.Part;
import kmerrill285.Inignoto.Inignoto;
import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.client.rendering.MeshRenderer;
import kmerrill285.Inignoto.game.client.rendering.chunk.ChunkBuilder;
import kmerrill285.Inignoto.game.client.rendering.effects.lights.DirectionalLight;
import kmerrill285.Inignoto.game.client.rendering.effects.lights.Fog;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.shadows.ShadowRenderer;
import kmerrill285.Inignoto.game.client.rendering.textures.Textures;
import kmerrill285.Inignoto.game.entity.Entity;
import kmerrill285.Inignoto.game.entity.player.PlayerEntity;
import kmerrill285.Inignoto.game.settings.Settings;
import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.tile.Tile.TileRayTraceType;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.chunk.Chunk;
import kmerrill285.Inignoto.game.world.chunk.MetaChunk;
import kmerrill285.Inignoto.game.world.chunk.TileData;
import kmerrill285.Inignoto.game.world.chunk.TilePos;
import kmerrill285.Inignoto.game.world.chunk.generator.BiomeChunkGenerator;
import kmerrill285.Inignoto.game.world.chunk.generator.ChunkGenerator;
import kmerrill285.Inignoto.resources.RayTraceResult;
import kmerrill285.Inignoto.resources.RayTraceResult.RayTraceType;
import kmerrill285.Inignoto.resources.raytracer.RayBox;
import kmerrill285.Inignoto.resources.raytracer.RayIntersection;
import kmerrill285.Inignoto.resources.raytracer.Raytracer;

public class World {
	
	public static ArrayList<Chunk> saveQueue = new ArrayList<Chunk>();
	private ChunkGenerator generator;
	private Random random;
			
	private long seed;
	
	private WorldSaver worldSaver;
	
	public ArrayList<Entity> entities = new ArrayList<Entity>();
	public ArrayList<Entity> players = new ArrayList<Entity>();
	
	private Fog fog;
	private Fog shadowBlendFog;
	
	private HashMap<String, Chunk> chunks = new HashMap<String, Chunk>();
	private HashMap<String, MetaChunk> metaChunks = new HashMap<String, MetaChunk>();
	
	public DirectionalLight light = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0, -1, 0), 1.0f);
		
	public ArrayList<Chunk> unloadedChunks = new ArrayList<Chunk>();
	public ArrayList<Chunk> activeChunks = new ArrayList<Chunk>();
	public ArrayList<Chunk> rendering = new ArrayList<Chunk>();
	
	public WorldSettings worldSettings = new WorldSettings();
	
	public World(String worldName, long s) {
		worldSaver = new WorldSaver(worldName, this, s);
		worldSaver.loadWorld();
		
		random = new Random(seed);
		generator = new BiomeChunkGenerator(this, seed);
		
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
	
	public MetaChunk getMetaChunk(int x, int y, int z) {
		return metaChunks.get(x+","+y+","+z);
	}
	
	public void removeMetaChunk(int x, int y, int z) {
		metaChunks.remove(x+","+y+","+z);
	}
	
	public void addMetaChunk(int x, int y, int z) {
		if (getMetaChunk(x, y, z) == null) {
			MetaChunk meta = new MetaChunk(x, y, z);
			metaChunks.put(x+","+y+","+z, meta);
		}
	}
	
	public Chunk getChunk(int x, int y, int z) {
		return chunks.get(x+","+y+","+z);
	}
	
	public void removeChunk(int x, int y, int z) {
		rendering.remove(chunks.remove(x+","+y+","+z));
	}
	
	public void addChunk(int x, int y, int z) {
		if (getChunk(x, y, z) == null) {
			chunks.put(x+","+y+","+z,new Chunk(x, y, z, this));
		}
		Chunk chunk = getChunk(x, y, z);

		activeChunks.add(chunk);
	}
	
	public void markChunkForBuilding(Chunk chunk) {
		chunk.generated = false;
	}
	
	public static Chunk pseudochunk = new Chunk(0, 0, 0, null);
	private Vector3i cp = new Vector3i(0);
	
	public void buildChunks() {
		saveChunks();
		for (int i = 0; i < Settings.CHUNK_GENERATION_SPEED; i++) {
			Chunk closest = findClosestChunk();
			
			if (closest != null) {
				buildChunk(closest);
			}
			
			buildMetaChunks();
		}
		
		
	}
	
	public void buildMetaChunks() {
		MetaChunk closest = findClosestMetaChunk();
		if (closest != null) {
			Chunk c = getChunk(closest.x, closest.y, closest.z);
			if (c != null) {
				applyMeta(c, closest, true);
			}
		}
	}
	
	
	private void saveChunks() {
		try {
			for (int i = 0; i < saveQueue.size(); i++) {
				saveQueue.get(i).save();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		saveQueue.clear();
	}
	private void applyMeta(Chunk chunk, MetaChunk metachunk, boolean genMesh) {
		if (!chunk.generated) {
			buildChunk(chunk);
		} else {
			if (chunk.getTiles() == null) {
				chunk.setTiles(new TileData[Chunk.SIZE * Chunk.SIZE * Chunk.SIZE_Y]);
			}
			this.getChunkGenerator().applyMeta(chunk, metachunk);
			if (genMesh) {
				chunk.setMesh = ChunkBuilder.buildChunk(chunk, true);
				chunk.setWaterMesh = ChunkBuilder.buildLiquidChunk(chunk);
			}			
		}
	}
	
	private void buildChunk(Chunk chunk) {
		
		MetaChunk meta = null;
		boolean setMesh = false;
		boolean pseudo = false;
//		if (chunk.isInActiveRange()) {
//			
//		} else {
//			World.pseudochunk.setPos(chunk.getX(), chunk.getY(), chunk.getZ());
//			World.pseudochunk.setWorld(this);
//			World.pseudochunk.setSavefile(null);
//			pseudo =true;
//			this.getChunkGenerator().generateChunk(World.pseudochunk, getMetaChunk(chunk.getX(), chunk.getY(), chunk.getZ()), true);
//			setMesh = true;
//			meta = getMetaChunk(chunk.getX(), chunk.getY(), chunk.getZ());
//		}
		chunk.setWorld(this);
		chunk.setTiles(new TileData[Chunk.SIZE * Chunk.SIZE * Chunk.SIZE_Y]);
		this.getChunkGenerator().generateChunk(chunk, getMetaChunk(chunk.getX(), chunk.getY(), chunk.getZ()), true);
		setMesh = true;
		meta = getMetaChunk(chunk.getX(), chunk.getY(), chunk.getZ());
		
		if (setMesh) {
			if (meta != null) {
				chunk.generated = true;
				this.applyMeta(chunk, meta, false);
			}
			chunk.setMesh = ChunkBuilder.buildChunk(pseudo ? World.pseudochunk : chunk, true);
			chunk.setWaterMesh = ChunkBuilder.buildLiquidChunk(pseudo ? World.pseudochunk : chunk);
			
		}
		
		chunk.generated = true;
		
	}
	
	private MetaChunk findClosestMetaChunk() {
		int cx = (int)Math.floor(Camera.position.x / Chunk.SIZE);
		int cy = (int)Math.floor(Camera.position.y / Chunk.SIZE_Y);
		int cz = (int)Math.floor(Camera.position.z / Chunk.SIZE);
		cp.x = cx;
		cp.y = cy;
		cp.z = cz;
		double distance = Double.MAX_VALUE;
		MetaChunk closest = null;
		for (String str : metaChunks.keySet()) {
			String[] data = str.split(",");
			int x = Integer.parseInt(data[0]);
			int y = Integer.parseInt(data[1]);
			int z = Integer.parseInt(data[2]);
			MetaChunk c = metaChunks.get(str);
			double dist = cp.distance(x, y, z);
			if (dist < distance) {
				distance = dist;
				closest = c;
			}
		}
		return closest;
	}
	
	private Chunk findClosestChunk() {
		int cx = (int)Math.floor(Camera.position.x / Chunk.SIZE);
		int cy = (int)Math.floor(Camera.position.y / Chunk.SIZE_Y);
		int cz = (int)Math.floor(Camera.position.z / Chunk.SIZE);
		cp.x = cx;
		cp.y = cy;
		cp.z = cz;
		double distance = Double.MAX_VALUE;
		Chunk closest = null;
		for (int i = 0; i < activeChunks.size(); i++) {
			Chunk c = activeChunks.get(i);
			if (!c.generated) {
				double dist = cp.distance(c.getX(), c.getY(), c.getZ());
				if (dist < distance) {
					distance = dist;
					closest = c;
				}
			}
		}
		
		return closest;
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
	
	public int ticks = 0;
	
	public void tickChunks() {
		
		if (this.worldSettings.tickSpeed <= 0) {
			this.worldSettings.tickSpeed = 1;
		}
		if (ticks % this.worldSettings.tickSpeed == 0)
		for (int i = 0; i < activeChunks.size(); i++) {
			Chunk chunk = activeChunks.get(i);
			if (chunk != null) 
			{
				chunk.tick();
			}
		}
		
		ticks++;
		
	}
	
	public void tick() {
		for (int i = 0; i < entities.size(); i++) {
			
			Entity e = entities.get(i);
			
			if (e instanceof PlayerEntity) {
				if (!players.contains(e)) {
					players.add(e);
				}
			}
			
			if (e.ticksExisted > 10) {
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
			for (int i = 0; i < activeChunks.size(); i++) {
				rendering.add(activeChunks.get(i));
			}
		}

		
		
		for (int i = 0; i < rendering.size(); i++) {
			if (rendering.get(i) == null) continue;
			try {
				rendering.get(i).render(shader);
			}catch (Exception e) {
				
			}
		}
		
		for (int i = 0; i < rendering.size(); i++) {
			if (rendering.get(i) == null) continue;
			try {
				rendering.get(i).renderWater(shader);
			}catch (Exception e) {
				
			}
		}
		
		adding = true;
		renderTileHover(shader);
	}
	
	public void renderChunksShadow(ShaderProgram shader, ShadowRenderer renderer) {
//		for (int i = 0; i < rendering.size(); i++) {
//			if (rendering.get(i) != null)
//			rendering.get(i).renderShadow(shader, renderer);
//		}
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
		
		
		
		if (Camera.currentTile != null) {
			
			TilePos pos = Camera.currentTile.getPosition();
			if (pos != null) {
				float size = 0.01f;
				
				if (Camera.currentTile.getType() == RayTraceType.TILE) {
					
					
					
					
					Part part = new Part(null);
					part.size = new Vector3i(2, 2, 2);
					part.outlineMesh = Part.buildOutlineMesh(part);
					part.outlineMesh.texture = Textures.WHITE_SQUARE;
					this.selection = part.outlineMesh;
					MeshRenderer.renderMesh(selection, new Vector3f(pos.x - (size / 2.0f), pos.y - (size / 2.0f), pos.z - (size / 2.0f)).add(0.5f, 0.5f, 0.5f), new Vector3f(1.0f + size, 1.0f + size, 1.0f + size).mul(1.01f), shader);
					this.selection.dispose();
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
		
		Vector3f raypos = new Vector3f(start);
		Vector3f raydir = new Vector3f(end).sub(start).div(length);
		
		RayBox raybox = new RayBox();
		raybox.min = new Vector3f(0);
		raybox.max = new Vector3f(0);
		for (int ii = 0; ii < 100; ii++) {
			if (raypos.distance(start) > length) {
				break;
			}
			pos.setPosition(Math.floor(raypos.x), Math.floor(raypos.y), Math.floor(raypos.z));
			raybox.min.set(pos.x, pos.y, pos.z);
			raybox.max.set(pos.x + 1, pos.y + 1, pos.z + 1);
			RayIntersection intersection = Raytracer.intersectBox(start, raydir, raybox);
			raypos.set(start);
			raypos.add(new Vector3f(raydir).mul(intersection.lambda.y + 0.001f));
			Tile tile = getTile(pos);
			TileData data = getTileData(pos, false);
			if (tile != null) {
				if (tile.isVisible()) {
					if (tile.getRayTraceType() == type) {
						Vector3f dir = new Vector3f(end).sub(start);
						dir.div(dir.length());
						float min = Float.MAX_VALUE;
						boolean hit = false;
						for (RayBox box : tile.collisionBoxes) {
							RayBox b2 = new RayBox();
							b2.min = new Vector3f(box.min).add(pos.x, pos.y, pos.z);
							b2.max = new Vector3f(box.max).add(pos.x, pos.y, pos.z);
							
							Vector3f center = new Vector3f(b2.min).add(b2.max).div(2);
							center.sub(pos.x, pos.y, pos.z);
							Vector3f c1 = new Vector3f(center);
							center.sub(0.5f, 0.5f, 0.5f);							
							Vector3f rotation = new Vector3f((float)Math.toRadians(tile.getPitchForState(data.getState())), (float)Math.toRadians(tile.getYawForState(data.getState())), 0);
							center.rotateX(rotation.x);
							center.rotateY(rotation.y + (float)Math.toRadians(180));
							center.add(0.5f, 0.5f, 0.5f);
							
							b2.min.add(center).sub(c1);
							b2.max.add(center).sub(c1);
							if (Raytracer.doesCollisionOccur(start, dir, b2, rotation)) {
								RayIntersection it = Raytracer.intersectBox(start, dir, b2, rotation);
								if (it.lambda.x < min) {
									min = it.lambda.x;
									hit = true;
								}
							}
						}
						if (hit && min <= length) {
							return new RayTraceResult(RayTraceType.TILE, pos, new Vector3f(start).add(dir.mul(min - 0.1f)));
						}
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
	
	public boolean setTile(TilePos pos, Tile tile, boolean sounds) {
		return setTile(pos.x, pos.y, pos.z, tile, sounds);
	}
	
	public boolean setTile(TilePos pos, Tile tile) {
		return setTile(pos.x, pos.y, pos.z, tile);
	}
	

	public boolean setTile(int x, int y, int z, Tile tile) {
		return setTile(x, y, z, tile, true);
	}
	
	public boolean setMetaData(int x, int y, int z, TileData tile) {
		int mx = (int)Math.floor((float)x / Chunk.SIZE);
		int my = (int)Math.floor((float)y / Chunk.SIZE_Y);
		int mz = (int)Math.floor((float)z / Chunk.SIZE);
		MetaChunk chunk = getMetaChunk(mx, my, mz);

		x -= mx * Chunk.SIZE;
		y -= my * Chunk.SIZE_Y;
		z -= mz * Chunk.SIZE;
		
		if (chunk == null) {
			addMetaChunk(x, y, z);
			chunk = getMetaChunk(x, y, z);
		}
		
		if (chunk != null) {
			chunk.setTileData(x, y, z, tile);
			return true;
		}
		return false;
	}
	
	public boolean setMetaTile(TilePos pos, Tile tile, boolean sounds) {
		return setMetaTile(pos.x, pos.y, pos.z, tile, sounds);
	}
	
	public boolean setMetaTile(int x, int y, int z, Tile tile, boolean sounds) {
		int mx = (int)Math.floor((float)x / Chunk.SIZE);
		int my = (int)Math.floor((float)y / Chunk.SIZE_Y);
		int mz = (int)Math.floor((float)z / Chunk.SIZE);
		MetaChunk chunk = getMetaChunk(mx, my, mz);

		x -= mx * Chunk.SIZE;
		y -= my * Chunk.SIZE_Y;
		z -= mz * Chunk.SIZE;
		
		if (chunk == null) {
			addMetaChunk(mx, my, mz);
			chunk = getMetaChunk(mx, my, mz);
		}
		
		if (chunk != null) {
			chunk.setTileData(x, y, z, new TileData(tile.getID()));
			return true;
		}
		return false;
	}
	
	public boolean setTile(int x, int y, int z, Tile tile, boolean sounds) {
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
			if (sounds)
			if (tile.sound != null) {
				
				Camera.soundSource.setPosition(x, y, z);
				Camera.soundSource.play(tile.sound[getRandom().nextInt(tile.sound.length)]);
			}
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
			{
				Tile tile = Tiles.getTile(data.getTile());
				if (tile.sound != null) {
					
					Camera.soundSource.setPosition(pos.x, pos.y, pos.z);
					Camera.soundSource.play(tile.sound[getRandom().nextInt(tile.sound.length)]);
				}
			}
			
			data.setMiningTime(data.getMiningTime() + strength / Tiles.getTile(data.getTile()).getHardness());
			int current = (int)(data.getMiningTime() / 20);
			int last = (int)(data.getLastMiningTime() / 20);
			
			
			
			if (data.getMiningTime() > 100.0) {
				data.setMiningTime(0.0f);
				Tile tile = chunk.getLocalTile(x, y, z);
				tile.dropAsItem(this, x + mx * Chunk.SIZE, y + my * Chunk.SIZE_Y, z + mz * Chunk.SIZE);
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
