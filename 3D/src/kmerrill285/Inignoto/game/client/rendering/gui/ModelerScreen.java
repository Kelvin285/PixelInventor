package kmerrill285.Inignoto.game.client.rendering.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.joml.Vector3f;
import org.joml.Vector4f;

import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.Mouse;
import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.textures.Texture;
import kmerrill285.Inignoto.game.client.rendering.textures.Textures;
import kmerrill285.Inignoto.game.settings.Settings;
import kmerrill285.Inignoto.modelloader.AnimModel;
import kmerrill285.Inignoto.modelloader.CustomModel;
import kmerrill285.Inignoto.modelloader.ModelLoader;
import kmerrill285.Inignoto.modelloader.ModelPart;
import kmerrill285.Inignoto.resources.FPSCounter;
import kmerrill285.Inignoto.resources.Utils;

public class ModelerScreen extends MenuScreen {

	public CustomModel model;
	
	public Mesh selectionMesh = null;
	
	public String lastLoadDir = System.getProperty("user.home");
	public Texture texture;
	public File modelDir = null;
	
	public int sidebarX = 1920 - 400;
	public boolean sidebarSelected = false;
	
	public float scrollPercent;
	public boolean scrollGrabbed = false;
	
	public String selectedPart = "";
	
	public class PartHolder {
		public ModelPart part;
		public int parents;
		public PartHolder(ModelPart part) {
			ModelPart parent = part.parent;
			this.part = part;
			int i = 0;
			while (parent != null) {
				i++;
				parent = parent.parent;
			}
			this.parents = i;
		}
	}
	
	public ArrayList<PartHolder> sortedParts = null;
	
	public ModelerScreen(GuiRenderer renderer) {
		super(renderer);
		Camera.position = new Vector3f(0, 1, 3);
		Camera.rotation = new Vector3f(0, 0, 0);
		Camera.update();
		texture = Textures.WHITE_SQUARE;
	}
	@Override
	public void tick() {
		if (Settings.USE.isPressed()) {
			Camera.rotation.y += Mouse.x - Mouse.lastX;
			Camera.rotation.x += Mouse.y - Mouse.lastY;
		}
		if (Settings.FORWARD.isPressed()) {
			Camera.position.add(Camera.getForward().mul(0.01f).mul((float)FPSCounter.getDelta()));
		}
		if (Settings.BACKWARD.isPressed()) {
			Camera.position.add(Camera.getForward().mul(-0.01f).mul((float)FPSCounter.getDelta()));
		}
		if (Settings.RIGHT.isPressed()) {
			Camera.position.add(Camera.getRight().mul(0.01f).mul((float)FPSCounter.getDelta()));
		}
		if (Settings.LEFT.isPressed()) {
			Camera.position.add(Camera.getRight().mul(-0.01f).mul((float)FPSCounter.getDelta()));
		}
		if (Settings.JUMP.isPressed()) {
			Camera.position.add(0, 0.01f * (float)FPSCounter.getDelta(), 0);
		}
		if (Settings.SNEAK.isPressed()) {
			Camera.position.add(0, -0.01f * (float)FPSCounter.getDelta(), 0);
		}
		
		//max visible: 19
		float barScale = 1;
		if (this.model != null) {
			barScale = (19.0f / this.model.model.parts.size());
		}

		float div = 50.0f;
		
		if (scrollGrabbed) {
			if (Mouse.y > Mouse.lastY) {
				this.scrollPercent += Math.abs(Mouse.y - Mouse.lastY) * barScale / div;
			}
			if (Mouse.y < Mouse.lastY) {
				this.scrollPercent -= Math.abs(Mouse.y - Mouse.lastY) * barScale / div;
			}
		}
		
		if (scrollPercent > 1.0) {
			scrollPercent = 1.0f;
		}
		
		if (scrollPercent < 0) {
			scrollPercent = 0;
		}
		
		Mouse.lastX = Mouse.x;
		Mouse.lastY = Mouse.y;
	}

	@Override
	public void render(ShaderProgram shader) {
		
		double mx = Mouse.x * (1920.0 / Utils.FRAME_WIDTH);
		double my = 1080 - Mouse.y * (1080.0 / Utils.FRAME_HEIGHT);
		
		boolean hovered = false;
		if (mx > 1920 - 48 - 64 && mx < 1920 - 48 + 64 - 64) {
			if (my > 32 && my < 32 + 64) {
				hovered = true;
			}
		}
		
		boolean texLoadHovered = false;
		
		if (mx > 48 * 7 - 15 * 1.5f && my > 1080 - 60) {
			if (mx < 48 * 7 + "Load Texture".length() * 1.5f * 15 - 15 * 1.5f) {
				if (my < 1080 - 30) {
					texLoadHovered = true;
				}
			}
		}
		
		if (!texLoadHovered) {
			this.renderer.drawString("Load Texture", 48 * 7, 1080 - 60, 1.5f, new Vector4f(1, 1, 1, 1), true);
		} else {
			this.renderer.drawString("Load Texture", 48 * 7, 1080 - 60, 1.5f, new Vector4f(0, 1, 0, 1), true);
			if (Settings.isMouseButtonJustDown(0)) {
				
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(lastLoadDir));
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					String f = selectedFile.getPath();
					
					System.out.println(f);
					lastLoadDir = selectedFile.getParent();
					try {
						this.texture = new Texture(selectedFile);
						if (this.model != null) {
							AnimModel model = ModelLoader.loadModelFromFile(modelDir);

							this.model = new CustomModel(model, texture);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "This is not a valid texture file!");
					}
				}
			}
		}
		
		
		boolean loadHovered = false;
		if (mx > 28 && my < 1050) {
			if (mx < 118 + " Model".length() * 1.5f * 15 && my > 1028) {
				loadHovered = true;
			}
		}
		if (!loadHovered) {
			this.renderer.drawString("Load Model", 48, 1080 - 60, 1.5f, new Vector4f(1, 1, 1, 1), true);
		} else {
			this.renderer.drawString("Load Model", 48, 1080 - 60, 1.5f, new Vector4f(0, 1, 0, 1), true);
			if (Settings.isMouseButtonJustDown(0)) {
				
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(lastLoadDir));
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					String f = selectedFile.getPath();
					
					System.out.println(f);
					lastLoadDir = selectedFile.getParent();
					try {
						AnimModel model = ModelLoader.loadModelFromFile(selectedFile);
						this.model = new CustomModel(model, texture);
						this.modelDir = selectedFile;
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "This is not a valid model file!");
					}
				}
			}
		}
		
		if (!hovered) {
			this.renderer.drawTexture(Textures.BACK_ARROW, 1920 - 48, 32, -64, 64, 0, new Vector4f(1, 1, 1, 1));
		} else {
			this.renderer.drawString("Back to Main Menu", (float)mx - "Back to Main Menu".length() * 15 * 2, (float)my, 2, new Vector4f(1, 1, 1, 1), true);

			this.renderer.drawTexture(Textures.BACK_ARROW, 1920 - 48, 32, -64, 64, 0, new Vector4f(0, 1, 0, 1));
			if (Settings.isMouseButtonJustDown(0)) {
				this.renderer.openScreen(new MenuScreen(renderer));
			}
		}
		
		//max visible: 19
		float barScale = 1;
		if (this.model != null) {
			barScale = (19.0f / this.model.model.parts.size());
		}
		this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, 0, 1080, 1920, -64, 0, new Vector4f(0.7f,0.7f, 0.7f, 1.0f));
		this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, 0, 1080, 1920, -64 - 10, 0, new Vector4f(0.2f, 0.2f, 0.2f, 1.0f));

		this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, 0, 0, 1920, 125, 0, new Vector4f(0.7f,0.7f, 0.7f, 1.0f));
		this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, 0, 0, 1920, 125 + 10, 0, new Vector4f(0.2f,0.2f, 0.2f, 1.0f));

		float barLeft = (1000 - 125) - (1000 - 125) * barScale;
		
		Vector3f grabColor = new Vector3f(0.7f, 0.7f, 0.7f);
		if (mx > 1920 - 64 + 16 && mx < 1920 - 64 + 16 + 32) {
			if (my < 1000 - barLeft * scrollPercent) {
				if (my > 1000 - barLeft * scrollPercent - (1000 - 125) * barScale) {
					grabColor = new Vector3f(1.0f, 1.0f, 1.0f);
					if (Settings.isMouseButtonJustDown(0)) {
						scrollGrabbed = true;
					}
				}
			}
		}
		
		
		if (this.model != null)
			this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, 1920 - 64 + 16, 1000 - barLeft * scrollPercent, 32, -(1080 - 80 - 125) * barScale, 0, new Vector4f(grabColor.x, grabColor.y, grabColor.z, 1.0f));

		
		
		this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, 1920 - 64, 0, 64, 1080, 0, new Vector4f(0.2f, 0.2f, 0.2f, 1.0f));
		
		if (this.model != null) {
			HashMap<String, ModelPart> parts = this.model.model.parts;
			sort(parts);
			float length = (parts.size() * (30 + 15));
			float view = (19 * (30 + 15));
			float div = (length - view);
			if (length - view < 0) {
				div = 0;
			}
			int i = 0;
			if (this.sortedParts != null)
			for (PartHolder holder : this.sortedParts) {
				
				
				if (holder.part != null) {
					
					Vector3f color = new Vector3f(1, 1, 1);
					if (selectedPart.equals(holder.part.name)) {
						color = new Vector3f(1, 1, 0);
					}
					float X = sidebarX + 32 + holder.parents * 15;
					float Y = 1000 - (i * (30 + 15)) - 30 + div * scrollPercent;
					float scale = 1.5f;
					if (mx > sidebarX) {
						if (my > Y) {
							if (mx < X + holder.part.name.length() * 15 * scale - 15 * scale) {
								if (my < Y + 30 * scale) {
									color = new Vector3f(0, 1, 1);
									if (Settings.isMouseButtonJustDown(0)) {
										if (this.model.extraMeshes.get(selectedPart) != null) {
											this.model.extraMeshes.get(selectedPart).remove(this.selectionMesh);
										}
										if (this.selectionMesh != null) {
											this.model.extraScale.remove(this.selectionMesh);
										}
										this.selectionMesh = null;
										selectedPart = holder.part.name;
										this.selectionMesh = CustomModel.buildMesh(holder.part, Textures.TILE_SELECTION, 
												new float[] {
													0, 0,
													0, 1,
													1, 1,
													1, 0,
													
													0, 0,
													0, 1,
													1, 1,
													1, 0,
													
													0, 0,
													0, 1,
													1, 1,
													1, 0,
													
													0, 0,
													0, 1,
													1, 1,
													1, 0,
													
													0, 0,
													0, 1,
													1, 1,
													1, 0,
													
													0, 0,
													0, 1,
													1, 1,
													1, 0
												},
												1.0f
												);
										this.model.extraMeshes.get(selectedPart).add(this.selectionMesh);
										this.model.extraScale.put(this.selectionMesh, 1.1f);
									}
								}
							}
						}
					}
					
					this.renderer.drawString(holder.part.name, X, Y, scale, new Vector4f(color.x, color.y, color.z, 1), true);
					
					if (i == sortedParts.size() - 1) {
						this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, sidebarX, Y - 50, X + holder.part.name.length() * 15 * scale - 15 * scale, 10 + 35, 0, new Vector4f(0.3f, 0.3f, 0.3f, 1.0f));

						this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, sidebarX, Y - 10, X + holder.part.name.length() * 15 * scale - 15 * scale, 10, 0, new Vector4f(0.3f, 0.3f, 0.3f, 1.0f));

					} else {
						this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, sidebarX, Y - 10, X + holder.part.name.length() * 15 * scale - 15 * scale, 10, 0, new Vector4f(0.3f, 0.3f, 0.3f, 1.0f));
					}
					
				}
				
				i++;
			}
		}
		
		this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, sidebarX, 125, 1920, 1080 - 125, 0, new Vector4f(0.5f, 0.5f, 0.5f, 1.0f));
		
		
		this.renderer.drawNormalTexture(Textures.BACK_ARROW, sidebarX - 20, 1080 / 2 - 32, 16, 32, 0, new Vector4f(1f, 1f, 1f, 1.0f));

		if (mx >= sidebarX - 20 && mx <= sidebarX) {
			if (my <= 1008 && my >= 125) {
				
				if (Settings.isMouseButtonJustDown(0)) {
					this.sidebarSelected = true;
				}
				
			}
		}
		Vector3f color = new Vector3f(0.3f, 0.3f, 0.3f);
		if (this.sidebarSelected == true) {
			color = new Vector3f(0.7f, 0.7f, 0.7f);
			sidebarX = (int) (mx + 10);
			if (sidebarX > 1920 - 100) {
				sidebarX = 1920 - 100;
				color = new Vector3f(0.7f, 0.0f, 0.0f);
			}
			if (sidebarX < 1920 - 900) {
				sidebarX = 1920 - 900;
				color = new Vector3f(0.7f, 0.0f, 0.0f);
			}
		}
		if (!Settings.isMouseButtonDown(0)) {
			this.sidebarSelected = false;
			this.scrollGrabbed = false;
		}
		this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, sidebarX - 20, 125, 1920, 1080 - 125, 0, new Vector4f(color.x, color.y, color.z, 1.0f));
		

	}
	
	public void render3D(ShaderProgram shaderProgram) {
		
		if (model != null) {
			model.render(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), shaderProgram);
		}
	}

	@Override
	public void close() {
		if (model != null) {
			model.dispose();
		}
		if (texture != null) {
			texture.dispose();
		}
	}
	
	private void sort(HashMap<String, ModelPart> parts) {
		
		this.sortedParts = new ArrayList<PartHolder>();
		for (String str : parts.keySet()) {
			ModelPart part = parts.get(str);
			if (part.parent == null) {
				add(part);
			}
			
		}
	}
	
	private void add(ModelPart part) {
		this.sortedParts.add(new PartHolder(part));
		if (part.children != null) {
			if (part.children.size() > 0) {
				for (int i = 0; i < part.children.size(); i++) {
					add(part.children.get(i));
				}
			}
		}
	}
}
