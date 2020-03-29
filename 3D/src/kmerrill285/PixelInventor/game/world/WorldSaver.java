package kmerrill285.PixelInventor.game.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import kmerrill285.PixelInventor.game.entity.StaticEntities;
import kmerrill285.PixelInventor.game.entity.StaticEntity;
import kmerrill285.PixelInventor.game.tile.Tile;
import kmerrill285.PixelInventor.game.tile.Tiles;
import kmerrill285.PixelInventor.game.world.chunk.Chunk;

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
		long seed = -1;
		
		File file = new File("PixelInventor/saves/"+getWorldName()+"/save.data");
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
						writer.write("b:"+chunk.getTile(x, y, z).getID() + "\n");
					}
				}
			}
			for (StaticEntity e : chunk.staticEntities) {
				writer.write(e.getSaveData()+"\n");
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
								String[] data = lines.get(i).split(":");
								if (data[0].equals("b")) {
									int ID = Integer.parseInt(data[1]);
									if (cache.containsKey(ID)) {
										chunk.setTile(x, y, z, cache.get(ID), false, false);
									} else {
										Tile tile = Tiles.getTile(ID);
										chunk.setTile(x, y, z, tile, false, false);
										cache.put(ID, tile);
									}
								} else {
									if (data[0].equals("se")) {
										StaticEntity e = StaticEntities.staticEntities.get(Integer.parseInt(data[1].split(",")[0]));
										e.load(data[1]);
										e.create(chunk);
										chunk.staticEntities.add(e);
									}
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
