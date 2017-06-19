/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'Arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.arionide.ui;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Point;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.azentreprise.arionide.Arionide;
import org.azentreprise.arionide.IWorkspace;
import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.events.ActionEvent;
import org.azentreprise.arionide.events.ActionType;
import org.azentreprise.arionide.events.InvalidateLayoutEvent;
import org.azentreprise.arionide.events.MoveEvent;
import org.azentreprise.arionide.events.MoveType;
import org.azentreprise.arionide.events.ValidateEvent;
import org.azentreprise.arionide.events.WheelEvent;
import org.azentreprise.arionide.events.WriteEvent;
import org.azentreprise.arionide.events.dispatching.IEventDispatcher;
import org.azentreprise.arionide.resources.Resources;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.primitives.AWTPrimitives;
import org.azentreprise.arionide.ui.primitives.IPrimitives;

public class AWTDrawingContext extends Canvas implements AppDrawingContext, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

	private static final long serialVersionUID = 1171699360872561984L;
	
	private final Map<RenderingHints.Key, Object> renderingHints = new HashMap<>();
		
	private final IPrimitives primitives = new AWTPrimitives();
	
	private final Stack<Float> opacityStack = new Stack<>();
	
	private final IEventDispatcher dispatcher;
	private final AppManager theManager;
	private final Frame theFrame;
	
	private Graphics2D theGraphics;
	
	private Font font;
	private FontAdapter adapter;
	
	private int ticks = 0;
	private long time = 0;
	
	public AWTDrawingContext(IEventDispatcher dispatcher, int width, int height) {
		
		System.setProperty("sun.awt.noerasebackground", "true");
		System.setProperty("sun.awt.erasebackgroundonresize", "false");

		this.dispatcher = dispatcher;
		this.theManager = new AppManager(this, dispatcher);
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
				
		this.renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		this.renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		this.renderingHints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		this.renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
	}
	
	public void load(Arionide theInstance, IWorkspace workspace, Resources resources, CoreRenderer renderer, LayoutManager manager) {
		this.theFrame.setVisible(true);

		try {
			this.font = Font.createFont(Font.TRUETYPE_FONT, resources.getResource("font"));
			this.adapter = new FontAdapter(this.font);
		} catch (FontFormatException | IOException exception) {
			Debug.exception(exception);
		}
		
		new Thread("Drawing thread") {
			public void run() {
				while(true) {
					draw();
				}
			}
		}.start();
		
		this.theManager.loadUI(theInstance, workspace, resources, renderer, manager);
	}

	public void draw() {
		this.ticks++;
		
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
				
		long now = System.currentTimeMillis();
		
		if(this.time + 1000 <= now) {
			System.out.println(this.ticks + " FPS");
			this.time = now;
			this.ticks = 0;
		}
	}

	public Graphics2D getRenderer() {
		return this.theGraphics;
	}
	
	public IPrimitives getPrimitives() {
		return this.primitives;
	}
	
	public FontAdapter getFontAdapter() {
		return this.adapter;
	}
	
	public void setDrawingColor(Color color) {
		this.getRenderer().setColor(color);
	}
	
	public void pushOpacity(float opacity) {
		
		Composite currentComposite = this.getRenderer().getComposite();
		
		if(currentComposite instanceof AlphaComposite) {
			this.opacityStack.push(((AlphaComposite) currentComposite).getAlpha());
		}
		
		this.getRenderer().setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
	}
	
	public void popOpacity() {
		this.getRenderer().setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.opacityStack.pop()));
	}
	
	public void mouseClicked(MouseEvent event) {
		Point point = event.getPoint();
		this.dispatcher.fire(new ActionEvent(point, ActionType.CLICK));
	}

	public void mousePressed(MouseEvent event) {
		Point point = event.getPoint();
		this.dispatcher.fire(new ActionEvent(point, ActionType.PRESS));
	}

	public void mouseReleased(MouseEvent event) {
		Point point = event.getPoint();
		this.dispatcher.fire(new ActionEvent(point, ActionType.RELEASE));
	}

	public void mouseEntered(MouseEvent event) {
		Point point = event.getPoint();
		this.dispatcher.fire(new MoveEvent(point, MoveType.ENTER));
	}

	public void mouseExited(MouseEvent event) {
		Point point = event.getPoint();
		this.dispatcher.fire(new MoveEvent(point, MoveType.EXIT));
	}

	public void mouseDragged(MouseEvent event) {
		Point point = event.getPoint();
		this.dispatcher.fire(new MoveEvent(point, MoveType.DRAG));
	}

	public void mouseMoved(MouseEvent event) {
		Point point = event.getPoint();
		this.dispatcher.fire(new MoveEvent(point, MoveType.MOVE));
	}
	
	public void mouseWheelMoved(MouseWheelEvent event) {
		Point point = event.getPoint();
		this.dispatcher.fire(new WheelEvent(point, event.getWheelRotation()));
	}

	public void keyTyped(KeyEvent e) {
		;
	}

	public void keyPressed(KeyEvent event) {
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

	public void keyReleased(KeyEvent e) {
		;
	}
}