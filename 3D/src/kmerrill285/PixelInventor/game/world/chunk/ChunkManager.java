package kmerrill285.PixelInventor.game.world.chunk;

import java.util.ArrayList;
import java.util.HashMap;

import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.settings.Settings;

public class ChunkManager {
	public HashMap<String, Chunk> chunks;
	
	ArrayList<Chunk> setup;
	ArrayList<Chunk> rebuild;
	ArrayList<Chunk> unloaded;
	ArrayList<Chunk> visible;
	ArrayList<Chunk> rendering;
	ArrayList<Chunk> loaded;
	ArrayList<Chunk> removing;

	private float lastX, lastY, lastZ;
	private float lastRX, lastRY;
	
	private ChunkGenerator generator;
	
	private boolean queueUpdate = false;
	
	public ChunkManager(ChunkGenerator generator) {
		chunks = new HashMap<String, Chunk>();
		setup = new ArrayList<Chunk>();
		rebuild = new ArrayList<Chunk>();
		unloaded = new ArrayList<Chunk>();
		visible = new ArrayList<Chunk>();
		rendering = new ArrayList<Chunk>();
		loaded = new ArrayList<Chunk>();
		removing = new ArrayList<Chunk>();
		
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
		loaded.clear();
		
		
		
		for (int x = cx - Settings.VIEW_X; x < cx + Settings.VIEW_X; x++) {
			for (int y = cy - Settings.VIEW_Y; y < cy + Settings.VIEW_Y; y++) {
				for (int z = cz - Settings.VIEW_X; z < cz + Settings.VIEW_X; z++) {
					Chunk chunk = getChunk(x, y, z);
					
					if (chunk == null) {
						chunk = new Chunk(x, y, z, this);
						setup.add(chunk);
					}
					loaded.add(chunk);
				}
			}
		}
		for (String str : chunks.keySet()) {
			unloaded.add(chunks.get(str));
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
			if (rebuild.contains(chunk))
				rebuild.remove(chunk);
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
			
			if (distY > Settings.VIEW_Y * 2 || distX > Settings.VIEW_X * 2 || distZ > Settings.VIEW_X * 2) {
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
		if (queueUpdate == false) {
			if (lastX != Camera.position.x ||
					lastY != Camera.position.y||
					lastZ != Camera.position.z ||
					lastRX != Camera.rotation.x ||
					lastRY != Camera.rotation.y) {
				updateRendered();
			}
			lastX = Camera.position.x;
			lastY = Camera.position.y;
			lastZ = Camera.position.z;
			lastRX = Camera.rotation.x;
			lastRY = Camera.rotation.y;
			updateRemoving();
		}
		queueUpdate = true;
		
		
		for (int i = 0; i < rendering.size(); i++) {
			if (rendering.get(i) != null)
			rendering.get(i).render(shader);
		}
		
	}
	
	public void dispose() {
		for (String str : chunks.keySet()) {
			chunks.get(str).dispose();
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
}
