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
import java.awt.Insets;
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
import org.azentreprise.arionide.events.WheelEvent;
import org.azentreprise.arionide.resources.Resources;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.layout.LayoutManager;

public class AWTDrawingContext extends Panel implements AppDrawingContext, WindowListener, ComponentListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

	private static final long serialVersionUID = 1171699360872561984L;
	
	private final Map<RenderingHints.Key, Object> renderingHints = new HashMap<>();
	
	private final IEventDispatcher dispatcher;
	private final AppManager theManager;
	private final Frame theFrame;
	
	public AWTDrawingContext(IEventDispatcher dispatcher, int width, int height) {
		this.dispatcher = dispatcher;
				
		this.theManager = new AppManager(this);
		this.theFrame = new Frame("Arionide");
		
		this.theFrame.setSize(width, height);
		this.theFrame.setLocationRelativeTo(null);
		
		this.theFrame.add(this);
		
		this.theFrame.addWindowListener(this);
		this.theFrame.addComponentListener(this);
	}

	public void paint(Graphics g) {
		this.draw((Graphics2D) g);
	}
	
	public void load(Arionide theInstance, IWorkspace workspace, Resources resources, CoreRenderer renderer, LayoutManager manager) {
		this.theFrame.setVisible(true);
		this.theManager.loadUI(theInstance, workspace, resources, renderer, manager);
	}

	public void draw(Graphics2D g2d) {
		g2d.setRenderingHints(this.renderingHints);
		this.theManager.draw(g2d);
		this.repaint();
	}

	public void setupRenderingProperties() {
		this.renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		this.renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		this.renderingHints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		this.renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
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
		Insets insets = this.theFrame.getInsets();
		point.translate(-insets.left, -insets.top);
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}