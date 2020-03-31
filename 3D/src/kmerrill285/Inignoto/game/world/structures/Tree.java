package kmerrill285.Inignoto.game.world.structures;

import java.util.Random;

import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.chunk.Chunk;

public class Tree extends Structure {
	private static final long serialVersionUID = -6518038958304776167L;
	
	Random random = new Random();
	public Tree() {
		super(1, 1, 1);
		this.getLocalTile(0, 0, 0).setTile(Tiles.AIR.getID());
	}
	
	public boolean addToChunk(Chunk chunk, int x, int y, int z, int X, int Y, int Z) {
		random.setSeed(X * Y * Z);
		if (random.nextInt(10000) <= 2) {
	
			int height = random.nextInt(10) + 5;
			for (int i = 0; i < height; i++) 
			{
				chunk.setLocalTile(x, y + i, z, Tiles.LOG);
			}
			for (int xx = -1; xx < 2; xx++) {
				for (int yy = -1; yy < 2; yy++) {
					for (int zz = -1; zz < 2; zz++) {
						chunk.setLocalTile(x + xx, y + height + yy, z + zz, Tiles.LEAVES);
					}
				}
			}
		}
		return true;
	}
}
