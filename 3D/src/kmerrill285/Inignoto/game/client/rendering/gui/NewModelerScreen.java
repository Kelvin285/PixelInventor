package kmerrill285.Inignoto.game.client.rendering.gui;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.liquidengine.legui.animation.AnimatorProvider;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.component.ImageView;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.TextAreaField;
import org.liquidengine.legui.component.Widget;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.component.optional.align.VerticalAlign;
import org.liquidengine.legui.event.FocusEvent;
import org.liquidengine.legui.event.KeyEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.image.LoadableImage;
import org.liquidengine.legui.image.loader.DefaultImageLoader;
import org.liquidengine.legui.input.Mouse.MouseButton;
import org.liquidengine.legui.listener.FocusEventListener;
import org.liquidengine.legui.listener.KeyEventListener;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.listener.processor.EventProcessorProvider;
import org.liquidengine.legui.style.Style.DisplayType;
import org.liquidengine.legui.style.Style.PositionType;
import org.liquidengine.legui.style.font.TextDirection;
import org.liquidengine.legui.system.context.CallbackKeeper;
import org.liquidengine.legui.system.context.Context;
import org.liquidengine.legui.system.context.DefaultCallbackKeeper;
import org.liquidengine.legui.system.handler.processor.SystemEventProcessor;
import org.liquidengine.legui.system.handler.processor.SystemEventProcessorImpl;
import org.liquidengine.legui.system.layout.LayoutManager;
import org.liquidengine.legui.system.renderer.Renderer;
import org.liquidengine.legui.system.renderer.nvg.NvgRenderer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL40;

import custom_models.Model;
import custom_models.Part;
import kmerrill285.Inignoto.events.Events;
import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.Mouse;
import kmerrill285.Inignoto.game.client.rendering.Mesh;
import kmerrill285.Inignoto.game.client.rendering.MeshRenderer;
import kmerrill285.Inignoto.game.client.rendering.chunk.BlockBuilder;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.textures.Texture;
import kmerrill285.Inignoto.game.client.rendering.textures.Textures;
import kmerrill285.Inignoto.game.settings.Settings;
import kmerrill285.Inignoto.game.settings.Translation;
import kmerrill285.Inignoto.game.tile.Tiles;
import kmerrill285.Inignoto.resources.FPSCounter;
import kmerrill285.Inignoto.resources.Utils;
import kmerrill285.Inignoto.resources.raytracer.RayBox;
import kmerrill285.Inignoto.resources.raytracer.RayIntersection;
import kmerrill285.Inignoto.resources.raytracer.Raytracer;

public class NewModelerScreen extends ModelerScreen {
	
	public Texture texture;
	public String lastLoadDir;
	public int floorSize = 1;
	public Mesh block;
	public Mesh cube;
	public Mesh grid;
	public Part selectedPart;
	public Mesh selectionMesh;
	public Mesh rotationHandle;
	
	public Part handle = new Part();
	
	public Vector3f mousePos3D = new Vector3f(0, 0, 0);
	public Vector3f lastMousePos3D = new Vector3f(0, 0, 0);
	
	public boolean Xselected, Yselected, Zselected;
	
	public enum SelectionMode {
		TRANSLATION, ROTATION, SCALE, SIZE
	}
	
	public SelectionMode selectionMode = SelectionMode.TRANSLATION;
	
	
	public class MouseIntersection {
		Vector3f hit;
		float distance;
		public MouseIntersection(Vector3f hit, float distance) {
			this.hit = hit;
			this.distance = distance;
		}
	}
	
	private Context context = null;
	private Renderer nRenderer = null;
	private Frame frame = null;
	private SystemEventProcessor  systemEventProcessor = null;
	private Widget widget;
	private Panel panel;
	private Panel navigation;
	private Button FILE;
	private Button EDIT;
	
	private Panel FILE_PANEL;
	private Panel PROPERTIES_PANEL;
	private Panel EDIT_PANEL;
	
    boolean isVersionNew;
    
    private Model model;
    
    private boolean mouseJustDown = false;
	
	public NewModelerScreen(GuiRenderer gui) {
		super(gui);
		texture = Textures.GRAY_MATERIAL;
		Camera.position = new Vector3f(0, 1, 3);
		Camera.rotation = new Vector3f(0, 0, 0);
		Camera.update();
		
		model = new Model();
		
		Part part = new Part();
		part.size = new Vector3i(32, 32, 32);
		part.scale = new Vector3f(1, 1, 1);
		part.name = "Part";
		part.position.y = 16.0f;
		part.buildPart(Textures.GRAY_MATERIAL);
		model.getParts().add(part);
		
		handle = new Part();
		handle.size = new Vector3i(32, 32, 0);
		handle.scale = new Vector3f(1.0f / 16.0f, 1 / 16.0f, 1);
		handle.name = "Cube";
		handle.uv.x = 8;
		handle.buildPart(Textures.WHITE_SQUARE);
		
		createGrid();
		block = BlockBuilder.buildMesh(Tiles.GRASS, 0, -1, 0);
		cube = BlockBuilder.buildMesh(Tiles.GRASS, 0, 0, 0);
		rotationHandle = BlockBuilder.buildMesh(Tiles.GRASS, -0.5f, -0.5f, -0.5f);

		frame = new Frame(1920, 1080);
		
		context = new Context(Utils.window);
		
		CallbackKeeper keeper = new DefaultCallbackKeeper();
		CallbackKeeper.registerCallbacks(Utils.window, keeper);
		
        GLFWKeyCallbackI glfwKeyCallbackI = (w1, key, code, action, mods) -> {
        		
        };
        keeper.getChainKeyCallback().add(glfwKeyCallbackI);
        
        systemEventProcessor = new SystemEventProcessorImpl();
        SystemEventProcessor.addDefaultCallbacks(keeper, systemEventProcessor);
        
        nRenderer = new NvgRenderer();
        nRenderer.initialize();
        
        
        isVersionNew = (GL40.glGetInteger(GL40.GL_MAJOR_VERSION) > 3) || (GL40.glGetInteger(GL40.GL_MAJOR_VERSION) == 3 && GL40.glGetInteger(GL40.GL_MINOR_VERSION) >= 2);

        

        widget = new Widget(0, 0, 1920, 1080);

        widget.setCloseable(false);

        widget.setMinimizable(false);
        widget.setTitleEnabled(false);
		widget.setResizable(false);
		widget.getStyle().getBackground().setColor(0.1f, 0.1f, 0.1f, 0.0f);
		widget.getContainer().getStyle().getBackground().setColor(0, 0, 0, 0);
		
        widget.getContainer().getStyle().setDisplay(DisplayType.FLEX);

        


       

        panel = new Panel();
        panel.setPosition(0, 0);
        panel.getStyle().getBackground().setColor(0.1f, 0.1f, 0.1f, 0.0f);
        panel.getStyle().getFlexStyle().setFlexGrow(1);
        panel.getStyle().setMargin(10f);
        panel.getStyle().setMinimumSize(1920, 30);
        panel.getStyle().setBorderRadius(0);
        
        panel.getListenerMap().addListener(KeyEvent.class, (KeyEventListener) event -> {
        	Events.keyCallback(event.getContext().getGlfwWindow(), event.getKey(), event.getScancode(), event.getAction(), event.getMods());
        });
        
        panel.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
			Events.mouseClick(event.getContext().getGlfwWindow(), event.getButton().getCode(), event.getButton().isPressed() ? 1 : 0, event.getButton() == MouseButton.MOUSE_BUTTON_UNKNOWN ? 1 : 0);
        });
        
        
        navigation = new Panel();
        navigation.setPosition(0, 0);
        navigation.getStyle().getBackground().setColor(0.4f, 0.4f, 0.4f, 1.0f);
        navigation.getStyle().getFlexStyle().setFlexGrow(0);
        navigation.getStyle().setMargin(0);
        navigation.getStyle().setBorderRadius(0);
        navigation.setFocusable(false);
        
        FILE = new Button(Translation.translateText("Inignoto:gui.file"));
        FILE.setTextDirection(TextDirection.HORIZONTAL);
        FILE.setSize(60, 20);
        FILE.setPosition(0, 0);
        FILE.getStyle().setFontSize(20f);
        FILE.getStyle().getBackground().setColor(0, 0, 0, 0);
        FILE.getStyle().getShadow().setSpread(0);
        FILE.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        FILE.getStyle().setTextColor(1, 1, 1, 1);
        
        EDIT = new Button(Translation.translateText("Inignoto:gui.edit"));
        EDIT.setTextDirection(TextDirection.HORIZONTAL);
        EDIT.setSize(60, 20);
        EDIT.setPosition(60, 0);
        EDIT.getStyle().setFontSize(20f);
        EDIT.getStyle().getBackground().setColor(0, 0, 0, 0);
        EDIT.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        EDIT.getStyle().setTextColor(1, 1, 1, 1);
        
        FILE_PANEL = new Panel();
        createFilePanel(FILE_PANEL);
        
        EDIT_PANEL = new Panel();
        createEditPanel(EDIT_PANEL);
        
        
        
//            widget.getContainer().add(imageView);
        widget.getContainer().add(panel);
        widget.getContainer().add(navigation);
        navigation.add(FILE);
        navigation.add(EDIT);
        FILE.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {

            if (event.getAction().equals(MouseClickEvent.MouseClickAction.CLICK)) {
            	if (panel.contains(FILE_PANEL)) {
            		panel.remove(FILE_PANEL);
            	} else {
            		panel.add(FILE_PANEL);
            	}
            }

        });
        
        EDIT.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {

            if (event.getAction().equals(MouseClickEvent.MouseClickAction.CLICK)) {
            	if (panel.contains(EDIT_PANEL)) {
            		panel.remove(EDIT_PANEL);
            	} else {
            		panel.add(EDIT_PANEL);
            	}
            }

        });
        
        
        
        panel.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {
        	if (panel.isFocused()) {
        		panel.remove(FILE_PANEL);
        		panel.remove(EDIT_PANEL);
        	}
        });
        

       createPropertiesPanel();
       createPartsPanel();
        

        frame.getContainer().add(widget);
	}
	
	private Panel PARTS_PANEL;
	private Label parts_panel_name;
	private ScrollablePanel parts_scroll_panel;
	public void createPartsPanel() {
		PARTS_PANEL = new Panel();
		PARTS_PANEL.setSize(120, 160);
		PARTS_PANEL.setPosition(300, 100);
		PARTS_PANEL.getStyle().getBackground().setColor(0.3f, 0.3f, 0.3f, 1.0f);
        
		parts_panel_name = new Label(Translation.translateText("Inignoto:gui.parts"));
		parts_panel_name.setSize(120, 20);
		parts_panel_name.getStyle().getBackground().setColor(0.4f, 0.4f, 0.4f, 1.0f);
		parts_panel_name.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
		parts_panel_name.getStyle().setTextColor(1.0f, 1.0f, 1.0f, 1.0f);
		PARTS_PANEL.add(parts_panel_name);
		
		 parts_scroll_panel = new ScrollablePanel();
		 parts_scroll_panel.setHorizontalScrollBarVisible(false);
         parts_scroll_panel.getContainer().setSize(PARTS_PANEL.getSize().x + 20, 9000);
         parts_scroll_panel.setSize(PARTS_PANEL.getSize().x, PARTS_PANEL.getSize().y - 20);
         parts_scroll_panel.getContainer().getStyle().getBackground().setColor(0.3f, 0.3f, 0.3f, 1);
         parts_scroll_panel.getStyle().getBackground().setColor(0.3f, 0.3f, 0.3f, 1);

         parts_scroll_panel.setPosition(0, 20);
         parts_scroll_panel.getStyle().setPosition(PositionType.RELATIVE);
         parts_scroll_panel.setAutoResize(true);
         PARTS_PANEL.add(parts_scroll_panel);
        
        parts_panel_name.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
			Events.mouseClick(event.getContext().getGlfwWindow(), event.getButton().getCode(), event.getButton().isPressed() ? 1 : 0, event.getButton() == MouseButton.MOUSE_BUTTON_UNKNOWN ? 1 : 0);
        });
        
        panel.add(PARTS_PANEL);
        
        refreshPartsPanel();
	}
	
	public void refreshPartsPanel() {
		parts_scroll_panel.getContainer().clearChildComponents();
		ArrayList<Part> parts = this.model.getParts();
		int y = 0;
		for (Part part : parts) {
			if (part.parent == null) {
				y = addPartToPanel(part, 0, y);
			}
		}
	}
	
	public int addPartToPanel(Part part, int x, int y) {
		Button button = new Button(part.name);
		button.setSize(9000, 30);
		button.setPosition(x * 30, y * 32);
		button.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 1.0f);
		button.getStyle().setTextColor(1.0f, 1.0f, 1.0f, 1.0f);
		button.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
		button.getStyle().setFontSize(20.0f);
		
		button.getTextState().setText(part.name);
		
		button.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
				if (event.getButton() == MouseButton.MOUSE_BUTTON_LEFT) {
					this.selectPart(part);
				}
	        });
		
		parts_scroll_panel.getContainer().add(button);
		
		int y2 = y + 1;
		if (part.children.size() > 0) {
			for (Part p : part.children) {
				y2 = addPartToPanel(p, x + 1, y2);
			}
		}
		y = y2 - 1;
		return y + 1;
	}
	
	private Label properties_panel_name;
	private TextAreaField part_name;
	private TextAreaField xPos;
	private TextAreaField yPos;
	private TextAreaField zPos;
	private TextAreaField xRot;
	private TextAreaField yRot;
	private TextAreaField zRot;
	private TextAreaField xScale;
	private TextAreaField yScale;
	private TextAreaField zScale;
	private TextAreaField xSize;
	private TextAreaField ySize;
	private TextAreaField zSize;
	private TextAreaField xCenter;
	private TextAreaField yCenter;
	private TextAreaField zCenter;

	private ScrollablePanel properties_scroll_panel;
	
	
	public void createPropertiesPanel() {
		PROPERTIES_PANEL = new Panel();
        PROPERTIES_PANEL.setSize(120, 160);
        PROPERTIES_PANEL.setPosition(0, 100);
        PROPERTIES_PANEL.getStyle().getBackground().setColor(0.3f, 0.3f, 0.3f, 1.0f);
        
        properties_panel_name = new Label(Translation.translateText("Inignoto:gui.properties"));
        properties_panel_name.setSize(120, 20);
        properties_panel_name.getStyle().getBackground().setColor(0.4f, 0.4f, 0.4f, 1.0f);
        properties_panel_name.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
        properties_panel_name.getStyle().setTextColor(1.0f, 1.0f, 1.0f, 1.0f);
        PROPERTIES_PANEL.add(properties_panel_name);
        
        properties_panel_name.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
			Events.mouseClick(event.getContext().getGlfwWindow(), event.getButton().getCode(), event.getButton().isPressed() ? 1 : 0, event.getButton() == MouseButton.MOUSE_BUTTON_UNKNOWN ? 1 : 0);
        });
        
        
        Label part_name_label = new Label(Translation.translateText("Inignoto:gui.part_name"));
        part_name_label.setSize(PROPERTIES_PANEL.getSize().x, 20);
        part_name_label.setPosition(0, 0);
        part_name_label.getStyle().setTextColor(1, 1, 1, 1);
        part_name_label.getStyle().setFontSize(20.0f);
        
        
        properties_scroll_panel = new ScrollablePanel();
        properties_scroll_panel.setHorizontalScrollBarVisible(false);
        properties_scroll_panel.getContainer().setSize(PROPERTIES_PANEL.getSize().x + 20, 300);
        properties_scroll_panel.setSize(PROPERTIES_PANEL.getSize().x, PROPERTIES_PANEL.getSize().y - 20);
        properties_scroll_panel.getContainer().getStyle().getBackground().setColor(0.3f, 0.3f, 0.3f, 1);
        properties_scroll_panel.getStyle().getBackground().setColor(0, 0, 0, 1);

        properties_scroll_panel.setPosition(0, 20);
        properties_scroll_panel.getStyle().setPosition(PositionType.RELATIVE);
        properties_scroll_panel.setAutoResize(true);
        PROPERTIES_PANEL.add(properties_scroll_panel);
        
        properties_scroll_panel.getContainer().add(part_name_label);
        part_name = new TextAreaField(Translation.translateText("Inignoto:gui.no_part"));
        part_name.setSize(PROPERTIES_PANEL.getSize().x, 20);
        part_name.setPosition(0, 20);
        part_name.setEditable(false);
        part_name.getStyle().setTextColor(1, 1, 1, 1);
        part_name.getStyle().setFontSize(20.0f);
        part_name.getStyle().setVerticalAlign(VerticalAlign.TOP);
        part_name.getStyle().setPadding(0);
        part_name.getStyle().setMaxHeight(20);
        part_name.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 1);
        
        part_name.getListenerMap().addListener(KeyEvent.class, (KeyEventListener) event -> {
        	if (this.selectedPart != null)
        	this.selectedPart.name = this.part_name.getTextState().getText();
        	this.refreshPartsPanel();
        });
        properties_scroll_panel.getContainer().add(part_name);

        Label position_label = new Label(Translation.translateText("Inignoto:gui.position"));
        position_label.setPosition(0, 20 * 3 + 5);
        position_label.getStyle().setTextColor(1, 1, 1, 1);
        position_label.getStyle().setFontSize(20.0f);
        properties_scroll_panel.getContainer().add(position_label);
        Label x_label = new Label("X");
        x_label.setPosition(0, 20 * 4 + 4);
        x_label.getStyle().setTextColor(1, 1, 1, 1);
        x_label.getStyle().setFontSize(20.0f);
        properties_scroll_panel.getContainer().add(x_label);
       
        

        xPos = new TextAreaField("0");
        xPos.setSize(PROPERTIES_PANEL.getSize().x, 20);
        xPos.setPosition(15, 20 * 4);
        xPos.setEditable(false);
        xPos.getStyle().setTextColor(1, 1, 1, 1);
        xPos.getStyle().setFontSize(20.0f);
        xPos.getStyle().setVerticalAlign(VerticalAlign.TOP);
        xPos.getStyle().setPadding(0);
        xPos.getStyle().setMaxHeight(20);
        xPos.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 1);
        xPos.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {
        	if (selectedPart != null) {
        		try {
            		float f = Float.parseFloat(xPos.getTextState().getText());
            		selectedPart.position.x = f;
            	} catch (Exception e) {
            		xPos.getTextState().setText(""+selectedPart.position.x);
            	}
        	}
        });
        
        Label y_label = new Label("Y");
        y_label.setPosition(0, 20 * 5 + 4);
        y_label.getStyle().setTextColor(1, 1, 1, 1);
        y_label.getStyle().setFontSize(20.0f);
        properties_scroll_panel.getContainer().add(y_label);
       
        

        yPos = new TextAreaField("0");
        yPos.setSize(PROPERTIES_PANEL.getSize().x, 20);
        yPos.setPosition(15, 20 * 5);
        yPos.setEditable(false);
        yPos.getStyle().setTextColor(1, 1, 1, 1);
        yPos.getStyle().setFontSize(20.0f);
        yPos.getStyle().setVerticalAlign(VerticalAlign.TOP);
        yPos.getStyle().setPadding(0);
        yPos.getStyle().setMaxHeight(20);
        yPos.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 1);
        yPos.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {
        	if (selectedPart != null) {
        		try {
            		float f = Float.parseFloat(yPos.getTextState().getText());
            		selectedPart.position.y = f;
            	} catch (Exception e) {
            		yPos.getTextState().setText(""+selectedPart.position.y);
            	}
        	}
        });
        properties_scroll_panel.getContainer().add(yPos);
        
        Label z_label = new Label("Z");
        z_label.setPosition(0, 20 * 6 + 4);
        z_label.getStyle().setTextColor(1, 1, 1, 1);
        z_label.getStyle().setFontSize(20.0f);
        properties_scroll_panel.getContainer().add(z_label);
       
        

        zPos = new TextAreaField("0");
        zPos.setSize(PROPERTIES_PANEL.getSize().x, 20);
        zPos.setPosition(15, 20 * 6);
        zPos.setEditable(false);
        zPos.getStyle().setTextColor(1, 1, 1, 1);
        zPos.getStyle().setFontSize(20.0f);
        zPos.getStyle().setVerticalAlign(VerticalAlign.TOP);
        zPos.getStyle().setPadding(0);
        zPos.getStyle().setMaxHeight(20);
        zPos.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 1);
        
        zPos.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {
        	if (selectedPart != null) {
        		try {
            		float f = Float.parseFloat(zPos.getTextState().getText());
            		selectedPart.position.z = f;
            	} catch (Exception e) {
            		zPos.getTextState().setText(""+selectedPart.position.z);
            	}
        	}
        });
        properties_scroll_panel.getContainer().add(zPos);
        
        {
        	Label rotation_label = new Label(Translation.translateText("Inignoto:gui.rotation"));
        	rotation_label.setPosition(0, 20 * 8 + 5);
        	rotation_label.getStyle().setTextColor(1, 1, 1, 1);
        	rotation_label.getStyle().setFontSize(20.0f);
            properties_scroll_panel.getContainer().add(rotation_label);
            Label xr_label = new Label("X");
            xr_label.setPosition(0, 20 * 9 + 4);
            xr_label.getStyle().setTextColor(1, 1, 1, 1);
            xr_label.getStyle().setFontSize(20.0f);
            properties_scroll_panel.getContainer().add(xr_label);
           
            

            xRot = new TextAreaField("0");
            xRot.setSize(PROPERTIES_PANEL.getSize().x, 20);
            xRot.setPosition(15, 20 * 9);
            xRot.setEditable(false);
            xRot.getStyle().setTextColor(1, 1, 1, 1);
            xRot.getStyle().setFontSize(20.0f);
            xRot.getStyle().setVerticalAlign(VerticalAlign.TOP);
            xRot.getStyle().setPadding(0);
            xRot.getStyle().setMaxHeight(20);
            xRot.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 1);
            xRot.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {
            	if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(xRot.getTextState().getText());
                		selectedPart.setRotation(new Vector3f(f, selectedPart.getEulerAngles().y, selectedPart.getEulerAngles().z));
                	} catch (Exception e) {
                		xRot.getTextState().setText(""+selectedPart.rotation.x);
                	}
            	}
            });
            properties_scroll_panel.getContainer().add(xRot);
            
            Label yr_label = new Label("Y");
            yr_label.setPosition(0, 20 * 10 + 4);
            yr_label.getStyle().setTextColor(1, 1, 1, 1);
            yr_label.getStyle().setFontSize(20.0f);
            properties_scroll_panel.getContainer().add(yr_label);
           
            

            yRot = new TextAreaField("0");
            yRot.setSize(PROPERTIES_PANEL.getSize().x, 20);
            yRot.setPosition(15, 20 * 10);
            yRot.setEditable(false);
            yRot.getStyle().setTextColor(1, 1, 1, 1);
            yRot.getStyle().setFontSize(20.0f);
            yRot.getStyle().setVerticalAlign(VerticalAlign.TOP);
            yRot.getStyle().setPadding(0);
            yRot.getStyle().setMaxHeight(20);
            yRot.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 1);
            yRot.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {
            	if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(yRot.getTextState().getText());
                		selectedPart.setRotation(new Vector3f(selectedPart.getEulerAngles().x, f, selectedPart.getEulerAngles().z));
                	} catch (Exception e) {
                		yRot.getTextState().setText(""+selectedPart.rotation.y);
                	}
            	}
            });
            properties_scroll_panel.getContainer().add(yRot);
            
            Label zr_label = new Label("Z");
            zr_label.setPosition(0, 20 * 11 + 4);
            zr_label.getStyle().setTextColor(1, 1, 1, 1);
            zr_label.getStyle().setFontSize(20.0f);
            properties_scroll_panel.getContainer().add(zr_label);
           
            

            zRot = new TextAreaField("0");
            zRot.setSize(PROPERTIES_PANEL.getSize().x, 20);
            zRot.setPosition(15, 20 * 11);
            zRot.setEditable(false);
            zRot.getStyle().setTextColor(1, 1, 1, 1);
            zRot.getStyle().setFontSize(20.0f);
            zRot.getStyle().setVerticalAlign(VerticalAlign.TOP);
            zRot.getStyle().setPadding(0);
            zRot.getStyle().setMaxHeight(20);
            zRot.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 1);
            zRot.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {
            	if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(zRot.getTextState().getText());
                		selectedPart.setRotation(new Vector3f(selectedPart.getEulerAngles().x, selectedPart.getEulerAngles().y, f));
                	} catch (Exception e) {
                		zRot.getTextState().setText(""+selectedPart.rotation.z);
                	}
            	}
            });
            properties_scroll_panel.getContainer().add(zRot);
        }
        
        
        
        {
        	Label size_label = new Label(Translation.translateText("Inignoto:gui.size"));
        	size_label.setPosition(0, 20 * 13 + 5);
        	size_label.getStyle().setTextColor(1, 1, 1, 1);
        	size_label.getStyle().setFontSize(20.0f);
            properties_scroll_panel.getContainer().add(size_label);
            Label xr_label = new Label("X");
            xr_label.setPosition(0, 20 * 14 + 4);
            xr_label.getStyle().setTextColor(1, 1, 1, 1);
            xr_label.getStyle().setFontSize(20.0f);
            properties_scroll_panel.getContainer().add(xr_label);
           
            

            xSize = new TextAreaField("0");
            xSize.setSize(PROPERTIES_PANEL.getSize().x, 20);
            xSize.setPosition(15, 20 * 14);
            xSize.setEditable(false);
            xSize.getStyle().setTextColor(1, 1, 1, 1);
            xSize.getStyle().setFontSize(20.0f);
            xSize.getStyle().setVerticalAlign(VerticalAlign.TOP);
            xSize.getStyle().setPadding(0);
            xSize.getStyle().setMaxHeight(20);
            xSize.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 1);
            xSize.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {
            	if (selectedPart != null) {
            		try {
                		int f = Integer.parseInt(xSize.getTextState().getText());
                		selectedPart.size.x = f;
                		selectedPart.buildPart();
                	} catch (Exception e) {
                		xSize.getTextState().setText(""+selectedPart.size.x);
                	}
            	}
            });
            properties_scroll_panel.getContainer().add(xSize);
            
            Label yr_label = new Label("Y");
            yr_label.setPosition(0, 20 * 15 + 4);
            yr_label.getStyle().setTextColor(1, 1, 1, 1);
            yr_label.getStyle().setFontSize(20.0f);
            properties_scroll_panel.getContainer().add(yr_label);
           
            

            ySize = new TextAreaField("0");
            ySize.setSize(PROPERTIES_PANEL.getSize().x, 20);
            ySize.setPosition(15, 20 * 15);
            ySize.setEditable(false);
            ySize.getStyle().setTextColor(1, 1, 1, 1);
            ySize.getStyle().setFontSize(20.0f);
            ySize.getStyle().setVerticalAlign(VerticalAlign.TOP);
            ySize.getStyle().setPadding(0);
            ySize.getStyle().setMaxHeight(20);
            ySize.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 1);
            ySize.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {
            	if (selectedPart != null) {
            		try {
                		int f = Integer.parseInt(ySize.getTextState().getText());
                		selectedPart.size.y = f;
                		selectedPart.buildPart();
                	} catch (Exception e) {
                		ySize.getTextState().setText(""+selectedPart.size.y);
                	}
            	}
            });
            properties_scroll_panel.getContainer().add(ySize);
            
            Label zr_label = new Label("Z");
            zr_label.setPosition(0, 20 * 16 + 4);
            zr_label.getStyle().setTextColor(1, 1, 1, 1);
            zr_label.getStyle().setFontSize(20.0f);
            properties_scroll_panel.getContainer().add(zr_label);
           
            

            zSize = new TextAreaField("0");
            zSize.setSize(PROPERTIES_PANEL.getSize().x, 20);
            zSize.setPosition(15, 20 * 16);
            zSize.setEditable(false);
            zSize.getStyle().setTextColor(1, 1, 1, 1);
            zSize.getStyle().setFontSize(20.0f);
            zSize.getStyle().setVerticalAlign(VerticalAlign.TOP);
            zSize.getStyle().setPadding(0);
            zSize.getStyle().setMaxHeight(20);
            zSize.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 1);
            zSize.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {
            	if (selectedPart != null) {
            		try {
                		int f = Integer.parseInt(zSize.getTextState().getText());
                		selectedPart.size.z = f;
                		selectedPart.buildPart();
                	} catch (Exception e) {
                		zSize.getTextState().setText(""+selectedPart.size.z);
                	}
            	}
            });
            properties_scroll_panel.getContainer().add(zSize);
        }
        
        {
        	Label scale_label = new Label(Translation.translateText("Inignoto:gui.scale"));
        	scale_label.setPosition(0, 20 * 18 + 5);
        	scale_label.getStyle().setTextColor(1, 1, 1, 1);
        	scale_label.getStyle().setFontSize(20.0f);
            properties_scroll_panel.getContainer().add(scale_label);
            Label xr_label = new Label("X");
            xr_label.setPosition(0, 20 * 19 + 4);
            xr_label.getStyle().setTextColor(1, 1, 1, 1);
            xr_label.getStyle().setFontSize(20.0f);
            properties_scroll_panel.getContainer().add(xr_label);
           
            

            xScale = new TextAreaField("0");
            xScale.setSize(PROPERTIES_PANEL.getSize().x, 20);
            xScale.setPosition(15, 20 * 19);
            xScale.setEditable(false);
            xScale.getStyle().setTextColor(1, 1, 1, 1);
            xScale.getStyle().setFontSize(20.0f);
            xScale.getStyle().setVerticalAlign(VerticalAlign.TOP);
            xScale.getStyle().setPadding(0);
            xScale.getStyle().setMaxHeight(20);
            xScale.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 1);
            xScale.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {
            	if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(xScale.getTextState().getText());
                		selectedPart.scale.x = f;
                	} catch (Exception e) {
                		xScale.getTextState().setText(""+selectedPart.scale.x);
                	}
            	}
            });
            properties_scroll_panel.getContainer().add(xScale);
            
            Label yr_label = new Label("Y");
            yr_label.setPosition(0, 20 * 20 + 4);
            yr_label.getStyle().setTextColor(1, 1, 1, 1);
            yr_label.getStyle().setFontSize(20.0f);
            properties_scroll_panel.getContainer().add(yr_label);
           
            

            yScale = new TextAreaField("0");
            yScale.setSize(PROPERTIES_PANEL.getSize().x, 20);
            yScale.setPosition(15, 20 * 20);
            yScale.setEditable(false);
            yScale.getStyle().setTextColor(1, 1, 1, 1);
            yScale.getStyle().setFontSize(20.0f);
            yScale.getStyle().setVerticalAlign(VerticalAlign.TOP);
            yScale.getStyle().setPadding(0);
            yScale.getStyle().setMaxHeight(20);
            yScale.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 1);
            yScale.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {
            	if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(yScale.getTextState().getText());
                		selectedPart.scale.y = f;
                	} catch (Exception e) {
                		yScale.getTextState().setText(""+selectedPart.scale.y);
                	}
            	}
            });
            properties_scroll_panel.getContainer().add(yScale);
            
            Label zr_label = new Label("Z");
            zr_label.setPosition(0, 20 * 21 + 4);
            zr_label.getStyle().setTextColor(1, 1, 1, 1);
            zr_label.getStyle().setFontSize(20.0f);
            properties_scroll_panel.getContainer().add(zr_label);
           
            

            zScale = new TextAreaField("0");
            zScale.setSize(PROPERTIES_PANEL.getSize().x, 20);
            zScale.setPosition(15, 20 * 21);
            zScale.setEditable(false);
            zScale.getStyle().setTextColor(1, 1, 1, 1);
            zScale.getStyle().setFontSize(20.0f);
            zScale.getStyle().setVerticalAlign(VerticalAlign.TOP);
            zScale.getStyle().setPadding(0);
            zScale.getStyle().setMaxHeight(20);
            zScale.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 1);
            zScale.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {
            	if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(zScale.getTextState().getText());
                		selectedPart.scale.z = f;
                	} catch (Exception e) {
                		zScale.getTextState().setText(""+selectedPart.scale.z);
                	}
            	}
            });
            properties_scroll_panel.getContainer().add(zScale);
        }
        
        {
        	Label origin_label = new Label(Translation.translateText("Inignoto:gui.center"));
        	origin_label.setPosition(0, 20 * 23 + 5);
        	origin_label.getStyle().setTextColor(1, 1, 1, 1);
        	origin_label.getStyle().setFontSize(20.0f);
            properties_scroll_panel.getContainer().add(origin_label);
            Label xr_label = new Label("X");
            xr_label.setPosition(0, 20 * 24 + 4);
            xr_label.getStyle().setTextColor(1, 1, 1, 1);
            xr_label.getStyle().setFontSize(20.0f);
            properties_scroll_panel.getContainer().add(xr_label);
           
            

            xCenter = new TextAreaField("0");
            xCenter.setSize(PROPERTIES_PANEL.getSize().x, 20);
            xCenter.setPosition(15, 20 * 24);
            xCenter.setEditable(false);
            xCenter.getStyle().setTextColor(1, 1, 1, 1);
            xCenter.getStyle().setFontSize(20.0f);
            xCenter.getStyle().setVerticalAlign(VerticalAlign.TOP);
            xCenter.getStyle().setPadding(0);
            xCenter.getStyle().setMaxHeight(20);
            xCenter.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 1);
            xCenter.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {
            	if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(xCenter.getTextState().getText());
                		selectedPart.origin.x = f;
                	} catch (Exception e) {
                		xCenter.getTextState().setText(""+selectedPart.origin.x);
                	}
            	}
            });
            properties_scroll_panel.getContainer().add(xCenter);
            
            Label yr_label = new Label("Y");
            yr_label.setPosition(0, 20 * 25 + 4);
            yr_label.getStyle().setTextColor(1, 1, 1, 1);
            yr_label.getStyle().setFontSize(20.0f);
            properties_scroll_panel.getContainer().add(yr_label);
           
            

            yCenter = new TextAreaField("0");
            yCenter.setSize(PROPERTIES_PANEL.getSize().x, 20);
            yCenter.setPosition(15, 20 * 25);
            yCenter.setEditable(false);
            yCenter.getStyle().setTextColor(1, 1, 1, 1);
            yCenter.getStyle().setFontSize(20.0f);
            yCenter.getStyle().setVerticalAlign(VerticalAlign.TOP);
            yCenter.getStyle().setPadding(0);
            yCenter.getStyle().setMaxHeight(20);
            yCenter.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 1);
            yCenter.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {
            	if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(yCenter.getTextState().getText());
                		selectedPart.origin.y = f;
                	} catch (Exception e) {
                		yCenter.getTextState().setText(""+selectedPart.origin.y);
                	}
            	}
            });
            properties_scroll_panel.getContainer().add(yCenter);
            
            Label zr_label = new Label("Z");
            zr_label.setPosition(0, 20 * 26 + 4);
            zr_label.getStyle().setTextColor(1, 1, 1, 1);
            zr_label.getStyle().setFontSize(20.0f);
            properties_scroll_panel.getContainer().add(zr_label);
           
            

            zCenter = new TextAreaField("0");
            zCenter.setSize(PROPERTIES_PANEL.getSize().x, 20);
            zCenter.setPosition(15, 20 * 26);
            zCenter.setEditable(false);
            zCenter.getStyle().setTextColor(1, 1, 1, 1);
            zCenter.getStyle().setFontSize(20.0f);
            zCenter.getStyle().setVerticalAlign(VerticalAlign.TOP);
            zCenter.getStyle().setPadding(0);
            zCenter.getStyle().setMaxHeight(20);
            zCenter.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 1);
            zCenter.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {
            	if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(zCenter.getTextState().getText());
                		selectedPart.origin.z = f;
                	} catch (Exception e) {
                		zCenter.getTextState().setText(""+selectedPart.origin.z);
                	}
            	}
            });
            properties_scroll_panel.getContainer().add(zCenter);
        }
        
        Label extra_label = new Label("");
        extra_label.setPosition(0, 20 * 30 + 4);
        extra_label.getStyle().setTextColor(1, 1, 1, 1);
        extra_label.getStyle().setFontSize(20.0f);
        properties_scroll_panel.getContainer().add(extra_label);
        
        xPos.getListenerMap().addListener(KeyEvent.class, (KeyEventListener) event -> {
        	if (event.getKey() == GLFW.GLFW_KEY_ENTER) {
        		if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(xPos.getTextState().getText());
                		selectedPart.position.x = f;
                	} catch (Exception e) {
                		xPos.getTextState().setText(""+selectedPart.position.x);
                	}
            	}
        	}
        });
        
        yPos.getListenerMap().addListener(KeyEvent.class, (KeyEventListener) event -> {
        	if (event.getKey() == GLFW.GLFW_KEY_ENTER) {
        		if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(yPos.getTextState().getText());
                		selectedPart.position.y = f;
                	} catch (Exception e) {
                		yPos.getTextState().setText(""+selectedPart.position.y);
                	}
            	}
        	}
        });
        
        zPos.getListenerMap().addListener(KeyEvent.class, (KeyEventListener) event -> {
        	if (event.getKey() == GLFW.GLFW_KEY_ENTER) {
        		if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(zPos.getTextState().getText());
                		selectedPart.position.z = f;
                	} catch (Exception e) {
                		zPos.getTextState().setText(""+selectedPart.position.z);
                	}
            	}
        	}
        });
        
        xCenter.getListenerMap().addListener(KeyEvent.class, (KeyEventListener) event -> {
        	if (event.getKey() == GLFW.GLFW_KEY_ENTER) {
        		if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(xCenter.getTextState().getText());
                		selectedPart.origin.x = f;
                	} catch (Exception e) {
                		xCenter.getTextState().setText(""+selectedPart.origin.x);
                	}
            	}
        	}
        });
        
        yCenter.getListenerMap().addListener(KeyEvent.class, (KeyEventListener) event -> {
        	if (event.getKey() == GLFW.GLFW_KEY_ENTER) {
        		if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(yCenter.getTextState().getText());
                		selectedPart.origin.y = f;
                	} catch (Exception e) {
                		yCenter.getTextState().setText(""+selectedPart.origin.y);
                	}
            	}
        	}
        });
        
        zCenter.getListenerMap().addListener(KeyEvent.class, (KeyEventListener) event -> {
        	if (event.getKey() == GLFW.GLFW_KEY_ENTER) {
        		if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(zCenter.getTextState().getText());
                		selectedPart.origin.z = f;
                	} catch (Exception e) {
                		zCenter.getTextState().setText(""+selectedPart.origin.z);
                	}
            	}
        	}
        });
        
        xRot.getListenerMap().addListener(KeyEvent.class, (KeyEventListener) event -> {
        	if (event.getKey() == GLFW.GLFW_KEY_ENTER) {
        		if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(xRot.getTextState().getText());
                		selectedPart.setRotation(new Vector3f(f, selectedPart.getEulerAngles().y, selectedPart.getEulerAngles().z));
                	} catch (Exception e) {
                		xRot.getTextState().setText(""+selectedPart.rotation.x);
                	}
            	}
        	}
        });
        
        yRot.getListenerMap().addListener(KeyEvent.class, (KeyEventListener) event -> {
        	if (event.getKey() == GLFW.GLFW_KEY_ENTER) {
        		if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(yRot.getTextState().getText());
                		selectedPart.setRotation(new Vector3f(selectedPart.getEulerAngles().x, f, selectedPart.getEulerAngles().z));
                	} catch (Exception e) {
                		yRot.getTextState().setText(""+selectedPart.rotation.y);
                	}
            	}
        	}
        });
        
        zRot.getListenerMap().addListener(KeyEvent.class, (KeyEventListener) event -> {
        	if (event.getKey() == GLFW.GLFW_KEY_ENTER) {
        		if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(zRot.getTextState().getText());
                		selectedPart.setRotation(new Vector3f(selectedPart.getEulerAngles().x, selectedPart.getEulerAngles().y, f));
                	} catch (Exception e) {
                		zRot.getTextState().setText(""+selectedPart.rotation.z);
                	}
            	}
        	}
        });
        
        xScale.getListenerMap().addListener(KeyEvent.class, (KeyEventListener) event -> {
        	if (event.getKey() == GLFW.GLFW_KEY_ENTER) {
        		if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(xScale.getTextState().getText());
                		selectedPart.scale.x = f;
                	} catch (Exception e) {
                		xScale.getTextState().setText(""+selectedPart.scale.x);
                	}
            	}
        	}
        });
        
        yScale.getListenerMap().addListener(KeyEvent.class, (KeyEventListener) event -> {
        	if (event.getKey() == GLFW.GLFW_KEY_ENTER) {
        		if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(yScale.getTextState().getText());
                		selectedPart.scale.y = f;
                	} catch (Exception e) {
                		yScale.getTextState().setText(""+selectedPart.scale.y);
                	}
            	}
        	}
        });
        
        zScale.getListenerMap().addListener(KeyEvent.class, (KeyEventListener) event -> {
        	if (event.getKey() == GLFW.GLFW_KEY_ENTER) {
        		if (selectedPart != null) {
            		try {
                		float f = Float.parseFloat(zScale.getTextState().getText());
                		selectedPart.scale.z = f;
                	} catch (Exception e) {
                		zScale.getTextState().setText(""+selectedPart.scale.z);
                	}
            	}
        	}
        });
        
        xSize.getListenerMap().addListener(KeyEvent.class, (KeyEventListener) event -> {
        	if (event.getKey() == GLFW.GLFW_KEY_ENTER) {
        		if (selectedPart != null) {
            		try {
                		int f = Integer.parseInt(xSize.getTextState().getText());
                		selectedPart.size.x = f;
                		selectedPart.buildPart();
                	} catch (Exception e) {
                		xSize.getTextState().setText(""+selectedPart.size.x);
                	}
            	}
        	}
        });
        
        ySize.getListenerMap().addListener(KeyEvent.class, (KeyEventListener) event -> {
        	if (event.getKey() == GLFW.GLFW_KEY_ENTER) {
        		if (selectedPart != null) {
            		try {
                		int f = Integer.parseInt(ySize.getTextState().getText());
                		selectedPart.size.y = f;
                		selectedPart.buildPart();
                	} catch (Exception e) {
                		ySize.getTextState().setText(""+selectedPart.size.y);
                	}
            	}
        	}
        });
        
        zSize.getListenerMap().addListener(KeyEvent.class, (KeyEventListener) event -> {
        	if (event.getKey() == GLFW.GLFW_KEY_ENTER) {
        		if (selectedPart != null) {
            		try {
                		int f = Integer.parseInt(zSize.getTextState().getText());
                		selectedPart.size.z = f;
                		selectedPart.buildPart();
                	} catch (Exception e) {
                		zSize.getTextState().setText(""+selectedPart.size.z);
                	}
            	}
        	}
        });
        
        properties_scroll_panel.getContainer().add(xPos);
                
        panel.add(PROPERTIES_PANEL);
	}
	
	public void createGrid() {		
		float[] vertices = new float[] {
				-5.5f, 0.0f, -5.5f,
				-5.5f, 0.0f, 5.5f,
				5.5f, 0.0f, 5.5f,
				5.5f, 0.0f, -5.5f
		};
		float[] texCoords = new float[] {
				0, 0, 0, 1, 1, 1, 1, 0
		};
		int[] indices = new int[] {
				0, 1, 2, 2, 3, 0
		};
		grid = new Mesh(vertices, texCoords, indices, Textures.GRID);
	}
	
	private boolean SETTING_PARENT;
	
	public void createEditPanel(Panel EDIT_PANEL) {
		EDIT_PANEL.setSize(150, 30 * 4);
		EDIT_PANEL.setPosition(this.FILE.getSize().x, 20);
		EDIT_PANEL.getStyle().setFontSize(20f);
		EDIT_PANEL.getStyle().getBackground().setColor(new Vector4f(0.3f, 0.3f, 0.3f, 1f));
		EDIT_PANEL.setEnabled(false);
		
		Button NEW = new Button(Translation.translateText("Inignoto:gui.new_part"));
        NEW.setSize(150, 30);
        NEW.setPosition(0, 0);
        NEW.getStyle().setFontSize(22f);
        NEW.getStyle().setTextColor(0, 0, 0, 1);
        NEW.getStyle().getBackground().setColor(new Vector4f(0, 0, 0, 0));
        NEW.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        NEW.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
        NEW.getStyle().setTextColor(1, 1, 1, 1);
        EDIT_PANEL.add(NEW);
        
        NEW.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {

            if (event.getAction().equals(MouseClickEvent.MouseClickAction.CLICK)) {
            	Part part = new Part();
        		part.size = new Vector3i(32, 32, 32);
        		part.scale = new Vector3f(1, 1, 1);
        		part.name = "Part";
        		part.position.y = 16.0f;
        		part.buildPart(Textures.GRAY_MATERIAL);
        		model.getParts().add(part);
        		
        		this.refreshPartsPanel();
            }

        });
        
        Button DUPLICATE = new Button(Translation.translateText("Inignoto:gui.duplicate_part"));
        DUPLICATE.setSize(150, 30);
        DUPLICATE.setPosition(0, 30);
        DUPLICATE.getStyle().setFontSize(22f);
        DUPLICATE.getStyle().setTextColor(0, 0, 0, 1);
        DUPLICATE.getStyle().getBackground().setColor(new Vector4f(0, 0, 0, 0));
        DUPLICATE.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        DUPLICATE.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
        DUPLICATE.getStyle().setTextColor(1, 1, 1, 1);
        EDIT_PANEL.add(DUPLICATE);
        
        DUPLICATE.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {

            if (event.getAction().equals(MouseClickEvent.MouseClickAction.CLICK)) {
            	if (this.selectedPart != null) {
            		Part.duplicatePart(this.selectedPart, null, this.model);
            		this.refreshPartsPanel();
            	}
            }

        });
        
        Button SET_PARENT = new Button(Translation.translateText("Inignoto:gui.set_parent"));
        SET_PARENT.setSize(150, 30);
        SET_PARENT.setPosition(0, 30 * 2);
        SET_PARENT.getStyle().setFontSize(22f);
        SET_PARENT.getStyle().setTextColor(0, 0, 0, 1);
        SET_PARENT.getStyle().getBackground().setColor(new Vector4f(0, 0, 0, 0));
        SET_PARENT.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        SET_PARENT.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
        SET_PARENT.getStyle().setTextColor(1, 1, 1, 1);
        EDIT_PANEL.add(SET_PARENT);
        
        SET_PARENT.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {

            if (event.getAction().equals(MouseClickEvent.MouseClickAction.CLICK)) {
            	if (this.selectedPart != null) {
            		SETTING_PARENT = true;
                	JOptionPane.showMessageDialog(null, "Click on a part to set the new parent of the selected part!");
            	}
            }

        });
        
        Button DELETE_PART = new Button(Translation.translateText("Inignoto:gui.delete_part"));
        DELETE_PART.setSize(150, 30);
        DELETE_PART.setPosition(0, 30 * 3);
        DELETE_PART.getStyle().setFontSize(22f);
        DELETE_PART.getStyle().setTextColor(0, 0, 0, 1);
        DELETE_PART.getStyle().getBackground().setColor(new Vector4f(0, 0, 0, 0));
        DELETE_PART.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        DELETE_PART.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
        DELETE_PART.getStyle().setTextColor(1, 1, 1, 1);
        EDIT_PANEL.add(DELETE_PART);
        
        DELETE_PART.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {

            if (event.getAction().equals(MouseClickEvent.MouseClickAction.CLICK)) {
            	if (this.selectedPart != null) {
            		if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this part?") == 0) {
            			this.model.getParts().remove(this.selectedPart);
                		this.refreshPartsPanel();
                		this.selectPart(null);
                	}
            		
            	}
            	
            }

        });
	}
	
	private void createFilePanel(Panel FILE_PANEL) {
		FILE_PANEL.setSize(150, 30 * 4);
        FILE_PANEL.setPosition(0, 20);
        FILE_PANEL.getStyle().setFontSize(20f);
        FILE_PANEL.getStyle().getBackground().setColor(new Vector4f(0.3f, 0.3f, 0.3f, 1f));
        FILE_PANEL.setEnabled(false);
        
        Button NEW = new Button(Translation.translateText("Inignoto:gui.new"));
        NEW.setSize(150, 30);
        NEW.setPosition(0, 0);
        NEW.getStyle().setFontSize(22f);
        NEW.getStyle().setTextColor(0, 0, 0, 1);
        NEW.getStyle().getBackground().setColor(new Vector4f(0, 0, 0, 0));
        NEW.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        NEW.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
        NEW.getStyle().setTextColor(1, 1, 1, 1);
        FILE_PANEL.add(NEW);
        
        Button SAVE = new Button(Translation.translateText("Inignoto:gui.save"));
        SAVE.setSize(150, 30);
        SAVE.setPosition(0, 30);
        SAVE.getStyle().setFontSize(22f);
        SAVE.getStyle().setTextColor(0, 0, 0, 1);
        SAVE.getStyle().getBackground().setColor(new Vector4f(0, 0, 0, 0));
        SAVE.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        SAVE.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
        SAVE.getStyle().setTextColor(1, 1, 1, 1);
        FILE_PANEL.add(SAVE);
        
        Button SAVE_AS = new Button(Translation.translateText("Inignoto:gui.save_as"));
        SAVE_AS.setSize(150, 30);
        SAVE_AS.setPosition(0, 30 * 2);
        SAVE_AS.getStyle().setFontSize(22f);
        SAVE_AS.getStyle().setTextColor(0, 0, 0, 1);
        SAVE_AS.getStyle().getBackground().setColor(new Vector4f(0, 0, 0, 0));
        SAVE_AS.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        SAVE_AS.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
        SAVE_AS.getStyle().setTextColor(1, 1, 1, 1);
        FILE_PANEL.add(SAVE_AS);
        
        Button IMPORT = new Button(Translation.translateText("Inignoto:gui.import"));
        IMPORT.setSize(150, 30);
        IMPORT.setPosition(0, 30 * 3);
        IMPORT.getStyle().setFontSize(22f);
        IMPORT.getStyle().setTextColor(0, 0, 0, 1);
        IMPORT.getStyle().getBackground().setColor(new Vector4f(0, 0, 0, 0));
        IMPORT.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        IMPORT.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
        IMPORT.getStyle().setTextColor(1, 1, 1, 1);
        FILE_PANEL.add(IMPORT);
        
        Panel NEW_PANEL = new Panel();
        createNewPanel(NEW_PANEL);
        
        Panel SAVE_PANEL = new Panel();
        createSavePanel(SAVE_PANEL);
        
        Panel SAVE_AS_PANEL = new Panel();
        createSaveAsPanel(SAVE_AS_PANEL);
        
        Panel IMPORT_PANEL = new Panel();
        createImportPanel(IMPORT_PANEL);
        
        NEW.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {

            if (event.getAction().equals(MouseClickEvent.MouseClickAction.CLICK)) {
            	if (panel.contains(NEW_PANEL)) {
            		panel.remove(NEW_PANEL);
            	} else {
            		panel.add(NEW_PANEL);
            		NEW_PANEL.setFocused(true);
            	}
            }

        });
        SAVE.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {

            if (event.getAction().equals(MouseClickEvent.MouseClickAction.CLICK)) {
            	if (panel.contains(SAVE_PANEL)) {
            		panel.remove(SAVE_PANEL);
            	} else {
            		panel.add(SAVE_PANEL);
            		SAVE_PANEL.setFocused(true);
            	}
            }

        });
        
        SAVE_AS.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {

            if (event.getAction().equals(MouseClickEvent.MouseClickAction.CLICK)) {
            	if (panel.contains(SAVE_AS_PANEL)) {
            		panel.remove(SAVE_AS_PANEL);
            	} else {
            		panel.add(SAVE_AS_PANEL);
            		SAVE_AS_PANEL.setFocused(true);
            	}
            }

        });
        
        IMPORT.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {

            if (event.getAction().equals(MouseClickEvent.MouseClickAction.CLICK)) {
            	if (panel.contains(IMPORT_PANEL)) {
            		panel.remove(IMPORT_PANEL);
            	} else {
            		panel.add(IMPORT_PANEL);
            		IMPORT_PANEL.setFocused(true);
            	}
            }

        });
        
        
        
        LoadableImage new_image = DefaultImageLoader.loadImage("assets/Inignoto/textures/modelmaker/new.png");
        LoadableImage right_arrow_image = DefaultImageLoader.loadImage("assets/Inignoto/textures/modelmaker/right_arrow.png");
        LoadableImage save_image = DefaultImageLoader.loadImage("assets/Inignoto/textures/modelmaker/save.png");
        LoadableImage import_image = DefaultImageLoader.loadImage("assets/Inignoto/textures/modelmaker/import.png");
        LoadableImage save_as_image = DefaultImageLoader.loadImage("assets/Inignoto/textures/modelmaker/save_as.png");
        LoadableImage translate_image = DefaultImageLoader.loadImage("assets/Inignoto/textures/modelmaker/translate.png");
        LoadableImage rotate_image = DefaultImageLoader.loadImage("assets/Inignoto/textures/modelmaker/rotate.png");
        LoadableImage scale_image = DefaultImageLoader.loadImage("assets/Inignoto/textures/modelmaker/scale.png");

        ImageView translate = new ImageView(translate_image);
        translate.setPosition(0, 30);
        translate.getStyle().setPosition(PositionType.RELATIVE);
        translate.setSize(50, 40);
        translate.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 0.0f);
        translate.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        translate.getStyle().getBorder().setEnabled(false);
        translate.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
            if (event.getAction().equals(MouseClickEvent.MouseClickAction.CLICK)) {
            	this.selectionMode = SelectionMode.TRANSLATION;
            }
        });
        panel.add(translate);
        
        ImageView rotate = new ImageView(rotate_image);
        rotate.setPosition(50, 30);
        rotate.getStyle().setPosition(PositionType.RELATIVE);
        rotate.setSize(40, 40);
        rotate.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 0.0f);
        rotate.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        rotate.getStyle().getBorder().setEnabled(false);
        rotate.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
            if (event.getAction().equals(MouseClickEvent.MouseClickAction.CLICK)) {
            	this.selectionMode = SelectionMode.ROTATION;
            }
        });
        panel.add(rotate);
        
        ImageView scale = new ImageView(scale_image);
        scale.setPosition(50 + 40, 30);
        scale.getStyle().setPosition(PositionType.RELATIVE);
        scale.setSize(50, 40);
        scale.getStyle().getBackground().setColor(0.2f, 0.2f, 0.2f, 0.0f);
        scale.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        scale.getStyle().getBorder().setEnabled(false);
        scale.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
            if (event.getAction().equals(MouseClickEvent.MouseClickAction.CLICK)) {
            	this.selectionMode = SelectionMode.SCALE;
            }
        });
        panel.add(scale);
        
        ImageView imageView = new ImageView(new_image);
        imageView.setPosition(10, 5);
        imageView.getStyle().setPosition(PositionType.RELATIVE);
        imageView.getStyle().setMinimumSize(50, 50);
        imageView.setSize(20, 20);
        imageView.getStyle().getBackground().setColor(0, 0, 0, 0);
        imageView.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        imageView.getStyle().getBorder().setEnabled(false);
        NEW.add(imageView);
        
        ImageView save = new ImageView(save_image);
        save.setPosition(10, 5);
        save.getStyle().setPosition(PositionType.RELATIVE);
        save.getStyle().setMinimumSize(50, 50);
        save.setSize(20, 20);
        save.getStyle().getBackground().setColor(0, 0, 0, 0);
        save.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        save.getStyle().getBorder().setEnabled(false);
        SAVE.add(save);
        
        ImageView save_as = new ImageView(save_as_image);
        save_as.setPosition(15, 5);
        save_as.getStyle().setPosition(PositionType.RELATIVE);
        save_as.getStyle().setMinimumSize(50, 50);
        save_as.setSize(20, 20);
        save_as.getStyle().getBackground().setColor(0, 0, 0, 0);
        save_as.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        save_as.getStyle().getBorder().setEnabled(false);
        SAVE_AS.add(save_as);

        ImageView import_img = new ImageView(import_image);
        import_img.setPosition(10, 5);
        import_img.getStyle().setPosition(PositionType.RELATIVE);
        import_img.getStyle().setMinimumSize(50, 50);
        import_img.setSize(20, 20);
        import_img.getStyle().getBackground().setColor(0, 0, 0, 0);
        import_img.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        import_img.getStyle().getBorder().setEnabled(false);
        IMPORT.add(import_img);
        
        {
        	ImageView rightArrow = new ImageView(right_arrow_image);
            rightArrow.setPosition(150 - 15, 10);
            rightArrow.getStyle().setPosition(PositionType.RELATIVE);
            rightArrow.getStyle().setMinimumSize(50, 50);
            rightArrow.setSize(10, 10);
            rightArrow.getStyle().getBackground().setColor(0, 0, 0, 0);
            rightArrow.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
            rightArrow.getStyle().getBorder().setEnabled(false);
            NEW.add(rightArrow);
        }
        {
        	ImageView rightArrow = new ImageView(right_arrow_image);
            rightArrow.setPosition(150 - 15, 10);
            rightArrow.getStyle().setPosition(PositionType.RELATIVE);
            rightArrow.getStyle().setMinimumSize(50, 50);
            rightArrow.setSize(10, 10);
            rightArrow.getStyle().getBackground().setColor(0, 0, 0, 0);
            rightArrow.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
            rightArrow.getStyle().getBorder().setEnabled(false);
            SAVE.add(rightArrow);
        }
        {
        	ImageView rightArrow = new ImageView(right_arrow_image);
            rightArrow.setPosition(150 - 15, 10);
            rightArrow.getStyle().setPosition(PositionType.RELATIVE);
            rightArrow.getStyle().setMinimumSize(50, 50);
            rightArrow.setSize(10, 10);
            rightArrow.getStyle().getBackground().setColor(0, 0, 0, 0);
            rightArrow.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
            rightArrow.getStyle().getBorder().setEnabled(false);
            SAVE_AS.add(rightArrow);
        }
        {
        	ImageView rightArrow = new ImageView(right_arrow_image);
            rightArrow.setPosition(150 - 15, 10);
            rightArrow.getStyle().setPosition(PositionType.RELATIVE);
            rightArrow.getStyle().setMinimumSize(50, 50);
            rightArrow.setSize(10, 10);
            rightArrow.getStyle().getBackground().setColor(0, 0, 0, 0);
            rightArrow.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
            rightArrow.getStyle().getBorder().setEnabled(false);
            IMPORT.add(rightArrow);
        }
        
	}
	
	private void createNewPanel(Panel NEW_PANEL) {
		NEW_PANEL.setSize(150, 60);
        NEW_PANEL.setPosition(150, 20);
        NEW_PANEL.getStyle().getBackground().setColor(new Vector4f(0.3f, 0.3f, 0.3f, 1f));
        
        NEW_PANEL.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {

        	if (!event.isFocused()) {
        		panel.remove(NEW_PANEL);
        	}

        });
        
        Button NEW_MODEL = new Button(Translation.translateText("Inignoto:gui.model"));
        NEW_MODEL.setSize(150, 30);
        NEW_MODEL.setPosition(0, 0);
        NEW_MODEL.getStyle().setFontSize(22f);
        NEW_MODEL.getStyle().setTextColor(0, 0, 0, 1);
        NEW_MODEL.getStyle().getBackground().setColor(new Vector4f(0, 0, 0, 0));
        NEW_MODEL.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        NEW_MODEL.getStyle().setTextColor(1, 1, 1, 1);
        NEW_MODEL.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
        NEW_PANEL.add(NEW_MODEL);
        
        
        Button NEW_TEXTURE = new Button(Translation.translateText("Inignoto:gui.texture"));
        NEW_TEXTURE.setSize(150, 30);
        NEW_TEXTURE.setPosition(0, 30);
        NEW_TEXTURE.getStyle().setFontSize(22f);
        NEW_TEXTURE.getStyle().setTextColor(0, 0, 0, 1);
        NEW_TEXTURE.getStyle().getBackground().setColor(new Vector4f(0, 0, 0, 0));
        NEW_TEXTURE.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        NEW_TEXTURE.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
        NEW_TEXTURE.getStyle().setTextColor(1, 1, 1, 1);
        NEW_PANEL.add(NEW_TEXTURE);
	}

	private void createSaveAsPanel(Panel NEW_PANEL) {

		NEW_PANEL.setSize(150, 60);
        NEW_PANEL.setPosition(150, 20 + 30 * 2);
        NEW_PANEL.getStyle().getBackground().setColor(new Vector4f(0.3f, 0.3f, 0.3f, 1f));
        
        NEW_PANEL.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {

        	if (!event.isFocused()) {
        		panel.remove(NEW_PANEL);
        	}

        });
        
        Button NEW_MODEL = new Button(Translation.translateText("Inignoto:gui.model"));
        NEW_MODEL.setSize(150, 30);
        NEW_MODEL.setPosition(0, 0);
        NEW_MODEL.getStyle().setFontSize(22f);
        NEW_MODEL.getStyle().setTextColor(0, 0, 0, 1);
        NEW_MODEL.getStyle().getBackground().setColor(new Vector4f(0, 0, 0, 0));
        NEW_MODEL.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        NEW_MODEL.getStyle().setTextColor(1, 1, 1, 1);
        NEW_MODEL.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
        NEW_PANEL.add(NEW_MODEL);
        
        
        Button NEW_TEXTURE = new Button(Translation.translateText("Inignoto:gui.texture"));
        NEW_TEXTURE.setSize(150, 30);
        NEW_TEXTURE.setPosition(0, 30);
        NEW_TEXTURE.getStyle().setFontSize(22f);
        NEW_TEXTURE.getStyle().setTextColor(0, 0, 0, 1);
        NEW_TEXTURE.getStyle().getBackground().setColor(new Vector4f(0, 0, 0, 0));
        NEW_TEXTURE.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        NEW_TEXTURE.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
        NEW_TEXTURE.getStyle().setTextColor(1, 1, 1, 1);
        NEW_PANEL.add(NEW_TEXTURE);
	}
	private void createSavePanel(Panel NEW_PANEL) {
		NEW_PANEL.setSize(150, 60);
        NEW_PANEL.setPosition(150, 20 + 30);
        NEW_PANEL.getStyle().getBackground().setColor(new Vector4f(0.3f, 0.3f, 0.3f, 1f));
        
        NEW_PANEL.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {

        	if (!event.isFocused()) {
        		panel.remove(NEW_PANEL);
        	}

        });
        
        Button NEW_MODEL = new Button(Translation.translateText("Inignoto:gui.model"));
        NEW_MODEL.setSize(150, 30);
        NEW_MODEL.setPosition(0, 0);
        NEW_MODEL.getStyle().setFontSize(22f);
        NEW_MODEL.getStyle().setTextColor(0, 0, 0, 1);
        NEW_MODEL.getStyle().getBackground().setColor(new Vector4f(0, 0, 0, 0));
        NEW_MODEL.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        NEW_MODEL.getStyle().setTextColor(1, 1, 1, 1);
        NEW_MODEL.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
        NEW_PANEL.add(NEW_MODEL);
        
        
        Button NEW_TEXTURE = new Button(Translation.translateText("Inignoto:gui.texture"));
        NEW_TEXTURE.setSize(150, 30);
        NEW_TEXTURE.setPosition(0, 30);
        NEW_TEXTURE.getStyle().setFontSize(22f);
        NEW_TEXTURE.getStyle().setTextColor(0, 0, 0, 1);
        NEW_TEXTURE.getStyle().getBackground().setColor(new Vector4f(0, 0, 0, 0));
        NEW_TEXTURE.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        NEW_TEXTURE.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
        NEW_TEXTURE.getStyle().setTextColor(1, 1, 1, 1);
        NEW_PANEL.add(NEW_TEXTURE);
	}
	private void createImportPanel(Panel NEW_PANEL) {
		NEW_PANEL.setSize(150, 60);
        NEW_PANEL.setPosition(150, 20 + 30 * 3);
        NEW_PANEL.getStyle().getBackground().setColor(new Vector4f(0.3f, 0.3f, 0.3f, 1f));
        
        NEW_PANEL.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {

        	if (!event.isFocused()) {
        		panel.remove(NEW_PANEL);
        	}

        });
        
        Button NEW_MODEL = new Button(Translation.translateText("Inignoto:gui.model"));
        NEW_MODEL.setSize(150, 30);
        NEW_MODEL.setPosition(0, 0);
        NEW_MODEL.getStyle().setFontSize(22f);
        NEW_MODEL.getStyle().setTextColor(0, 0, 0, 1);
        NEW_MODEL.getStyle().getBackground().setColor(new Vector4f(0, 0, 0, 0));
        NEW_MODEL.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        NEW_MODEL.getStyle().setTextColor(1, 1, 1, 1);
        NEW_MODEL.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
        NEW_PANEL.add(NEW_MODEL);
        
        NEW_MODEL.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
        	if (event.getAction() == MouseClickEvent.MouseClickAction.PRESS) {
        		if (event.getButton().getCode() == 0) {
        			loadModel();
        		}
        	}
        });
        
        
        Button NEW_TEXTURE = new Button(Translation.translateText("Inignoto:gui.texture"));
        NEW_TEXTURE.setSize(150, 30);
        NEW_TEXTURE.setPosition(0, 30);
        NEW_TEXTURE.getStyle().setFontSize(22f);
        NEW_TEXTURE.getStyle().setTextColor(0, 0, 0, 1);
        NEW_TEXTURE.getStyle().getBackground().setColor(new Vector4f(0, 0, 0, 0));
        NEW_TEXTURE.getStyle().getShadow().setColor(new Vector4f(0, 0, 0, 0));
        NEW_TEXTURE.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
        NEW_TEXTURE.getStyle().setTextColor(1, 1, 1, 1);
        NEW_PANEL.add(NEW_TEXTURE);
        
        NEW_TEXTURE.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
        	if (event.getAction() == MouseClickEvent.MouseClickAction.PRESS) {
        		if (event.getButton().getCode() == 0) {
        			loadTexture();
        		}
        	}
        });
	}
	
	public void loadTexture() {
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
					this.model.changeTexture(texture);
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, Translation.translateText("Inignoto:gui.not_valid_texture"));
			}
		}
	}
	
	public void loadModel() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(lastLoadDir));
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			String f = selectedFile.getPath();
			
			System.out.println(f);
			lastLoadDir = selectedFile.getParent();
			try {
//				AnimModel model = ModelLoader.loadModelFromFile(selectedFile);
//				this.model = new CustomModel(model, texture);
//				this.modelDir = selectedFile;
//				this.lastAction = -1;
//				this.actions.clear();
				JOptionPane.showMessageDialog(null, "Option not currently supported");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, Translation.translateText("Inignoto:gui.not_valid_model"));
			}
		}
	}
	
	private boolean propertyScaleX = false;
	private boolean propertyScaleY = false;
	private boolean propertyDrag = false;
	
	private boolean partScaleX = false;
	private boolean partScaleY = false;
	private boolean partDrag = false;
	
	private int cursor_type = 0;
	private final int normal = 0, grab = 1, hresize = 2, vresize = 3, typing = 4;
	
	public void tick() {
		
		switch(cursor_type) {
		case normal:
			GLFW.glfwSetCursor(Utils.window, Utils.NORMAL_CURSOR);
			break;
		case grab:
			GLFW.glfwSetCursor(Utils.window, Utils.HAND_CURSOR);
			break;
		case hresize:
			GLFW.glfwSetCursor(Utils.window, Utils.HRESIZE_CURSOR);
			break;
		case vresize:
			GLFW.glfwSetCursor(Utils.window, Utils.VRESIZE_CURSOR);
			break;
		case typing:
			GLFW.glfwSetCursor(Utils.window, Utils.TYPE_CURSOR);
			break;
		}
		
		cursor_type = normal;
		double[] xpos = new double[1];
		double[] ypos = new double[1];
		GLFW.glfwGetCursorPos(Utils.window, xpos, ypos);
		Mouse.x = (float) xpos[0];
		Mouse.y = (float) ypos[0];
		
		if (Settings.isMouseButtonDown(2)) {
			Camera.position.add(Camera.getUp().mul((Mouse.lastY - Mouse.y) * 0.01f));
			Camera.position.add(Camera.getRight().mul((Mouse.lastX - Mouse.x) * 0.01f));
			cursor_type = grab;
		}
		if (Settings.isMouseButtonDown(1)) {
			Camera.rotation.y += Mouse.x - Mouse.lastX;
			Camera.rotation.x += Mouse.y - Mouse.lastY;
			cursor_type = grab;
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
		
		
		widget.setPosition(0, 0);
		widget.setSize(context.getFramebufferSize().x + 1, context.getFramebufferSize().y + 1);
		
		panel.getStyle().setMinimumSize(widget.getSize().x + 4, widget.getSize().y + 4);
		panel.setPosition(-1, -1);
		navigation.setPosition(0, 0);
		navigation.getStyle().setMinimumSize(widget.getSize().x, 20);
		
		if (Mouse.lastX >= PROPERTIES_PANEL.getPosition().x && Mouse.lastY >= PROPERTIES_PANEL.getPosition().y &&
				Mouse.lastX <= PROPERTIES_PANEL.getSize().x + PROPERTIES_PANEL.getPosition().x && 
				Mouse.lastY <= PROPERTIES_PANEL.getPosition().y + 20) {
			cursor_type = grab;
		}
		if (Mouse.lastX >= PROPERTIES_PANEL.getPosition().x - 3 && Mouse.lastX <= PROPERTIES_PANEL.getPosition().x + 1
					&& Mouse.lastY >= PROPERTIES_PANEL.getPosition().y && 
					Mouse.lastY <= PROPERTIES_PANEL.getPosition().y + PROPERTIES_PANEL.getSize().y ||
					Mouse.lastX >= PROPERTIES_PANEL.getPosition().x + PROPERTIES_PANEL.getSize().x - 1 &&
					Mouse.lastX <= PROPERTIES_PANEL.getPosition().x + PROPERTIES_PANEL.getSize().x + 3
					&& Mouse.lastY >= PROPERTIES_PANEL.getPosition().y && 
					Mouse.lastY <= PROPERTIES_PANEL.getPosition().y + PROPERTIES_PANEL.getSize().y) {
			cursor_type = hresize;
		}
		
		if (Mouse.lastY >= PROPERTIES_PANEL.getPosition().y - 3 && Mouse.lastY <= PROPERTIES_PANEL.getPosition().y + 1
				&& Mouse.lastX >= PROPERTIES_PANEL.getPosition().x && 
				Mouse.lastX <= PROPERTIES_PANEL.getPosition().x + PROPERTIES_PANEL.getSize().x ||
				Mouse.lastY >= PROPERTIES_PANEL.getPosition().y - 3 + PROPERTIES_PANEL.getSize().y
				&& Mouse.lastY <= PROPERTIES_PANEL.getPosition().y + 3 + PROPERTIES_PANEL.getSize().y
				&& Mouse.lastX >= PROPERTIES_PANEL.getPosition().x && 
				Mouse.lastX <= PROPERTIES_PANEL.getPosition().x + PROPERTIES_PANEL.getSize().x) {
			cursor_type = vresize;
		}
		
		
		if (Settings.isMouseButtonDown(0)) {
			if (Mouse.lastX >= PROPERTIES_PANEL.getPosition().x && Mouse.lastY >= PROPERTIES_PANEL.getPosition().y &&
					Mouse.lastX <= PROPERTIES_PANEL.getSize().x + PROPERTIES_PANEL.getPosition().x && 
					Mouse.lastY <= PROPERTIES_PANEL.getPosition().y + 20) {
				if (mouseJustDown) {
					propertyDrag = true;
				}
				if (propertyDrag)
				PROPERTIES_PANEL.getPosition().add(Mouse.x - Mouse.lastX, Mouse.y - Mouse.lastY);
			}
			if (Mouse.lastX >= PROPERTIES_PANEL.getPosition().x - 3 && Mouse.lastX <= PROPERTIES_PANEL.getPosition().x + 1
					&& Mouse.lastY >= PROPERTIES_PANEL.getPosition().y && 
					Mouse.lastY <= PROPERTIES_PANEL.getPosition().y + PROPERTIES_PANEL.getSize().y) {
				if (mouseJustDown) {
					propertyScaleX = true;
				}
				if (propertyScaleX) {
					PROPERTIES_PANEL.getPosition().add(Mouse.x - Mouse.lastX, 0);
					if (PROPERTIES_PANEL.getSize().x > 20) {
						PROPERTIES_PANEL.getSize().sub(Mouse.x - Mouse.lastX, 0);
					}else {
						PROPERTIES_PANEL.getSize().add(1, 0);
					}
				}
			}
			if (Mouse.lastX >= PROPERTIES_PANEL.getPosition().x + PROPERTIES_PANEL.getSize().x - 1 &&
					Mouse.lastX <= PROPERTIES_PANEL.getPosition().x + PROPERTIES_PANEL.getSize().x + 3
					&& Mouse.lastY >= PROPERTIES_PANEL.getPosition().y && 
					Mouse.lastY <= PROPERTIES_PANEL.getPosition().y + PROPERTIES_PANEL.getSize().y) {
				if (mouseJustDown) {
					propertyScaleX = true;
				}
				if (propertyScaleX) {
					if (PROPERTIES_PANEL.getSize().x > 20) {
						PROPERTIES_PANEL.getSize().add(Mouse.x - Mouse.lastX, 0);
					}else {
						PROPERTIES_PANEL.getSize().add(1, 0);
					}
				}
			}
			
			
			if (Mouse.lastY >= PROPERTIES_PANEL.getPosition().y - 3 && Mouse.lastY <= PROPERTIES_PANEL.getPosition().y + 1
					&& Mouse.lastX >= PROPERTIES_PANEL.getPosition().x && 
					Mouse.lastX <= PROPERTIES_PANEL.getPosition().x + PROPERTIES_PANEL.getSize().x) {
				if (mouseJustDown) {
					propertyScaleY = true;
				}
				if (propertyScaleY) {
					PROPERTIES_PANEL.getPosition().add(0, Mouse.y - Mouse.lastY);
					if (PROPERTIES_PANEL.getSize().y > 20) {
						PROPERTIES_PANEL.getSize().sub(0, Mouse.y - Mouse.lastY);
					}else {
						PROPERTIES_PANEL.getSize().add(0, 1);
					}
				}
			}
			
			if (Mouse.lastY >= PROPERTIES_PANEL.getPosition().y - 3 + PROPERTIES_PANEL.getSize().y
					&& Mouse.lastY <= PROPERTIES_PANEL.getPosition().y + 3 + PROPERTIES_PANEL.getSize().y
					&& Mouse.lastX >= PROPERTIES_PANEL.getPosition().x && 
					Mouse.lastX <= PROPERTIES_PANEL.getPosition().x + PROPERTIES_PANEL.getSize().x) {
				if (mouseJustDown) {
					propertyScaleY = true;
				}
				if (propertyScaleY) {
					if (PROPERTIES_PANEL.getSize().y > 20) {
						PROPERTIES_PANEL.getSize().add(0, Mouse.y - Mouse.lastY);
					}else {
						PROPERTIES_PANEL.getSize().add(0, 1);
					}
				}
			}
			
		} else 
		{
			propertyDrag = false;
			propertyScaleX = false;
			propertyScaleY = false;
			
			
		}
		
		if (Mouse.lastX >= PARTS_PANEL.getPosition().x && Mouse.lastY >= PARTS_PANEL.getPosition().y &&
				Mouse.lastX <= PARTS_PANEL.getSize().x + PARTS_PANEL.getPosition().x && 
				Mouse.lastY <= PARTS_PANEL.getPosition().y + 20) {
			cursor_type = grab;
		}
		if (Mouse.lastX >= PARTS_PANEL.getPosition().x - 3 && Mouse.lastX <= PARTS_PANEL.getPosition().x + 1
					&& Mouse.lastY >= PARTS_PANEL.getPosition().y && 
					Mouse.lastY <= PARTS_PANEL.getPosition().y + PARTS_PANEL.getSize().y ||
					Mouse.lastX >= PARTS_PANEL.getPosition().x + PARTS_PANEL.getSize().x - 1 &&
					Mouse.lastX <= PARTS_PANEL.getPosition().x + PARTS_PANEL.getSize().x + 3
					&& Mouse.lastY >= PARTS_PANEL.getPosition().y && 
					Mouse.lastY <= PARTS_PANEL.getPosition().y + PARTS_PANEL.getSize().y) {
			cursor_type = hresize;
		}
		
		if (Mouse.lastY >= PARTS_PANEL.getPosition().y - 3 && Mouse.lastY <= PARTS_PANEL.getPosition().y + 1
				&& Mouse.lastX >= PARTS_PANEL.getPosition().x && 
				Mouse.lastX <= PARTS_PANEL.getPosition().x + PARTS_PANEL.getSize().x ||
				Mouse.lastY >= PARTS_PANEL.getPosition().y - 3 + PARTS_PANEL.getSize().y
				&& Mouse.lastY <= PARTS_PANEL.getPosition().y + 3 + PARTS_PANEL.getSize().y
				&& Mouse.lastX >= PARTS_PANEL.getPosition().x && 
				Mouse.lastX <= PARTS_PANEL.getPosition().x + PARTS_PANEL.getSize().x) {
			cursor_type = vresize;
		}
		
		
		if (Settings.isMouseButtonDown(0)) {
			if (Mouse.lastX >= PARTS_PANEL.getPosition().x && Mouse.lastY >= PARTS_PANEL.getPosition().y &&
					Mouse.lastX <= PARTS_PANEL.getSize().x + PARTS_PANEL.getPosition().x && 
					Mouse.lastY <= PARTS_PANEL.getPosition().y + 20) {
				if (mouseJustDown) {
					partDrag = true;
				}
				if (partDrag)
				PARTS_PANEL.getPosition().add(Mouse.x - Mouse.lastX, Mouse.y - Mouse.lastY);
			}
			if (Mouse.lastX >= PARTS_PANEL.getPosition().x - 3 && Mouse.lastX <= PARTS_PANEL.getPosition().x + 1
					&& Mouse.lastY >= PARTS_PANEL.getPosition().y && 
					Mouse.lastY <= PARTS_PANEL.getPosition().y + PARTS_PANEL.getSize().y) {
				if (mouseJustDown) {
					partScaleX = true;
				}
				if (partScaleX) {
					PARTS_PANEL.getPosition().add(Mouse.x - Mouse.lastX, 0);
					if (PARTS_PANEL.getSize().x > 20) {
						PARTS_PANEL.getSize().sub(Mouse.x - Mouse.lastX, 0);
					}else {
						PARTS_PANEL.getSize().add(1, 0);
					}
				}
			}
			if (Mouse.lastX >= PARTS_PANEL.getPosition().x + PARTS_PANEL.getSize().x - 1 &&
					Mouse.lastX <= PARTS_PANEL.getPosition().x + PARTS_PANEL.getSize().x + 3
					&& Mouse.lastY >= PARTS_PANEL.getPosition().y && 
					Mouse.lastY <= PARTS_PANEL.getPosition().y + PARTS_PANEL.getSize().y) {
				if (mouseJustDown) {
					partScaleX = true;
				}
				if (partScaleX) {
					if (PARTS_PANEL.getSize().x > 20) {
						PARTS_PANEL.getSize().add(Mouse.x - Mouse.lastX, 0);
					}else {
						PARTS_PANEL.getSize().add(1, 0);
					}
				}
			}
			
			
			if (Mouse.lastY >= PARTS_PANEL.getPosition().y - 3 && Mouse.lastY <= PARTS_PANEL.getPosition().y + 1
					&& Mouse.lastX >= PARTS_PANEL.getPosition().x && 
					Mouse.lastX <= PARTS_PANEL.getPosition().x + PARTS_PANEL.getSize().x) {
				if (mouseJustDown) {
					partScaleY = true;
				}
				if (partScaleY) {
					PARTS_PANEL.getPosition().add(0, Mouse.y - Mouse.lastY);
					if (PARTS_PANEL.getSize().y > 20) {
						PARTS_PANEL.getSize().sub(0, Mouse.y - Mouse.lastY);
					}else {
						PARTS_PANEL.getSize().add(0, 1);
					}
				}
			}
			
			if (Mouse.lastY >= PARTS_PANEL.getPosition().y - 3 + PARTS_PANEL.getSize().y
					&& Mouse.lastY <= PARTS_PANEL.getPosition().y + 3 + PARTS_PANEL.getSize().y
					&& Mouse.lastX >= PARTS_PANEL.getPosition().x && 
					Mouse.lastX <= PARTS_PANEL.getPosition().x + PARTS_PANEL.getSize().x) {
				if (mouseJustDown) {
					partScaleY = true;
				}
				if (partScaleY) {
					if (PARTS_PANEL.getSize().y > 20) {
						PARTS_PANEL.getSize().add(0, Mouse.y - Mouse.lastY);
					}else {
						PARTS_PANEL.getSize().add(0, 1);
					}
				}
			}
			
		} else 
		{
			partDrag = false;
			partScaleX = false;
			partScaleY = false;
			
			
		}
		
		parts_scroll_panel.setSize(PARTS_PANEL.getSize().x, PARTS_PANEL.getSize().y);
		parts_panel_name.setSize(PARTS_PANEL.getSize().x, 20);
		
		properties_scroll_panel.setSize(PROPERTIES_PANEL.getSize().x, PROPERTIES_PANEL.getSize().y);
		properties_panel_name.setSize(PROPERTIES_PANEL.getSize().x, 20);
		part_name.setSize(PROPERTIES_PANEL.getSize().x, 20);
//		xPos.setSize(PROPERTIES_PANEL.getSize().x, 20);
//		yPos.setSize(PROPERTIES_PANEL.getSize().x, 20);
//		zPos.setSize(PROPERTIES_PANEL.getSize().x, 20);

		if (!xPos.isFocused()) 
		{
			if (selectedPart != null)
			xPos.getTextState().setText(""+selectedPart.position.x);
		}
		if (!yPos.isFocused()) 
		{
			if (selectedPart != null)
			yPos.getTextState().setText(""+selectedPart.position.y);
		}
		if (!zPos.isFocused()) 
		{
			if (selectedPart != null)
			zPos.getTextState().setText(""+selectedPart.position.z);
		}
		
		if (!xRot.isFocused()) 
		{
			if (selectedPart != null)
				xRot.getTextState().setText(""+selectedPart.getEulerAngles().x);
		}
		if (!yRot.isFocused()) 
		{
			if (selectedPart != null)
				yRot.getTextState().setText(""+selectedPart.getEulerAngles().y);
		}
		if (!zRot.isFocused()) 
		{
			if (selectedPart != null)
				zRot.getTextState().setText(""+selectedPart.getEulerAngles().z);
		}
		
		if (!xScale.isFocused()) 
		{
			if (selectedPart != null)
				xScale.getTextState().setText(""+selectedPart.scale.x);
		}
		if (!yScale.isFocused()) 
		{
			if (selectedPart != null)
				yScale.getTextState().setText(""+selectedPart.scale.y);
		}
		if (!zScale.isFocused()) 
		{
			if (selectedPart != null)
				zScale.getTextState().setText(""+selectedPart.scale.z);
		}
		
		if (!xSize.isFocused()) 
		{
			if (selectedPart != null)
				xSize.getTextState().setText(""+selectedPart.size.x);
		}
		if (!ySize.isFocused()) 
		{
			if (selectedPart != null)
				ySize.getTextState().setText(""+selectedPart.size.y);
		}
		if (!zSize.isFocused()) 
		{
			if (selectedPart != null)
				zSize.getTextState().setText(""+selectedPart.size.z);
		}
		
		if (!xCenter.isFocused()) 
		{
			if (selectedPart != null)
				xCenter.getTextState().setText(""+selectedPart.origin.x);
		}
		if (!yCenter.isFocused()) 
		{
			if (selectedPart != null)
				yCenter.getTextState().setText(""+selectedPart.origin.y);
		}
		if (!zCenter.isFocused()) 
		{
			if (selectedPart != null)
				zCenter.getTextState().setText(""+selectedPart.origin.z);
		}
		
		if (PROPERTIES_PANEL.getPosition().x < -PROPERTIES_PANEL.getSize().x + 20) {
			PROPERTIES_PANEL.setPosition(-PROPERTIES_PANEL.getSize().x + 20 + 2, PROPERTIES_PANEL.getPosition().y);
		}
		if (PROPERTIES_PANEL.getPosition().y < 20) {
			PROPERTIES_PANEL.setPosition(PROPERTIES_PANEL.getPosition().x, 22);
		}
		if (PROPERTIES_PANEL.getPosition().x + 20 > widget.getSize().x) {
			PROPERTIES_PANEL.setPosition(widget.getSize().x - 20 - 2, PROPERTIES_PANEL.getPosition().y);
		}
		if (PROPERTIES_PANEL.getPosition().y + 20 > widget.getSize().y) {
			PROPERTIES_PANEL.setPosition(PROPERTIES_PANEL.getPosition().x, widget.getSize().y - 22);
		}
		
		if (this.Xselected || this.Yselected || this.Zselected) {
			this.cursor_type = this.grab;
		}
		
		
		
		Mouse.lastX = Mouse.x;
		Mouse.lastY = Mouse.y;
	}
	
	public void render(ShaderProgram shader) {
		
	}
	
	public void render3D(ShaderProgram shaderProgram) {
		
		int[] width = new int[1];
		int[] height = new int[1];
		GLFW.glfwGetWindowSize(Utils.window, width, height);
		double mx = Mouse.x * (1920.0 / width[0]);
		double my = (height[0] - Mouse.y) * (1080.0 / height[0]);
		if (Double.isInfinite(mx)) {
			mx = 0;
			my = 0;
		}
		
		shaderProgram.unbind();

		context.updateGlfwWindow();
		
		Vector2i windowSize = context.getFramebufferSize();
		
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1);
		GL11.glViewport(0, 0, (int)windowSize.x, (int)windowSize.y);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
//		nRenderer.render(frame, context);
		
		
		shaderProgram.bind();

		if (model != null) {
			model.render(shaderProgram, true, selectedPart);
			if (this.selectedPart != null) {
				if (selectionMesh != null)
				this.selectedPart.renderSelected(shaderProgram, selectionMesh);
			}
		}
		
		for (int x = -floorSize; x < floorSize + 1; x++) {
			for (int z = -floorSize; z < floorSize + 1; z++) {
				if (Point.distance(x, z, 0, 0) <= floorSize - 0.5f) {
					MeshRenderer.renderMesh(block, new Vector3f(-0.5f + x, 0, -0.5f + z), shaderProgram);
				}
			}
		}
		
		MeshRenderer.renderMesh(grid, new Vector3f(0, 0.0001f, 0), shaderProgram);
		
		MouseIntersection closest = null;
		float distance = Float.MAX_VALUE;
		Part part = null;
		if (this.model != null) {
			ArrayList<Part> parts = this.model.getParts();
			for (Part p : parts) {
				RayBox box = new RayBox();
				Vector3f size = new Vector3f(p.size).mul(p.scale).mul(Part.SCALING * 2.0f);
				Vector3f position = new Vector3f(p.position).mul(Part.SCALING);
				box.min = new Vector3f(position.x - size.x / 2.0f, position.y - size.y / 2.0f, position.z - size.z / 2.0f);
				box.max = new Vector3f(box.min).add(size.x, size.y, size.z);
				
				MouseIntersection i = this.getMouseIntersection(box, p.rotation);
				if (i != null) {
					if (i.distance < distance) {
						distance = i.distance;
						closest = i;
						part = p;
					}
				}
			}
		}
		
		
		if (selectedPart != null) {
			if (this.selectionMode == SelectionMode.TRANSLATION) {
				handleTranslation(shaderProgram);
			}
			if (this.selectionMode == SelectionMode.SCALE) {
				handleScaling(shaderProgram);
			}
			
			if (this.selectionMode == SelectionMode.ROTATION) {
				handleRotation(shaderProgram);
			}
		}
		mouseJustDown = false;
		if (Settings.isMouseButtonJustDown(0)) {
			mouseJustDown = true;
			if (closest != null) {
				selectPart(part);
			} else {
				if (!Xselected && !Yselected && !Zselected)
				deselect();
			}
		}
		
		shaderProgram.unbind();
		GL11.glViewport(0, 0, (int)windowSize.x, (int)windowSize.y);
//		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
		nRenderer.render(frame, context);

		systemEventProcessor.processEvents(frame, context);
		EventProcessorProvider.getInstance().processEvents();
		LayoutManager.getInstance().layout(frame);
		AnimatorProvider.getAnimator().runAnimations();
		
		
		shaderProgram.bind();
	}
	
	public void handleRotation(ShaderProgram shaderProgram) {
		
		int[] width = new int[1];
		int[] height = new int[1];
		GLFW.glfwGetWindowSize(Utils.window, width, height);
		double mx = Mouse.x * (1920.0 / width[0]);
		double my = (height[0] - Mouse.y) * (1080.0 / height[0]);
		if (Double.isInfinite(mx)) {
			mx = 0;
			my = 0;
		}
		
		double lmx = lastMousePos3D.x;
		double lmy = lastMousePos3D.y;
		if (Double.isInfinite(lmx)) {
			lmx = 0;
			lmy = 0;
		}
		lastMousePos3D.x = (float)mx;
		lastMousePos3D.y = (float)my;
		

		
		float DISTANCE = Float.MAX_VALUE;
		{
			float w = 0.05f;
			Vector3f position = new Vector3f(selectedPart.position).mul(Part.SCALING);
			Vector3f size = new Vector3f(selectedPart.size).mul(selectedPart.scale).mul(Part.SCALING * 0.5f);
			size.rotate(selectedPart.rotation);
			size.absolute();
			rotationHandle.texture = Textures.GREEN;
			MeshRenderer.renderMesh(rotationHandle, new Vector3f(position).add(0, size.y + 1, 0), new Vector3f(0.5f, w, 0.5f), shaderProgram);
			
			position = new Vector3f(selectedPart.position).mul(Part.SCALING);
			size = new Vector3f(selectedPart.size).mul(selectedPart.scale).mul(Part.SCALING * 0.5f);
			size.rotate(selectedPart.rotation);
			size.absolute();
			rotationHandle.texture = Textures.RED;
			MeshRenderer.renderMesh(rotationHandle, new Vector3f(position).add(size.x + 1, 0, 0), new Vector3f(w, 0.5f, 0.5f), shaderProgram);
			
			position = new Vector3f(selectedPart.position).mul(Part.SCALING);
			size = new Vector3f(selectedPart.size).mul(selectedPart.scale).mul(Part.SCALING * 0.5f);
			size.rotate(selectedPart.rotation);
			size.absolute();
			rotationHandle.texture = Textures.BLUE;
			MeshRenderer.renderMesh(rotationHandle, new Vector3f(position).add(0, 0, size.z + 1), new Vector3f(0.5f, 0.5f, w), shaderProgram);
		}
		
		
		float w = 0.05f;
		Vector3f position = new Vector3f(selectedPart.position).mul(Part.SCALING);
		Vector3f size = new Vector3f(selectedPart.size).mul(selectedPart.scale).mul(Part.SCALING * 0.5f);
		size.rotate(selectedPart.rotation);
		size.absolute();
		RayBox Y_HANDLE = new RayBox();
		Y_HANDLE.min = new Vector3f(position).sub(0.5f, w / 2.0f, 0.5f).add(0, size.y + 1, 0);
		Y_HANDLE.max = new Vector3f(position).add(0.5f, w / 2.0f, 0.5f).add(0, size.y + 1, 0);
		MouseIntersection i = this.getMouseIntersection(Y_HANDLE, new Vector3f(0, 0, 0));
		if (i != null) {
			DISTANCE = i.distance;
			if (Settings.isMouseButtonJustDown(0)) {
				this.Yselected = true;
				lastMousePos3D.x = (float)mx;
				lastMousePos3D.y = (float)my;
				mousePos3D.x = (float)mx;
				mousePos3D.y = (float)my;
				lmx = mx;
				lmy = my;
				if (Double.isInfinite(lmx)) {
					lmx = 0;
					lmy = 0;
				}
			}
		}
		
		position = new Vector3f(selectedPart.position).mul(Part.SCALING);
		size = new Vector3f(selectedPart.size).mul(selectedPart.scale).mul(Part.SCALING * 0.5f);
		size.rotate(selectedPart.rotation);
		size.absolute();
		RayBox X_HANDLE = new RayBox();
		X_HANDLE.min = new Vector3f(position).sub(w / 2.0f, 0.5f, 0.5f).add(size.x + 1, 0, 0);
		X_HANDLE.max = new Vector3f(position).add(w / 2.0f, 0.5f, 0.5f).add(size.x + 1, 0, 0);
		i = this.getMouseIntersection(X_HANDLE, new Vector3f(0, 0, 0));
		if (i != null) {
			if (i.distance < DISTANCE) {
				DISTANCE = i.distance;
				if (Settings.isMouseButtonJustDown(0)) {
					this.Xselected = true;
					this.Yselected = false;
					lastMousePos3D.x = (float)mx;
					lastMousePos3D.y = (float)my;
					mousePos3D.x = (float)mx;
					mousePos3D.y = (float)my;
					lmx = mx;
					lmy = my;
					if (Double.isInfinite(lmx)) {
						lmx = 0;
						lmy = 0;
					}
				}
			}
		}
		
		position = new Vector3f(selectedPart.position).mul(Part.SCALING);
		size = new Vector3f(selectedPart.size).mul(selectedPart.scale).mul(Part.SCALING * 0.5f);
		size.rotate(selectedPart.rotation);
		size.absolute();
		RayBox Z_HANDLE = new RayBox();
		Z_HANDLE.min = new Vector3f(position).sub(0.5f, 0.5f, w / 2.0f).add(0, 0, size.z + 1);
		Z_HANDLE.max = new Vector3f(position).add(0.5f, 0.5f, w / 2.0f).add(0, 0, size.z + 1);
		i = this.getMouseIntersection(Z_HANDLE, new Vector3f(0, 0, 0));
		if (i != null) {
			if (i.distance < DISTANCE) {
				DISTANCE = i.distance;
				if (Settings.isMouseButtonJustDown(0)) {
					this.Zselected = true;
					this.Xselected = false;
					this.Yselected = false;
					lastMousePos3D.x = (float)mx;
					lastMousePos3D.y = (float)my;
					mousePos3D.x = (float)mx;
					mousePos3D.y = (float)my;
					lmx = mx;
					lmy = my;
					if (Double.isInfinite(lmx)) {
						lmx = 0;
						lmy = 0;
					}
					
					
				}
			}
		}
		
		if (this.Yselected) {
			float angle = (float)Math.toDegrees(Math.atan2(mx - mousePos3D.x, my - mousePos3D.y));
			float lastAngle = (float)Math.toDegrees(Math.atan2(lmx - mousePos3D.x, lmy - mousePos3D.y));
			if (angle < 0) {
				angle = 360 + angle;
			}
			if (lastAngle < 0) {
				lastAngle = 360 + lastAngle;
			}
			if (Vector2f.distance((float)mx, (float)my, mousePos3D.x, mousePos3D.y) > 10)
			selectedPart.rotate(new Vector3f(0, lastAngle - angle, 0));
			
		}
		
		if (this.Xselected) {
			float angle = (float)Math.toDegrees(Math.atan2(mx - mousePos3D.x, my - mousePos3D.y));
			float lastAngle = (float)Math.toDegrees(Math.atan2(lmx - mousePos3D.x, lmy - mousePos3D.y));
			if (angle < 0) {
				angle = 360 + angle;
			}
			if (lastAngle < 0) {
				lastAngle = 360 + lastAngle;
			}
			if (Vector2f.distance((float)mx, (float)my, mousePos3D.x, mousePos3D.y) > 10)
			selectedPart.rotate(new Vector3f(lastAngle - angle, 0, 0));
			
			
		}
		
		if (this.Zselected) {
			float angle = (float)Math.toDegrees(Math.atan2(mx - mousePos3D.x, my - mousePos3D.y));
			float lastAngle = (float)Math.toDegrees(Math.atan2(lmx - mousePos3D.x, lmy - mousePos3D.y));
			if (angle < 0) {
				angle = 360 + angle;
			}
			if (lastAngle < 0) {
				lastAngle = 360 + lastAngle;
			}
			if (Vector2f.distance((float)mx, (float)my, mousePos3D.x, mousePos3D.y) > 10)
			selectedPart.rotate(new Vector3f(0, 0, lastAngle - angle));
			
			
		}
		
		if (!Settings.isMouseButtonDown(0)) {
			this.Xselected = false;
			this.Yselected = false;
			this.Zselected = false;
		}
	}
	
	public Vector3f getRotationDeltas(Vector3f position, Vector3f rotation) {
		float xAngle = (float)Math.toDegrees(Math.atan2(mousePos3D.y - position.y, mousePos3D.z - position.z));
		float yAngle = (float)Math.toDegrees(Math.atan2(mousePos3D.z - position.z, mousePos3D.x - position.x));
		float zAngle = (float)Math.toDegrees(Math.atan2(mousePos3D.y - position.y, mousePos3D.x - position.x));
		
		float lastXAngle = (float)Math.toDegrees(Math.atan2(lastMousePos3D.y - position.y, lastMousePos3D.z - position.z));
		float lastYAngle = (float)Math.toDegrees(Math.atan2(lastMousePos3D.z - position.z, lastMousePos3D.x - position.x));
		float lastZAngle = (float)Math.toDegrees(Math.atan2(lastMousePos3D.y - position.y, lastMousePos3D.x - position.x));
		
		Vector3f rot = new Vector3f(lastXAngle - xAngle, lastYAngle - yAngle, lastZAngle - zAngle);
		rot = Raytracer.rotateDir(rot, rotation);
		return rot;
	}
	
	public void handleScaling(ShaderProgram shaderProgram) {
		int[] width = new int[1];
		int[] height = new int[1];
		GLFW.glfwGetWindowSize(Utils.window, width, height);
		double mx = Mouse.x * (1920.0 / width[0]);
		double my = (height[0] - Mouse.y) * (1080.0 / height[0]);
		if (Double.isInfinite(mx)) {
			mx = 0;
			my = 0;
		}
		
		double lmx = lastMousePos3D.x;
		double lmy = lastMousePos3D.y;
		if (Double.isInfinite(lmx)) {
			lmx = 0;
			lmy = 0;
		}
		
		mousePos3D.x = (float)mx;
		mousePos3D.y = (float)my;
		mousePos3D.z = 0;
		{
			Vector3f position = new Vector3f(selectedPart.position).mul(Part.SCALING);
			Vector3f size = new Vector3f(selectedPart.size).mul(selectedPart.scale).mul(Part.SCALING);
			position.add(-0.1f, size.y + 0.5f, -0.1f);
			position.rotate(selectedPart.rotation);
			cube.texture = Textures.GREEN;
			MeshRenderer.renderMesh(cube, position, new Vector3f(0.2f, 0.2f, 0.2f), shaderProgram);

			position = new Vector3f(selectedPart.position).mul(Part.SCALING);
			size = new Vector3f(selectedPart.size).mul(selectedPart.scale).mul(Part.SCALING);
			position.add(size.x + 0.5f, -0.1f, -0.1f);
			position.rotate(selectedPart.rotation);
			cube.texture = Textures.RED;
			MeshRenderer.renderMesh(cube, position, new Vector3f(0.2f, 0.2f, 0.2f), shaderProgram);
			
			position = new Vector3f(selectedPart.position).mul(Part.SCALING);
			size = new Vector3f(selectedPart.size).mul(selectedPart.scale).mul(Part.SCALING);
			position.add(-0.1f, -0.1f, size.z + 0.5f);
			position.rotate(selectedPart.rotation);
			cube.texture = Textures.BLUE;
			MeshRenderer.renderMesh(cube, position, new Vector3f(0.2f, 0.2f, 0.2f), shaderProgram);
		}
		Vector3f position = new Vector3f(selectedPart.position).mul(Part.SCALING);
		Vector3f size = new Vector3f(selectedPart.size).mul(selectedPart.scale).mul(Part.SCALING);
		position.add(-0.1f, size.y + 0.5f, -0.1f);
		position.rotate(selectedPart.rotation);
		RayBox Y_HANDLE = new RayBox();
		Y_HANDLE.min = new Vector3f(new Vector3f(position));
		Y_HANDLE.max = new Vector3f(Y_HANDLE.min).add(0.2f, 0.2f, 0.2f);
		
		MouseIntersection i = getMouseIntersection(Y_HANDLE, new Vector3f());
		if (i != null) {
			if (Settings.isMouseButtonJustDown(0)) {
				this.Yselected = true;
				
				lastMousePos3D = new Vector3f(lastMousePos3D);
			}
		}
		if (this.Yselected) {
			if (this.mousePos3D.distance(lastMousePos3D) > 50) {
				System.out.println(this.mousePos3D.y - lastMousePos3D.y);
				this.lastMousePos3D = new Vector3f(this.mousePos3D);
				int dir = 1;
				if (Settings.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
					dir = -1;
				}
				this.selectedPart.size.y+=dir;
				this.selectedPart.buildPart();
			}
		}
		
		
		position = new Vector3f(selectedPart.position).mul(Part.SCALING);
		size = new Vector3f(selectedPart.size).mul(selectedPart.scale).mul(Part.SCALING);
		position.add(size.x + 0.5f, -0.1f, -0.1f);
		position.rotate(selectedPart.rotation);
		RayBox X_HANDLE = new RayBox();
		X_HANDLE.min = new Vector3f(new Vector3f(position));
		X_HANDLE.max = new Vector3f(X_HANDLE.min).add(0.2f, 0.2f, 0.2f);
		
		i = getMouseIntersection(X_HANDLE, new Vector3f());
		if (i != null) {
			if (Settings.isMouseButtonJustDown(0)) {
				this.Xselected = true;
				mousePos3D = i.hit;
				lastMousePos3D = i.hit;
			}
		}
		if (this.Xselected) {
			X_HANDLE.min = new Vector3f(position.add(-100, -0.1f, -100));
			X_HANDLE.max = new Vector3f(X_HANDLE.min).add(200, 0.1f, 200);
			i = getMouseIntersection(X_HANDLE, new Vector3f());
			if (i == null) {
				X_HANDLE.min = new Vector3f(position.add(-100, -100, -0.1f));
				X_HANDLE.max = new Vector3f(X_HANDLE.min).add(200, 200, 0.1f);
				i = getMouseIntersection(X_HANDLE, new Vector3f());
			}
			if (i != null) {
				if (Math.abs(i.hit.x - lastMousePos3D.x) > Part.SCALING) {
					mousePos3D.x = (int)(i.hit.x * (1.0f / Part.SCALING)) * Part.SCALING;
					selectedPart.position.x = (int)selectedPart.position.x;
				}
				if (mousePos3D.x < lastMousePos3D.x) {
					selectedPart.size.add(new Vector3i((int)((mousePos3D.x - lastMousePos3D.x) * 16), 0, 0));
				} else {
					selectedPart.size.add(new Vector3i((int)((mousePos3D.x - lastMousePos3D.x) * 32), 0, 0));
				}
				selectedPart.buildPart();
				this.selectPart(selectedPart);
				lastMousePos3D.x = mousePos3D.x;
			}
		}
		
		
		position = new Vector3f(selectedPart.position).mul(Part.SCALING);
		size = new Vector3f(selectedPart.size.x, selectedPart.size.y, selectedPart.size.z).mul(selectedPart.scale).mul(Part.SCALING);
		position.add(-0.1f, -0.1f, size.z + 0.5f);
		position.rotate(selectedPart.rotation);
		RayBox Z_HANDLE = new RayBox();
		Z_HANDLE.min = new Vector3f(new Vector3f(position));
		Z_HANDLE.max = new Vector3f(Z_HANDLE.min).add(0.2f, 0.2f, 0.2f);
		
		i = getMouseIntersection(Z_HANDLE, new Vector3f());
		if (i != null) {
			if (Settings.isMouseButtonJustDown(0)) {
				this.Zselected = true;
				mousePos3D = i.hit;
				lastMousePos3D = i.hit;
			}
		}
		if (this.Zselected) {
			Z_HANDLE.min = new Vector3f(position.add(-100, -0.1f, -100));
			Z_HANDLE.max = new Vector3f(Z_HANDLE.min).add(200, 0.1f, 200);
			i = getMouseIntersection(Z_HANDLE, new Vector3f());
			if (i == null) {
				Z_HANDLE.min = new Vector3f(position.add(-0.1f, -100, -100));
				Z_HANDLE.max = new Vector3f(Z_HANDLE.min).add(0.1f, 200, 200);
				i = getMouseIntersection(Z_HANDLE, new Vector3f());
			}
			if (i != null) {
				if (Math.abs(i.hit.z - lastMousePos3D.z) > Part.SCALING) {
					mousePos3D.z = (int)(i.hit.z * (1.0f / Part.SCALING)) * Part.SCALING;
					selectedPart.position.z = (int)selectedPart.position.z;
				}
				if (mousePos3D.z < lastMousePos3D.z) {
					selectedPart.size.add(new Vector3i(0, 0, (int)((mousePos3D.z - lastMousePos3D.z) * 16)));
				} else {
					selectedPart.size.add(new Vector3i(0, 0, (int)((mousePos3D.z - lastMousePos3D.z) * 32)));
				}
				selectedPart.buildPart();
				this.selectPart(selectedPart);
				lastMousePos3D.z = mousePos3D.z;
			}
		}
		
		if (!Settings.isMouseButtonDown(0)) {
			this.Xselected = false;
			this.Yselected = false;
			this.Zselected = false;
			mousePos3D = new Vector3f(0, 0, 0);
		}
	}
	public void handleTranslation(ShaderProgram shaderProgram) {
		{
			handle.mesh.texture = Textures.POSITION_HANDLE_Y;
			Vector3f position = new Vector3f(selectedPart.position).mul(Part.SCALING);
			Vector3f size = new Vector3f(selectedPart.size).mul(selectedPart.scale).mul(Part.SCALING);
			size.rotate(selectedPart.rotation);
			size.absolute();
			position.add(0.25f, size.y + 0.5f, 0);
			
			MeshRenderer.renderMesh(handle.mesh, position, new Vector3f(0.5f, 0.5f, 0.5f), shaderProgram);
			MeshRenderer.renderMesh(handle.mesh, new Vector3f(position).sub(0.25f, 0.0f, 0.25f), new Vector3f(0, 90, 0), new Vector3f(0.5f, 0.5f, 0.5f), shaderProgram);

			
			handle.mesh.texture = Textures.POSITION_HANDLE_X;
			position = new Vector3f(selectedPart.position).mul(Part.SCALING);
			size = new Vector3f(selectedPart.size).mul(selectedPart.scale).mul(Part.SCALING);
			size.rotate(selectedPart.rotation);
			size.absolute();
			position.add(size.x + 0.5f, -0.25f, 0);
			
			MeshRenderer.renderMesh(handle.mesh, position, new Vector3f(0, 0, -90), new Vector3f(0.5f, 0.5f, 0.5f), shaderProgram);
			MeshRenderer.renderMesh(handle.mesh, new Vector3f(position).sub(0.0f, -0.25f, 0.25f), new Vector3f(90, 0, -90), new Vector3f(0.5f, 0.5f, 0.5f), shaderProgram);


			handle.mesh.texture = Textures.POSITION_HANDLE_Z;
			position = new Vector3f(selectedPart.position).mul(Part.SCALING);
			size = new Vector3f(selectedPart.size).mul(selectedPart.scale).mul(Part.SCALING);
			size.rotate(selectedPart.rotation);
			size.absolute();
			position.add(0, -0.25f, size.z + 0.5f);
			
			MeshRenderer.renderMesh(handle.mesh, position, new Vector3f(0, -90, -90), new Vector3f(0.5f, 0.5f, 0.5f), shaderProgram);
			MeshRenderer.renderMesh(handle.mesh, new Vector3f(position).sub(-0.25f, -0.25f, 0.0f), new Vector3f(90, -90, -90), new Vector3f(0.5f, 0.5f, 0.5f), shaderProgram);

			
		}
		Vector3f position = new Vector3f(selectedPart.position).mul(Part.SCALING);
		Vector3f size = new Vector3f(selectedPart.size).mul(selectedPart.scale).mul(Part.SCALING);
		size.rotate(selectedPart.rotation);
		size.absolute();
		position.add(-0.1f, size.y, -0.1f);
		
		RayBox Y_HANDLE = new RayBox();
		Y_HANDLE.min = new Vector3f(new Vector3f(position));
		Y_HANDLE.max = new Vector3f(Y_HANDLE.min).add(0.2f, 1.0f, 0.2f);
		
		MouseIntersection i = getMouseIntersection(Y_HANDLE, new Vector3f());
		if (i != null) {
			if (Settings.isMouseButtonJustDown(0)) {
				this.Yselected = true;
				mousePos3D = i.hit;
				lastMousePos3D = i.hit;
			}
		}
		if (this.Yselected) {
			Y_HANDLE.min = new Vector3f(position.add(-100, -100, -0.1f));
			Y_HANDLE.max = new Vector3f(Y_HANDLE.min).add(200, 200, 0.1f);
			i = getMouseIntersection(Y_HANDLE, new Vector3f());
			if (i == null) {
				Y_HANDLE.min = new Vector3f(position.add(-0.1f, -100, -100));
				Y_HANDLE.max = new Vector3f(Y_HANDLE.min).add(0.1f, 200, 200);
				i = getMouseIntersection(Y_HANDLE, new Vector3f());
			}
			if (i != null) {
				if (Settings.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT)) {
					if (i.hit.y - lastMousePos3D.y > Part.SCALING || i.hit.y - lastMousePos3D.y < Part.SCALING / 2.0f) {
						
						mousePos3D.y = (int)(i.hit.y * (1.0f / Part.SCALING)) * Part.SCALING;
						selectedPart.position.y = (int)selectedPart.position.y;
						
					}
				} else {
					mousePos3D = i.hit;
				}
				selectedPart.translate(new Vector3f(0, (mousePos3D.y - lastMousePos3D.y) * 32.0f, 0));
			}
		}
		
		
		position = new Vector3f(selectedPart.position).mul(Part.SCALING);
		size = new Vector3f(selectedPart.size).mul(selectedPart.scale).mul(Part.SCALING);
		size.rotate(selectedPart.rotation);
		size.absolute();
		position.add(size.x, -0.1f, -0.1f);
		RayBox X_HANDLE = new RayBox();
		X_HANDLE.min = new Vector3f(new Vector3f(position));
		X_HANDLE.max = new Vector3f(X_HANDLE.min).add(1.0f, 0.2f, 0.2f);
		
		i = getMouseIntersection(X_HANDLE, new Vector3f());
		if (i != null) {
			if (Settings.isMouseButtonJustDown(0)) {
				this.Xselected = true;
				mousePos3D = i.hit;
				lastMousePos3D = i.hit;
			}
		}
		if (this.Xselected) {
			X_HANDLE.min = new Vector3f(position.add(-100, -0.1f, -100));
			X_HANDLE.max = new Vector3f(X_HANDLE.min).add(200, 0.1f, 200);
			i = getMouseIntersection(X_HANDLE, new Vector3f());
			if (i == null) {
				X_HANDLE.min = new Vector3f(position.add(-100, -100, -0.1f));
				X_HANDLE.max = new Vector3f(X_HANDLE.min).add(200, 200, 0.1f);
				i = getMouseIntersection(X_HANDLE, new Vector3f());
			}
			if (i != null) {
				if (Settings.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT)) {
					if (Math.abs(i.hit.x - lastMousePos3D.x) > Part.SCALING || i.hit.x - lastMousePos3D.x < Part.SCALING / 2.0f) {
						mousePos3D.x = (int)(i.hit.x * (1.0f / Part.SCALING)) * Part.SCALING;
						selectedPart.position.x = (int)selectedPart.position.x;
					}
				} else {
					mousePos3D = i.hit;
				}
				selectedPart.translate(new Vector3f((mousePos3D.x - lastMousePos3D.x) * 32.0f, 0, 0));
			}
		}
		
		
		position = new Vector3f(selectedPart.position).mul(Part.SCALING);
		size = new Vector3f(selectedPart.size).mul(selectedPart.scale).mul(Part.SCALING);
		size.rotate(selectedPart.rotation);
		size.absolute();
		position.add(-0.1f, -0.1f, size.z);
		RayBox Z_HANDLE = new RayBox();
		Z_HANDLE.min = new Vector3f(new Vector3f(position));
		Z_HANDLE.max = new Vector3f(Z_HANDLE.min).add(0.2f, 0.2f, 1.0f);
		
		i = getMouseIntersection(Z_HANDLE, new Vector3f());
		if (i != null) {
			if (Settings.isMouseButtonJustDown(0)) {
				this.Zselected = true;
				mousePos3D = i.hit;
				lastMousePos3D = i.hit;
			}
		}
		if (this.Zselected) {
			Z_HANDLE.min = new Vector3f(position.add(-100, -0.1f, -100));
			Z_HANDLE.max = new Vector3f(Z_HANDLE.min).add(200, 0.1f, 200);
			i = getMouseIntersection(Z_HANDLE, new Vector3f());
			if (i == null) {
				Z_HANDLE.min = new Vector3f(position.add(-0.1f, -100, -100));
				Z_HANDLE.max = new Vector3f(Z_HANDLE.min).add(0.1f, 200, 200);
				i = getMouseIntersection(Z_HANDLE, new Vector3f());
			}
			if (i != null) {
				if (Settings.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT)) {
					if (Math.abs(i.hit.z - lastMousePos3D.z) > Part.SCALING || i.hit.z - lastMousePos3D.z < Part.SCALING / 2.0f) {
						mousePos3D.z = (int)(i.hit.z * (1.0f / Part.SCALING)) * Part.SCALING;
						selectedPart.position.z = (int)selectedPart.position.z;
					}
				} else {
					mousePos3D = i.hit;
				}
				selectedPart.translate(new Vector3f(0, 0, (mousePos3D.z - lastMousePos3D.z) * 32.0f));
			}
		}
		
		if (!Settings.isMouseButtonDown(0)) {
			this.Xselected = false;
			this.Yselected = false;
			this.Zselected = false;
			mousePos3D = new Vector3f(0, 0, 0);
		}
		lastMousePos3D = new Vector3f(mousePos3D);
	}
		
	public void deselect() {
		selectPart(null);
	}
	
	public void selectPart(Part part) {
		
		if (this.selectedPart != null)
		if (this.SETTING_PARENT) {
			this.SETTING_PARENT = false;
			if (part == null) {
				if (JOptionPane.showConfirmDialog(null, "Do you want to remove this part's parent?") == 0) {
					if (this.selectedPart.parent != null) {
						this.selectedPart.parent.children.remove(this.selectedPart);
					}
					this.selectedPart.parent = null;
					this.refreshPartsPanel();
					return;
				}
			} else {
				if (part == this.selectedPart) {
					return;
				}
				this.selectedPart.parent = part;
				part.children.add(this.selectedPart);
				this.refreshPartsPanel();
				JOptionPane.showMessageDialog(null, "Set the parent of " + this.selectedPart.name + " to " + part.name);
				return;
			}
		}
		
		this.selectionMesh = null;
		selectedPart = part;
		
		if (selectedPart != null) {
			this.part_name.getTextState().setText(selectedPart.name);
			this.part_name.setEditable(true);
			this.xPos.getTextState().setText(""+selectedPart.position.x);
			this.xPos.setEditable(true);
			this.yPos.getTextState().setText(""+selectedPart.position.y);
			this.yPos.setEditable(true);
			this.zPos.getTextState().setText(""+selectedPart.position.z);
			this.zPos.setEditable(true);
			this.xRot.setEditable(true);
			this.yRot.setEditable(true);
			this.zRot.setEditable(true);
			this.xScale.setEditable(true);
			this.yScale.setEditable(true);
			this.zScale.setEditable(true);
			this.xSize.setEditable(true);
			this.ySize.setEditable(true);
			this.zSize.setEditable(true);
			this.xCenter.setEditable(true);
			this.yCenter.setEditable(true);
			this.zCenter.setEditable(true);
		} else {
			this.part_name.getTextState().setText(Translation.translateText("Inignoto:gui.no_part"));
			this.part_name.setEditable(false);
			this.xPos.getTextState().setText("0");
			this.xPos.setEditable(false);
			this.yPos.getTextState().setText("0");
			this.yPos.setEditable(false);
			this.zPos.getTextState().setText("0");
			this.zPos.setEditable(false);
			
			this.xRot.getTextState().setText("0");
			this.xRot.setEditable(false);
			this.yRot.getTextState().setText("0");
			this.yRot.setEditable(false);
			this.zRot.getTextState().setText("0");
			this.zRot.setEditable(false);
			
			this.xScale.getTextState().setText("0");
			this.xScale.setEditable(false);
			this.yScale.getTextState().setText("0");
			this.yScale.setEditable(false);
			this.zScale.getTextState().setText("0");
			this.zScale.setEditable(false);
			
			this.xSize.getTextState().setText("0");
			this.xSize.setEditable(false);
			this.ySize.getTextState().setText("0");
			this.ySize.setEditable(false);
			this.zSize.getTextState().setText("0");
			this.zSize.setEditable(false);
			
			this.xCenter.getTextState().setText("0");
			this.xCenter.setEditable(false);
			this.yCenter.getTextState().setText("0");
			this.yCenter.setEditable(false);
			this.zCenter.getTextState().setText("0");
			this.zCenter.setEditable(false);
			return;
		}
		
		this.selectionMesh = Part.buildMesh(part, Textures.TILE_SELECTION, 
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
				});
	}
	
	public MouseIntersection getMouseIntersection(RayBox box, Quaternionf rotation) {
		int[] width = new int[1];
		int[] height = new int[1];
		GLFW.glfwGetWindowSize(Utils.window, width, height);
		double mx = Mouse.x * (1920.0 / width[0]);
		double my = (height[0] - Mouse.y) * (1080.0 / height[0]);
		if (Double.isInfinite(mx)) {
			mx = 0;
			my = 0;
		}
		
		float X = (float)mx / 1920.0f - 0.5f;
		float Y = (1.0f - (float)my / 1080.0f) - 0.5f;

		float size = 0.001f;
		float yMul = 1.7f;
		float xMul = 3;
		float div = 1.0f / (Utils.Z_FAR - Utils.Z_NEAR);
		Vector3f right = Camera.getRight().mul(X * xMul * div);
		Vector3f up = Camera.getUp().mul(Y * yMul * div);
		
		Vector3f origin = new Vector3f(Camera.position);
		Vector3f dir = Camera.getForward().mul(div).add(right).add(up);
		
		RayIntersection i = Raytracer.intersectBox(origin, dir, box, new Vector3f(0, 0, 0));
		if (i.lambda.x > 0.0 && i.lambda.x < i.lambda.y) {
			return new MouseIntersection(origin.add(new Vector3f(dir).mul(i.lambda.x)), i.lambda.x);
		}
		return null;
	}
	
	public MouseIntersection getMouseIntersection(RayBox box, Vector3f rotation) {
		int[] width = new int[1];
		int[] height = new int[1];
		GLFW.glfwGetWindowSize(Utils.window, width, height);
		double mx = Mouse.x * (1920.0 / width[0]);
		double my = (height[0] - Mouse.y) * (1080.0 / height[0]);
		if (Double.isInfinite(mx)) {
			mx = 0;
			my = 0;
		}
		
		float X = (float)mx / 1920.0f - 0.5f;
		float Y = (1.0f - (float)my / 1080.0f) - 0.5f;

		float size = 0.001f;
		float yMul = 1.7f;
		float xMul = 3;
		float div = 1.0f / (Utils.Z_FAR - Utils.Z_NEAR);
		Vector3f right = Camera.getRight().mul(X * xMul * div);
		Vector3f up = Camera.getUp().mul(Y * yMul * div);
		
		Vector3f origin = new Vector3f(Camera.position);
		Vector3f dir = Camera.getForward().mul(div).add(right).add(up);
		
		RayIntersection i = Raytracer.intersectBox(origin, dir, box, new Vector3f(0, 0, 0));
		if (i.lambda.x > 0.0 && i.lambda.x < i.lambda.y) {
			return new MouseIntersection(origin.add(new Vector3f(dir).mul(i.lambda.x)), i.lambda.x);
		}
		return null;
	}
	
	
    
    public void close() {
    	super.close();
    	model.dispose();
    	nRenderer.destroy();
    }

}
