package kmerrill285.PixelInventor.game.world.chunk;

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.settings.Settings;
import kmerrill285.PixelInventor.game.world.World;
import kmerrill285.PixelInventor.game.world.chunk.generator.ChunkGenerator;

public class ChunkManager {
	public HashMap<String, Chunk> chunks;
	
	ArrayList<Chunk> setup;
	ArrayList<Chunk> rebuild;
	ArrayList<Chunk> unloaded;
	ArrayList<Chunk> visible;
	ArrayList<Chunk> rendering;
	ArrayList<Chunk> loaded;
	ArrayList<Chunk> removing;
	ArrayList<Chunk> nextToSetup;

	private float lastX, lastY, lastZ;
	private float lastRX, lastRY;
	
	private ChunkGenerator generator;
	private World world;
	
	private boolean queueUpdate = false;
	
	private boolean moved = false;
	
	public ChunkManager(World world, ChunkGenerator generator) {
		this.world = world;
		chunks = new HashMap<String, Chunk>();
		setup = new ArrayList<Chunk>();
		rebuild = new ArrayList<Chunk>();
		unloaded = new ArrayList<Chunk>();
		visible = new ArrayList<Chunk>();
		rendering = new ArrayList<Chunk>();
		loaded = new ArrayList<Chunk>();
		removing = new ArrayList<Chunk>();
		nextToSetup = new ArrayList<Chunk>();
		
		this.generator = generator;
	}
	
	public void update() {
		if (queueUpdate == false) {
			return;
		}
		updateLoading();
		updateSetup();
		updateRebuild();
		updateVisibility();
		updateUnloaded();
		queueUpdate = false;
	}
	
	private void updateLoading() {
		int cx = (int)(Camera.position.x / Chunk.SIZE);
		int cy = (int)(Camera.position.y / Chunk.SIZE);
		int cz = (int)(Camera.position.z / Chunk.SIZE);
		
		Vector3f forward = Camera.getForward(Camera.rotation.x * -1, Camera.rotation.y);
		
		loaded.clear();
		
		cx -= forward.x * 5;
		cy -= forward.y * 5;
		cz -= forward.z * 5;
		
		loadChunks(cx, cy, cz);
		
		cx += forward.x * 5;
		cy += forward.y * 5;
		cz += forward.z * 5;
		loadChunks(cx, cy, cz);
		
		cx += forward.x * 5;
		cy += forward.y * 5;
		cz += forward.z * 5;
		loadChunks(cx, cy, cz);
		
		
		for (String str : chunks.keySet()) {
			unloaded.add(chunks.get(str));
		}
	}
	
	private void loadChunks(int cx, int cy, int cz) {
		int max = 100;
		int i = 0;
		if (moved == true) setup.clear();
		A:
		for (int VIEW_X = 0; VIEW_X < Settings.VIEW_X; VIEW_X++)
			for (int VIEW_Y = 0; VIEW_Y < Settings.VIEW_Y; VIEW_Y++)
		for (int x = cx - VIEW_X; x < cx + VIEW_X; x++) {
			for (int y = cy - VIEW_Y; y < cy + VIEW_Y; y++) {
				for (int z = cz - VIEW_X; z < cz + VIEW_X; z++) {
					Chunk chunk = getChunk(x, y, z);
					
					if (chunk == null) {
						chunk = new Chunk(x, y, z, this);
						setup.add(chunk);
						i++;
						if (i > max) break A;
					}
				}
			}
		}
	}
	
	private void updateSetup() {
		int maxSetup = 100;
		
		for (int i = 0; i < setup.size(); i++) {
			if (i > maxSetup) break;
			generator.generateChunk(setup.get(i));
			Chunk chunk = setup.get(i);
			addChunk(chunk.getX(), chunk.getY(), chunk.getZ(), chunk);
			chunk.rebuild();
			loaded.add(chunk);
		}
		
		
		
		setup.clear();
	}
	
	
	private void updateRebuild() {
		int rebuilt = 0;
		int max_rebuild = 100;
		
		for (int i = 0; i < rebuild.size(); i++) {
			rebuild.get(i).rebuild();
			rebuilt++;
			if (rebuilt > max_rebuild) break;
			
			for (int xx = -1; xx < 2; xx++) {
				for (int yy = -2; yy < 2; yy++) {
					for (int zz = -2; zz < 2; zz++) {
						if (xx != 0 && yy != 0 && zz != 0) {
							int x = xx + rebuild.get(i).getX();
							int y = yy + rebuild.get(i).getY();
							int z = zz + rebuild.get(i).getZ();
							Chunk chunk = getChunk(x, y, z);
							if (chunk != null) {
								rebuild.add(chunk);
							}
						}
					}
				}
			}
		}
		
		rebuild.clear();
	}
	
	private void updateUnloaded() {
		int cx = (int)(Camera.position.x / Chunk.SIZE);
		int cy = (int)(Camera.position.y / Chunk.SIZE);
		int cz = (int)(Camera.position.z / Chunk.SIZE);
		
		for (int i = 0; i < unloaded.size(); i++) {
			if (loaded.contains(unloaded.get(i)) || setup.contains(unloaded.get(i))) {
				continue;
			}
			int x = unloaded.get(i).getX();
			int y = unloaded.get(i).getY();
			int z = unloaded.get(i).getZ();
			
			double distX = Math.abs(x - cx);
			double distZ = Math.abs(z - cz);
			double distY = Math.abs(y - cy);
			
			if (distY > Settings.VIEW_Y || distX > Settings.VIEW_X || distZ > Settings.VIEW_X) {
				removing.add(unloaded.get(i));
			} else 
			{
				if (!visible.contains(unloaded.get(i))) {
					visible.add(unloaded.get(i));
				}
			}
			
		}
		unloaded.clear();
	}
	
	private void updateRemoving() {
		for (int i = 0; i < removing.size(); i++) {
			removing.get(i).dispose();
			removeChunk(removing.get(i));
		}
		removing.clear();
	}
	
	private void updateVisibility() {
		visible.clear();
		for (int i = 0; i < loaded.size(); i++) {
			Chunk chunk = loaded.get(i);
			if (chunk != null)
			if (chunk.isSurrounded()) continue;
			visible.add(chunk);
		}
	}
	
	private void updateRendered() {
		rendering.clear();
		for (int i = 0; i < visible.size(); i++) {
			if (visible.get(i) != null)
			if (visible.get(i).shouldRender())
			rendering.add(visible.get(i));
			
		}
		
	}

	public void render(ShaderProgram shader) {
		moved = false;
		if (queueUpdate == false) {
			if (lastX != Camera.position.x ||
					lastY != Camera.position.y||
					lastZ != Camera.position.z ||
					lastRX != Camera.rotation.x ||
					lastRY != Camera.rotation.y) {
				updateRendered();
				moved = true;
			}
			lastX = Camera.position.x;
			lastY = Camera.position.y;
			lastZ = Camera.position.z;
			lastRX = Camera.rotation.x;
			lastRY = Camera.rotation.y;
			updateRemoving();
			
			
		}
		
		queueUpdate = true;
		
		int cx = (int)(Camera.position.x / Chunk.SIZE);
		int cy = (int)(Camera.position.y / Chunk.SIZE);
		int cz = (int)(Camera.position.z / Chunk.SIZE);
		
		Vector3f f = Camera.getForward(-Camera.rotation.x, Camera.rotation.y);
		float mul = 4;
		int frx = (int)((f.x * mul * Chunk.SIZE) / Chunk.SIZE);
		int fry = (int)((f.y * mul * Chunk.SIZE) / Chunk.SIZE);
		int frz = (int)((f.z * mul * Chunk.SIZE) / Chunk.SIZE);
		
		float dist = 2;
		
		Vector3f cp = new Vector3f(cx, cy, cz);
		Vector3f fr = new Vector3f(frx, fry, frz);
		ArrayList<Chunk> rendered = new ArrayList<Chunk>();
		
		for (int i = 0; i < rendering.size(); i++) {
			if (rendering.get(i) != null) {
				Vector3f pos = new Vector3f(rendering.get(i).getX(), rendering.get(i).getY(), rendering.get(i).getZ());
				if (cp.distance(pos) <= 5) {
					rendering.get(i).tick();
				}
			}
		}
		
		for (float j = 0; j < fr.length(); j+=0.1f) {
			Vector3f nf = new Vector3f(fr).normalize().mul(j);
			for (int i = 0; i < rendering.size(); i++) {
				if (!rendered.contains(rendering.get(i)))
				if (rendering.get(i) != null) {
					Vector3f pos = new Vector3f(rendering.get(i).getX(), rendering.get(i).getY(), rendering.get(i).getZ());
					
					if (pos.distance(cx + nf.x, cy + nf.y, cz + nf.z) <= dist + j * 2) {
						rendering.get(i).render(shader);
						rendered.add(rendering.get(i));
					}
				}
			}
		}
		
	}
	
	
	public void renderShadow(ShaderProgram shader, Matrix4f lightMatrix) {
		for (int i = 0; i < rendering.size(); i++) {
			if (rendering.get(i) != null) {
				rendering.get(i).renderShadow(shader, lightMatrix);
			}
		}
		
	}
	
	public void dispose() {
		try {
			for (String str : chunks.keySet()) {
				
				chunks.get(str).dispose();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		chunks.clear();
	}
	
	public Chunk getChunk(int x, int y, int z) {
		return chunks.get(x+","+y+","+z);
	}
	
	private void addChunk(int x, int y, int z, Chunk chunk) {
		chunks.put(x+","+y+","+z, chunk);
		
	}
	
	private void removeChunk(Chunk chunk) {
		removeChunk(chunk.getX(), chunk.getY(), chunk.getZ());
	}
	
	private void removeChunk(int x, int y, int z) {
		if (chunks.remove(x+","+y+","+z) == null)
		for (String str : chunks.keySet()) {
			if (str.contentEquals(x+","+y+","+z)) {
				chunks.remove(str);
				break;
			}
		}
	}

	public World getWorld() {
		return this.world;
	}

	public boolean canRender() {
		return queueUpdate;
	}
}
