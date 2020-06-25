package kmerrill285.Inignoto.game.client.rendering.gui;

import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;

public class ModelerScreen extends MenuScreen {

//	public CustomModel model;
//	
//	public Mesh selectionMesh = null;
//	
//	public String lastLoadDir = System.getProperty("user.home");
//	public Texture texture;
//	public File modelDir = null;
//	
//	public int sidebarX = 1920 - 400;
//	public boolean sidebarSelected = false;
//	
//	public float scrollPercent;
//	public boolean scrollGrabbed = false;
//	
//	public String selectedPart = "";
//	
//	public Mesh grid;
//	public Mesh block;
//	
//	public int floorSize = 1;
//
//	public int selectedAttribute = -1;
//	
//	private boolean updated = false;
//	
//	public float clickX, clickY;
//	
//	
//	public class ActionHolder {
//		public ModelPart part;
//		public ModelTransformation transformation;
//		public ActionHolder(ModelPart part) {
//			transformation = new ModelTransformation(part.transformation);
//			this.part = part;
//		}
//		public void swap() {
//			ModelTransformation t = new ModelTransformation(part.transformation);
//			part.transformation = new ModelTransformation(transformation);
//			transformation = t;
//		}
//	}
//	
//	
//	public class PartHolder {
//		public ModelPart part;
//		public int parents;
//		public PartHolder(ModelPart part) {
//			ModelPart parent = part.parent;
//			this.part = part;
//			int i = 0;
//			while (parent != null) {
//				i++;
//				parent = parent.parent;
//			}
//			this.parents = i;
//		}
//	}
//	
//	public ArrayList<PartHolder> sortedParts = null;
//	
//	public ArrayList<ActionHolder> actions = new ArrayList<ActionHolder>();
//	public int lastAction = -1;
//	
//	public void startAction(ModelPart part) {
//		ActionHolder holder = new ActionHolder(part);
//		ArrayList<ActionHolder> newActions = new ArrayList<ActionHolder>();
//		for (int i = 0; i < lastAction + 1; i++) {
//			newActions.add(actions.get(i));
//		}
//		newActions.add(holder);
//		lastAction = newActions.size() - 1;
//		actions = newActions;
//	}
//	
//	public void undo() {
//		if (lastAction < 0) {
//			return;
//		}
//		actions.get(lastAction).swap();
//		lastAction--;
//		this.model.rebuild();
//	}
//	
//	public void redo() {
//		if (lastAction + 1 < actions.size()) {
//			lastAction++;
//			actions.get(lastAction).swap();
//		}
//		this.model.rebuild();
//	}
//	
//	public ModelerScreen(GuiRenderer renderer) {
//		super(renderer);
//		Camera.position = new Vector3f(0, 1, 3);
//		Camera.rotation = new Vector3f(0, 0, 0);
//		Camera.update();
//		texture = Textures.WHITE_SQUARE;
//		
//		float[] vertices = new float[] {
//				-5.5f, 0.0f, -5.5f,
//				-5.5f, 0.0f, 5.5f,
//				5.5f, 0.0f, 5.5f,
//				5.5f, 0.0f, -5.5f
//		};
//		float[] texCoords = new float[] {
//				0, 0, 0, 1, 1, 1, 1, 0
//		};
//		int[] indices = new int[] {
//				0, 1, 2, 2, 3, 0
//		};
//		grid = new Mesh(vertices, texCoords, indices, Textures.GRID);
//		block = BlockBuilder.buildMesh(Tiles.GRASS, 0, -1, 0);
//	}
//	@Override
//	public void tick() {
//		
//		if (Settings.isMouseButtonDown(2)) {
//			Camera.position.add(Camera.getUp().mul((Mouse.lastY - Mouse.y) * 0.01f));
//			Camera.position.add(Camera.getRight().mul((Mouse.lastX - Mouse.x) * 0.01f));
//		}
//		if (Settings.USE.isPressed()) {
//			Camera.rotation.y += Mouse.x - Mouse.lastX;
//			Camera.rotation.x += Mouse.y - Mouse.lastY;
//		}
//		if (Settings.FORWARD.isPressed()) {
//			Camera.position.add(Camera.getForward().mul(0.01f).mul((float)FPSCounter.getDelta()));
//		}
//		if (Settings.BACKWARD.isPressed()) {
//			Camera.position.add(Camera.getForward().mul(-0.01f).mul((float)FPSCounter.getDelta()));
//		}
//		if (Settings.RIGHT.isPressed()) {
//			Camera.position.add(Camera.getRight().mul(0.01f).mul((float)FPSCounter.getDelta()));
//		}
//		if (Settings.LEFT.isPressed()) {
//			Camera.position.add(Camera.getRight().mul(-0.01f).mul((float)FPSCounter.getDelta()));
//		}
//		if (Settings.JUMP.isPressed()) {
//			Camera.position.add(0, 0.01f * (float)FPSCounter.getDelta(), 0);
//		}
//		if (Settings.SNEAK.isPressed()) {
//			Camera.position.add(0, -0.01f * (float)FPSCounter.getDelta(), 0);
//		}
//		
//		//max visible: 19
//		float barScale = 1;
//		if (this.model != null) {
//			barScale = (19.0f / this.model.model.parts.size());
//		}
//
//		float div = 50.0f;
//		
//		if (scrollGrabbed) {
//			if (Mouse.y > Mouse.lastY) {
//				this.scrollPercent += Math.abs(Mouse.y - Mouse.lastY) * barScale / div;
//			}
//			if (Mouse.y < Mouse.lastY) {
//				this.scrollPercent -= Math.abs(Mouse.y - Mouse.lastY) * barScale / div;
//			}
//		}
//		
//		if (scrollPercent > 1.0) {
//			scrollPercent = 1.0f;
//		}
//		
//		if (scrollPercent < 0) {
//			scrollPercent = 0;
//		}
//		
//		if (!Settings.isMouseButtonDown(0)) {
//			selectedAttribute = -1;
//			updated = false;
//		}
//		
//		if (this.model != null)
//		if (selectedAttribute != -1) {
//			//x, y, z, rx, ry, rz, sx, sy, sz, ox, oy, oz
//			float change = (Mouse.x - Mouse.lastX) * 0.05f;
//			
//			ModelPart part = this.model.model.parts.get(this.selectedPart);
//			ModelTransformation transform = part.transformation;
//
//			int dragDir = 0;
//			if (Mouse.x > clickX + 10) {
//				clickX = Mouse.x;
//				dragDir = 1;
//			}
//			if (Mouse.x < clickX - 10) {
//				clickX = Mouse.x;
//				dragDir = -1;
//			}
//			
//			boolean delete = Settings.DELETE.isJustPressed();
//			if (change != 0 || delete) {
//				if (updated == false || delete) {
//					this.startAction(part);
//					updated = true;
//				}
//			}
//			
//			switch (selectedAttribute) {
//			case 0:
//				if (Settings.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT)) {
//					transform.x += dragDir;
//					transform.x = (int)transform.x;
//				} else {
//					transform.x += change;
//					transform.x *= 100;
//					transform.x = (int)transform.x;
//					transform.x *= 0.01;
//				}
//				if (delete) {
//					transform.x = 0;
//				}
//				break;
//			case 1:
//				if (Settings.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT)) {
//					transform.y += dragDir;
//					transform.y = (int)transform.y;
//				} else {
//					transform.y += change;
//					transform.y *= 100;
//					transform.y = (int)transform.y;
//					transform.y *= 0.01;
//				}
//				if (delete) {
//					transform.y = 0;
//				}
//				break;
//			case 2:
//				if (Settings.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT)) {
//					transform.z += dragDir;
//					transform.z = (int)transform.z;
//				} else {
//					transform.z += change;
//					transform.z *= 100;
//					transform.z = (int)transform.z;
//					transform.z *= 0.01;
//				}
//				
//				if (delete) {
//					transform.z = 0;
//				}
//				break;
//			case 3:
//				transform.rotX += change * 10.0f;
//				transform.rotX *= 100;
//				transform.rotX = (int)transform.rotX;
//				transform.rotX *= 0.01;
//				if (delete) {
//					transform.rotX = 0;
//				}
//				break;
//			case 4:
//				transform.rotY += change * 10.0f;
//				transform.rotY *= 100;
//				transform.rotY = (int)transform.rotY;
//				transform.rotY *= 0.01;
//				if (delete) {
//					transform.rotY = 0;
//				}
//				break;
//			case 5:
//				transform.rotZ += change * 10.0f;
//				transform.rotZ *= 100;
//				transform.rotZ = (int)transform.rotZ;
//				transform.rotZ *= 0.01;
//				if (delete) {
//					transform.rotZ = 0;
//				}
//				break;
//			case 6:
//				transform.size_x += dragDir;
//				transform.size_x = (int)transform.size_x;
//				if (delete) {
//					transform.size_x = 0;
//				}
//				if (delete || change != 0) {
//					this.model.rebuild();
//				}
//				break;
//			case 7:
//				transform.size_y += dragDir;
//				transform.size_y = (int)transform.size_y;
//				if (delete) {
//					transform.size_y = 0;
//				}
//				if (delete || change != 0) {
//					this.model.rebuild();
//				}
//				break;
//			case 8:
//				transform.size_z += dragDir;
//				transform.size_z = (int)transform.size_z;
//				if (delete) {
//					transform.size_z = 0;
//				}
//				if (delete || change != 0) {
//					this.model.rebuild();
//				}
//				break;
//			case 9:
//				transform.offsX += change;
//				transform.offsX *= 100;
//				transform.offsX = (int)transform.offsX;
//				transform.offsX *= 0.01;
//				if (delete) {
//					transform.offsX = 0;
//				}
//				break;
//			case 10:
//				transform.offsY += change;
//				transform.offsY *= 100;
//				transform.offsY = (int)transform.offsY;
//				transform.offsY *= 0.01;
//				if (delete) {
//					transform.offsY = 0;
//				}
//				break;
//			case 11:
//				transform.offsZ += change;
//				transform.offsZ *= 100;
//				transform.offsZ = (int)transform.offsZ;
//				transform.offsZ *= 0.01;
//				if (delete) {
//					transform.offsZ = 0;
//				}
//				break;
//			}
//		}
//		
//		Mouse.lastX = Mouse.x;
//		Mouse.lastY = Mouse.y;
//	}
//
//	@Override
//	public void render(ShaderProgram shader) {
//		
//		double mx = Mouse.x * (1920.0 / Utils.FRAME_WIDTH);
//		double my = 1080 - Mouse.y * (1080.0 / Utils.FRAME_HEIGHT);
//		
//		boolean hovered = false;
//		if (mx > 1920 - 48 - 64 && mx < 1920 - 48 + 64 - 64) {
//			if (my > 32 && my < 32 + 64) {
//				hovered = true;
//			}
//		}
//		
//		boolean texLoadHovered = false;
//		
//		if (this.selectableString(Translation.translateText("Inignoto:gui.save_model"), 48 * 14, 1080 - 60, 1.5f, new Vector4f(1, 1, 1, 1), new Vector4f(0, 1, 0, 1), true)) {
//			if (this.model != null)
//			if (Settings.isMouseButtonJustDown(0)) {
//				JFileChooser fileChooser = new JFileChooser();
//				fileChooser.setCurrentDirectory(new File(lastLoadDir));
//				int result = fileChooser.showSaveDialog(null);
//				if (result == JFileChooser.APPROVE_OPTION) {
//					File selectedFile = fileChooser.getSelectedFile();
//					ModelLoader.saveModel(model, selectedFile);
//				}
//			}
//		}
//		this.selectableString(Translation.translateText("Inignoto:gui.save_texture"), 48 * 20, 1080 - 60, 1.5f, new Vector4f(1, 1, 1, 1), new Vector4f(0, 1, 0, 1), true);
//		
//		Vector4f undoColor = new Vector4f(1, 1, 1, 1);
//		Vector4f undoHover = new Vector4f(0, 1, 0, 1);
//		
//		Vector4f redoColor = new Vector4f(1, 1, 1, 1);
//		Vector4f redoHover = new Vector4f(0, 1, 0, 1);
//		
//		if (this.lastAction - 1 < -1) {
//			undoColor = new Vector4f(0.25f, 0.25f, 0.25f, 1.0f);
//			undoHover = new Vector4f(0.25f, 0.25f, 0.25f, 1.0f);
//		}
//		
//		if (this.lastAction == this.actions.size() - 1 || this.actions.size() == 0) {
//			redoColor = new Vector4f(0.25f, 0.25f, 0.25f, 1.0f);
//			redoHover = new Vector4f(0.25f, 0.25f, 0.25f, 1.0f);
//		}
//		
//		if (this.selectableString(Translation.translateText("Inignoto:gui.undo"), 48 * 33, 1080 - 60, 1.5f, undoColor, undoHover, true)) {
//			if (Settings.isMouseButtonJustDown(0)) {
//				undo();
//			}
//		}
//		if (this.selectableString(Translation.translateText("Inignoto:gui.redo"), 48 * 36, 1080 - 60, 1.5f, redoColor, redoHover, true)) {
//			if (Settings.isMouseButtonJustDown(0)) {
//				redo();
//			}
//		}
//
//		if (mx > 48 * 7 - 15 * 1.5f && my > 1080 - 60) {
//			if (mx < 48 * 7 + Translation.translateText("Inignoto:gui.load_texture").length() * 1.5f * 15 - 15 * 1.5f) {
//				if (my < 1080 - 30) {
//					texLoadHovered = true;
//				}
//			}
//		}
//		
//		
//		if (!texLoadHovered) {
//			this.renderer.drawString(Translation.translateText("Inignoto:gui.load_texture"), 48 * 7, 1080 - 60, 1.5f, new Vector4f(1, 1, 1, 1), true);
//		} else {
//			this.renderer.drawString(Translation.translateText("Inignoto:gui.load_texture"), 48 * 7, 1080 - 60, 1.5f, new Vector4f(0, 1, 0, 1), true);
//			if (Settings.isMouseButtonJustDown(0)) {
//				
//				JFileChooser fileChooser = new JFileChooser();
//				fileChooser.setCurrentDirectory(new File(lastLoadDir));
//				int result = fileChooser.showOpenDialog(null);
//				if (result == JFileChooser.APPROVE_OPTION) {
//					File selectedFile = fileChooser.getSelectedFile();
//					String f = selectedFile.getPath();
//					
//					System.out.println(f);
//					lastLoadDir = selectedFile.getParent();
//					try {
//						this.texture = new Texture(selectedFile);
//						if (this.model != null) {
//							this.model.texture = this.texture;
//							this.model.rebuild();
//						}
//					} catch (Exception e) {
//						JOptionPane.showMessageDialog(null, Translation.translateText("Inignoto:gui.not_valid_texture"));
//					}
//				}
//			}
//		}
//		
//		
//		boolean loadHovered = false;
//		if (mx > 28 && my < 1050) {
//			if (mx < 28 + Translation.translateText("Inignoto:gui.load_model").length() * 1.5f * 15 && my > 1028) {
//				loadHovered = true;
//			}
//		}
//		if (!loadHovered) {
//			this.renderer.drawString(Translation.translateText("Inignoto:gui.load_model"), 48, 1080 - 60, 1.5f, new Vector4f(1, 1, 1, 1), true);
//		} else {
//			this.renderer.drawString(Translation.translateText("Inignoto:gui.load_model"), 48, 1080 - 60, 1.5f, new Vector4f(0, 1, 0, 1), true);
//			if (Settings.isMouseButtonJustDown(0)) {
//				
//				JFileChooser fileChooser = new JFileChooser();
//				fileChooser.setCurrentDirectory(new File(lastLoadDir));
//				int result = fileChooser.showOpenDialog(null);
//				if (result == JFileChooser.APPROVE_OPTION) {
//					File selectedFile = fileChooser.getSelectedFile();
//					String f = selectedFile.getPath();
//					
//					System.out.println(f);
//					lastLoadDir = selectedFile.getParent();
//					try {
//						AnimModel model = ModelLoader.loadModelFromFile(selectedFile);
//						this.model = new CustomModel(model, texture);
//						this.modelDir = selectedFile;
//						this.lastAction = -1;
//						this.actions.clear();
//					} catch (Exception e) {
//						JOptionPane.showMessageDialog(null, Translation.translateText("Inignoto:gui.not_valid_model"));
//					}
//				}
//			}
//		}
//		
//		
//		if (mx >= 10 && my >= 10 && mx <= 58 && my <= 58) {
//			this.renderer.drawString(Translation.translateText("Inignoto:gui.new_cube"), (float)mx, (float)my, 1.5f, new Vector4f(1, 1, 1, 1), true);
//
//			this.renderer.drawNormalTexture(Textures.MODELER, 10, 10, 48, 48, 0, new Vector4f(0, 1, 0, 1));
//			if (Settings.isMouseButtonJustDown(0)) {
//				if (model == null) {
//					AnimModel anim = new AnimModel();
//					
//					String name = JOptionPane.showInputDialog("Input a name for the new part\n(no duplicate names)");
//					
//					while (name.isEmpty()) {
//						name = JOptionPane.showInputDialog("You cannot input an empty name!");
//					}
//					
//					ModelPart cube = new ModelPart(name);
//					cube.transformation = new ModelTransformation();
//					cube.transformation.size_x = 18.0f;
//					cube.transformation.size_y = 18.0f;
//					cube.transformation.size_z = 18.0f;
//					cube.transformation.y = 9.0f;
//					anim.parts.put(name, cube);
//					
//					model = new CustomModel(anim, Textures.WHITE_SQUARE);
//				} else {
//					String name = JOptionPane.showInputDialog("Input a name for the new part\n(no duplicate names)");
//					while(model.model.parts.containsKey(name)) {
//						name = JOptionPane.showInputDialog("This part name already exists!");
//					}
//					while (name.isEmpty()) {
//						name = JOptionPane.showInputDialog("You cannot input an empty name!");
//					}
//					ModelPart cube = new ModelPart(name);
//					cube.transformation = new ModelTransformation();
//					cube.transformation.size_x = 18.0f;
//					cube.transformation.size_y = 18.0f;
//					cube.transformation.size_z = 18.0f;
//					cube.transformation.y = 9.0f;
//					model.model.parts.put(name, cube);
//					this.model.rebuild();
//					
//				}
//				
//			}
//		} else {
//			this.renderer.drawNormalTexture(Textures.MODELER, 10, 10, 48, 48, 0, new Vector4f(1, 1, 1, 1));
//		}
//		
//		if (mx >= 10 && my >= 68 && mx <= 58 && my <= 68 + 48) {
//			this.renderer.drawString(Translation.translateText("Inignoto:gui.trash"), (float)mx, (float)my, 1.5f, new Vector4f(1, 1, 1, 1), true);
//			this.renderer.drawNormalTexture(Textures.TRASH, 10, 68, 48, 48, 0, new Vector4f(0, 1, 0, 1));
//			if (Settings.isMouseButtonJustDown(0)) {
//				if (this.model != null) {
//					if (this.model.model.parts.containsKey(this.selectedPart)) {
//						int confirm = JOptionPane.showConfirmDialog(null, "You are about to delete the part: \"" + this.selectedPart + "\", and all of its children.\n"
//								+ "This cannot be undone.  Are you sure you want to delete this part?");
//						if (confirm == 0) {
//							ModelPart part = this.model.model.parts.get(this.selectedPart);
//							deletePart(part);
//							this.model.rebuild();
//						}
//					}
//				}
//			}
//		} else {
//			this.renderer.drawNormalTexture(Textures.TRASH, 10, 68, 48, 48, 0, new Vector4f(1, 1, 1, 1));
//		}
//		if (mx >= 68 && my >= 10 && mx <= 68 + 48 && my <= 58) {
//			this.renderer.drawString(Translation.translateText("Inignoto:gui.duplicate"), (float)mx, (float)my, 1.5f, new Vector4f(1, 1, 1, 1), true);
//			this.renderer.drawNormalTexture(Textures.DUPLICATE, 68, 10, 48, 48, 0, new Vector4f(0, 1, 0, 1));
//
//			if (Settings.isMouseButtonJustDown(0)) {
//				if (this.model != null) {
//					if (this.model.model.parts.containsKey(this.selectedPart)) {
//						ModelPart part = this.model.model.parts.get(this.selectedPart);
//						String name = part.name;
//						int i = 1;
//						while (this.model.model.parts.containsKey(name)) {
//							i++;
//							name = part.name + "("+i+")";
//						}
//						ModelPart part2 = new ModelPart(name);
//						part2.transformation = new ModelTransformation(part.transformation);
//						this.model.model.parts.put(name, part2);
//						this.model.rebuild();
//					}
//				}
//			}
//			
//		} else {
//			this.renderer.drawNormalTexture(Textures.DUPLICATE, 68, 10, 48, 48, 0, new Vector4f(1, 1, 1, 1));
//		}
//		this.renderer.drawNormalTexture(Textures.POINTER, 68, 68, 48, 48, 0, new Vector4f(1, 1, 1, 1));
//
//		if (this.selectableString(Translation.translateText("Inignoto:gui.rename"), 48 * 3 + 10, 68, 1.5f, new Vector4f(1, 1, 1, 1), new Vector4f(0, 1, 0, 1), true)) {
//			if (Settings.isMouseButtonJustDown(0))
//			if (this.model != null) {
//				if (this.model.model.parts.containsKey(this.selectedPart)) {
//					ModelPart part = this.model.model.parts.get(this.selectedPart);
//					String name = JOptionPane.showInputDialog("Input a new name for the part");
//					while (this.model.model.parts.containsKey(name)) {
//						name = JOptionPane.showInputDialog("This name already exists!");
//					}
//					while (name.isEmpty()) {
//						name = JOptionPane.showInputDialog("You cannot input an empty name!");
//					}
//					this.model.model.parts.remove(part.name);
//					part.name = name;
//					this.selectedPart = name;
//					this.model.model.parts.put(part.name, part);
//					this.model.rebuild();
//					
//				}
//			}
//		}
//		
//		if (this.selectableString(Translation.translateText("Inignoto:gui.set_parent"), 48 * 3 + 10, 10, 1.5f, new Vector4f(1, 1, 1, 1), new Vector4f(0, 1, 0, 1), true)) {
//			if (Settings.isMouseButtonJustDown(0)) {
//				if (this.model != null) {
//					if (this.model.model.parts.containsKey(this.selectedPart)) {
//						ModelPart selected = this.model.model.parts.get(this.selectedPart);
//						
//						ArrayList<String> names = new ArrayList<String>();
//						names.add("\nNO PARENT\n");
//						for (String str : this.model.model.parts.keySet()) {
//							ModelPart part = this.model.model.parts.get(str);
//							if (part == selected) continue;
//							boolean canAdd = true;
//							ModelPart next = part.parent;
//							if (next != null) {
//								while (next != null) {
//									if (next == selected) {
//										canAdd = false;
//										break;
//									}
//									next = next.parent;
//								}
//							}
//							if (canAdd) {
//								names.add(part.name);
//							}
//						}
//						String input = (String)JOptionPane.showInputDialog(null, "Choose a new parent for the part.\nYou can not choose a part that is already a child object under the current part.\nSelecting a new parent for a part will reset the position and rotation for that part.", "Model Parent", JOptionPane.QUESTION_MESSAGE, null, names.toArray(), "NO PARENT\n=========");
//						if (input.equals("\nNO PARENT\n")) {
//							if (selected.parent != null) {
//								selected.parent.children.remove(selected);
//								selected.parent = null;
//								selected.transformation.x = 0;
//								selected.transformation.y = 0;
//								selected.transformation.z = 0;
//								
//								selected.transformation.rotX = 0;
//								selected.transformation.rotY = 0;
//								selected.transformation.rotZ = 0;
//							}
//						} else {
//							ModelPart newParent = this.model.model.parts.get(input);
//							if (selected.parent != null) {
//								selected.parent.children.remove(selected);
//								selected.parent = null;
//							}
//							selected.parent = newParent;
//							newParent.children.add(selected);
//							selected.transformation.x = 0;
//							selected.transformation.y = 0;
//							selected.transformation.z = 0;
//							
//							selected.transformation.rotX = 0;
//							selected.transformation.rotY = 0;
//							selected.transformation.rotZ = 0;
//						}
//					}
//					
//				}
//			}
//		}
//
//		if (!hovered) {
//			this.renderer.drawTexture(Textures.BACK_ARROW, 1920 - 48, 32, -64, 64, 0, new Vector4f(1, 1, 1, 1));
//		} else {
//			this.renderer.drawString(Translation.translateText("Inignoto:gui.back_to_main_menu"), (float)mx - Translation.translateText("Inignoto:gui.back_to_main_menu").length() * 15 * 2, (float)my, 2, new Vector4f(1, 1, 1, 1), true);
//
//			this.renderer.drawTexture(Textures.BACK_ARROW, 1920 - 48, 32, -64, 64, 0, new Vector4f(0, 1, 0, 1));
//			if (Settings.isMouseButtonJustDown(0)) {
//				this.renderer.openScreen(new MenuScreen(renderer));
//			}
//		}
//		
//		//max visible: 19
//		float barScale = 1;
//		if (this.model != null) {
//			barScale = (19.0f / this.model.model.parts.size());
//		}
//		this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, 0, 1080, 1920, -64, 0, new Vector4f(0.7f,0.7f, 0.7f, 1.0f));
//		this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, 0, 1080, 1920, -64 - 10, 0, new Vector4f(0.2f, 0.2f, 0.2f, 1.0f));
//
//		this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, 0, 0, 1920, 125, 0, new Vector4f(0.7f,0.7f, 0.7f, 1.0f));
//		this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, 0, 0, 1920, 125 + 10, 0, new Vector4f(0.2f,0.2f, 0.2f, 1.0f));
//
//		float barLeft = (1000 - 125) - (1000 - 125) * barScale;
//		
//		Vector3f grabColor = new Vector3f(0.7f, 0.7f, 0.7f);
//		if (mx > 1920 - 64 + 16 && mx < 1920 - 64 + 16 + 32) {
//			if (my < 1000 - barLeft * scrollPercent) {
//				if (my > 1000 - barLeft * scrollPercent - (1000 - 125) * barScale) {
//					grabColor = new Vector3f(1.0f, 1.0f, 1.0f);
//					if (Settings.isMouseButtonJustDown(0)) {
//						scrollGrabbed = true;
//					}
//				}
//			}
//		}
//		
//		
//		if (this.model != null)
//			this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, 1920 - 64 + 16, 1000 - barLeft * scrollPercent, 32, -(1080 - 80 - 125) * barScale, 0, new Vector4f(grabColor.x, grabColor.y, grabColor.z, 1.0f));
//
//		
//		
//		this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, 1920 - 64, 0, 64, 1080, 0, new Vector4f(0.2f, 0.2f, 0.2f, 1.0f));
//		
//		if (this.model != null) {
//			HashMap<String, ModelPart> parts = this.model.model.parts;
//			sort(parts);
//			float length = (parts.size() * (30 + 15));
//			float view = (19 * (30 + 15));
//			float div = (length - view);
//			if (length - view < 0) {
//				div = 0;
//			}
//			int i = 0;
//			
//			if (this.sortedParts != null)
//			for (PartHolder holder : this.sortedParts) {
//				
//				
//				if (holder.part != null) {
//					
//					Vector3f color = new Vector3f(1, 1, 1);
//					if (selectedPart.equals(holder.part.name)) {
//						color = new Vector3f(1, 1, 0);
//					}
//					float X = sidebarX + 32 + holder.parents * 15;
//					float Y = 1000 - (i * (30 + 15)) - 30 + div * scrollPercent;
//					float scale = 1.5f;
//					if (mx > sidebarX) {
//						if (my > Y) {
//							if (mx < X + holder.part.name.length() * 15 * scale - 15 * scale) {
//								if (my < Y + 30 * scale) {
//									color = new Vector3f(0, 1, 1);
//									if (Settings.isMouseButtonJustDown(0)) {
//										if (this.model.extraMeshes.get(selectedPart) != null) {
//											this.model.extraMeshes.get(selectedPart).remove(this.selectionMesh);
//										}
//										if (this.selectionMesh != null) {
//											this.model.extraScale.remove(this.selectionMesh);
//										}
//										this.selectionMesh = null;
//										selectedPart = holder.part.name;
//										this.selectionMesh = CustomModel.buildMesh(holder.part, Textures.TILE_SELECTION, 
//												new float[] {
//													0, 0,
//													0, 1,
//													1, 1,
//													1, 0,
//													
//													0, 0,
//													0, 1,
//													1, 1,
//													1, 0,
//													
//													0, 0,
//													0, 1,
//													1, 1,
//													1, 0,
//													
//													0, 0,
//													0, 1,
//													1, 1,
//													1, 0,
//													
//													0, 0,
//													0, 1,
//													1, 1,
//													1, 0,
//													
//													0, 0,
//													0, 1,
//													1, 1,
//													1, 0
//												},
//												1.0f
//												);
//										this.model.extraMeshes.get(selectedPart).add(this.selectionMesh);
//										this.model.extraScale.put(this.selectionMesh, 1.1f);
//									}
//								}
//							}
//						}
//					}
//					
//					this.renderer.drawString(holder.part.name, X, Y, scale, new Vector4f(color.x, color.y, color.z, 1), true);
//					
//					if (i == sortedParts.size() - 1) {
//						this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, sidebarX, Y - 50, X + holder.part.name.length() * 15 * scale - 15 * scale, 10 + 35, 0, new Vector4f(0.3f, 0.3f, 0.3f, 1.0f));
//
//						this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, sidebarX, Y - 10, X + holder.part.name.length() * 15 * scale - 15 * scale, 10, 0, new Vector4f(0.3f, 0.3f, 0.3f, 1.0f));
//
//					} else {
//						this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, sidebarX, Y - 10, X + holder.part.name.length() * 15 * scale - 15 * scale, 10, 0, new Vector4f(0.3f, 0.3f, 0.3f, 1.0f));
//					}
//					
//				}
//				
//				i++;
//			}
//		}
//		
//		this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, sidebarX, 125, 1920, 1080 - 125, 0, new Vector4f(0.5f, 0.5f, 0.5f, 1.0f));
//		
//		
//		this.renderer.drawNormalTexture(Textures.BACK_ARROW, sidebarX - 20, 1080 / 2 - 32, 16, 32, 0, new Vector4f(1f, 1f, 1f, 1.0f));
//
//		if (mx >= sidebarX - 20 && mx <= sidebarX) {
//			if (my <= 1008 && my >= 125) {
//				
//				if (Settings.isMouseButtonJustDown(0)) {
//					this.sidebarSelected = true;
//				}
//				
//			}
//		}
//		Vector3f color = new Vector3f(0.3f, 0.3f, 0.3f);
//		if (this.sidebarSelected == true) {
//			color = new Vector3f(0.7f, 0.7f, 0.7f);
//			sidebarX = (int) (mx + 10);
//			if (sidebarX > 1920 - 100) {
//				sidebarX = 1920 - 100;
//				color = new Vector3f(0.7f, 0.0f, 0.0f);
//			}
//			if (sidebarX < 1920 - 900) {
//				sidebarX = 1920 - 900;
//				color = new Vector3f(0.7f, 0.0f, 0.0f);
//			}
//		}
//		if (!Settings.isMouseButtonDown(0)) {
//			this.sidebarSelected = false;
//			this.scrollGrabbed = false;
//		}
//		
//		this.renderer.drawNormalTexture(Textures.WHITE_SQUARE, sidebarX - 20, 125, 1920, 1080 - 125, 0, new Vector4f(color.x, color.y, color.z, 1.0f));
//		
//		if (this.model != null)
//		if (this.model.model.parts.containsKey(selectedPart)) {
//			AnimModel model = this.model.model;
//			
//			if (Settings.isMouseButtonJustDown(0)) {
//				clickX = Mouse.x;
//				clickY = Mouse.y;
//				if (selectableString("X: " + model.parts.get(selectedPart).transformation.x, 50, 950, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//					selectedAttribute = 0;
//				}
//				if (selectableString("Y: " + model.parts.get(selectedPart).transformation.y, 50, 950 - 30 * 1.5f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//					selectedAttribute = 1;
//				}
//				if (selectableString("Z: " + model.parts.get(selectedPart).transformation.z, 50, 950 - 30 * 1.5f * 2.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//					selectedAttribute = 2;
//				}
//				if (selectableString(Translation.translateText("Inignoto:gui.rotationX")+": " + model.parts.get(selectedPart).transformation.rotX, 50, 950 - 30 * 1.5f * 3.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//					selectedAttribute = 3;
//				}
//				if (selectableString(Translation.translateText("Inignoto:gui.rotationY")+": " + model.parts.get(selectedPart).transformation.rotY, 50, 950 - 30 * 1.5f * 4.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//					selectedAttribute = 4;
//				}
//				if (selectableString(Translation.translateText("Inignoto:gui.rotationZ")+": " + model.parts.get(selectedPart).transformation.rotZ, 50, 950 - 30 * 1.5f * 5.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//					selectedAttribute = 5;
//				}
//				if (selectableString(Translation.translateText("Inignoto:gui.scaleX")+": " + model.parts.get(selectedPart).transformation.size_x, 50, 950 - 30 * 1.5f * 6.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//					selectedAttribute = 6;
//				}
//				if (selectableString(Translation.translateText("Inignoto:gui.scaleY")+": " + model.parts.get(selectedPart).transformation.size_y, 50, 950 - 30 * 1.5f * 7.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//					selectedAttribute = 7;
//				}
//				if (selectableString(Translation.translateText("Inignoto:gui.scaleZ")+": " + model.parts.get(selectedPart).transformation.size_z, 50, 950 - 30 * 1.5f * 8.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//					selectedAttribute = 8;
//				}
//				if (selectableString(Translation.translateText("Inignoto:gui.offsetX")+": " + model.parts.get(selectedPart).transformation.offsX, 50, 950 - 30 * 1.5f * 9.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//					selectedAttribute = 9;
//				}
//				if (selectableString(Translation.translateText("Inignoto:gui.offsetY")+": " + model.parts.get(selectedPart).transformation.offsY, 50, 950 - 30 * 1.5f * 10.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//					selectedAttribute = 10;
//				}
//				if (selectableString(Translation.translateText("Inignoto:gui.offsetZ")+": " + model.parts.get(selectedPart).transformation.offsZ, 50, 950 - 30 * 1.5f * 11.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//					selectedAttribute = 11;
//				}
//			} else {
//				if (selectableString("X: " + model.parts.get(selectedPart).transformation.x, 50, 950, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//				}
//				if (selectableString("Y: " + model.parts.get(selectedPart).transformation.y, 50, 950 - 30 * 1.5f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//				}
//				if (selectableString("Z: " + model.parts.get(selectedPart).transformation.z, 50, 950 - 30 * 1.5f * 2.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//				}
//				if (selectableString(Translation.translateText("Inignoto:gui.rotationX")+": " + model.parts.get(selectedPart).transformation.rotX, 50, 950 - 30 * 1.5f * 3.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//				}
//				if (selectableString(Translation.translateText("Inignoto:gui.rotationY")+": " + model.parts.get(selectedPart).transformation.rotY, 50, 950 - 30 * 1.5f * 4.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//				}
//				if (selectableString(Translation.translateText("Inignoto:gui.rotationZ")+": " + model.parts.get(selectedPart).transformation.rotZ, 50, 950 - 30 * 1.5f * 5.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//				}
//				if (selectableString(Translation.translateText("Inignoto:gui.scaleX")+": " + model.parts.get(selectedPart).transformation.size_x, 50, 950 - 30 * 1.5f * 6.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//				}
//				if (selectableString(Translation.translateText("Inignoto:gui.scaleY")+": " + model.parts.get(selectedPart).transformation.size_y, 50, 950 - 30 * 1.5f * 7.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//				}
//				if (selectableString(Translation.translateText("Inignoto:gui.scaleZ")+": " + model.parts.get(selectedPart).transformation.size_z, 50, 950 - 30 * 1.5f * 8.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//				}
//				if (selectableString(Translation.translateText("Inignoto:gui.offsetX")+": " + model.parts.get(selectedPart).transformation.offsX, 50, 950 - 30 * 1.5f * 9.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//				}
//				if (selectableString(Translation.translateText("Inignoto:gui.offsetY")+": " + model.parts.get(selectedPart).transformation.offsY, 50, 950 - 30 * 1.5f * 10.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//				}
//				if (selectableString(Translation.translateText("Inignoto:gui.offsetZ")+": " + model.parts.get(selectedPart).transformation.offsZ, 50, 950 - 30 * 1.5f * 11.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f), true)) {
//				}
//			}
//			if (model.parts.get(selectedPart).parent != null) {
//				this.renderer.drawString(Translation.translateText("Inignoto:gui.parent")+": " + model.parts.get(selectedPart).parent.name, 50, 950 - 30 * 1.5f * 12.0f, 1.5f, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), true);
//			} else {
//				this.renderer.drawString(Translation.translateText("Inignoto:gui.no_parent"), 50, 950 - 30 * 1.5f * 12.0f, 1.5f, new Vector4f(0f, 0f, 0f, 1.0f), true);
//			}
//		}
//		
//		if (Settings.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT) && Settings.DELETE.isJustPressed()) {
//			if (this.model != null) {
//				if (this.model.model.parts.containsKey(this.selectedPart)) {
//					int confirm = JOptionPane.showConfirmDialog(null, "You are about to delete the part: \"" + this.selectedPart + "\", and all of its children.\n"
//							+ "This cannot be undone.  Are you sure you want to delete this part?");
//					if (confirm == 0) {
//						ModelPart part = this.model.model.parts.get(this.selectedPart);
//						deletePart(part);
//						this.model.rebuild();
//					}
//				}
//			}
//		}
//		
//		if (Settings.EXIT.isJustPressed()) {
//			if (this.model.extraMeshes.get(selectedPart) != null) {
//				this.model.extraMeshes.get(selectedPart).remove(this.selectionMesh);
//			}
//			if (this.selectionMesh != null) {
//				this.model.extraScale.remove(this.selectionMesh);
//			}
//			this.selectionMesh = null;
//			selectedPart = "";
//			
//		}
//	}
//	
//	
//	private void deletePart(ModelPart part) {
//		this.model.model.parts.remove(part.name);
//		if (part.parent != null) {
//			part.parent.children.remove(part);
//		}
//		for (int i = 0; i < part.children.size(); i++) {
//			deletePart(part.children.get(i));
//		}
//		part.children.clear();
//	}
//	
//	public boolean selectableString(String str, float x, float y, float size, Vector4f color, Vector4f selectedColor, boolean bold) {
//		double mx = Mouse.x * (1920.0 / Utils.FRAME_WIDTH);
//		double my = 1080 - Mouse.y * (1080.0 / Utils.FRAME_HEIGHT);
//		
//		if (mx > x - 15 * size && mx < x + str.length() * 15 * size - 15 * size) {
//			
//			if (my > y && my < y + 30 * size) {
//				this.renderer.drawString(str, x, y, size, selectedColor, bold);
//
//				return true;
//			}
//		}
//		this.renderer.drawString(str, x, y, size, color, bold);
//		return false;
//	}
	
	public ModelerScreen(GuiRenderer renderer) {
		super(renderer);
	}

	public void render3D(ShaderProgram shaderProgram) {
		
//		if (model != null) {
//			model.render(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), shaderProgram);
//		}
//		for (int x = -floorSize; x < floorSize + 1; x++) {
//			for (int z = -floorSize; z < floorSize + 1; z++) {
//				if (Point.distance(x, z, 0, 0) <= floorSize - 0.5f) {
//					MeshRenderer.renderMesh(block, new Vector3f(-0.5f + x, 0, -0.5f + z), shaderProgram);
//				}
//			}
//		}
//		
//		MeshRenderer.renderMesh(grid, new Vector3f(0, 0.0001f, 0), shaderProgram);
		

	}

//	@Override
//	public void close() {
//		if (model != null) {
//			model.dispose();
//		}
//		if (texture != null) {
//			texture.dispose();
//		}
//	}
//	
//	private void sort(HashMap<String, ModelPart> parts) {
//		
//		this.sortedParts = new ArrayList<PartHolder>();
//		for (String str : parts.keySet()) {
//			ModelPart part = parts.get(str);
//			if (part.parent == null) {
//				add(part);
//			}
//			
//		}
//	}
//	
//	private void add(ModelPart part) {
//		this.sortedParts.add(new PartHolder(part));
//		if (part.children != null) {
//			if (part.children.size() > 0) {
//				for (int i = 0; i < part.children.size(); i++) {
//					add(part.children.get(i));
//				}
//			}
//		}
//	}
}
