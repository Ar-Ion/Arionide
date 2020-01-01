/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2019 Innovazion. All rights reserved.
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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.HashMap;
import java.util.Map;

import ch.innovazion.arionide.Arionide;
import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.Workspace;
import ch.innovazion.arionide.events.ActionEvent;
import ch.innovazion.arionide.events.ActionType;
import ch.innovazion.arionide.events.InvalidateLayoutEvent;
import ch.innovazion.arionide.events.MoveEvent;
import ch.innovazion.arionide.events.MoveType;
import ch.innovazion.arionide.events.PressureEvent;
import ch.innovazion.arionide.events.ValidateEvent;
import ch.innovazion.arionide.events.WheelEvent;
import ch.innovazion.arionide.events.WriteEvent;
import ch.innovazion.arionide.events.dispatching.IEventDispatcher;
import ch.innovazion.arionide.resources.Resources;
import ch.innovazion.arionide.ui.core.CoreOrchestrator;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.render.PrimitiveRenderingSystem;
import ch.innovazion.arionide.ui.render.font.FontRenderer;
import ch.innovazion.arionide.ui.topology.Point;
import ch.innovazion.arionide.ui.topology.Size;

public class AWTContext extends Canvas implements AppDrawingContext, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

	private static final long serialVersionUID = 1171699360872561984L;
	
	private final Map<RenderingHints.Key, Object> renderingHints = new HashMap<>();
				
	private final IEventDispatcher dispatcher;
	private final AppManager theManager;
	private final Frame theFrame;
	
	private Graphics2D theGraphics;
		
	private int lastRGB = 0;
	private int lastAlpha = 0;
		
	public AWTContext(Arionide theInstance, IEventDispatcher dispatcher, int width, int height) {
		
		System.setProperty("sun.awt.noerasebackground", "true");
		System.setProperty("sun.awt.erasebackgroundonresize", "false");

		this.dispatcher = dispatcher;
		this.theManager = new AppManager(theInstance, this, dispatcher);
		
		this.theFrame = new Frame("Arionide");
		this.theFrame.setSize(width, height);
		this.theFrame.setLocationRelativeTo(null);
		this.theFrame.add(this);
						
		this.theFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				theManager.shutdown();	
			}
		});
		
		this.theFrame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent event) {
				dispatcher.fire(new InvalidateLayoutEvent());
			}
		});
		
		this.theFrame.addKeyListener(this);
		this.addKeyListener(this);
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		
		this.setFocusTraversalKeysEnabled(false);
		this.theFrame.setFocusTraversalKeysEnabled(false);
				
		this.renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}
	
	public void load(Workspace workspace, Resources resources, CoreOrchestrator orchestrator, LayoutManager manager) {
		this.theFrame.setVisible(true);
		
		this.theManager.initUI(workspace, resources, orchestrator, manager);
	}

	public void draw() {		
		BufferStrategy strategy = this.getBufferStrategy();
		
		if(strategy == null) {
			this.createBufferStrategy(2);
			return;
		}
		
		try {
			this.theGraphics = (Graphics2D) strategy.getDrawGraphics();
			this.theGraphics.setRenderingHints(this.renderingHints);
			this.theManager.draw();
		} finally {
			this.theGraphics.dispose();
		}
		
		strategy.show();
	}
	
	public void update() {
		this.theManager.update();
	}

	public Graphics2D getRenderer() {
		return this.theGraphics;
	}
	
	public Resources getResources() {
		return this.theManager.getResources();
	}
	
	public FontRenderer getFontRenderer() {
		return null;
	}
	
	public PrimitiveRenderingSystem getRenderingSystem() {
		return null;
	}
	
	public PrimitiveRenderingSystem getOverlayRenderingSystem() {
		return null;
	}
	
	public Size getWindowSize() {
		return new Size(this.getWidth(), this.getHeight());
	}
	
	public void setColor(int rgb) {
		if(rgb > 0xFFFFFF) {
			throw new IllegalArgumentException("Alpha values are not allowed");
		}
				
		if(rgb != this.lastRGB) {
			this.lastRGB = rgb;
			this.commitColor();
		}
	}
	
	public void setAlpha(int alpha) {
		Utils.checkColorRange("Alpha", alpha);
		
		if(alpha != this.lastAlpha) {
			this.lastAlpha = alpha;
			this.commitColor();
		}
	}
	
	private void commitColor() {
		this.getRenderer().setColor(new Color(Utils.packARGB(this.lastRGB, this.lastAlpha), true));
	}
	
	public void purge() {
		this.theManager.purge();
	}
	
	public void mouseClicked(MouseEvent event) {
		Point point = new Point(event.getX(), event.getY());
		this.dispatcher.fire(new ActionEvent(point, event.getButton(), ActionType.CLICK));
	}

	public void mousePressed(MouseEvent event) {
		Point point = new Point(event.getX(), event.getY());
		this.dispatcher.fire(new ActionEvent(point, event.getButton(), ActionType.PRESS));
	}

	public void mouseReleased(MouseEvent event) {
		Point point = new Point(event.getX(), event.getY());
		this.dispatcher.fire(new ActionEvent(point, event.getButton(), ActionType.RELEASE));
	}

	public void mouseEntered(MouseEvent event) {
		Point point = new Point(event.getX(), event.getY());
		this.dispatcher.fire(new MoveEvent(point, MoveType.ENTER));
	}

	public void mouseExited(MouseEvent event) {
		Point point = new Point(event.getX(), event.getY());
		this.dispatcher.fire(new MoveEvent(point, MoveType.EXIT));
	}

	public void mouseDragged(MouseEvent event) {
		Point point = new Point(event.getX(), event.getY());
		this.dispatcher.fire(new MoveEvent(point, MoveType.DRAG));
	}

	public void mouseMoved(MouseEvent event) {
		Point point = new Point(event.getX(), event.getY());
		this.dispatcher.fire(new MoveEvent(point, MoveType.MOVE));
	}
	
	public void mouseWheelMoved(MouseWheelEvent event) {
		Point point = new Point(event.getX(), event.getY());
		this.dispatcher.fire(new WheelEvent(point, event.getWheelRotation()));
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
		} else {
			this.dispatcher.fire(new WriteEvent(event.getKeyChar(), event.getKeyCode(), event.getModifiers()));
		}
	}

	public void keyReleased(KeyEvent event) {
		this.dispatcher.fire(new PressureEvent(event.getKeyChar(), event.getKeyCode(), event.getModifiers(), false));
	}

	@Override
	public void moveCursor(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCursorVisible(boolean visible) {
		// TODO Auto-generated method stub
		
	}
}