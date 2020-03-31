package kmerrill285.Inignoto.game.world.chunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;

import org.joml.Vector3f;
import org.joml.Vector3i;

import kmerrill285.Inignoto.Inignoto;
import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.client.rendering.MeshRenderer;
import kmerrill285.Inignoto.game.client.rendering.chunk.MegachunkBuilder;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.settings.Settings;
import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.resources.Constants;
import kmerrill285.Inignoto.resources.FPSCounter;

public class Chunk {
	

	public static final int SIZE = 16;
	public static final int SIZE_Y = 256;
		
	private static final int NUM_TILES = Chunk.SIZE * Chunk.SIZE * Chunk.SIZE_Y;
	
	private static final Vector3f scale = new Vector3f(SIZE, SIZE_Y, SIZE);
	
	private World world;
	
	private int x, y, z;
	private Vector3i pos;
	
	public int voxels = 0;
	
	private TileData[] tiles;
	
	public Mesh mesh;
	
	public boolean needsToSave = false;
	
	public float loadValue = 1.0f;
	
	public boolean generated = false;
	
	
	public Chunk(int x, int y, int z, World world) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		int X = x * SIZE;
		int Y = y * SIZE_Y;
		int Z = z * SIZE;
		pos = new Vector3i(X, Y, Z);
		
		this.world = world;
	}
	
	public void setPos(int x, int y, int z) {
		int X = x * SIZE;
		int Y = y * SIZE_Y;
		int Z = z * SIZE;
		pos.x = X;
		pos.y = Y;
		pos.z = Z;
		this.x = x;
		this.y = y;
		this.z = z;
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
	
	
	public Vector3i getWorldPos() {
		return this.pos;
	}
	
	public boolean isEmpty() {
		return voxels <= 0;
	}

	public TileData[] getTiles() {
		return tiles;
	}

	public void setTiles(TileData[] tiles) {
		this.tiles = tiles;
	}
	
	public Tile getLocalTile(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			if (tiles == null) {
				getWorld().getChunkGenerator().generateChunk(this, false);
			}
			if (tiles == null) {
				tiles = new TileData[NUM_TILES];
			}
			if (tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] == null) return Tiles.AIR;
			return Tiles.getTile(tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y].getTile());
		}
		return Tiles.AIR;
	}
	
	private boolean changed = false;
	public TileData getTileData(int x, int y, int z, boolean modifying) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			if (tiles == null) {
				tiles = new TileData[NUM_TILES];
				getWorld().getChunkGenerator().generateChunk(this, false);
			}
			if (modifying) changed = true;
			if (tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] == null) return new TileData(Tiles.AIR.getID());
			return tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y];
		}
		return new TileData(Tiles.AIR.getID());
	}
	
	public void setTileData(int x, int y, int z, TileData data) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			if (tiles == null) {
				return;
			}
			tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] = data;
		}
	}
	
	
	public void setLocalTile(int x, int y, int z, Tile tile) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			if (tiles == null) {
				return;
			}
			
			Tile local = getLocalTile(x, y, z);
			if (tile == Tiles.AIR && local != Tiles.AIR) {
				if (voxels > 0) {
					voxels--;
				}
			}
			if (tile != Tiles.AIR && local == Tiles.AIR) {
				voxels++;
			}
			if (tiles == null) 
			{
				tiles = new TileData[Chunk.SIZE * Chunk.SIZE_Y * Chunk.SIZE];
			}
			if (tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] == null) {
				tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] = new TileData(tile.getID());
			} else {
				tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y].setTile(tile.getID());
			}
			
		}
	}

	
	
	public boolean isLocalTileNotFull(int x, int y, int z) {
		Tile local = getLocalTile(x, y, z);
		return !local.isFullCube() || !local.isVisible();
	}

	public World getWorld() {
		return world;
	}
	private File savefile;
	
	public void save() {
		if (!this.needsToSave) return;
		if (tiles == null) return;
		String DIR = "PixelInventor/saves/"+Inignoto.game.world.getWorldSaver().getWorldName()+"/";
		File dir = new File(DIR);
		dir.mkdirs();
		this.savefile = new File(DIR+"chunk"+getX()+","+getY()+","+getZ()+".chnk");
		try {
			savefile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		try {
			FileOutputStream fos = new FileOutputStream(savefile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(tiles);
			
			oos.close();
			fos.close();
			this.needsToSave = false;
		} catch (Exception e) {
			e.printStackTrace();
			this.savefile.delete();
			System.out.println("Failed to save chunk!");
		}
		
	}
	
	public boolean load() {
		if (savefile == null) {
			String DIR = "PixelInventor/saves/"+Inignoto.game.world.getWorldSaver().getWorldName()+"/";
			setSavefile(new File(DIR+"chunk"+getX()+","+getY()+","+getZ()+".chnk"));
		}
		if (!savefile.exists()) {return false;}
		
		boolean setTile = false;
		if (savefile != null) {
			tiles = new TileData[SIZE * SIZE_Y * SIZE];
			try {
				FileInputStream fis = new FileInputStream(savefile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				TileData[] t = (TileData[])ois.readObject();
				ois.close();
				fis.close();
				tiles = t;
				for (int i = 0; i < t.length; i++) {
					if (t[i].getTile() != Tiles.AIR.getID()) {
						voxels++;
					}
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return setTile;
	}
	
	public boolean canTick() {
		if (voxels <= 0) return false;
		int x = getX() * SIZE + SIZE / 2;
		int y = getY() * SIZE_Y + SIZE_Y / 2;
		int z = getZ() * SIZE + SIZE / 2;
		return Camera.position.distance(x, 0, z) <= SIZE * Constants.ACTIVE_CHUNK_DISTANCE && Camera.position.distance(0, y, 0) <= Settings.VERTICAL_VIEW_DISTANCE * SIZE_Y * 1.5;
	}
	
	public boolean isActive() {
		if (voxels <= 0) return false;
		int x = getX() * SIZE + SIZE / 2;
		int y = getY() * SIZE_Y + SIZE_Y / 2;
		int z = getZ() * SIZE + SIZE / 2;
		return Camera.position.distance(x, y, z) <= SIZE * SIZE * 1.5f;
	}
	
	public boolean isWithinViewingRange() {
		int x = getX() * SIZE + SIZE / 2;
		int y = getY() * SIZE_Y + SIZE_Y / 2;
		int z = getZ() * SIZE + SIZE / 2;
		return Camera.position.distance(x, 0, z) <= SIZE * Settings.VIEW_DISTANCE && Camera.position.distance(0, y, 0) <= Settings.VERTICAL_VIEW_DISTANCE * SIZE_Y * 1.5;
	}
	
	public boolean canRender() {
		int x = getX() * SIZE + SIZE / 2;
		int z = getZ() * SIZE + SIZE / 2;
		
		return Camera.position.distance(x, 0, z) <= SIZE * Settings.VIEW_DISTANCE;
	}

	public void tick() {
		if (this.canTick()) {
			TilePos pos = new TilePos(0, 0, 0);
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE_Y; y++) {
					for (int z = 0; z < SIZE; z++) {
						pos.x = x + getX() * SIZE;
						pos.y = y + getY() * SIZE_Y;
						pos.z = z + getZ() * SIZE;
						TileData data = getTileData(x, y, z, false);
						if (data != null) {
							if (data.getTile() == Tiles.AIR.getID())
								continue;
							Tile tile = Tiles.getTile(data.getTile());
							if (tile.getTickPercent() > 0)
							if (getWorld().getRandom().nextDouble() * 100 <= tile.getTickPercent()) {
								tile.tick(getWorld(), pos, getWorld().getRandom());
							}
							if (data.getMiningTime() > 0) {
								if (getWorld().getRandom().nextInt(100) <= 75) {
									data.setMiningTime(data.getMiningTime() - 1);
									this.markForRerender();
								}
								if (data.getMiningTime() < 0) {
									data.setMiningTime(0);
									if ((int)data.getMiningTime() / 20 != (int)data.lastMiningTime / 20) {
										this.markForRerender();
									}
								}
							}
							
						}
					}
				}
			}
		}
	}
	private boolean needsToRebuild = false;
	boolean triedToLoad = false;
	public void render(ShaderProgram shader) {
		if (meshesToRemove.size() > 0) {
			meshesToRemove.pop().dispose();
		}
		if (mesh != null) {
			if (canRender()) {
				if (loadValue > 0) {
					loadValue -= 0.005f * FPSCounter.getDelta();
				} else {
					loadValue = 0;
				}
				
				shader.setUniformFloat("loadValue", loadValue);
				MeshRenderer.renderMesh(mesh, new Vector3f(getX() * SIZE, getY() * SIZE_Y, getZ() * SIZE), shader);
				shader.setUniformFloat("loadValue", 0);
			}
		}
		this.testForActivation();
	}
	
	public void testForActivation() {
		if (needsToRebuild) {
			if (this.mesh != null) this.mesh.dispose();
			needsToRebuild = false;
			mesh = MegachunkBuilder.buildChunk(this);
		}
		if (isActive()) {
			if (tiles == null) {
				getWorld().getChunkGenerator().generateChunk(this, false);
			}
		} else {
			if (mesh != null) {
				if (changed) {
					mesh = MegachunkBuilder.buildChunk(this);
					changed = false;
				}
				if (!needsToSave)
					tiles = null;
			}
		}
	}
	
	public void dispose() {
		if (mesh != null) mesh.dispose();
		if (needsToSave) this.save();
		while (meshesToRemove.size() > 0) {
			meshesToRemove.pop().dispose();
		}
		tiles = null;
		this.mesh = null;
		this.meshesToRemove = null;
		this.setSavefile(null);
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void markForRerender() {
		this.setNeedsToRebuild(true);
	}
	
	public void markForSave() {
		this.needsToSave = true;
		if (!World.saveQueue.contains(this))
			World.saveQueue.add(this);
	}
	
	private ArrayDeque<Mesh> meshesToRemove = new ArrayDeque<Mesh>();
	public void removeMesh(final Mesh mesh) {
		meshesToRemove.add(mesh);
	}

	public void setSavefile(File savefile) {
		this.savefile = savefile;
	}

	public boolean needsToRebuild() {
		return needsToRebuild;
	}

	public void setNeedsToRebuild(boolean needsToRebuild) {
		this.needsToRebuild = needsToRebuild;
	}


	
}
