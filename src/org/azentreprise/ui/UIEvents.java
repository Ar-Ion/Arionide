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
package org.azentreprise.ui;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;

import org.azentreprise.ui.components.UIComponent;
import org.azentreprise.ui.views.UIView;

public class UIEvents {
	
	public static final byte EVENT_MOUSE_ENTER = 0x0;
	public static final byte EVENT_MOUSE_EXIT = 0x1;
	public static final byte EVENT_MOUSE_CLICK = 0x2;
	
	private static final Map<UIComponent, Boolean> mouseHandlers = new ConcurrentHashMap<UIComponent, Boolean>();
	private static final List<UIComponent> keyboardHandlers = new ArrayList<UIComponent>();
	
	private static JFrame frame;
	
	public static void addListeners(JFrame frame) {
		EventDispatcher dispatcher = new EventDispatcher();
		
		frame.addMouseListener(dispatcher);
		frame.addMouseMotionListener(dispatcher);
		frame.addMouseWheelListener(dispatcher);
		frame.getContentPane().addKeyListener(dispatcher);
		
		UIEvents.frame = frame;
	}
	
	public static void registerMouseHandler(UIComponent handler) {
		UIEvents.mouseHandlers.put(handler, false);
	}
	
	public static void registerKeyboardHandler(UIComponent handler) {
		UIEvents.keyboardHandlers.add(handler);
	}
	
	public static void unregisterSubHandlers(UIView view) {
		for(UIComponent component : view.getComponents()) {
			UIEvents.mouseHandlers.remove(component);
			UIEvents.keyboardHandlers.remove(component);
		}
		
		UIMain.setFrameCursor(Cursor.getDefaultCursor());
	}
	
	private static void dispatchMouseEvent(MouseEvent event) {		
		for(Entry<UIComponent, Boolean> handler : UIEvents.mouseHandlers.entrySet()) {
			Point absolute = event.getPoint();
			
			UIEvents.transform(absolute);
			
			Rectangle bounds = (Rectangle) handler.getKey().getParentView().getAbsoluteBounds();
			
			absolute.translate(-bounds.x, -bounds.y);
			
			if(handler.getKey().getPrerenderBounds().contains(absolute)) {
				if(!handler.getValue()) {
					handler.setValue(true);
					handler.getKey().handleMouseEvent(UIEvents.EVENT_MOUSE_ENTER);
				}
				
				if(event.getID() == MouseEvent.MOUSE_PRESSED) { // MacOS bug with MOUSE_CLICKED
					handler.getKey().handleMouseEvent(UIEvents.EVENT_MOUSE_CLICK);
				}
			} else {
				if(handler.getValue()) {
					handler.setValue(false);
					handler.getKey().handleMouseEvent(UIEvents.EVENT_MOUSE_EXIT);
				}
			}
		}
	}
	
	public static void transform(Point point) {
		Insets insets = frame.getInsets();
		point.translate(-insets.left, -insets.top);
	}
	
	private static void dispatchKeyboardEvent(KeyEvent event) {
		for(UIComponent handler : UIEvents.keyboardHandlers) {
			if(!handler.handleKeyboardEvent(event.getKeyCode(), event.getKeyChar(), event.getModifiers())) {
				break;
			}
		}
	}
	
	private static class EventDispatcher implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
		public void mouseWheelMoved(MouseWheelEvent event) {
			UIEvents.dispatchMouseEvent(event);
		}
		
		public void mouseDragged(MouseEvent event) {
			UIEvents.dispatchMouseEvent(event);
		}

		public void mouseMoved(MouseEvent event) {
			UIEvents.dispatchMouseEvent(event);
		}

		public void mouseClicked(MouseEvent event) {
			UIEvents.dispatchMouseEvent(event);
		}

		public void mouseEntered(MouseEvent event) {
			UIEvents.dispatchMouseEvent(event);
		}

		public void mouseExited(MouseEvent event) {
			UIEvents.dispatchMouseEvent(event);
		}

		public void mousePressed(MouseEvent event) {
			UIEvents.dispatchMouseEvent(event);
		}

		public void mouseReleased(MouseEvent event) {
			UIEvents.dispatchMouseEvent(event);
		}

		public void keyPressed(KeyEvent event) {
			UIEvents.dispatchKeyboardEvent(event);
		}

		public void keyReleased(KeyEvent event) {
			return;
		}

		public void keyTyped(KeyEvent event) {
			return;
		}
	}
}
