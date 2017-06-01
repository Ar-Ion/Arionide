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
package org.azentreprise.arionide.ui.overlay;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.azentreprise.arionide.debugging.IAm;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.animations.Animation;
import org.azentreprise.arionide.ui.animations.FieldModifierAnimation;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.layout.Surface;
import org.azentreprise.ui.render.RoundRectRenderer;

public abstract class View extends Surface {
	
	private final AppManager appManager;
	private final LayoutManager layoutManager;
	
	private final List<Component> components = new ArrayList<>();
	
	private final Animation alphaAnimation;
	
	private Color borderColor = new Color(0, 0, 0, 0);
	private int focus = 0;
	public float alpha = 0.0f;
	
	public View(AppManager appManager, LayoutManager layoutManager) {
		this.appManager = appManager;
		this.layoutManager = layoutManager;
		
		this.alphaAnimation = new FieldModifierAnimation(this.appManager, "alpha", View.class, this);
		
		this.layoutManager.register(this, null, 0.0f, 0.0f, 1.0f, 1.0f);
	}
	
	public void setBorderColor(Color color) {
		this.borderColor = color;
	}
	
	public Animation getAlphaAnimation() {
		return this.alphaAnimation;
	}

	public void drawSurface(Graphics2D g2d) {
		Rectangle bounds = g2d.getClipBounds();

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.alpha));
				
		g2d.setColor(this.borderColor);
		RoundRectRenderer.draw(g2d, 0, 0, bounds.width - 1, bounds.height - 1);
		
		for(Component component : this.components) {
			component.draw(g2d);
		}
	}
	
	protected void add(Component component, float x, float y, float width, float height) {
		this.components.add(component);
		this.layoutManager.register(component, this, x, y, width, height);
	}
	
	protected Component get(int componentID) {
		return this.components.get(componentID);
	}
	
	public void show() {
		super.show();
		this.components.forEach(comp -> comp.show());
	}
	
	public void hide() {
		super.hide();
		this.components.forEach(comp -> comp.hide());
	}
	
	@IAm("opening a view")
	public void openView(View target, boolean transition) {
		this.hide(transition);
		target.show(transition);
	}
	
	public void show(boolean transition) {
		this.show();
		
		if(transition) {
			this.getAlphaAnimation().startAnimation(500, 1.0f);
		}
	}
	
	public void hide(boolean transition) {
		this.hide();
		
		if(transition) {
			this.getAlphaAnimation().startAnimation(500, 0.0f);
		}
	}
	
	public AppManager getAppManager() {
		return this.appManager;
	}
	
	public LayoutManager getLayoutManager() {
		return this.layoutManager;
	}
	
	private void updateFocus() {
		if(this.components.size() > this.focus) {
			
			if(this.focus < 0) {
				this.components.forEach(comp -> this.componentFocusLost(comp));
			} else {
				Component component = this.components.get(this.focus);
				
				component.focusGained();
				
				this.components.forEach(other -> this.componentFocusLost(other != component ? other : null));
			}
		}
	}
	
	private void componentFocusLost(Component comp) {
		if(comp != null) {
			comp.focusLost();
		}
	}
	
	public void requestFocus(Component component) {
		this.setFocus(this.components.indexOf(component));
	}
	
	protected void setFocus(int componentID) {
		if(this.hasFocusableComponent()) {
			if(componentID > this.focus) {
				while(componentID < this.components.size() && !this.components.get(componentID).isFocusable()) {
					componentID++;
				}
			} else {
				while(componentID > 0 && !this.components.get(componentID).isFocusable()) {
					componentID--;
				}
			}
			
			if(componentID >= 0 && componentID < this.components.size()) {
				this.focus = componentID;
				this.updateFocus();
			}
		}
	}
	
	private boolean hasFocusableComponent() {
		boolean hasFocusableComponent = false;
		
		for(Component comp : this.components) {
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
}