/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
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
package org.azentreprise.arionide.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.azentreprise.arionide.Arionide;
import org.azentreprise.arionide.Utils;
import org.azentreprise.arionide.Workspace;
import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.events.ActionEvent;
import org.azentreprise.arionide.events.ActionType;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.events.MoveEvent;
import org.azentreprise.arionide.events.MoveType;
import org.azentreprise.arionide.events.PressureEvent;
import org.azentreprise.arionide.events.ValidateEvent;
import org.azentreprise.arionide.events.WheelEvent;
import org.azentreprise.arionide.events.WriteEvent;
import org.azentreprise.arionide.events.dispatching.IEventDispatcher;
import org.azentreprise.arionide.resources.Resources;
import org.azentreprise.arionide.threading.DrawingThread;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.core.opengl.OpenGLCoreRenderer;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.render.GLPrimitiveRenderer;
import org.azentreprise.arionide.ui.render.PrimitiveRenderer;
import org.azentreprise.arionide.ui.render.PrimitiveRenderingSystem;
import org.azentreprise.arionide.ui.render.PrimitiveType;
import org.azentreprise.arionide.ui.render.font.FontRenderer;
import org.azentreprise.arionide.ui.render.font.FontResources;
import org.azentreprise.arionide.ui.render.font.GLFontRenderer;
import org.azentreprise.arionide.ui.render.gl.GLRectangleRenderingContext;
import org.azentreprise.arionide.ui.render.gl.GLTextRenderingContext;
import org.xml.sax.SAXException;

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
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;

public class OpenGLContext implements AppDrawingContext, GLEventListener, KeyListener, MouseListener, EventHandler {
	
	private final IEventDispatcher dispatcher;
	private final AppManager theManager;

	private final GLProfile profile = GLProfile.get("GL4");
	private final GLCapabilities caps = new GLCapabilities(this.profile);
	private final GLWindow window;
	
	private final FPSAnimator animator;
	
	private final GLPrimitiveRenderer primitives = new GLPrimitiveRenderer();
	private final PrimitiveRenderingSystem system = new PrimitiveRenderingSystem(this.primitives);
	
	private final FloatBuffer clearColor = FloatBuffer.allocate(4);
	private final FloatBuffer clearDepth = FloatBuffer.allocate(1);
	
	private DrawingThread thread;
	private OpenGLCoreRenderer core;
	private Resources resources;
	private GLFontRenderer fontRenderer;
	
	private GL4 gl;
	
	private int alpha = 0;
		
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

	public void load(Workspace workspace, Resources resources, CoreRenderer renderer, LayoutManager manager) {
		assert renderer instanceof OpenGLCoreRenderer;
		
		this.core = (OpenGLCoreRenderer) renderer;
		this.resources = resources;
		
		try {
			this.fontRenderer = new GLFontRenderer(new FontResources(this.resources));
		} catch (GLException | IOException | ParserConfigurationException | SAXException exception) {
			Debug.exception(exception);
		}
		
		this.window.setVisible(true);
		this.animator.start();
		
		this.theManager.loadUI(workspace, resources, renderer, manager);
	}

	public Dimension getSize() {
		return new Dimension(2, 2);
	}

	public void setCursor(Cursor cursor) {
		this.window.setPointerVisible(cursor != null);
	}

	public void init(GLAutoDrawable arg0) {
		this.gl = this.window.getGL().getGL4();
		
		this.primitives.init(this);
		this.core.init(this.gl);
		
		this.system.registerGenericPrimitive(PrimitiveType.RECT, new GLRectangleRenderingContext());
		this.system.registerGenericPrimitive(PrimitiveType.TEXT, new GLTextRenderingContext(this.fontRenderer));
		
		this.clearColor.put(0, 0.0f).put(1, 0.0f).put(2, 0.0f).put(3, 1.0f);
		this.clearDepth.put(0, 1.0f);
	}
	
	public void display(GLAutoDrawable arg0) {
		if(this.thread != null) {
			this.thread.incrementTicks();
	        
			this.gl.glClearBufferfv(GL4.GL_COLOR, 0, this.clearColor);
	        this.gl.glClearBufferfv(GL4.GL_DEPTH, 0, this.clearDepth);
			
	        this.core.render3D(this);
	        	        
	        this.primitives.beginUI();
	        	        	        
			this.theManager.draw();
			
	        this.core.render2D(this);
			this.system.processRenderingQueue();
	        
			this.primitives.endUI();
		}
	}
	public void dispose(GLAutoDrawable arg0) {
		this.window.destroy();
	}

	public void reshape(GLAutoDrawable drawble, int x, int y, int width, int height) {
		this.gl.glViewport(x, y, width, height);
		
		this.fontRenderer.windowRatioChanged((float) width / height);
		this.primitives.viewportChanged(width, height);
		this.core.update(new Rectangle(this.window.getX(), this.window.getY(), width, height));
	}
	
	public GL4 getRenderer() {
		return this.gl;
	}
	
	public Resources getResources() {
		return this.resources;
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

	public PrimitiveRenderer getPrimitives() {
		return this.primitives;
	}
	
	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
	}
	
	public PrimitiveRenderingSystem getRenderingSystem() {
		return this.system;
	}

	public void setColor(int rgb) {
		if(rgb > 0xFFFFFF) {
			throw new IllegalArgumentException("Alpha values are not allowed");
		}		
				
		float r = Utils.getRed(rgb) / 255.0f;
		float g = Utils.getGreen(rgb) / 255.0f;
		float b = Utils.getBlue(rgb) / 255.0f;
		
		this.primitives.setColor(this.gl, r, g, b);
	}
	
	public void setAlpha(int alpha) {
		Utils.checkColorRange("Alpha", alpha);
		
		if(alpha != this.alpha) {			
			this.alpha = alpha;
			
			float a = alpha / 255.0f;
			
			this.primitives.setAlpha(this.gl, a);
		}
	}
	
	public void toggleFullscreen() {
		this.window.setFullscreen(!this.window.isFullscreen());
	}

	public void purge() {
		this.theManager.purge();
	}
	
	public void mouseClicked(MouseEvent event) {
		this.dispatcher.fire(new ActionEvent(this.getPoint(event), event.getButton(), ActionType.CLICK));
	}

	public void mousePressed(MouseEvent event) {
		this.dispatcher.fire(new ActionEvent(this.getPoint(event), event.getButton(), ActionType.PRESS));
	}

	public void mouseReleased(MouseEvent event) {
		this.dispatcher.fire(new ActionEvent(this.getPoint(event), event.getButton(), ActionType.RELEASE));
	}

	public void mouseEntered(MouseEvent event) {
		this.dispatcher.fire(new MoveEvent(this.getPoint(event), MoveType.ENTER));
	}

	public void mouseExited(MouseEvent event) {
		this.dispatcher.fire(new MoveEvent(this.getPoint(event), MoveType.EXIT));
	}

	public void mouseDragged(MouseEvent event) {
		this.dispatcher.fire(new MoveEvent(this.getPoint(event), MoveType.DRAG));
	}

	public void mouseMoved(MouseEvent event) {
		this.dispatcher.fire(new MoveEvent(this.getPoint(event), MoveType.MOVE));
	}
	
	public void mouseWheelMoved(MouseEvent event) {
		double rotation = (event.getRotation()[0] + event.getRotation()[1]) / 2.0d;
		
		rotation += Math.signum(rotation) * 0.5d;
		
		this.dispatcher.fire(new WheelEvent(this.getPoint(event), rotation * AppDrawingContext.MOUSE_WHEEL_SENSIBILITY));
	}
	
	private Point2D getPoint(MouseEvent event) {
		return new Point2D.Double(2.0d * event.getX() / this.window.getWidth(), 2.0d * event.getY() / this.window.getHeight());
	}

	public void keyTyped(KeyEvent e) {
		;
	}

	public void keyPressed(KeyEvent event) {
		this.dispatcher.fire(new PressureEvent(event.getKeyChar(), event.getKeyCode(), event.getModifiers(), true));

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
		} else {
			this.dispatcher.fire(new WriteEvent(event.getKeyChar(), event.getKeyCode(), event.getModifiers()));
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

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(MessageEvent.class);
	}
}