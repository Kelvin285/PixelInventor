package kmerrill285.PixelInventor.game.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.game.world.chunk.Chunk;

public class WorldSaver {
	private String worldName;
	private World world;
	public WorldSaver(String worldName, World world) {
		this.worldName = worldName;
		this.world = world;
		loadWorld();
	}
	
	public void saveWorld() {
		File file = new File("PixelInventor/saves/"+getWorldName()+"/save.data");
		if (!file.exists())
			try {
				File dir = new File("PixelInventor/saves/"+getWorldName()+"/");
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
		File file = new File("PixelInventor/saves/"+getWorldName()+"/save.data");
		if (file.exists()) {
			Scanner scanner;
			try {
				scanner = new Scanner(file);
				long seed = -1;
				while (scanner.hasNext()) {
					String str = scanner.nextLine().trim();
					if (seed == -1) {
						seed = Long.parseLong(str);
					}
					
				}
				world.setSeed(seed);
				scanner.close();
			} catch (FileNotFoundException e) {
				file.mkdirs();
			}
		}
	}
	
	public void saveChunk(Chunk chunk) {
		if (!chunk.needsToSave()) return;
		String pos = chunk.getX()+","+chunk.getY()+","+chunk.getZ();
		String name = "Chunk"+pos;
		File file = new File("PixelInventor/saves/"+getWorldName()+"/"+name+".chunk");
		if (!file.exists())
			try {
				File dir = new File("PixelInventor/saves/"+getWorldName()+"/");
				dir.mkdirs();
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		try {
			FileWriter writer = new FileWriter(file);
			for (int x = 0; x < Chunk.SIZE; x++) {
				for (int y = 0; y < Chunk.SIZE; y++) {
					for (int z = 0; z < Chunk.SIZE; z++) {
						writer.write(chunk.getTile(x, y, z).getID() + "\n");
					}
				}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean tryLoadChunk(Chunk chunk) {
		String pos = chunk.getX()+","+chunk.getY()+","+chunk.getZ();
		String name = "Chunk"+pos;
		File file = new File("PixelInventor/saves/"+getWorldName()+"/"+name+".chunk");
		if (file.exists()) {
			try {
				Scanner scanner = new Scanner(file);
				ArrayList<String> lines = new ArrayList<String>();
				while (scanner.hasNext()) {
					String str = scanner.nextLine();
					lines.add(str.trim());
				}
				scanner.close();
				HashMap<Integer, Tile> cache = new HashMap<Integer, Tile>();
				for (int x = 0; x < Chunk.SIZE; x++) {
					for (int y = 0; y < Chunk.SIZE; y++) {
						for (int z = 0; z < Chunk.SIZE; z++) {
							int i = x + y * Chunk.SIZE + z * Chunk.SIZE * Chunk.SIZE;
							if (i < lines.size()) {
								int ID = Integer.parseInt(lines.get(i));
								if (cache.containsKey(ID)) {
									chunk.setTile(x, y, z, cache.get(ID), false);
								} else {
									Tile tile = Tiles.getTile(ID);
									chunk.setTile(x, y, z, tile, false);
									cache.put(ID, tile);
								}
							}
						}
					}
				}
				return true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	
	public String getWorldName() {
		return this.worldName;
	}
	
	public World getWorld() {
		return this.world;
	}
}
