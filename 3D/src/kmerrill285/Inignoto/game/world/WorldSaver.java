package kmerrill285.Inignoto.game.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class WorldSaver {
	private String worldName;
	private World world;
	private long seed;
	public WorldSaver(String worldName, World world, long s) {
		this.worldName = worldName;
		this.world = world;
		this.seed = s;
		loadWorld();
	}
	
	public void saveWorld() {
		File file = new File("Inignoto/saves/"+getWorldName()+"/save.data");
		if (!file.exists())
			try {
				File dir = new File("Inignoto/saves/"+getWorldName()+"/");
				dir.mkdirs();
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(""+world.getSeed()+"\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadWorld() {
		long seed = -1;
		
		File file = new File("Inignoto/saves/"+getWorldName()+"/save.data");
		if (file.exists()) {
			Scanner scanner;
			try {
				scanner = new Scanner(file);
				while (scanner.hasNext()) {
					String str = scanner.nextLine().trim();
					if (seed == -1) {
						seed = Long.parseLong(str);
						continue;
					}
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				file.mkdirs();
			}
		}
		if (seed == -1) {
			seed = this.seed;
		}
		world.setSeed(seed);
		System.out.println("World seed: " + seed);
	}
	
	public String getWorldName() {
		return this.worldName;
	}
	
	public World getWorld() {
		return this.world;
	}
}
