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

import org.azentreprise.arionide.Arionide;
import org.azentreprise.arionide.Utils;
import org.azentreprise.arionide.Workspace;
import org.azentreprise.arionide.events.ActionEvent;
import org.azentreprise.arionide.events.ActionType;
import org.azentreprise.arionide.events.InvalidateLayoutEvent;
import org.azentreprise.arionide.events.MoveEvent;
import org.azentreprise.arionide.events.MoveType;
import org.azentreprise.arionide.events.PressureEvent;
import org.azentreprise.arionide.events.ValidateEvent;
import org.azentreprise.arionide.events.WheelEvent;
import org.azentreprise.arionide.events.WriteEvent;
import org.azentreprise.arionide.events.dispatching.IEventDispatcher;
import org.azentreprise.arionide.resources.Resources;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.render.PrimitiveRenderingSystem;
import org.azentreprise.arionide.ui.render.font.FontRenderer;
import org.azentreprise.arionide.ui.topology.Point;
import org.azentreprise.arionide.ui.topology.Size;

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
	
	public void load(Workspace workspace, Resources resources, CoreRenderer renderer, LayoutManager manager) {
		this.theFrame.setVisible(true);
		
		this.theManager.initUI(workspace, resources, renderer, manager);
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
}