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

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.azentreprise.arionide.Arionide;
import org.azentreprise.arionide.IWorkspace;
import org.azentreprise.arionide.events.ActionEvent;
import org.azentreprise.arionide.events.ActionType;
import org.azentreprise.arionide.events.IEventDispatcher;
import org.azentreprise.arionide.events.InvalidateLayoutEvent;
import org.azentreprise.arionide.events.MoveEvent;
import org.azentreprise.arionide.events.MoveType;
import org.azentreprise.arionide.events.ValidateEvent;
import org.azentreprise.arionide.events.WheelEvent;
import org.azentreprise.arionide.events.WriteEvent;
import org.azentreprise.arionide.resources.Resources;
import org.azentreprise.arionide.threading.WorkingThread;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.layout.LayoutManager;

public class AWTDrawingContext extends Panel implements AppDrawingContext, WindowListener, ComponentListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

	private static final long serialVersionUID = 1171699360872561984L;
	
	private final Map<RenderingHints.Key, Object> renderingHints = new HashMap<>();
		
	private final IEventDispatcher dispatcher;
	private final AppManager theManager;
	private final Frame theFrame;
	
	private Image buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	private Graphics2D bufferGraphics = (Graphics2D) this.buffer.getGraphics();
	
	private int ticks = 0;
	private long time = 0;
	
	public AWTDrawingContext(IEventDispatcher dispatcher, int width, int height) {
		this.dispatcher = dispatcher;
		
		this.theManager = new AppManager(this, dispatcher);
		this.theFrame = new Frame("Arionide");
		
		this.theFrame.setSize(width, height);
		this.theFrame.setLocationRelativeTo(null);
		
		this.theFrame.add(this);
		
		this.theFrame.addWindowListener(this);
		this.theFrame.addComponentListener(this);

		this.theFrame.addKeyListener(this);
		this.addKeyListener(this);
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		
		this.setFocusTraversalKeysEnabled(false);
		this.theFrame.setFocusTraversalKeysEnabled(false);
	}

	public void paint(Graphics g) {
		this.ticks++;

		this.draw((Graphics2D) g);
					
		long now = System.currentTimeMillis();
		
		if(this.time + 1000 <= now) {
			System.out.println(this.ticks + " FPS");
			this.time = now;
			this.ticks = 0;
		}
		
		this.repaint();
	}
	
	public void load(Arionide theInstance, IWorkspace workspace, Resources resources, CoreRenderer renderer, LayoutManager manager) {
		this.theFrame.setVisible(true);
		this.theManager.loadUI(theInstance, workspace, resources, renderer, manager);
	}

	public void draw(Graphics2D g2d) {		
		this.bufferGraphics.setRenderingHints(this.renderingHints);
		this.theManager.draw(this.bufferGraphics);
		
		g2d.drawImage(this.buffer, 0, 0, null);
	}
	
	private void resetBuffer(int width, int height) {
		if(this.buffer != null) {
			this.buffer.flush();
		}
		
		if(this.bufferGraphics != null) {
			this.bufferGraphics.dispose();
		}
		
		this.buffer = this.createImage(width, height);		
		this.bufferGraphics = (Graphics2D) this.buffer.getGraphics();
	}

	public void setupRenderingProperties() {
		this.renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		this.renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		this.renderingHints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		this.renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
	}
	
	public WorkingThread getWrapperThread() {
		return null;
	}

	public void windowOpened(WindowEvent e) {
		;
	}

	public void windowClosing(WindowEvent event) {
		this.theManager.shutdown();
	}

	public void windowClosed(WindowEvent e) {
		;
	}

	public void windowIconified(WindowEvent e) {
		;
	}

	public void windowDeiconified(WindowEvent e) {
		;
	}

	public void windowActivated(WindowEvent e) {
		;
	}

	public void windowDeactivated(WindowEvent e) {
		;
	}

	public void componentResized(ComponentEvent event) {
		this.resetBuffer(this.getWidth(), this.getHeight());
		this.dispatcher.fire(new InvalidateLayoutEvent());
	}

	public void componentMoved(ComponentEvent e) {
		;
	}

	public void componentShown(ComponentEvent e) {
		;
	}

	public void componentHidden(ComponentEvent e) {
		;
	}
	
	public void mouseClicked(MouseEvent event) {
		Point point = event.getPoint();
		this.transform(point);
		this.dispatcher.fire(new ActionEvent(point, ActionType.CLICK));
	}

	public void mousePressed(MouseEvent event) {
		Point point = event.getPoint();
		this.transform(point);
		this.dispatcher.fire(new ActionEvent(point, ActionType.PRESS));
	}

	public void mouseReleased(MouseEvent event) {
		Point point = event.getPoint();
		this.transform(point);
		this.dispatcher.fire(new ActionEvent(point, ActionType.RELEASE));
	}

	public void mouseEntered(MouseEvent event) {
		Point point = event.getPoint();
		this.transform(point);
		this.dispatcher.fire(new MoveEvent(point, MoveType.ENTER));
	}

	public void mouseExited(MouseEvent event) {
		Point point = event.getPoint();
		this.transform(point);
		this.dispatcher.fire(new MoveEvent(point, MoveType.EXIT));
	}

	public void mouseDragged(MouseEvent event) {
		Point point = event.getPoint();
		this.transform(point);
		this.dispatcher.fire(new MoveEvent(point, MoveType.DRAG));
	}

	public void mouseMoved(MouseEvent event) {
		Point point = event.getPoint();
		this.transform(point);
		this.dispatcher.fire(new MoveEvent(point, MoveType.MOVE));
	}
	
	public void mouseWheelMoved(MouseWheelEvent event) {
		Point point = event.getPoint();
		this.transform(point);
		this.dispatcher.fire(new WheelEvent(point, event.getWheelRotation()));
	}
	
	private void transform(Point point) {
		// uncomment if bugs using Windows
		
		/* Insets insets = this.theFrame.getInsets();
		   point.translate(-insets.left, -insets.top); */
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
			this.dispatcher.fire(new WriteEvent(event.getKeyChar(), event.getKeyCode(), event.isShiftDown(), event.isMetaDown() || event.isControlDown(), event.isAltDown()));
		}
	}

	public void keyReleased(KeyEvent e) {
		;
	}
}