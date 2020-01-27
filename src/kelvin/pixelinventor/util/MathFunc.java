package kelvin.pixelinventor.util;

public class MathFunc {
	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
	public static double lerp(double height, double height2, double d)
	{
	    return height + d * (height2 - height);
	}
}
