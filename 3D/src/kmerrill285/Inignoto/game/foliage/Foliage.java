package kmerrill285.Inignoto.game.foliage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import custom_models.CustomModelLoader;
import custom_models.Model;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.textures.Texture;
import kmerrill285.Inignoto.game.client.rendering.textures.Textures;
import kmerrill285.Inignoto.game.settings.Translation;
import kmerrill285.Inignoto.game.world.chunk.Chunk;

public abstract class Foliage {
	
	public static ArrayList<Foliage> registry = new ArrayList<Foliage>();
	
	public abstract void tick(int x, int y, int z, Chunk chunk);
	public abstract void render(float x, float y, float z, Chunk chunk, ShaderProgram shader);
	
	public final String name;
	
	public Texture texture;
	public Model model;
	
	public Foliage(String name) {
		this.name = name;
		File file = new File("assets/"+name.split(":")[0]+"/models/foliage/"+name.split(":")[1]+".model");
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
						
						if (a.equals("texture")) { 
							texture = Textures.loadTexture(b.split(":")[0],"foliage/"+b.split(":")[1]);
						}
						if (a.equals("3dmodel")) model = CustomModelLoader.getOrLoadModel(b.split(":")[0], "models/3dmodel/foliage/"+b.split(":")[1]+".3dmodel", texture).combine(texture);
					}
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static Foliage registerFoliage(Foliage foliage) {
		registry.add(foliage);
		return foliage;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTranslatedName() {
		return Translation.translateText(name.split(":")[0]+":foliage."+name.split(":")[1]);
	}
	
	
	
	public static Foliage TALL_GRASS;
	public static Foliage PURPLE_FLOWER;
	
	public static void loadFoliage() {
		TALL_GRASS = registerFoliage(new TallGrassFoliage("Inignoto:tallgrass"));
		PURPLE_FLOWER = registerFoliage(new FlowerFoliage("Inignoto:purpleflower"));
	}
	
	public static void disposeOfFoliage() {
		for (Foliage f : registry) {
			f.model.dispose();
		}
	}
	
}
