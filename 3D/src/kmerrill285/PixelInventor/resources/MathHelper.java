package kmerrill285.PixelInventor.resources;

import org.joml.Vector3f;

public class MathHelper {
	public static float lerp(float point1, float point2, float alpha)
	{
	    return point1 + alpha * (point2 - point1);
	}
	
	public static float cubic_S_curve(float alpha)
	{
	  return alpha*alpha*( 3.0f - 2.0f*alpha );
	}
	
	public static float smoothLerp(float point1, float point2, float alpha) {
		return lerp(point1, point2, cubic_S_curve(alpha));
	}
	
	public static double floorDiv(double a, double b) {
		return Math.floor(a / b);
	}
	
	public static javax.vecmath.Vector3f toJavaxVector(Vector3f vec) {
		return new javax.vecmath.Vector3f(vec.x, vec.y, vec.z);
	}
	
	//https://bits.stephan-brumme.com/roundUpToNextPowerOfTwo.html
	public static int nextPowerOfTwo(int x) {
  	  x--;
  	  x |= x >> 1; // handle 2 bit numbers
  	  x |= x >> 2; // handle 4 bit numbers
  	  x |= x >> 4; // handle 8 bit numbers
  	  x |= x >> 8; // handle 16 bit numbers
  	  x |= x >> 16; // handle 32 bit numbers
  	  x++;
  	  return x;
  	}
}
