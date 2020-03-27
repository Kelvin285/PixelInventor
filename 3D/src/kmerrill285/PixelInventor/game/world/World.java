package kmerrill285.PixelInventor.game.world;

import java.util.ArrayList;
import java.util.Random;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

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
import kmerrill285.PixelInventor.resources.Constants;
import kmerrill285.PixelInventor.resources.RayTraceResult;
import kmerrill285.PixelInventor.resources.RayTraceResult.RayTraceType;

public class World {
	
	private ChunkGenerator generator;
	private Random random;
			
	private long seed;
	
	private WorldSaver worldSaver;
	
	public ArrayList<Entity> entities = new ArrayList<Entity>();
	
	private Fog fog;
	private Fog shadowBlendFog;
	
	int megaview = 8;
	int numMegachunks = 0;
	private Megachunk[] megachunk = new Megachunk[megaview * megaview * megaview];
	
	public DirectionalLight light = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0, -1, 0), 1.0f);
		
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
	
	public Megachunk getMegachunk(int x, int y, int z) {
		for (int i = 0; i < numMegachunks; i++) {
			Megachunk m = megachunk[i];
			if (m != null)
			if (m.getX() == x && m.getY() == y && m.getZ() == z) {
				return m;
			}
		}
		return null;
	}
	
	public void addMegachunk(int x, int y, int z) {
		if (numMegachunks < megachunk.length) {
			megachunk[numMegachunks] = new Megachunk(x, y, z, this);
			numMegachunks++;
		}
	}
	
	public void removeMegachunk(int x, int y, int z) {
		if (numMegachunks > 0) {
			for (int i = 0; i < megachunk.length; i++) {
				if (megachunk[i] != null)
				if (megachunk[i].getX() == x && megachunk[i].getY() == y && megachunk[i].getZ() == z) {
					megachunk[i].dispose();
					megachunk[i] = null;
					for (int b = i; b < megachunk.length - 1; b++) {
						megachunk[b] = megachunk[b + 1];
					}
					megachunk[megachunk.length - 1] = null;
					break;
				}
			}
			numMegachunks--;
		}
	}
	
	public void buildMegachunks() {
		double distance[] = {Double.MAX_VALUE};
		Megachunk[] m = {null};
		Vector2i closest = new Vector2i(0, 0);
		
		for (int i = 0; i < numMegachunks; i++) {
			if (megachunk[i] != null) {
				megachunk[i].updateAndBuild(distance, m, closest, Camera.position);
			}
		}
		if (distance[0] != Double.MAX_VALUE) {
			m[0].setLocalChunk(closest.x, closest.y, new Chunk(closest.x, closest.y, m[0]));
			this.getChunkGenerator().generateChunk(m[0].getLocalChunk(closest.x, closest.y));
			m[0].getLocalChunk(closest.x, closest.y).mesh = MegachunkBuilder.buildChunk(m[0].getLocalChunk(closest.x, closest.y));
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Vector3i mpos = new Vector3i(0);
	
	private Vector3i closepos = new Vector3i(0);
	private int mv, mx, my, mz;
	private float cy;
	private double distance;
	private boolean found;
	private Megachunk m;
	private double dist;
	public void updateChunkManager() {
		mx = (int)(Camera.position.x / (Megachunk.SIZE * Chunk.SIZE));
		my = (int)(Camera.position.y / Chunk.SIZE_Y);
		mz = (int)(Camera.position.z / (Megachunk.SIZE * Chunk.SIZE));

		cy = Camera.position.y % Chunk.SIZE_Y;
		if (cy < 0) cy += Chunk.SIZE_Y;
		
		
		distance = Double.MAX_VALUE;
		found = false;
		
		for (int x = -megaview / 2; x < megaview / 2 + 1; ++x) {
			for (int z = -megaview / 2; z < megaview / 2 + 1; ++z) {
				for (int y = -1; y < 2; y++) {
					m = getMegachunk(mx + x, y + my, mz + z);
					
					mpos.x = (mx + x) * Megachunk.SIZE * Chunk.SIZE;
					mpos.y = (y + my) * Chunk.SIZE_Y;
					mpos.z = (mz + z) * Megachunk.SIZE * Chunk.SIZE;

					if (m == null) {
						dist = mpos.distance((int)Camera.position.x, (int)Camera.position.y, (int)Camera.position.z);
						if (dist < distance) {
							closepos = new Vector3i(x + mx, y + my, z + mz);
							distance = dist;
							found = true;
						}
					}
				}
			}
			
		}
		
		if (found) {
			m = getMegachunk(closepos.x, closepos.y, closepos.z);
			if (m == null) {
				addMegachunk(closepos.x, closepos.y, closepos.z);
			}
		}
	}
	
	public void tickMegachunks() {
		for (int i = 0; i < megachunk.length; i++) {
			if (megachunk[i] != null) {
				megachunk[i].tick();
			}
		}
	}
	
	public void tick() {
		
		updateLight();
		
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			
			int mx = (int)Math.floor((float)e.getTilePos().x / (Megachunk.SIZE * Chunk.SIZE));
			int my = (int)Math.floor((float)e.getTilePos().y / Chunk.SIZE_Y);
			int mz = (int)Math.floor((float)e.getTilePos().z / (Megachunk.SIZE * Chunk.SIZE));
			
			if (this.getMegachunk(mx, my, mz) != null) {
				e.tick();
				if (e.isDead) {
					entities.remove(i);
				}
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
		
		renderTileHover(shader);
		
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).render(shader);
		}
		
		removeRenderchunks();
	}
	
	public void removeRenderchunks() {
		int mx = (int)Math.floor(Camera.position.x / (Megachunk.SIZE * Chunk.SIZE));
		int my = (int)Math.floor(Camera.position.y / Chunk.SIZE_Y);
		int mz = (int)Math.floor(Camera.position.z / (Megachunk.SIZE * Chunk.SIZE));

		float cy = Camera.position.y % Chunk.SIZE_Y;
		if (cy < 0) cy += Chunk.SIZE_Y;
		Vector3i mpos = new Vector3i(0);
		
		for (int i = 0; i < numMegachunks; i++) {
			if (megachunk[i] != null) {
				mpos.x = megachunk[i].getX() * Megachunk.SIZE * Chunk.SIZE;
				mpos.y = 0;
				mpos.z = megachunk[i].getZ() * Megachunk.SIZE * Chunk.SIZE;				
				
				if (mpos.distance((int)Camera.position.x, 0, (int)Camera.position.z) > (megaview * Megachunk.SIZE * Chunk.SIZE) * 0.75f) {
					removeMegachunk(megachunk[i].getX(), megachunk[i].getY(), megachunk[i].getZ());
					break;
				}
			}
		}
	}
	
	public void renderMegachunks(ShaderProgram shader) {
		for (int i = 0; i < numMegachunks; i++) {
			if (megachunk[i] != null)
			megachunk[i].render(shader);
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
		for (int i = 0; i < megachunk.length; i++) {
			if (megachunk[i] != null)
			megachunk[i].dispose();
		}
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
	
	public void setTile(TilePos pos, Tile tile) {
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
			chunk.setTileAt(x, y, z, tile);
			if (chunk.getLocalChunk(x / Megachunk.SIZE, z / Megachunk.SIZE) != null)
			chunk.getLocalChunk(x / Megachunk.SIZE, z / Megachunk.SIZE).markForRerender();
		}
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
			TileData data = chunk.getTileDataAt(x, y, z);
			data.setMiningTime(data.getMiningTime() + strength / Tiles.getTile(data.getTile()).getHardness());
			int current = (int)(data.getMiningTime() / 20);
			int last = (int)(data.getLastMiningTime() / 20);
			if (data.getMiningTime() > 100.0) {
				data.setMiningTime(0.0f);
				if (chunk.getLocalChunk(x / Megachunk.SIZE, z / Megachunk.SIZE) != null)
					chunk.getLocalChunk(x / Megachunk.SIZE, z / Megachunk.SIZE).markForRerender();
				chunk.setTileAt(x, y, z, Tiles.AIR);
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
