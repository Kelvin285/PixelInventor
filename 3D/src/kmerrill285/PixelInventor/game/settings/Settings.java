package kmerrill285.PixelInventor.game.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import org.lwjgl.glfw.GLFW;

public class Settings {
	public static float FOV = 60.0f;
	public static float ACTUAL_FOV = 60.0f;
	public static float MOUSE_SENSITIVITY = 0.1f;
	
	public static int VIEW_X = 6, VIEW_Y = 4; //normal = 8x8
	public static int FAR_PLANE_VIEW = 64; //max = 128
	
	public static boolean FAR_PLANE_ENABLED = true;
	public static boolean HEAD_BOB = true;
	
	public static boolean CASCADED_SHADOWS = true;
	public static boolean SHADOWS = true;
	public static boolean REFLECTIONS = true;
	public static boolean RAYTRACING = true;
	
	public static float EXPOSURE = 1.0f;
	
	public static int RAYTRACE_VIEW = 8;
	
	public static boolean POST_PROCESSING = true;
	public static int frameSkip = 0;
	
	
	public static HashMap<String, InputSetting> inputs = new HashMap<String, InputSetting>();
	
	public static InputSetting JUMP = new InputSetting(GLFW.GLFW_KEY_SPACE, false, "PixelInventor:input.jump");
	public static InputSetting SNEAK = new InputSetting(GLFW.GLFW_KEY_LEFT_SHIFT, false, "PixelInventor:input.sneak");
	public static InputSetting RUN = new InputSetting(GLFW.GLFW_KEY_LEFT_CONTROL, false, "PixelInventor:input.run");
	public static InputSetting EXIT = new InputSetting(GLFW.GLFW_KEY_ESCAPE, false, "PixelInventor:input.exit");
	public static InputSetting INVENTORY = new InputSetting(GLFW.GLFW_KEY_E, false, "PixelInventor:input.inventory");
	public static InputSetting FORWARD = new InputSetting(GLFW.GLFW_KEY_W, false, "PixelInventor:input.forward");
	public static InputSetting BACKWARD = new InputSetting(GLFW.GLFW_KEY_S, false, "PixelInventor:input.backward");
	public static InputSetting LEFT = new InputSetting(GLFW.GLFW_KEY_A, false, "PixelInventor:input.left");
	public static InputSetting RIGHT = new InputSetting(GLFW.GLFW_KEY_D, false, "PixelInventor:input.right");
	public static InputSetting ATTACK = new InputSetting(0, true, "PixelInventor:input.attack");
	public static InputSetting USE = new InputSetting(1, true, "PixelInventor:input.use");
	
	public static HashMap<Integer, Boolean> keys = new HashMap<Integer, Boolean>();
	public static HashMap<Integer, Boolean> pressedKey = new HashMap<Integer, Boolean>();
	public static HashMap<Integer, Boolean> buttons = new HashMap<Integer, Boolean>();
	public static HashMap<Integer, Boolean> pressedButton = new HashMap<Integer, Boolean>();

	
	public static void loadSettings() {
		File file = new File("PixelInventor/settings.txt");
		if (!file.exists()) {
			File dir = new File("PixelInventor");
			dir.mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
					{
						if (a.contentEquals("FOV")) {
							FOV = Float.parseFloat(b);
						}
						if (a.contentEquals("MOUSE_SENSITIVITY")) {
							MOUSE_SENSITIVITY = Float.parseFloat(b);
						}
						if (a.contentEquals("VIEW_X")) {
							VIEW_X = Integer.parseInt(b);
						}
						if (a.contentEquals("VIEW_Y")) {
							VIEW_Y = Integer.parseInt(b);
						}
						if (a.contentEquals("FAR_PLANE_VIEW")) {
							FAR_PLANE_VIEW = Integer.parseInt(b);
						}
						if (a.contentEquals("FAR_PLANE_ENABLED")) {
							FAR_PLANE_ENABLED = Boolean.parseBoolean(b);
						}
						if (a.contentEquals("HEAD_BOB")) {
							HEAD_BOB = Boolean.parseBoolean(b);
						}
						if (a.contentEquals("SHADOWS")) {
							SHADOWS = Boolean.parseBoolean(b);
						}
						if (a.contentEquals("REFLECTIONS")) {
							REFLECTIONS = Boolean.parseBoolean(b);
						}
						if (a.contentEquals("RAYTRACING")) {
							RAYTRACING = Boolean.parseBoolean(b);
						}
						if (a.contentEquals("EXPOSURE")) {
							EXPOSURE = Float.parseFloat(b);
						}
						if (a.contentEquals("POST_PROCESSING")) {
							POST_PROCESSING = Boolean.parseBoolean(b);
						}
						if (a.contentEquals("RAYTRACE_VIEW")) {
							RAYTRACE_VIEW = Integer.parseInt(b);
						}
						if (a.contentEquals("FRAME_SKIP")) {
							frameSkip = Integer.parseInt(b);
						}
						if (a.contentEquals("JUMP")) {
							JUMP.read(b);
						}
						if (a.contentEquals("SNEAK")) {
							SNEAK.read(b);
						}
						if (a.contentEquals("RUN")) {
							RUN.read(b);
						}
						if (a.contentEquals("EXIT")) {
							EXIT.read(b);
						}
						if (a.contentEquals("INVENTORY")) {
							INVENTORY.read(b);
						}
						if (a.contentEquals("FORWARD")) {
							FORWARD.read(b);
						}
						if (a.contentEquals("BACKWARD")) {
							BACKWARD.read(b);
						}
						if (a.contentEquals("LEFT")) {
							LEFT.read(b);
						}
						if (a.contentEquals("RIGHT")) {
							RIGHT.read(b);
						}
						if (a.contentEquals("ATTACK")) {
							ATTACK.read(b);
						}
						if (a.contentEquals("USE")) {
							USE.read(b);
						}
					}
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveSettings() {
		File file = new File("PixelInventor/settings.txt");
		if (!file.exists()) {
			File dir = new File("PixelInventor");
			dir.mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileWriter writer = new FileWriter(file);
			String str = "";
			str += getSaveString("FOV", FOV);
			str += getSaveString("MOUSE_SENSITIVITY", MOUSE_SENSITIVITY);
			str += getSaveString("VIEW_X", VIEW_X);
			str += getSaveString("VIEW_Y", VIEW_Y);
			str += getSaveString("FAR_PLANE_VIEW", FAR_PLANE_VIEW);
			str += getSaveString("FAR_PLANE_ENABLED", FAR_PLANE_ENABLED);
			str += getSaveString("HEAD_BOB", HEAD_BOB);
			str += getSaveString("SHADOWS", SHADOWS);
			str += getSaveString("REFLECTIONS", REFLECTIONS);
			str += getSaveString("RAYTRACING", RAYTRACING);
			str += getSaveString("EXPOSURE", EXPOSURE);
			str += getSaveString("POST_PROCESSING", POST_PROCESSING);
			str += getSaveString("RAYTRACE_VIEW", RAYTRACE_VIEW);
			str += getSaveString("FRAME_SKIP", frameSkip);
			str += getSaveString("JUMP", JUMP.write());
			str += getSaveString("SNEAK", SNEAK.write());
			str += getSaveString("RUN", RUN.write());
			str += getSaveString("EXIT", EXIT.write());
			str += getSaveString("INVENTORY", INVENTORY.write());
			str += getSaveString("FORWARD", FORWARD.write());
			str += getSaveString("BACKWARD", BACKWARD.write());
			str += getSaveString("LEFT", LEFT.write());
			str += getSaveString("RIGHT", RIGHT.write());
			str += getSaveString("ATTACK", ATTACK.write());
			str += getSaveString("USE", USE.write());
			
			writer.write(str);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String getSaveString(String a, Object b) {
		return "\""+a+"\"=\""+b+"\"\n";
	}
	
	public static boolean isKeyDown(int key) {
		if (keys.get(key) != null) {
			return keys.get(key);
		}
		keys.put(key, false);
		return false;
	}
	
	public static boolean isKeyJustDown(int key) {
		if (pressedKey.get(key) != null) {
			if (pressedKey.get(key)) {
				pressedKey.put(key, false);
				return true;
			}
		}
		pressedKey.put(key, false);
		return false;
	}
	
	public static boolean isMouseButtonDown(int button) {
		if (buttons.get(button) != null) {
			return buttons.get(button);
		}
		buttons.put(button, false);
		return false;
	}
	
	public static boolean isMouseButtonJustDown(int button) {
		if (pressedButton.get(button) != null) {
			if (pressedButton.get(button)) {
				pressedButton.put(button, false);
				return true;
			}
		}
		pressedButton.put(button, false);
		return false;
	}
}
