package kmerrill285.PixelInventor.game.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Translation {
	
	public static Translation currentTranslation = null;
	public static HashMap<String, Translation> translations = new HashMap<String, Translation>();
	
	private HashMap<String, String> keys = new HashMap<String, String>();
	
	public String translate(String input) {
		if (keys.containsKey(input)) {
			return keys.get(input);
		}
		return input;
	}
	
	public static String translateText(String input) {
		if (currentTranslation == null) return input;
		setTranslation("");
		if (currentTranslation == null) return input;
		return currentTranslation.translate(input);
	}
	
	public static void loadTranslations(String modid) {
		File dir = new File("assets/"+modid+"/translations/");
		if (!dir.exists()) dir.mkdirs();
		
		File[] children = dir.listFiles();
		for (File file : children) {
			Translation translation = new Translation();
			try {
				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					String str = scanner.nextLine();
					String[] data = str.split("=");
					if (data.length == 2) {
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
						
						if (!a.isEmpty() && !b.isEmpty())
						translation.keys.put(a, b);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			translations.put(file.getName().replace(".txt", "").trim(), translation);
		}
		setTranslation("english");
	}
	
	public static void setTranslation(String translation) {

		for (String str : translations.keySet()) {
			if (str.contentEquals(translation)) {
				currentTranslation = translations.get(str);
				System.out.println("Translation set to " + str + ".");
				return;
			}
		}
		for (String str : translations.keySet()) {
			currentTranslation = translations.get(str);
			if (translation.contentEquals("")) {
				System.out.println("No translation loaded currently.  Setting translation to the first translation file found: " + str);
			} else {
				System.out.println("Couldn't find translation " + translation + ". Setting to " + str + " instead.");
			}
			return;
		}
	}
	
}
