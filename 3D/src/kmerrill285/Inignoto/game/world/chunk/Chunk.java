package kmerrill285.Inignoto.game.world.chunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.joml.Vector3f;
import org.joml.Vector3i;

import kmerrill285.Inignoto.Inignoto;
import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.client.rendering.MeshRenderer;
import kmerrill285.Inignoto.game.client.rendering.chunk.ChunkBuilder;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.shadows.ShadowRenderer;
import kmerrill285.Inignoto.game.foliage.Foliage;
import kmerrill285.Inignoto.game.settings.Settings;
import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.tile.Tile.TileRayTraceType;
import kmerrill285.Inignoto.game.tile.data.TileState;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.resources.Constants;
import kmerrill285.Inignoto.resources.FPSCounter;

public class Chunk {
	

	public static final int SIZE = 16;
	public static final int SIZE_Y = 16;
		
	private static final int NUM_TILES = Chunk.SIZE * Chunk.SIZE * Chunk.SIZE_Y;
		
	private World world;
	
	private int x, y, z;
	private Vector3i pos;
	
	public int voxels = 0;
	
	private TileState[] tiles;
	
	public Mesh mesh;
	public Mesh waterMesh;
	
	public Mesh setMesh;
	public Mesh setWaterMesh;
	
	public boolean needsToSave = false;
	
	public float loadValue = 1.0f;
	
	public boolean generated = false;
	public boolean isGenerating = false;
	
	public HashMap<String, Foliage> foliage = new HashMap<String, Foliage>();
	public HashMap<String, Float> mining_time = new HashMap<String, Float>();
	public HashMap<String, Float> last_mining_time = new HashMap<String, Float>();

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

	public TileState[] getTiles() {
		return tiles;
	}

	public void setTiles(TileState[] tiles) {
		this.tiles = tiles;
	}
	
	public boolean isWithinChunk(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			return true;
		}
		return false;
	}
	
	public Tile getLocalTile(int x, int y, int z) {
		return getLocalTile(x, y, z, true);
	}
	
	public Tile getLocalTile(int x, int y, int z, boolean cascade) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			if (tiles == null) {
				return Tiles.AIR;
			}
			if (tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] == null) return Tiles.AIR;
			return Tiles.getTile(tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y].getTile());
		}
		if (!cascade) return Tiles.AIR;
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) return Tiles.AIR;
			if (world.getChunk(X, Y, Z).generated) {
				return world.getChunk(X, Y, Z).getLocalTile(x, y, z);
			}
		}
		return Tiles.AIR;
	}
	
	public TileState getTileState(int x, int y, int z) {
		return getTileState(x, y, z, false);
	}
	
	public TileState getTileState(int x, int y, int z, boolean modifying) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			if (tiles == null) {
				return Tiles.AIR.getDefaultState();
			}
			if (tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] == null) return Tiles.AIR.getDefaultState();
			return tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y];
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) Tiles.AIR.getDefaultState();
			if (world.getChunk(X, Y, Z).generated) {
				return world.getChunk(X, Y, Z).getTileState(x, y, z, modifying);
			}
		}
		return Tiles.AIR.getDefaultState();
	}
	
	public void setTileState(int x, int y, int z, TileState data) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			
			if (tiles == null) {
				return;
			}
			tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] = data;
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) return;
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).setTileState(x, y, z, data);
			}
		}
	}
	
	public void setFoliage(int x, int y, int z, Foliage foliage) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			if (foliage == null) {
				this.foliage.remove(x+","+y+","+z);
			} else {
				this.foliage.put(x+","+y+","+z, foliage);
			}
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) return;
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).setFoliage(x, y, z, foliage);
			}
		}
	}
	
	public Foliage getFoliage(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			return this.foliage.get(x+","+y+","+z);
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) return null;
			if (world.getChunk(X, Y, Z).generated) {
				return world.getChunk(X, Y, Z).getFoliage(x, y, z);
			}
		}
		return null;
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
				tiles = new TileState[Chunk.SIZE * Chunk.SIZE_Y * Chunk.SIZE];
			}
			if (tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] == null) {
				tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] = tile.getDefaultState();
			} else {
				tiles[x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE_Y] = tile.getDefaultState();
			}
			
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) return;
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).setLocalTile(x, y, z, tile);
			}
		}
	}
	
	public float getMiningTime(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			Float mining_time = this.mining_time.get(x+","+y+","+z);
			return mining_time != null ? mining_time : 0;
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) return 0;
			if (world.getChunk(X, Y, Z).generated) {
				return world.getChunk(X, Y, Z).getMiningTime(x, y, z);
			}
		}
		return 0;
	}
	
	public float getLastMiningTime(int x, int y, int z) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			Float mining_time = this.last_mining_time.get(x+","+y+","+z);
			return mining_time != null ? mining_time : 0;
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) return 0;
			if (world.getChunk(X, Y, Z).generated) {
				return world.getChunk(X, Y, Z).getLastMiningTime(x, y, z);
			}
		}
		return 0;
	}
	
	public void setLastMiningTime(int x, int y, int z, float mining_time) {
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			this.last_mining_time.put(x+","+y+","+z, mining_time);
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) return;
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).setLastMiningTime(x, y, z, mining_time);
			}
		}
	}
	
	public void setMiningTime(int x, int y, int z, float mining_time) {
		setLastMiningTime(x, y, z, mining_time);
		if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.SIZE && y < Chunk.SIZE_Y && z < Chunk.SIZE) {
			this.mining_time.put(x+","+y+","+z, mining_time);
		}
		int X = getX();
		int Y = getY();
		int Z = getZ();
		while (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			X++;
		}
		while (x < 0) {
			x += Chunk.SIZE;
			X--;
		}
		while (y >= Chunk.SIZE_Y) {
			y -= Chunk.SIZE_Y;
			Y++;
		}
		while (y < 0) {
			y += Chunk.SIZE_Y;
			Y--;
		}
		while (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			Z++;
		}
		while (z < 0) {
			z += Chunk.SIZE;
			Z--;
		}
		if (world.getChunk(X, Y, Z) != null) {
			if (X == getX() && Y == getY() && Z == getZ()) return;
			if (world.getChunk(X, Y, Z).generated) {
				world.getChunk(X, Y, Z).setMiningTime(x, y, z, mining_time);
			}
		}
	}

	public boolean isLocalTileNotSame(int x, int y, int z, Tile tile) {
		Tile local = getLocalTile(x, y, z);
		return local.getID() != tile.getID();
	}
	
	public boolean isLocalTileAir(int x, int y, int z) {
		Tile local = getLocalTile(x, y, z);
		return local == Tiles.AIR;
	}
	
	public boolean isLocalTileNotFull(int x, int y, int z) {
		TileState local = this.getTileState(x, y, z, false);
		return !local.isFullCube() || !local.isVisible() || local.getRayTraceType() != TileRayTraceType.SOLID;
	}

	public World getWorld() {
		return world;
	}
	private File savefile;
	
	public void save() {
		if (!this.needsToSave) return;
		if (tiles == null) return;
		saving = true;
		String DIR = "Inignoto/saves/"+Inignoto.game.world.getWorldSaver().getWorldName()+"/";
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
			for (int i = 0; i < tiles.length; i++) {
				if (tiles[i] == null) {
					tiles[i] = Tiles.AIR.getDefaultState();
				}
				fos.write(tiles[i].getTile());
				fos.write(tiles[i].getState());
			}
			fos.close();
			this.needsToSave = false;
		} catch (Exception e) {
			e.printStackTrace();
			this.savefile.delete();
			System.out.println("Failed to save chunk!");
		}
		saving = false;
		
	}
	
	public boolean saving = false;
	
	public boolean load() {
		if (saving) {
			return true;
		}
		if (savefile == null) {
			String DIR = "Inignoto/saves/"+Inignoto.game.world.getWorldSaver().getWorldName()+"/";
			setSavefile(new File(DIR+"chunk"+getX()+","+getY()+","+getZ()+".chnk"));
		}
		if (!savefile.exists()) {return false;}
		
		boolean setTile = false;
		if (savefile != null) {
			try {
				FileInputStream fis = new FileInputStream(savefile);
				this.generated = false;
				tiles = new TileState[SIZE * SIZE_Y * SIZE];
				
				
				for (int i = 0; i < tiles.length; i++) {
					tiles[i] = Tiles.getTile(fis.read()).getStateHolder().getStateFor(fis.read());
				}
				fis.close();
				this.generated = true;
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return setTile;
	}
	
	public boolean canTick() {
		int x = getX() * SIZE + SIZE / 2;
		int z = getZ() * SIZE + SIZE / 2;
		return Camera.position.distance(x, 0, z) <= SIZE * Constants.ACTIVE_CHUNK_DISTANCE;
	}
	
	public boolean isActive() {
		return isInActiveRange();
	}
	
	public boolean isInActiveRange() {
		int x = getX() * SIZE + SIZE / 2;
		int y = getY() * SIZE_Y + SIZE_Y / 2;
		int z = getZ() * SIZE + SIZE / 2;
		return Camera.position.distance(x, y, z) <= SIZE * SIZE * 1.5f;
	}
	
	public boolean isWithinViewingRange() {
		int x = getX() * SIZE + SIZE / 2;
		int z = getZ() * SIZE + SIZE / 2;
		return Camera.position.distance(x, 0, z) <= SIZE * Settings.VIEW_DISTANCE;
	}
	
	public boolean canRender() {
		int x = getX() * SIZE + SIZE / 2;
		int y = getY() * SIZE_Y + SIZE_Y / 2;
		int z = getZ() * SIZE + SIZE / 2;
		if (Camera.frustum.intersects(pos.x, pos.y, SIZE, SIZE) || Camera.downFrustum.intersects(pos.x, pos.y, SIZE, SIZE)) return true;
		return Camera.position.distance(x, 0, z) <= SIZE * Settings.VIEW_DISTANCE || new Vector3f(Camera.position).mul(0, 1, 0).distance(0, y, 0) <= SIZE_Y * Settings.VERTICAL_VIEW_DISTANCE * 1.5;
	}

	public void tick() {
		if (this.canTick()) {
			TilePos pos = new TilePos(0, 0, 0);
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE_Y; y++) {
					for (int z = 0; z < SIZE; z++) {
						
//						if (getFoliage(x, y, z) != null) { 
//							getFoliage(x, y, z).tick(x, y, z, this);
//						}
						
						pos.x = x + getX() * SIZE;
						pos.y = y + getY() * SIZE_Y;
						pos.z = z + getZ() * SIZE;
						TileState data = getTileState(x, y, z, false);
						if (data != null) {
							if (data.getTile() == Tiles.AIR.getID())
								continue;
							Tile tile = Tiles.getTile(data.getTile());
							
							if (data.getRayTraceType() == TileRayTraceType.LIQUID) {
								tile.tick(getWorld(), pos, getWorld().getRandom(), data);
							}
							
							if (tile.getTickPercent() > 0)
							if (getWorld().getRandom().nextDouble() * 100 <= tile.getTickPercent()) {
								tile.tick(getWorld(), pos, getWorld().getRandom(), data);
							}
							if (getMiningTime(x, y, z) > 0) {
								if (getWorld().getRandom().nextInt(100) <= 75) {
									setMiningTime(x, y, z, getMiningTime(x, y, z) - 0.1f);
									this.markForRerender();
								}
								if (getMiningTime(x, y, z) < 0) {
									this.mining_time.remove(x+","+y+","+z);
									this.last_mining_time.remove(x+","+y+","+z);
									if ((int)getMiningTime(x, y, z) / 20 != (int)getLastMiningTime(x, y, z) / 20) {
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
		if (setMesh != null) {
			mesh = setMesh;
			setMesh = null;
		}
		A:
		if (mesh != null) {
			if (canRender()) {
				if (this.generated == false) break A;
				
				if (loadValue > 0) {
					loadValue -= 0.02f * FPSCounter.getDelta();
				} else {
					loadValue = 0;
				}
				
				shader.setUniformFloat("loadValue", loadValue);
				MeshRenderer.renderMesh(mesh, new Vector3f(getX() * SIZE, getY() * SIZE_Y, getZ() * SIZE), shader);
				
				
				for (String str : this.foliage.keySet()) {
					String[] data = str.split(",");
					int x = Integer.parseInt(data[0]);
					int y = Integer.parseInt(data[1]);
					int z = Integer.parseInt(data[2]);
					this.foliage.get(str).render(x + getX() * SIZE, y + getY() * SIZE_Y, z + getZ() * SIZE, this, shader);
				}
				
				shader.setUniformFloat("loadValue", 0);
			}
		} else {
			this.markForRerender();
		}
		this.testForActivation();
	}
	
	public void renderWater(ShaderProgram shader) {
		if (setWaterMesh != null) {
			waterMesh = setWaterMesh;
			setWaterMesh = null;
		}
		
		if (waterMesh != null) {
			if (canRender()) {
				shader.setUniformFloat("loadValue", loadValue);
				MeshRenderer.renderMesh(waterMesh, new Vector3f(getX() * SIZE, getY() * SIZE_Y, getZ() * SIZE), shader);
				shader.setUniformFloat("loadValue", 0);
			}
		}
	}
	

	public void renderShadow(ShaderProgram shader, ShadowRenderer renderer) {
		if (mesh != null) {
			shader.setUniformFloat("loadValue", loadValue);
			MeshRenderer.renderMesh(mesh, new Vector3f(getX() * SIZE, getY() * SIZE_Y, getZ() * SIZE), shader, renderer);
			shader.setUniformFloat("loadValue", 0);
		}
	}
	
	public void testForActivation() {
		
		if (needsToRebuild) {
			if (this.mesh != null) this.mesh.dispose();
			if (this.waterMesh != null) this.waterMesh.dispose();
			needsToRebuild = false;
			mesh = ChunkBuilder.buildChunk(this, true);
			waterMesh = ChunkBuilder.buildLiquidChunk(this);
		}
		
		if (isActive()) {
			if (tiles == null) {				
				this.generated = false;
				this.world.markChunkForBuilding(this);
			}
		} 
		else {
			if (mesh != null) {
				if (!needsToSave && generated) {
					tiles = null;
					
				}
			}
		}
	}
	
	public void dispose() {
		if (mesh != null) mesh.dispose();
		if (waterMesh != null) waterMesh.dispose();
		if (needsToSave) this.save();
		tiles = null;
		this.mesh = null;
		this.waterMesh = null;
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
