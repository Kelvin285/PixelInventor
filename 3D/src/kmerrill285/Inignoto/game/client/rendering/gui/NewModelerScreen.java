package kmerrill285.Inignoto.game.client.rendering.gui;

import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVG.nvgFillColor;
import static org.lwjgl.nanovg.NanoVG.nvgRect;
import static org.lwjgl.nanovg.NanoVG.nvgRotate;
import static org.lwjgl.nanovg.NanoVG.nvgStroke;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeColor;
import static org.lwjgl.nanovg.NanoVG.nvgTranslate;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glDeleteRenderbuffers;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

import java.awt.Point;
import java.io.File;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.liquidengine.legui.animation.AnimatorProvider;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.component.ImageView;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.Widget;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.event.FocusEvent;
import org.liquidengine.legui.event.KeyEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.image.FBOImage;
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
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL40;

import kmerrill285.Inignoto.events.Events;
import kmerrill285.Inignoto.game.client.Camera;
import kmerrill285.Inignoto.game.client.Mouse;
import kmerrill285.Inignoto.game.client.rendering.MeshRenderer;
import kmerrill285.Inignoto.game.client.rendering.shader.ShaderProgram;
import kmerrill285.Inignoto.game.client.rendering.textures.Texture;
import kmerrill285.Inignoto.game.client.rendering.textures.Textures;
import kmerrill285.Inignoto.game.settings.Settings;
import kmerrill285.Inignoto.game.settings.Translation;
import kmerrill285.Inignoto.modelloader.AnimModel;
import kmerrill285.Inignoto.modelloader.CustomModel;
import kmerrill285.Inignoto.modelloader.ModelLoader;
import kmerrill285.Inignoto.modelloader.ModelPart;
import kmerrill285.Inignoto.modelloader.ModelTransformation;
import kmerrill285.Inignoto.resources.FPSCounter;
import kmerrill285.Inignoto.resources.Utils;
import kmerrill285.Inignoto.resources.raytracer.RayBox;
import kmerrill285.Inignoto.resources.raytracer.RayIntersection;
import kmerrill285.Inignoto.resources.raytracer.Raytracer;

public class NewModelerScreen extends ModelerScreen {
	
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
	private FBOImage fboTexture = null;
	private long nvgContext;
	private Widget widget;
	private Panel panel;
	private Panel navigation;
	private Button FILE;
	private Button EDIT;
	
	private Panel FILE_PANEL;
	
	private static int textureWidth = 200;

    private static int textureHeight = 200;

    private static int frameBufferID;

    private static int textureID;

    private static int renderBufferID;
    
    boolean isVersionNew;
	
	
	public NewModelerScreen(GuiRenderer gui) {
		super(gui);
		
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
        
        nvgContext = 0;

        fboTexture = null;
        
        isVersionNew = (GL40.glGetInteger(GL40.GL_MAJOR_VERSION) > 3) || (GL40.glGetInteger(GL40.GL_MAJOR_VERSION) == 3 && GL40.glGetInteger(GL40.GL_MINOR_VERSION) >= 2);

        if (isVersionNew) {

            int flags = NanoVGGL3.NVG_STENCIL_STROKES | NanoVGGL3.NVG_ANTIALIAS;

            nvgContext = NanoVGGL3.nvgCreate(flags);

        } else {

            int flags = NanoVGGL2.NVG_STENCIL_STROKES | NanoVGGL2.NVG_ANTIALIAS;

            nvgContext = NanoVGGL2.nvgCreate(flags);

        }

        if (nvgContext != 0) {

            fboTexture = createFBOTexture(1920, 1080);



            widget = new Widget(0, 0, 1920, 1080);

            widget.setCloseable(false);

            widget.setMinimizable(false);
            widget.setTitleEnabled(false);
    		widget.setResizable(false);
    		widget.getStyle().getBackground().setColor(0.1f, 0.1f, 0.1f, 1.0f);
    		
            widget.getContainer().getStyle().setDisplay(DisplayType.FLEX);

            


           

            panel = new Panel();
            panel.setPosition(0, 0);
            panel.getStyle().getBackground().setColor(0.1f, 0.1f, 0.1f, 1.0f);
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
            
            panel.getListenerMap().addListener(FocusEvent.class, (FocusEventListener) event -> {
            	if (panel.isFocused()) {
            		panel.remove(FILE_PANEL);
            	}
            });
            
            frame.getContainer().add(widget);

        }
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
					this.model.texture = this.texture;
					this.model.rebuild();
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
				AnimModel model = ModelLoader.loadModelFromFile(selectedFile);
				this.model = new CustomModel(model, texture);
				this.modelDir = selectedFile;
				this.lastAction = -1;
				this.actions.clear();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, Translation.translateText("Inignoto:gui.not_valid_model"));
			}
		}
	}
	
	public void tick() {
		double[] xpos = new double[1];
		double[] ypos = new double[1];
		GLFW.glfwGetCursorPos(Utils.window, xpos, ypos);
		Mouse.x = (float) xpos[0];
		Mouse.y = (float) ypos[0];
		if (Settings.isMouseButtonDown(2)) {
			Camera.position.add(Camera.getUp().mul((Mouse.lastY - Mouse.y) * 0.01f));
			Camera.position.add(Camera.getRight().mul((Mouse.lastX - Mouse.x) * 0.01f));
		}
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
		
		widget.setPosition(0, 0);
		widget.setSize(context.getFramebufferSize().x + 1, context.getFramebufferSize().y + 1);
		
		panel.getStyle().setMinimumSize(widget.getSize().x + 4, widget.getSize().y + 4);
		panel.setPosition(-1, -1);
		navigation.setPosition(0, 0);
		navigation.getStyle().setMinimumSize(widget.getSize().x, 20);
		
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
//		if (fboTexture != null) {
//            renderToFBO(nvgContext);
//        }
		
		shaderProgram.unbind();

		context.updateGlfwWindow();
		
		Vector2i windowSize = context.getFramebufferSize();
		
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1);
		GL11.glViewport(0, 0, (int)windowSize.x, (int)windowSize.y);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
		nRenderer.render(frame, context);
		
		systemEventProcessor.processEvents(frame, context);
		EventProcessorProvider.getInstance().processEvents();
		LayoutManager.getInstance().layout(frame);
		AnimatorProvider.getAnimator().runAnimations();
		
		
		shaderProgram.bind();

		if (model != null) {
			model.render(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), shaderProgram);
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
		ModelPart part = null;
		if (this.model != null) {
			HashMap<String, ModelPart> parts = this.model.model.parts;
			for (String str : parts.keySet()) {
				ModelTransformation transform = this.model.currentTransformations.get(str);
				if (transform != null) {
					RayBox box = new RayBox();
					box.min = new Vector3f(transform.x - transform.size_x / 2.0f, transform.y - transform.size_y / 2.0f, transform.z - transform.size_z / 2.0f);
					box.max = new Vector3f(box.min).add(transform.size_x, transform.size_y, transform.size_z);
					MouseIntersection i = this.getMouseIntersection(box, new Vector3f(transform.rotX, transform.rotY, transform.rotZ));
					if (i != null) {
						if (i.distance < distance) {
							distance = i.distance;
							closest = i;
							part = parts.get(str);
						}
					}
				}
			}
		}
		if (Settings.isMouseButtonJustDown(0)) {
			if (closest != null) {
				selectPart(part);
			} else {
				deselect();
			}
		}
		
	}
	
	public void deselect() {
		if (this.model.extraMeshes.get(selectedPart) != null) {
			this.model.extraMeshes.get(selectedPart).remove(this.selectionMesh);
		}
		if (this.selectionMesh != null) {
			this.model.extraScale.remove(this.selectionMesh);
		}
		this.selectionMesh = null;
		selectedPart = "";
	}
	
	public void selectPart(ModelPart part) {
		if (this.model.extraMeshes.get(selectedPart) != null) {
			this.model.extraMeshes.get(selectedPart).remove(this.selectionMesh);
		}
		if (this.selectionMesh != null) {
			this.model.extraScale.remove(this.selectionMesh);
		}
		this.selectionMesh = null;
		selectedPart = part.name;
		this.selectionMesh = CustomModel.buildMesh(part, Textures.TILE_SELECTION, 
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
	
	
	public static FBOImage createFBOTexture(int textureWidth, int textureHeight) {

        frameBufferID = GL30.glGenFramebuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);

        textureID = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureID);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, textureWidth, textureHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        renderBufferID = glGenRenderbuffers();

        glBindRenderbuffer(GL_RENDERBUFFER, renderBufferID);

        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, textureWidth, textureHeight);

        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderBufferID);
        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, textureID, 0);

        glDrawBuffer(GL_COLOR_ATTACHMENT0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);



        return new FBOImage(textureID, textureWidth, textureHeight);

    }



    private static long lastTime = System.nanoTime();



    public static void renderToFBO(long nvgContext) {

        // bind fbo



        long thisTime = System.nanoTime();

        float angle = 5 * (lastTime - thisTime) / 1E10f;



        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);

        glViewport(0, 0, textureWidth, textureHeight);



        glClearColor(0.3f, 0.5f, 0.7f, 0.0f);



        // Clear screen

        glClear(GL_COLOR_BUFFER_BIT);



        glEnable(GL_BLEND);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        nvgBeginFrame(nvgContext, textureWidth, textureHeight, 1);


        
        try (

                NVGColor nvgColorOne = NVGColor.calloc();

                NVGColor nvgColorTwo = NVGColor.calloc()

        ) {

            nvgColorOne.r(0);

            nvgColorOne.g(1);

            nvgColorOne.b(0);

            nvgColorOne.a(1);



            nvgColorTwo.r(0);

            nvgColorTwo.g(0);

            nvgColorTwo.b(0);

            nvgColorTwo.a(1);



            nvgTranslate(nvgContext, textureWidth / 2f, textureHeight / 2f);

            nvgRotate(nvgContext, angle);



            nvgBeginPath(nvgContext);

            nvgRect(nvgContext, -textureWidth / 4f, -textureHeight / 4f, textureWidth / 2f, textureHeight / 2f);

            nvgStrokeColor(nvgContext, nvgColorTwo);

            nvgStroke(nvgContext);



            nvgBeginPath(nvgContext);

            nvgRect(nvgContext, -textureWidth / 4f, -textureHeight / 4f, textureWidth / 2f, textureHeight / 2f);

            nvgFillColor(nvgContext, nvgColorOne);

            nvgFill(nvgContext);
        }



        nvgEndFrame(nvgContext);

        GL40.glDisable(GL40.GL_BLEND);



        // unbind fbo

        GL40.glBindFramebuffer(GL40.GL_FRAMEBUFFER, 0);

    }
    
    public void close() {
    	super.close();
    	if (nvgContext != 0) {
            glDeleteRenderbuffers(renderBufferID);
            glDeleteTextures(textureID);
            glDeleteFramebuffers(frameBufferID);
            if (isVersionNew) {
                NanoVGGL3.nnvgDelete(nvgContext);
            } else {
                NanoVGGL2.nnvgDelete(nvgContext);
            }

        }
    	nRenderer.destroy();
    }

}
