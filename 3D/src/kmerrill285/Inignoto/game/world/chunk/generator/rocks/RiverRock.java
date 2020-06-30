package kmerrill285.Inignoto.game.world.chunk.generator.rocks;

import java.util.Random;

import org.joml.Vector3f;

import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.chunk.Chunk;
import kmerrill285.Inignoto.game.world.chunk.TilePos;
import kmerrill285.Inignoto.game.world.chunk.generator.feature.Structure;

public class RiverRock extends Structure {
	private static final long serialVersionUID = -6518038958304776167L;
	
	Random random = new Random();
	public RiverRock() {
		super(1, 1, 1);
		this.getLocalTile(0, 0, 0).setTile(Tiles.AIR.getID());
	}
	
	public boolean addToChunk(Chunk chunk, int x, int y, int z, int X, int Y, int Z) {
		random.setSeed(X * Y * Z);
		TilePos pos = new TilePos(X, Y, Z);
				
		boolean alt = random.nextInt(10) <= 3;
		if (random.nextInt(200) == 0) {
	         int size = random.nextInt(3);
	         Vector3f vec = new Vector3f(0, 0, 0);
	         for (int h = -size; h < size; h++) {
	        	 for (int i = -size; i < size * 2; i++) {
	        		 for (int j = -size; j < size; j++) {
		        		 vec.set(h, i, j);
		        		 if (vec.distance(0, 0, 0) <= size) {
		        			 pos.setPosition(X + h, Y + i, Z + j);
		        			 if (chunk.isWithinChunk(x + h, y + i, z + j)) {
		        				 if (!alt) {
		        					 chunk.setLocalTile(x + h, y + i, z + j, Tiles.STONE);
		        				 } else {
		        					 chunk.setLocalTile(x + h, y + i, z + j, Tiles.SMOOTH_STONE);
		        				 }
		        			 }
		        		 }
		        	 }
	        	 }
	         }
		}
		return true;
	}
		   
}
