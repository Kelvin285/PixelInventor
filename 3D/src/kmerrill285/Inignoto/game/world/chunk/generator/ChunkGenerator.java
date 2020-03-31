package kmerrill285.Inignoto.game.world.chunk.generator;

import java.util.Random;

import imported.FastNoise;
import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.game.world.chunk.Chunk;
import kmerrill285.Inignoto.game.world.chunk.TileData;
import kmerrill285.Inignoto.game.world.structures.Structure;

public class ChunkGenerator {
	
	protected FastNoise noise;
	protected Random random;
	protected World world;
	
	public ChunkGenerator(World world, long seed) {
		noise = new FastNoise((int)seed);
		random = new Random(seed);
		this.world = world;
	}

	public void generateChunk(Chunk chunk, boolean structures) {
		if (chunk.load() == true) return;
		if (chunk.getTiles() == null) {
			chunk.setTiles(new TileData[Chunk.SIZE * Chunk.SIZE_Y * Chunk.SIZE]);
		}
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
				int height = (int)getHeight(X, Z);
				Tile topTile = getTopTile(X, Z);
				for (int y = 0; y < Chunk.SIZE_Y; y++) {
					int Y = ny + y;
					
					if (Y == height) {
						chunk.setLocalTile(x, y, z, topTile);
					} 
					if (Y < height){
						chunk.setLocalTile(x, y, z, Tiles.DIRT);
						Structure.TREE.addToChunk(chunk, x, y, z, X, Y, Z);
					}
					
				}
			}
		}
	}
	
	public float getHeight(float x, float z) {
		float height = noise.GetSimplexFractal(x * 2, 0, z * 2) * 24;
		float mountain = noise.GetSimplex(x / 500.0f, 0.0f, z / 500.0f) * 512;
		
		return height + 64 + mountain;
	}
	
	public Tile getTopTile(float x, float z) {
		double purple = noise.GetSimplex(x, 0, z);
		Tile topTile = Tiles.GRASS;
		if (purple > 0) {
			topTile = Tiles.PURPLE_GRASS;
		}
		return topTile;
	}

}
