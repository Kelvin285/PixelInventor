package kmerrill285.Inignoto.game.world.chunk.generator.feature.trees;

import java.awt.Color;
import java.util.Random;

import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.game.world.World;
import kmerrill285.Inignoto.game.world.chunk.Chunk;
import kmerrill285.Inignoto.game.world.chunk.TilePos;
import kmerrill285.Inignoto.game.world.chunk.generator.feature.Structure;
import kmerrill285.Inignoto.game.world.chunk.generator.feature.LSystem.LSystem;
import kmerrill285.Inignoto.game.world.chunk.generator.feature.LSystem.LSystemPos;

public class SimpleTree extends Structure {
	private static final long serialVersionUID = -6518038958304776167L;
	
	Random random = new Random();
	public SimpleTree() {
		super(1, 1, 1);
		this.getLocalTile(0, 0, 0).setTile(Tiles.AIR.getID());
	}
	
	public boolean addToChunk(Chunk chunk, int x, int y, int z, int X, int Y, int Z) {
		random.setSeed(X * Y * Z);
		TilePos pos = new TilePos(X, Y, Z);
		if (random.nextInt(500) == 0) {
	         if (chunk.getTiles() != null)
	         {
	        	 if (chunk.getLocalTile(x, y + 1, z) != Tiles.AIR) {
	        		 return false;
	        	 }
	        	 World world = chunk.getWorld();
	        	 int height = new Random().nextInt(2) + 4;
	        	 
	        	 for (int xx = -2; xx < 3; xx++) {
	        		 for (int yy = 0; yy < 4; yy++) {
	        			 for (int zz = -2; zz < 3; zz++) {
							 pos.setPosition(X + xx, Y + height + yy, Z + zz);
	        				 if (yy < 2) {
	        					 boolean place = true;
        						 if (yy == 1) {
        							 if (Math.abs(xx) == Math.abs(zz)) {
        								 place = random.nextBoolean();
        							 }
        						 }
        						 if (place) {
        							 if (chunk.isWithinChunk(x + xx, y + yy + height, z + zz)) {
        								 chunk.setLocalTile(x + xx, y + yy + height, z + zz, Tiles.LEAVES);
        							 } else {
        								 world.setMetaTile(pos, Tiles.LEAVES, false);
        							 }
		        	        		 
        						 }
	        				 } else {
	        					 if (xx >= -1 && xx <= 1 && zz >= -1 && zz <= 1) {
	        						 boolean place = true;
	        						 if (yy == 3) {
	        							 if (Math.abs(xx) == Math.abs(zz)) {
	        								 place = random.nextBoolean();
	        							 }
	        						 }
	        						 if (place) {
	        							 if (chunk.isWithinChunk(x + xx, y + yy + height, z + zz)) {
	        								 chunk.setLocalTile(x + xx, y + yy + height, z + zz, Tiles.LEAVES);
	        							 } else {
	        								 world.setMetaTile(pos, Tiles.LEAVES, false);
	        							 }
	        						 }
	        						 
	        					 }
	        				 }
	        			 }
	        		 }
	        	 }
	        	 
	        	 for (int i = 0; i < height; i++) {
	        		 if (chunk.isWithinChunk(x, y + i, z)) {
						 chunk.setLocalTile(x, y + i, z, Tiles.LOG);
					 } else {
						 pos.setPosition(X, Y + i, Z);
		        		 world.setMetaTile(pos, Tiles.LOG, false);
					 }
	        		 
	        	 }
	         }
		}
		return true;
	}
}
