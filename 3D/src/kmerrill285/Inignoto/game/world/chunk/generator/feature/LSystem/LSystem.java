package kmerrill285.Inignoto.game.world.chunk.generator.feature.LSystem;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class LSystem {
	   public ArrayList<L> system = new ArrayList<L>();
	   
	   public int leaves;
	   public double leafSize;
	   
	   public HashMap<String, Color> stuff = new HashMap<String, Color>();
	   
	   public LSystem(LSystemPos pos, int maxIterations, int leaves, double leafSize, double size, String...system) {
		   this.system.add(new L(pos, this, system, maxIterations, size, true, stuff));
		   this.leaves = leaves;
		   this.leafSize = leafSize;
	   }

		public void rebuild(LSystemPos pos, int maxIterations, int leaves, double leafSize, double size, String...system) {
			 this.system.add(new L(pos, this, system, maxIterations, size, true, stuff));
			 this.leaves = leaves;
			 this.leafSize = leafSize;
		}
	   
	   public void run(Random rand, int iterations) {
		   
		   for (int i = 0; i < iterations; i++) {
			   int A = system.size();
			   
			   for (int a = 0; a < A; a++) {
				   if (system.get(a).iterations <= 0) {
					   system.remove(a);
					   break;
				   }
				   system.get(a).runFunction(rand);
				   system.get(a).size *= 0.9;
			   }
		   }
	   }

}