package kmerrill285.PixelInventor.game.world.chunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;

import org.joml.Vector3f;
import org.joml.Vector3i;

import kmerrill285.PixelInventor.PixelInventor;
import kmerrill285.PixelInventor.game.client.Camera;
import kmerrill285.PixelInventor.game.client.rendering.Mesh;
import kmerrill285.PixelInventor.game.client.rendering.MeshRenderer;
import kmerrill285.PixelInventor.game.client.rendering.chunk.MegachunkBuilder;
import kmerrill285.PixelInventor.game.client.rendering.shader.ShaderProgram;
import kmerrill285.PixelInventor.game.settings.Settings;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.game.world.World;
import kmerrill285.PixelInventor.resources.Constants;
import kmerrill285.PixelInventor.resources.FPSCounter;

public class Chunk {
	

	public static final int SIZE = 16;
	public static final int SIZE_Y = SIZE * Megachunk.SIZE;
		
	private static final int NUM_TILES = Chunk.SIZE * Chunk.SIZE * Chunk.SIZE_Y;
	
	private static final Vector3f scale = new Vector3f(SIZE, SIZE_Y, SIZE);
	
	private Megachunk parent;
	
	private int x, z;
	private Vector3i pos;
	
	public int voxels = 0;
	
	private TileData[] tiles;
	
	public Mesh mesh;
	
	public boolean needsToSave = false;
	
	public float loadValue = 1.0f;
	
	
	public Chunk(int x, int z, Megachunk parent) {
		this.x = x;
		this.z = z;
		
		int X = x * SIZE;
		int Y = 0;
		int Z = z * SIZE;
		pos = new Vector3i(X, Y, Z);
		
		this.setParent(parent);
	}
	
	public void setPos(int x, int z) {
		int X = x * SIZE;
		int Y = 0;
		int Z = z * SIZE;
		pos.x = X;
		pos.y = Y;
		pos.z = Z;
		this.x = x;
		this.z = z;
	}
	
	public int getX() {
		return x;
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
				getParent().getWorld().getChunkGenerator().generateChunk(this, false);
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
				getParent().getWorld().getChunkGenerator().generateChunk(this, false);
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
//				Megachunk.structurechunk.setPos(getX(), getZ());
//				Megachunk.structurechunk.mesh = null;
//				Megachunk.structurechunk.parent = this.parent;
//				getParent().getWorld().getChunkGenerator().generateChunk(Megachunk.structurechunk, false);
//				Megachunk.structurechunk.setTileData(x, y, z, data);
//				this.mesh = MegachunkBuilder.buildChunk(Megachunk.structurechunk);
				return;
			}
			tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] = data;
		}
	}
	
	
	public void setLocalTile(int x, int y, int z, Tile tile) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			if (tiles == null) {
//				Megachunk.structurechunk.setPos(getX(), getZ());
//				Megachunk.structurechunk.setParent(this.parent);
//				getParent().getWorld().getChunkGenerator().generateChunk(Megachunk.structurechunk, false);
//				Megachunk.structurechunk.setLocalTile(x, y, z, tile);
//				this.mesh = MegachunkBuilder.buildChunk(Megachunk.structurechunk);
				return;
			}
			
			Tile local = getLocalTile(x, y, z);
			if (tile == Tiles.AIR && local != Tiles.AIR) {
				if (voxels > 0) {
					voxels--;
				}
				if (getParent().getVoxels() > 0) {
					getParent().setVoxels(getParent().getVoxels() - 1);
				}
			}
			if (tile != Tiles.AIR && local == Tiles.AIR) {
				voxels++;
				getParent().setVoxels(getParent().getVoxels() + 1);
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

	public Megachunk getParent() {
		return parent;
	}
	private File savefile;
	
	public void save() {
		if (!this.needsToSave) return;
		if (tiles == null) return;
		String DIR = "PixelInventor/saves/"+PixelInventor.game.world.getWorldSaver().getWorldName()+"/megachunk"+parent.getX()+"_"+parent.getY()+"_"+parent.getZ()+"/";
		File dir = new File(DIR);
		dir.mkdirs();
		this.savefile = new File(DIR+"chunk"+getX()+","+getZ()+".chnk");
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
			String DIR = "PixelInventor/saves/"+PixelInventor.game.world.getWorldSaver().getWorldName()+"/megachunk"+parent.getX()+"_"+parent.getY()+"_"+parent.getZ()+"/";
			setSavefile(new File(DIR+"chunk"+getX()+","+getZ()+".chnk"));
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
		int x = getX() * SIZE + parent.getX() * Megachunk.SIZE * SIZE + SIZE / 2;
		int y = parent.getY() * SIZE_Y + SIZE_Y / 2;
		int z = getZ() * SIZE + parent.getZ() * Megachunk.SIZE * SIZE + SIZE / 2;
		return Camera.position.distance(x, y, z) <= SIZE * Constants.ACTIVE_CHUNK_DISTANCE && Camera.position.distance(0, y, 0) <= SIZE_Y * 1.5;
	}
	
	public boolean isActive() {
		if (voxels <= 0) return false;
		int x = getX() * SIZE + parent.getX() * Megachunk.SIZE * SIZE + SIZE / 2;
		int y = parent.getY() * SIZE_Y + SIZE_Y / 2;
		int z = getZ() * SIZE + parent.getZ() * Megachunk.SIZE * SIZE + SIZE / 2;
		return Camera.position.distance(x, y, z) <= SIZE * Megachunk.SIZE * 1.5f;
	}
	
	public boolean isWithinViewingRange() {
		int x = getX() * SIZE + parent.getX() * Megachunk.SIZE * SIZE + SIZE / 2;
		int y = parent.getY() * SIZE_Y + SIZE_Y / 2;
		int z = getZ() * SIZE + parent.getZ() * Megachunk.SIZE * SIZE + SIZE / 2;
		return Camera.position.distance(x, 0, z) <= SIZE * Settings.VIEW_DISTANCE && Camera.position.distance(0, y, 0) <= SIZE_Y * 1.5;
	}
	
	public boolean canRender() {
		int x = getX() * SIZE + parent.getX() * Megachunk.SIZE * SIZE + SIZE / 2;
		int y = parent.getY() * SIZE_Y + SIZE_Y / 2;
		int z = getZ() * SIZE + parent.getZ() * Megachunk.SIZE * SIZE + SIZE / 2;
		if (!Camera.frustum.intersects(x - SIZE / 2, z - SIZE / 2, SIZE, SIZE) && 
				!Camera.downFrustum.intersects(x - SIZE / 2, z - SIZE / 2, SIZE, SIZE)) {
			return false;
		}
		return Camera.position.distance(x, 0, z) <= SIZE * Settings.VIEW_DISTANCE;
	}

	public void tick() {
		if (this.canTick()) {
			TilePos pos = new TilePos(0, 0, 0);
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE_Y; y++) {
					for (int z = 0; z < SIZE; z++) {
						pos.x = x + SIZE * Megachunk.SIZE * parent.getX();
						pos.y = y + SIZE_Y * parent.getY();
						pos.z = z + SIZE * Megachunk.SIZE * parent.getZ();
						TileData data = getTileData(x, y, z, false);
						if (data != null) {
							if (data.getTile() == Tiles.AIR.getID())
								continue;
							Tile tile = Tiles.getTile(data.getTile());
							if (tile.getTickPercent() > 0)
							if (parent.getWorld().getRandom().nextDouble() * 100 <= tile.getTickPercent()) {
								tile.tick(parent.getWorld(), pos, parent.getWorld().getRandom());
							}
							if (data.getMiningTime() > 0) {
								if (parent.getWorld().getRandom().nextInt(100) <= 75) {
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
				MeshRenderer.renderMesh(mesh, new Vector3f(parent.getX() * Megachunk.SIZE * SIZE, parent.getY() * SIZE_Y, parent.getZ() * Megachunk.SIZE * SIZE), shader);
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
				parent.getWorld().getChunkGenerator().generateChunk(this, false);
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

	public void setParent(Megachunk parent) {
		this.parent = parent;
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
