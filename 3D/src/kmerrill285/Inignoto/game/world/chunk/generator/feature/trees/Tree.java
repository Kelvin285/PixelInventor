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

public class Tree extends Structure {
	private static final long serialVersionUID = -6518038958304776167L;
	
	Random random = new Random();
	public Tree() {
		super(1, 1, 1);
		this.getLocalTile(0, 0, 0).setTile(Tiles.AIR.getID());
	}
	
	public boolean addToChunk(Chunk chunk, int x, int y, int z, int X, int Y, int Z) {
		random.setSeed(X * Y * Z);
		TilePos pos = new TilePos(X, Y, Z);
		if (random.nextInt(5000) == 0) {
	         if (chunk.getTiles() != null)
	        	 tree(pos, chunk.getWorld(), random);
		}
		return true;
	}
		   
	

	
   public void tree(TilePos pos, World worldIn, Random r) {
	   
	    LSystemPos lpos = new LSystemPos(32, -3, 32);
		String[] tree = {"place","place","place<75","rotrand<50", "splitrand<75","place<75"};

		LSystem stem = new LSystem(lpos, 10, 4, 7.0, 2f,tree);
		Random rand = new Random(r.nextLong());
	 	
	 	stem.run(rand, 10);
	 	
	 	boolean swap = rand.nextBoolean();
	 	TilePos pos2 = new TilePos(0, 0, 0);
	 	
	 	for (String str : stem.stuff.keySet()) {
	 		String[] data = str.split(",");
	 		int x = Integer.parseInt(data[0]);
	 		int y = Integer.parseInt(data[1]);
	 		int z = Integer.parseInt(data[2]);
	 		Color color = stem.stuff.get(str);
	 		if (color == Color.BLACK) {
 				int X = x;
 				int Z = z;
 				if (swap)
 				{
 					X = z;
 					Z = x;
 				}
 				
 				pos2.setPosition(pos.x + X - 32, pos.y + y - 2, pos.z + Z - 32);
 				worldIn.setTile(pos2, Tiles.LOG, false);
 			}
 			if (color == Color.GREEN) {
 				int X = x;
 				int Z = z;
 				if (swap)
 				{
 					X = z;
 					Z = x;
 				}
 				pos2.setPosition(pos.x + X - 32, pos.y + y - 2, pos.z + Z - 32);
 				worldIn.setTile(pos2, Tiles.LEAVES, false);
 			}
	 	}
	}
}
