package kelvin.pixelinventor.game.world.generator.noise;

import java.awt.Point;
import java.util.Random;

import kelvin.pixelinventor.util.math.MathFunc;

public class VoronoiNoise {
	
	private Random random;
	private long seed;
	public VoronoiNoise(long seed) {
		random = new Random(seed);
		this.seed = seed;
	}
	
	private void setRandom(int x, int y) {
		random.setSeed(seed * x * y);
	}
	
	public double getSmoothVoronoiAt(int x, int y, int size, double m) {
		   double dist = Integer.MAX_VALUE;
		   double height = 0;
			int w = 3;
			int h = 3;
			
			Point[] ps = new Point[w * h];
			double[] heights = new double[w * h];
			for (int X = 0; X < w; X++) {
				for (int Y = 0; Y < h; Y++) {
					Point p2 = getVoronoiPoint(x + (X - w / 2) * size, y + (Y - h / 2) * size, size);
					ps[X + Y * h] = p2;
				}
			}
			for (Point p : ps) {
				setRandom(p.x, p.y);
				double d = Point.distance(x + random.nextDouble() * 10, y + random.nextDouble() * 10, p.x, p.y);
				if (d <= dist) {
					dist = d;
					height = getVoronoiHeight(p.x, p.y, size, m);
				}
			}
			for (int X = 0; X < w; X++) {
				for (int Y = 0; Y < h; Y++) {
					Point center = ps[X + Y * h];
					
					setRandom(x, y);
					double dist1 = Point.distance(x + random.nextDouble() * 10, y + random.nextDouble() * 10, center.x, center.y);
					
					double height2 = getVoronoiHeight(center.x, center.y, size, m);
					if (dist1 == 0) dist1 = 1;
					height = MathFunc.lerp(height, height2, (dist / dist1) * 0.75);
				}
			}
			
			return height;
			
			
	   }
	
	public double getVoronoiAt(int x, int y, int size, double m) {
		   double dist = Integer.MAX_VALUE;
		   double height = 0;
			int w = 3;
			int h = 3;
			
			Point[] ps = new Point[w * h];
			for (int X = 0; X < w; X++) {
				for (int Y = 0; Y < h; Y++) {
					Point p2 = getVoronoiPoint(x + (X - w / 2) * size, y + (Y - h / 2) * size, size);
					ps[X + Y * h] = p2;
				}
			}
			for (Point p : ps) {
				setRandom(p.x, p.y);
				double d = Point.distance(x + random.nextDouble() * 10, y + random.nextDouble() * 10, p.x, p.y);
				if (d <= dist) {
					dist = d;
					height = getVoronoiHeight(p.x, p.y, size, m);
				}
			}
			return height;
	   }
	   
	   public Point getPointAt(int x, int y, int size, double m) {
		   double dist = Integer.MAX_VALUE;
		   Point pt = new Point(0, 0);
			int w = 3;
			int h = 3;
			
			Point[] ps = new Point[w * h];
			for (int X = 0; X < w; X++) {
				for (int Y = 0; Y < h; Y++) {
					Point p2 = getVoronoiPoint(x + (X - w / 2) * size, y + (Y - h / 2) * size, size);
					ps[X + Y * h] = p2;
				}
			}
			for (Point p : ps) {
				setRandom(p.x, p.y);
				double d = Point.distance(x + random.nextDouble() * 10, y + random.nextDouble() * 10, p.x, p.y);
				if (d <= dist) {
					dist = d;
					pt = p;
				}
			}
			return pt;
	   }
		
	   public Point getVoronoiPoint(int x, int y, int size) {
		   Point p = new Point((x / size) * size + (size / 2), (y / size) * size + (size / 2));
		   setRandom(p.x, p.y);
		   p.x += (int)(random.nextDouble() * ((double)size / 2.0));
		   p.y += (int)(random.nextDouble() * ((double)size / 2.0));
		   return p;
	   }
	   
	   public double getVoronoiHeight(int x, int y, int size, double m) {
		   setRandom(x, y);
		   return (random.nextDouble() * m);
	   }
}
