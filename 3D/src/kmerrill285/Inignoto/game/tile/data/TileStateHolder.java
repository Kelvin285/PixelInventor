package kmerrill285.Inignoto.game.tile.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import kmerrill285.Inignoto.game.tile.Tile;
import kmerrill285.Inignoto.game.tile.Tiles;

public class TileStateHolder {
	
	public static ArrayList<TileState> REGISTRY = new ArrayList<TileState>();
	public static TileState NO_STATE;
	
	
	public ArrayList<TileState> data;
	
	public final int num_states;
	
	private static int INDEX = 0;
	
	public static void initialize() {
		NO_STATE = new TileState(Tiles.AIR.getID(), 0, "Inignoto:tileinfo/air", INDEX++);
	}
	
	public TileStateHolder(Tile tile) {
		data = new ArrayList<TileState>();
		int states = 0;
		File file = new File("assets/"+tile.getName().split(":")[0]+"/tilestates/"+tile.getName().split(":")[1]+".states");
		if (file.exists()) {
			try {
				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					String str = scanner.nextLine();
					String[] data = str.split("=");
					boolean start = false;
					String a = "";
					String b = "";
					
					for (char c : data[0].toCharArray()) {
						if (start == true && c == '"') break;
						if (start == true) a += c;
						if (c == '"') start = true;
					}
					
					start = false;
					for (char c : data[1].toCharArray()) {
						if (start == true && c == '"') break;
						if (start == true) b += c;
						if (c == '"') start = true;
					}
					
					if (!a.isEmpty() && !b.isEmpty()) {
						try {
							int STATE = Integer.parseInt(a);
							String FILE = b;
							this.data.add(registerTileData(tile, STATE, FILE));
							states++;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		num_states = states;
	}
	
	
	public static TileState registerTileData(Tile tile, int state, String location) {
		TileState data = new TileState(tile.getID(), state, location, INDEX++);
		REGISTRY.add(data);
		return data;
	}
	
	public TileState getStateFor(int state) {
		return data.get(state);
	}
}
