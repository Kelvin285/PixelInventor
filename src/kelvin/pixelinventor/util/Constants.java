package kelvin.pixelinventor.util;

import kelvin.pixelinventor.imported.FastNoise;

public class Constants {
	public static final int TILESIZE = 16;
	public static final int RENDERSIZE = 8;
	public static final int[] DEFAULT_STATE = {0, 0};
	
	public static final double[] NO_LIGHT = {0, 0, 0, 1};
	public static final double[] WHITE_LIGHT = {1, 1, 1, 1};
	
	public static final FastNoise noise = new FastNoise();
	
	public static final int[][] MSQUARES = {
			{0, 0},
			{1, 0},
			{2, 0},
			{3, 0},
			{4, 0},
			{5, 0},
			{6, 0},
			{7, 0},
			{8, 0},
			{9, 0},
			{10, 0},
			{11, 0},
			{12, 0},
			{13, 0},
			{14, 0},
			{15, 0},
			{16, 0},
			{17, 0},
			{18, 0},
			{19, 0}
	};
}
