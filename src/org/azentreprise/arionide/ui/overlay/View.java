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
package org.azentreprise.arionide.ui.overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.azentreprise.arionide.debugging.IAm;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.animations.Animation;
import org.azentreprise.arionide.ui.animations.FieldModifierAnimation;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.layout.Surface;
import org.azentreprise.arionide.ui.render.PrimitiveFactory;
import org.azentreprise.arionide.ui.render.Shape;
import org.azentreprise.arionide.ui.topology.Bounds;

public abstract class View extends Surface {
	
	private final AppManager appManager;
	private final LayoutManager layoutManager;

	private final List<Component> components = new ArrayList<>();
	
	private final Animation alphaAnimation;
	private final Shape borders = PrimitiveFactory.instance().newRectangle();
	
	private final int focusViewUID;
	
	private boolean hasBorders;
	private int alpha = 0;
	
	public View(AppManager appManager, LayoutManager layoutManager) {
		this.appManager = appManager;
		this.layoutManager = layoutManager;

		this.alphaAnimation = new FieldModifierAnimation(this.appManager, "alpha", View.class, this);
		
		this.focusViewUID = this.getAppManager().getFocusManager().requestViewUID();
	}
	
	public View setBounds(Bounds bounds) {
		super.setBounds(bounds);
		this.borders.updateBounds(bounds);
		return this;
	}
	
	public void setBorderColor(int rgb) {
		this.borders.updateRGB(rgb);
		this.hasBorders = rgb != -1;
	}
	
	public void load() {
		for(Component component : this.components) {
			component.load();
		}
	}
	
	public AppManager getAppManager() {
		return this.appManager;
	}

	public void drawSurface(AppDrawingContext context) {		
		this.borders.updateAlpha(this.appManager.getAlphaLayering().push(AlphaLayer.VIEW, this.alpha));
		
		if(this.hasBorders) {
			context.getRenderingSystem().renderLater(this.borders);
		}
		
		this.drawComponents(context);
		
		this.appManager.getAlphaLayering().pop(AlphaLayer.VIEW);
	}
	
	protected void drawComponents(AppDrawingContext context) {
		for(Component component : this.components) {
			component.draw(context);
		}
	}
	
	public void update() {
		for(Component component : this.components) {
			component.update();
		}
	}
	
	protected void add(Component component, float x1, float y1, float x2, float y2) {
		this.components.add(component);
		this.layoutManager.register(component, this, x1, y1, x2, y2);
		this.getAppManager().getFocusManager().registerComponent(component);
	}
	
	protected List<Component> getComponents() {
		return Collections.unmodifiableList(this.components);
	}
	
	protected void setupFocusCycle(int... elements) {
		List<Integer> cycle = new ArrayList<>();
		
		if(elements.length == 0) {
			int fillingIndex = 0;
			
			elements = new int[this.components.size()];
			
			while(fillingIndex < this.components.size()) {
				elements[fillingIndex] = fillingIndex++;
			}
		}
				
		for(int element : elements) {
			cycle.add(this.focusViewUID + element);
		}
				
		this.getAppManager().getFocusManager().setupCycle(cycle);
	}
	
	public void show() {
		super.show();
		this.components.forEach(Component::show);
	}
	
	public void hide() {
		super.hide();
		this.components.forEach(Component::hide);
	}
	
	public void openView(View target) {
		this.openView(target, true);
	}
	
	@IAm("opening a view")
	public void openView(View target, boolean transition) {
		target.show(transition);
		this.hide(transition);
	}
	
	public void show(boolean transition) {		
		this.show();
		
		if(transition) {
			this.alphaAnimation.startAnimation(500, 255);
		} else {
			this.alpha = 255;
		}
	}
	
	public void hide(boolean transition) {		
		if(transition) {
			this.getAppManager().getEventDispatcher().pause();
			
			this.alphaAnimation.startAnimation(500, after -> {
				this.hide();
				this.getAppManager().getEventDispatcher().resume();
			}, 0);
		} else {
			this.alpha = 0;
			this.hide();
		}
	}
}