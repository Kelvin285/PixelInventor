package kmerrill285.Inignoto.game.world.chunk.generator;

import java.util.Random;

import imported.FastNoise;
import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.tile.Tile.TileRayTraceType;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.game.world.chunk.Chunk;
import kmerrill285.Inignoto.game.world.chunk.MetaChunk;
import kmerrill285.Inignoto.game.world.chunk.TileData;
import kmerrill285.Inignoto.game.world.chunk.generator.feature.Structure;
import kmerrill285.Inignoto.resources.MathHelper;

public class ChunkGenerator {
	
	protected FastNoise noise;
	protected Random random;
	protected World world;
	
	public ChunkGenerator(World world, long seed) {
		noise = new FastNoise((int)seed);
		random = new Random(seed);
		this.world = world;
	}

	public void applyMeta(Chunk chunk, MetaChunk metachunk) {
		for (String str : metachunk.tiles.keySet()) {
			String[] data = str.split(",");
			int x = Integer.parseInt(data[0]);
			int y = Integer.parseInt(data[1]);
			int z = Integer.parseInt(data[2]);
			chunk.setTileData(x, y, z, new TileData(metachunk.getTileData(x, y, z).getTile()));
		}
		chunk.needsToSave = true;
		chunk.save();
		chunk.getWorld().removeMetaChunk(metachunk.x, metachunk.y, metachunk.z);
	}
	
	public void generateChunk(Chunk chunk, MetaChunk metachunk, boolean structures) {
		if (chunk.load() == true) return;
		if (chunk.getTiles() == null) {
			chunk.setTiles(new TileData[Chunk.SIZE * Chunk.SIZE_Y * Chunk.SIZE]);
		}
		chunk.isGenerating = true;

		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int y = 0; y < Chunk.SIZE_Y; y++) {
				for (int z = 0; z < Chunk.SIZE; z++) {
					chunk.setLocalTile(x, y, z, Tiles.AIR);
				}
			}
		}
		int nx = chunk.getX() * Chunk.SIZE;
		int ny = chunk.getY() * Chunk.SIZE_Y;
		int nz = chunk.getZ() * Chunk.SIZE;
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int z = 0; z < Chunk.SIZE; z++) {
				int X = nx + x;
				int Z = nz + z;
				float baseheight = (int)getBaseHeight(X, Z);
				float RIVERS = getRivers(X, Z);
				int rivers = (int)RIVERS;
				
				float terrace = noise.GetSimplex(X * 0.1f, Z * 0.1f) + 0.5f;

				if (rivers > 0) {
					baseheight += (getHillHeight(X, Z) * RIVERS);

					
					if (baseheight < 0) {
						baseheight = MathHelper.lerp(baseheight, 0, 0.7f);
					}
					if (terrace > 0) {
						if (baseheight >= 20 && baseheight <= 30) {
							baseheight = MathHelper.lerp(baseheight, 25, 0.7f);
						}
						if (terrace > 0.2)
						if (baseheight >= 60 && baseheight <= 80) {
							baseheight = MathHelper.lerp(baseheight, 70, 0.85f);
						}
					}
					if (terrace < 0 && terrace > -0.1) {
						if (baseheight >= 0 && baseheight <= 20) {
							baseheight = MathHelper.lerp(baseheight, 10, 0.7f);
						}
					}
				}
				
				int height = (int)baseheight;
								
				for (int y = 0; y < Chunk.SIZE_Y; y++) {
					int Y = ny + y;
					
					if (Y < height){
						chunk.setLocalTile(x, y, z, Tiles.DIRT);
					}
					
					if (Y >= height + rivers && Y <= height - 1 && rivers < 0) {
						chunk.setLocalTile(x, y, z, Tiles.WATER);
						if (Y == height - 1) {
							chunk.setLocalTile(x, y, z, Tiles.AIR);
						}
					}
					
				}
				
			}
		}
		
		populateChunk(chunk, metachunk, true);
		
		chunk.isGenerating = false;
	}

	public void populateChunk(Chunk chunk, MetaChunk metachunk, boolean structures) {
		
		int nx = chunk.getX() * Chunk.SIZE;
		int ny = chunk.getY() * Chunk.SIZE_Y;
		int nz = chunk.getZ() * Chunk.SIZE;
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int z = 0; z < Chunk.SIZE; z++) {
				int X = nx + x;
				int Z = nz + z;
				float baseheight = (int)getBaseHeight(X, Z);
				float RIVERS = getRivers(X, Z);
				int rivers = (int)RIVERS;
				
				float terrace = noise.GetSimplex(X * 0.1f, Z * 0.1f) + 0.5f;

				if (rivers > 0) {
					baseheight += (getHillHeight(X, Z) * RIVERS);

					
					if (baseheight < 0) {
						baseheight = MathHelper.lerp(baseheight, 0, 0.7f);
					}
					if (terrace > 0) {
						if (baseheight >= 20 && baseheight <= 30) {
							baseheight = MathHelper.lerp(baseheight, 25, 0.7f);
						}
						if (terrace > 0.2)
						if (baseheight >= 60 && baseheight <= 80) {
							baseheight = MathHelper.lerp(baseheight, 70, 0.85f);
						}
					}
					if (terrace < 0 && terrace > -0.1) {
						if (baseheight >= 0 && baseheight <= 20) {
							baseheight = MathHelper.lerp(baseheight, 10, 0.7f);
						}
					}
				}
				
				int height = (int)baseheight;
				Tile topTile = getTopTile(X, Z);
				
				
				if (rivers < 0) {
					topTile = Tiles.SAND;
					double stone = noise.GetSimplex(x * 15, z * 15);
					stone += 0.5f;
					if (stone <= 0.1f) {
						topTile = Tiles.STONE;
					}
				}
								
				for (int y = 0; y < Chunk.SIZE_Y; y++) {
					int Y = ny + y;
					
					int stone_layer = (int)(30 * (10 * noise.GetSimplex((Y + terrace * 5) * 0.5f, 0)));
					

					
					
					if (Y < height){
						if (Y < height - 2){
							if (chunk.getLocalTile(x, y, z).getRayTraceType() == TileRayTraceType.SOLID) {
								if (rivers < 0 && Y >= height + rivers - 3) {
									chunk.setLocalTile(x, y, z, Tiles.STONE);
								} else {
									chunk.setLocalTile(x, y, z, Tiles.SMOOTH_STONE);
								}
								if (stone_layer % 4 == 0 || stone_layer % 7 == 0) {
									chunk.setLocalTile(x, y, z, Tiles.STONE);
								}
							}
							
						}
					}
					
					
					
					if (rivers >= 0) {
						if (Y == height) {
							chunk.setLocalTile(x, y, z, topTile);
							if (topTile == Tiles.SAND) {
								Structure.RIVER_ROCK.addToChunk(chunk, x, y, z, X, Y, Z);
							}
							if (topTile == Tiles.GRASS) {
								Structure.BIG_TREE.addToChunk(chunk, x, y, z, X, Y, Z);
							}
						} 
					}
					
					if (rivers == -1 || rivers == 1) {
						if (RIVERS < -1.5f) {
							if (Y == height + rivers - 1) {
								chunk.setLocalTile(x, y, z, topTile);
							}
						} else {
							if (Y == height + rivers) {
								chunk.setLocalTile(x, y, z, topTile);
							}
						}
					}
										
					if (Y == height + rivers && rivers < 0) {
						Structure.RIVER_ROCK.addToChunk(chunk, x, y, z, X, Y, Z);
					}
					
				}
				
			}
		}
		
	}
	
	
	public float getHillHeight(float x, float z) {
		x += noise.GetSimplexFractal(x * 0.5f, z) * 50;
		z += noise.GetSimplexFractal(x, z * 0.5f) * 50;
		
		float base = noise.GetSimplex(x * 0.4f, z * 0.4f) * 10;
		
		if (base < -5) base += (-5 - base);
		
		base += noise.GetPerlin(x * 0.7f, z * 0.7f);

		
		return base;
	}
	
	public float getRivers(float x, float z) {
		x += noise.GetSimplexFractal(x * 0.5f, z) * 50;
		z += noise.GetSimplexFractal(x, z * 0.5f) * 50;
		float river = Math.abs(noise.GetSimplex(x * 0.1f, z * 0.1f) + 0.5f);
		river -= 0.5f;
		
		return (Math.abs(river) - 0.08f) * 50;
	}
	public float getBaseHeight(float x, float z) {
		x += noise.GetSimplex(x * 0.1f, z) * 50;
		z += noise.GetSimplex(z, z * 0.1f) * 50;
		
		x += noise.GetSimplexFractal(x * 0.5f, z) * 50;
		z += noise.GetSimplexFractal(x, z * 0.5f) * 50;
		
		float base = noise.GetSimplex(x * 0.1f, z * 0.1f) * 10;
		
		base += noise.GetPerlin(x * 0.5f, z * 0.5f) * 2;
				
		return base;
	}
	
	public Tile getTopTile(float x, float z) {
//		double purple = noise.GetSimplex(x, z);
		Tile topTile = Tiles.GRASS;
//		if (purple > 0) {
//			topTile = Tiles.PURPLE_GRASS;
//		}
		return topTile;
	}

}
