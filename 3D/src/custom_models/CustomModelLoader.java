package custom_models;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.joml.Quaternionf;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import kmerrill285.Inignoto.game.client.rendering.textures.Texture;
import kmerrill285.Inignoto.resources.Utils;

public class CustomModelLoader {
	
	public static HashMap<String, Model> models = new HashMap<String, Model>();
	
	public static Model loadModel(File file, Texture texture) {
		Model model = new Model();
		try {
			Scanner scanner = new Scanner(file);
			String str = "";
			while(scanner.hasNext()) {
				str += scanner.nextLine()+"\n";
			}
			scanner.close();
			
			String[] lines = str.split("\n");
			ArrayList<Part> parts = new ArrayList<Part>();
			Part part = null;
			for (String s : lines ) {
				if (s.startsWith("Part")) {
					part = new Part(model);
				} else {
					String[] data = s.trim().split(" ");
					if (data[0].contains("Position")) {
						float x = Float.parseFloat(data[1]);
						float y = Float.parseFloat(data[2]);
						float z = Float.parseFloat(data[3]);
						part.setPosition(new Vector3f(x, y, z));
					} else
					if (data[0].contains("Rotation")) {
						float x = Float.parseFloat(data[1]);
						float y = Float.parseFloat(data[2]);
						float z = Float.parseFloat(data[3]);
						float w = Float.parseFloat(data[4]);
						part.setRotation(new Quaternionf(x, y, z, w));
					} else
					if (data[0].contains("Size")) {
						int x = Integer.parseInt(data[1]);
						int y = Integer.parseInt(data[2]);
						int z = Integer.parseInt(data[3]);
						part.size = new Vector3i(x, y, z);
					} else
					if (data[0].contains("Scale")) {
						float x = Float.parseFloat(data[1]);
						float y = Float.parseFloat(data[2]);
						float z = Float.parseFloat(data[3]);
						part.setScale(new Vector3f(x, y, z));
					} else
					if (data[0].contains("Angles")) {
						float x = Float.parseFloat(data[1]);
						float y = Float.parseFloat(data[2]);
						float z = Float.parseFloat(data[3]);
						part.axisAngles = new Vector3f(x, y, z);
					} else
					if (data[0].contains("Locked")) {
						part.locked = Boolean.parseBoolean(data[1]);
					} else
					if (data[0].contains("Visible")) {
						part.visible = Boolean.parseBoolean(data[1]);
					} else
					if (data[0].contains("Origin")) {
						float x = Float.parseFloat(data[1]);
						float y = Float.parseFloat(data[2]);
						float z = Float.parseFloat(data[3]);
						part.origin = new Vector3f(x, y, z);
					} else
					if (data[0].contains("UV")) {
						int x = Integer.parseInt(data[1]);
						int y = Integer.parseInt(data[2]);
						part.uv = new Vector2i(x, y);
						part.buildPart(texture);
						parts.add(part);
					}
					else
					if (data[0].contains("Parent")) {
						int a = Integer.parseInt(data[1]);
						int b = Integer.parseInt(data[2]);
						parts.get(a).parent = parts.get(b);
						parts.get(b).children.add(parts.get(a));
					}
					else {
						part.name = s;
					}
				}
			}
			model.getParts().clear();
			model.getParts().addAll(parts);
		}catch (Exception e) {
			
		}
		return model;
	}
	
	public static ArrayList<Keyframe> loadAnimation(File file) {
		return Model.loadAnimation(file);
	}
	
	public static Model getOrLoadModel(String modid, String directory, Texture texture) {
		String path = Utils.getResourcePath(modid, directory);
		File file = new File(path);
		if (models.containsKey(modid+":"+directory)) {
			return models.get(modid+":"+directory);
		}
		Model model = loadModel(file, texture);
		models.put(modid+":"+directory, model);
		return model;
	}
}
