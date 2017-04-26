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
package org.azentreprise.ui.views;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.azentreprise.Debug;
import org.azentreprise.ui.Drawable;
import org.azentreprise.ui.UIEvents;
import org.azentreprise.ui.UIMain;
import org.azentreprise.ui.components.UIComponent;
import org.azentreprise.ui.render.RoundRectRenderer;

public abstract class UIView implements Drawable {
	
	private final ArrayList<UIComponent> innerComponents = new ArrayList<UIComponent>();
		
	private Color background = new Color(0, 0, 0, 0);
	private Color borderColor = new Color(0, 0, 0, 0);
	private final float x1, x2, y1, y2;
	private int focus = 0;
	
	private Rectangle absoluteBounds = new Rectangle();
	private Rectangle relativeBounds = new Rectangle();
	
	private final UIView parent;
	private final Frame rootComponent;
	
	public UIView(UIView parent, Frame rootComponent, float x1, float y1, float x2, float y2) {
		this.parent = parent;
		this.rootComponent = rootComponent;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public void drawView(Graphics2D g2d) {
		Rectangle bounds = g2d.getClipBounds();
		this.absoluteBounds = bounds;
		bounds.setLocation((int) (bounds.width * this.x1), (int) (bounds.height * this.y1));
		bounds.setSize((int) ((bounds.width + 1) * this.x2) - bounds.x, (int) ((bounds.height + 1) * this.y2) - bounds.y);
		
		this.relativeBounds = bounds;
		
		g2d = (Graphics2D) g2d.create(bounds.x, bounds.y, bounds.width, bounds.height);

		g2d.setColor(this.background);
		RoundRectRenderer.fill(g2d, 0, 0, bounds.width - 1, bounds.height - 1);
		g2d.setColor(this.borderColor);
		RoundRectRenderer.draw(g2d, 0, 0, bounds.width - 1, bounds.height - 1);
	
		this.draw(g2d);
		
		for(UIComponent component : this.innerComponents) {
			component.prerender(bounds.width, bounds.height);
			component.render(g2d);
		}
	}
	
	public void draw(Graphics2D g2d) {
		return;
	}

	public Rectangle getAbsoluteBounds() {
		return this.absoluteBounds;
	}
	
	public Frame getRootComponent() {
		return this.rootComponent;
	}
	
	public UIView getParentView() {
		return this.parent;
	}
	
	public List<UIComponent> getComponents() {
		return Collections.unmodifiableList(this.innerComponents);
	}
	
	protected Rectangle getRelativeBounds() {
		return this.relativeBounds;
	}
		
	protected void setBackground(Color background) {
		this.background = background;
	}
	
	protected void add(UIComponent component) {
		this.innerComponents.add(component);
	}
	
	protected void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}
	
	protected void back() {
		try {
			UIMain.show(this.parent.getClass().getConstructor(UIView.class, Frame.class).newInstance(this.parent.parent, this.parent.getRootComponent()));
		} catch (Exception exception) {
			Debug.exception(exception);
		}
	}
	
	private void updateFocus() {
		if(this.innerComponents.size() > this.focus) {
			
			if(this.focus < 0) {
				this.innerComponents.forEach(comp -> this.componentFocusLost(comp));
			} else {
				UIComponent component = this.innerComponents.get(this.focus);
				component.focusGained();
				this.innerComponents.forEach(comp -> this.componentFocusLost(comp != component ? comp : null));
			}
		}
	}
	
	private void componentFocusLost(UIComponent comp) {
		if(comp != null) {
			comp.focusLost();
		}
	}
	
	public void requestFocus(UIComponent component) {
		this.setFocus(this.innerComponents.indexOf(component));
	}
	
	protected void setFocus(int componentID) {
		if(this.hasFocusableComponent()) {
			if(componentID > this.focus) {
				while(componentID < this.innerComponents.size() && !this.innerComponents.get(componentID).isFocusable()) {
					componentID++;
				}
			} else {
				while(componentID > 0 && !this.innerComponents.get(componentID).isFocusable()) {
					componentID--;
				}
			}
			
			if(componentID >= 0 && componentID < this.innerComponents.size()) {
				this.focus = componentID;
				this.updateFocus();
			}
		}
	}
	
	public boolean flagNoFocus() {
		boolean buffer = this.focus < 0;
		
		this.focus = -1;
		this.updateFocus();
		
		return buffer;
	}
	
	private boolean hasFocusableComponent() {
		boolean hasFocusableComponent = false;
		
		for(UIComponent comp : this.innerComponents) {
			if(comp.isFocusable()) {
				hasFocusableComponent = true;
				break;
			}
		}
		
		return hasFocusableComponent;
	}
	
	public void nextFocus() {		
		this.setFocus(this.focus + 1);
	}
	
	public void prevFocus() {
		this.setFocus(this.focus - 1);
	}
	
	public void action() {
		if(this.focus > 0) {
			this.innerComponents.get(this.focus).handleMouseEvent(UIEvents.EVENT_MOUSE_CLICK);
		}
	}
}