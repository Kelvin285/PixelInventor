package kmerrill285.PixelInventor.resources;

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
}
