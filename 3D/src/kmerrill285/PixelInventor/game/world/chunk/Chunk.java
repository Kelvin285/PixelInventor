package kmerrill285.PixelInventor.game.world.chunk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Scanner;

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
	
	private boolean needsToSave = false;
	
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
				getParent().getWorld().getChunkGenerator().generateChunk(this);
			}
			if (tiles == null) {
				tiles = new TileData[NUM_TILES];
			}
			if (tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] == null) return Tiles.AIR;
			return Tiles.getTile(tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y].getTile());
		}
		return Tiles.AIR;
	}
	
	
	public TileData getTileData(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			if (tiles == null) {
				getParent().getWorld().getChunkGenerator().generateChunk(this);
			}
			if (tiles == null) {
				tiles = new TileData[NUM_TILES];
			}
			if (tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] == null) return new TileData(Tiles.AIR.getID());
			return tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y];
		}
		return new TileData(Tiles.AIR.getID());
	}
	
	
	public void setLocalTile(int x, int y, int z, Tile tile) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			if (tiles == null) {
				getParent().getWorld().getChunkGenerator().generateChunk(this);
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
			FileWriter fos = new FileWriter(savefile);
			
			String str = "";
			for (int i = 0; i < tiles.length; i++) {
				if (tiles[i] == null) tiles[i] = new TileData(Tiles.AIR.getID());
				str += tiles[i].getTile() +"," + tiles[i].waterLevel + "," + tiles[i].miningTime + "\n";
			}
			fos.write(str);
			fos.close();
			tiles = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public boolean load() {
		if (savefile == null) {
			String DIR = "PixelInventor/saves/"+PixelInventor.game.world.getWorldSaver().getWorldName()+"/megachunk"+parent.getX()+"_"+parent.getY()+"_"+parent.getZ()+"/";
			setSavefile(new File(DIR+"chunk"+getX()+","+getZ()+".chnk"));
		}
		if (!savefile.exists()) return false;
		if (tiles == null) {
			tiles = new TileData[NUM_TILES];
		}
		if (savefile != null) {
			try {
				Scanner scanner = new Scanner(savefile);
				int i = 0;
				while (scanner.hasNext()) {
					String str = scanner.nextLine().trim();
					if (!str.isEmpty()) {
						String[] data = str.split(",");
						if (data != null && data.length >= 1)
						tiles[i] = new TileData(Integer.parseInt(data[0]));
						if (data != null && data.length >= 2)
						tiles[i].waterLevel = Integer.parseInt(data[1]);
						if (data != null && data.length >= 3)
						tiles[i].miningTime = Float.parseFloat(data[2]);
						if (data != null && data.length >= 1)
						if (tiles[i].getTile() != Tiles.AIR.getID()) voxels++;
						i++;
					}
				}
				
				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		return true;
	}
	
	public boolean isActive() {
		if (voxels <= 0) return false;
		int x = getX() * SIZE + parent.getX() * Megachunk.SIZE * SIZE + SIZE / 2;
		int z = getZ() * SIZE + parent.getZ() * Megachunk.SIZE * SIZE + SIZE / 2;
		return Camera.position.distance(x, 0, z) <= SIZE * Megachunk.SIZE;
	}
	
	public boolean canRender() {
		int x = getX() * SIZE + parent.getX() * Megachunk.SIZE * SIZE + SIZE / 2;
		int z = getZ() * SIZE + parent.getZ() * Megachunk.SIZE * SIZE + SIZE / 2;
		return Camera.position.distance(x, 0, z) <= SIZE * Settings.VIEW_DISTANCE;
	}

	public void tick() {
		if (this.isActive()) {
			TilePos pos = new TilePos(0, 0, 0);
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE_Y; y++) {
					for (int z = 0; z < SIZE; z++) {
						pos.x = x + SIZE * Megachunk.SIZE * parent.getX();
						pos.y = y + SIZE_Y * parent.getY();
						pos.z = z + SIZE * Megachunk.SIZE * parent.getZ();
						TileData data = getTileData(x, y, z);
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
		if (needsToRebuild) {
			if (this.mesh != null) this.mesh.dispose();
			needsToRebuild = false;
			mesh = MegachunkBuilder.buildChunk(this);
		}
		if (meshesToRemove.size() > 0) {
			meshesToRemove.pop().dispose();
		}
		if (mesh != null) {
			if (canRender()) {
				MeshRenderer.renderMesh(mesh, new Vector3f(parent.getX() * Megachunk.SIZE * SIZE, parent.getY() * SIZE_Y, parent.getZ() * Megachunk.SIZE * SIZE), shader);
			}
		}
		this.testForActivation();
	}
	
	public void testForActivation() {
		if (isActive()) {
			if (tiles == null) {
				parent.getWorld().getChunkGenerator().generateChunk(this);
			}
		} else {
			if (mesh != null) {
				if (!needsToSave)
					tiles = null;
				else
					if (!World.saveQueue.contains(this))
					World.saveQueue.add(this);
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
		this.needsToRebuild = true;
	}
	
	public void markForSave() {
		this.needsToSave = true;
	}
	
	private ArrayDeque<Mesh> meshesToRemove = new ArrayDeque<Mesh>();
	public void removeMesh(final Mesh mesh) {
		meshesToRemove.add(mesh);
	}

	public void setSavefile(File savefile) {
		this.savefile = savefile;
	}

	
}
