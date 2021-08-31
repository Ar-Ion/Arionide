/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
 *
 * Arionide is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Arionide is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Arionide.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package ch.innovazion.arionide.ui;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.nio.FloatBuffer;
import java.util.Set;

import com.jogamp.common.util.InterruptSource.Thread;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;

import ch.innovazion.arionide.Arionide;
import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.Workspace;
import ch.innovazion.arionide.debugging.Debug;
import ch.innovazion.arionide.events.ActionEvent;
import ch.innovazion.arionide.events.ActionType;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.EventHandler;
import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.events.MoveEvent;
import ch.innovazion.arionide.events.MoveType;
import ch.innovazion.arionide.events.PressureEvent;
import ch.innovazion.arionide.events.ValidateEvent;
import ch.innovazion.arionide.events.WheelEvent;
import ch.innovazion.arionide.events.dispatching.IEventDispatcher;
import ch.innovazion.arionide.resources.Resources;
import ch.innovazion.arionide.threading.DrawingThread;
import ch.innovazion.arionide.ui.core.CoreOrchestrator;
import ch.innovazion.arionide.ui.gc.GLTrashContext;
import ch.innovazion.arionide.ui.gc.Trash;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.render.Cursor;
import ch.innovazion.arionide.ui.render.PrimitiveFactory;
import ch.innovazion.arionide.ui.render.PrimitiveRenderingSystem;
import ch.innovazion.arionide.ui.render.PrimitiveType;
import ch.innovazion.arionide.ui.render.font.FontRenderer;
import ch.innovazion.arionide.ui.render.font.FontResources;
import ch.innovazion.arionide.ui.render.font.GLFontRenderer;
import ch.innovazion.arionide.ui.render.gl.GLRenderingContext;
import ch.innovazion.arionide.ui.topology.Bounds;
import ch.innovazion.arionide.ui.topology.Point;
import ch.innovazion.arionide.ui.topology.Size;

public class OpenGLContext implements AppDrawingContext, GLEventListener, KeyListener, MouseListener, EventHandler {
	
	private final IEventDispatcher dispatcher;
	private final AppManager theManager;

	private final GLProfile profile = GLProfile.get("GL4");
	private final GLCapabilities caps = new GLCapabilities(this.profile);
	private final GLWindow window;
	
	private final FPSAnimator animator;
	
	private final PrimitiveRenderingSystem coreSystem = new PrimitiveRenderingSystem();
	private final PrimitiveRenderingSystem overlaySystem = new PrimitiveRenderingSystem();

	private final FloatBuffer clearColor = FloatBuffer.allocate(4);
	private final FloatBuffer clearDepth = FloatBuffer.allocate(1);
	
	private DrawingThread thread;
	private CoreOrchestrator orchestrator;
	private Resources resources;
	private GLFontRenderer fontRenderer;
	
	private GL4 gl;
	
	private Cursor theCursor;
			
	public OpenGLContext(Arionide theInstance, IEventDispatcher dispatcher, int width, int height) {		
		this.dispatcher = dispatcher;
		this.theManager = new AppManager(theInstance, this, dispatcher);
		this.caps.setDoubleBuffered(true);
		this.caps.setHardwareAccelerated(true);
		this.caps.setDepthBits(32);

		this.window = GLWindow.create(this.caps);
		this.animator = new FPSAnimator(this.window, 60, true);
		
		this.window.addGLEventListener(this);
		this.window.addKeyListener(this);
		this.window.addMouseListener(this);
		
		this.window.setSize(width, height);
		this.window.setTitle("Arionide");
		
		this.window.addWindowListener(new WindowAdapter() {
            public void windowDestroyNotify(WindowEvent arg0) {
				theManager.shutdown();
			}
		});
		
		dispatcher.registerHandler(this);
	}

	public void load(Workspace workspace, Resources resources, CoreOrchestrator orchestrator, LayoutManager manager) {		
		this.orchestrator = orchestrator;
		this.resources = resources;
		
		try {
			this.fontRenderer = new GLFontRenderer(new FontResources(this.resources));
		} catch (Exception exception) {
			Debug.exception(exception);
		}
		
		this.theManager.initUI(workspace, resources, orchestrator, manager);
	
		this.window.setVisible(true);
		this.animator.start();
	}

	public void init(GLAutoDrawable arg0) {
		this.gl = window.getGL().getGL4();
		
		//gl.glHint(GL4.GL_GENERATE_MIPMAP_HINT, GL4.GL_NICEST);
		
		Trash.init(new GLTrashContext(gl));
		
		orchestrator.orchestrateInitialisation(this);
		
		GLRenderingContext.init(gl, resources, fontRenderer);
		
		coreSystem.registerPrimitive(PrimitiveType.POLYGON, GLRenderingContext.polygon);
		coreSystem.registerPrimitive(PrimitiveType.UNEDGED_RECT, GLRenderingContext.unedgedRectangle);
		coreSystem.registerPrimitive(PrimitiveType.EDGE, GLRenderingContext.edge);
		coreSystem.registerPrimitive(PrimitiveType.CURSOR, GLRenderingContext.cursor);
		coreSystem.registerPrimitive(PrimitiveType.TEXT, GLRenderingContext.text);

		overlaySystem.registerPrimitive(PrimitiveType.POLYGON, GLRenderingContext.polygon);
		overlaySystem.registerPrimitive(PrimitiveType.UNEDGED_RECT, GLRenderingContext.unedgedRectangle);
		overlaySystem.registerPrimitive(PrimitiveType.EDGE, GLRenderingContext.edge);
		overlaySystem.registerPrimitive(PrimitiveType.CURSOR, GLRenderingContext.cursor);
		overlaySystem.registerPrimitive(PrimitiveType.TEXT, GLRenderingContext.text);
		
		theCursor = PrimitiveFactory.instance().newCursor(3.0f);
		theCursor.updateBounds(new Bounds(-1.0f, -1.0f, 2.0f, 2.0f));
		theCursor.prepare();
		
		clearColor.put(0, 0.0f).put(1, 0.0f).put(2, 0.0f).put(3, 1.0f);
		clearDepth.put(0, 1.0f);
		
		theManager.loadUI();
	}
	
	public void display(GLAutoDrawable arg0) {
		if(thread != null) {			
			thread.incrementTicks();
	        
			gl.glClearBufferfv(GL4.GL_COLOR, 0, clearColor);
	        gl.glClearBufferfv(GL4.GL_DEPTH, 0, clearDepth);
			
	        orchestrator.orchestrateMain(this);
	        	        
	        gl.glEnable(GL4.GL_BLEND);
	        gl.glBlendFunc(GL4.GL_SRC_ALPHA,  GL4.GL_ONE_MINUS_SRC_ALPHA);
	        
	        theManager.draw();

	        orchestrator.orchestrateOverlay(this);
	        
	        if(orchestrator.getController().isActive()) {
				coreSystem.renderLater(theCursor);
	        }
	        
			coreSystem.processRenderingQueue();
			overlaySystem.synchronise(coreSystem);
			
	        overlaySystem.processRenderingQueue();
	        coreSystem.synchronise(overlaySystem);

			gl.glDisable(GL4.GL_BLEND);
			
			Trash.instance().burnGarbage();
		}
	}
	public void dispose(GLAutoDrawable arg0) {
		this.window.destroy();
	}

	public void reshape(GLAutoDrawable drawble, int x, int y, int width, int height) {
		gl.glViewport(x, y, width, height);
		
		coreSystem.updateAspectRatio((float) width / height);
		overlaySystem.updateAspectRatio((float) width / height);
		
		orchestrator.updateBounds(new Bounds(this.window.getX(), this.window.getY(), width, height));
	}
	
	public void draw() {
		this.thread = (DrawingThread) Thread.currentThread();
		
		while(true) {
			try {
				Thread.sleep(100000);
			} catch (InterruptedException e) {
				;
			}
		} // OpenGL has an independent threading system
	}
	
	public void update() {
		this.theManager.update();
	}
	
	public void setCursorVisible(boolean visible) {
		this.window.setPointerVisible(visible);
	}

	public void toggleFullscreen() {
		this.window.setFullscreen(!this.window.isFullscreen());
	}

	public void purge() {
		this.theManager.purge();
	}
		
	public void moveCursor(int x, int y) {
	}
	
	public Size getWindowSize() {
		return new Size(2.0f, 2.0f);
	}

	public GL4 getRenderer() {
		return this.gl;
	}
	
	public Resources getResources() {
		return this.resources;
	}

	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
	}
	
	public PrimitiveRenderingSystem getRenderingSystem() {
		return this.coreSystem;
	}
	
	public PrimitiveRenderingSystem getOverlayRenderingSystem() {
		return this.overlaySystem;
	}
	
	public void mouseClicked(MouseEvent event) {
		this.dispatcher.fire(new ActionEvent(this.getEventOrigin(event), event.getButton(), ActionType.CLICK));
	}

	public void mousePressed(MouseEvent event) {
		System.out.println("Press");
		this.dispatcher.fire(new ActionEvent(this.getEventOrigin(event), event.getButton(), ActionType.PRESS));
	}

	public void mouseReleased(MouseEvent event) {
		this.dispatcher.fire(new ActionEvent(this.getEventOrigin(event), event.getButton(), ActionType.RELEASE));
	}

	public void mouseEntered(MouseEvent event) {
		this.dispatcher.fire(new MoveEvent(this.getEventOrigin(event), MoveType.ENTER));
	}

	public void mouseExited(MouseEvent event) {
		this.dispatcher.fire(new MoveEvent(this.getEventOrigin(event), MoveType.EXIT));
	}

	public void mouseDragged(MouseEvent event) {
		this.dispatcher.fire(new MoveEvent(this.getEventOrigin(event), MoveType.DRAG));
	}


	public void mouseMoved(MouseEvent event) {	
		if(orchestrator.getController().isActive()) {
			window.setPointerVisible(false);
			window.confinePointer(true);
						
			int deltaX = window.getWidth() / 4;
			int deltaY = window.getHeight() / 4;

			if(event.getX() / getTransform()[0] > window.getWidth() - deltaX || event.getX() / getTransform()[0]  < deltaX || event.getY() / getTransform()[1]  > window.getHeight() - deltaY || event.getY() / getTransform()[1] < deltaY) {
				window.warpPointer((int) (getTransform()[0] * window.getWidth() / 2), (int) (getTransform()[1] * window.getHeight() / 2));
				dispatcher.fire(new MoveEvent(new Point(1.0f, 1.0f), MoveType.RESET));
				return;
			}
		} else {
			window.setPointerVisible(true);
		}
				
		dispatcher.fire(new MoveEvent(this.getEventOrigin(event), MoveType.MOVE));
	}
	
	public void mouseWheelMoved(MouseEvent event) {
		double rotation = (event.getRotation()[0] + event.getRotation()[1]) / 2.0d;
		
		rotation += Math.signum(rotation) * 0.5d;
		
		this.dispatcher.fire(new WheelEvent(this.getEventOrigin(event), rotation * AppDrawingContext.MOUSE_WHEEL_SENSIBILITY));
	}
	
	private float[] getTransform() {
		final GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        final AffineTransform transform = gfxConfig.getDefaultTransform();
        return new float[] { (float) transform.getScaleX(), (float) transform.getScaleY() };
	}
	
	private Point getEventOrigin(MouseEvent event) {

		return new Point(2.0f / getTransform()[0] * event.getX() / this.window.getWidth(), 2.0f / getTransform()[1] * event.getY() / this.window.getHeight());
	}

	public void keyTyped(KeyEvent e) {
		;
	}

	public void keyPressed(KeyEvent event) {
		PressureEvent pressure = new PressureEvent(event.getKeyChar(), event.getKeyCode(), event.getModifiers(), true);
		
		this.dispatcher.fire(pressure);

		if(event.getKeyCode() == KeyEvent.VK_ENTER) {
			this.dispatcher.fire(new ValidateEvent());
		} else if(event.getKeyCode() == KeyEvent.VK_TAB) {
			if(!event.isShiftDown()) {
				this.theManager.getFocusManager().next();
			} else {
				this.theManager.getFocusManager().prev();
			}
		} else if(event.getKeyCode() == KeyEvent.VK_F && (event.isMetaDown() || event.isControlDown())) {
			this.toggleFullscreen();
		}
	}

	public void keyReleased(KeyEvent event) {
		this.dispatcher.fire(new PressureEvent(event.getKeyChar(), event.getKeyCode(), event.getModifiers(), false));
	}

	public <T extends Event> void handleEvent(T event) {
		MessageEvent message = (MessageEvent) event;
	
		if(message.getMessageType().equals(MessageType.DEBUG)) {
			this.window.setTitle("Arionide - " + message.getMessage());
		}
	}

	public Set<Class<? extends Event>> getHandleableEvents() {
		return Utils.asSet(MessageEvent.class);
	}
}