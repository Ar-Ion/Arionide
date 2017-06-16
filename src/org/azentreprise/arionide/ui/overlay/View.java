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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.arionide.ui.overlay;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.azentreprise.arionide.debugging.IAm;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.animations.Animation;
import org.azentreprise.arionide.ui.animations.FieldModifierAnimation;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.layout.Surface;

public abstract class View extends Surface {
	
	protected static final int[] NATURAL_FOCUS_CYCLE = new int[0];
	
	private final AppManager appManager;
	private final LayoutManager layoutManager;

	private final List<Component> components = new ArrayList<>();
	
	private final Animation alphaAnimation;
	
	private final int focusViewUID;
	
	private Color borderColor = null;
	public float alpha = 0.0f;
	
	public View(AppManager appManager, LayoutManager layoutManager) {
		this.appManager = appManager;
		this.layoutManager = layoutManager;

		this.alphaAnimation = new FieldModifierAnimation(this.appManager, "alpha", View.class, this);
		
		this.focusViewUID = this.getAppManager().getFocusManager().requestViewUID();
	}
	
	public void setBorderColor(Color color) {
		this.borderColor = color;
	}
	
	public Animation getAlphaAnimation() {
		return this.alphaAnimation;
	}

	public void drawSurface(AppDrawingContext context) {
		context.pushOpacity(this.alpha);
		
		if(this.borderColor != null) {
			context.setDrawingColor(this.borderColor);
			context.getPrimitives().drawRoundRect(context, this.getBounds());
		}
		
		for(Component component : this.components) {
			component.draw(context);
		}
		
		context.popOpacity();
	}
	
	protected void add(Component component, float x, float y, float width, float height) {
		this.components.add(component);
		this.getLayoutManager().register(component, this, x, y, width, height);
		this.getAppManager().getFocusManager().registerComponent(component);
	}
	
	protected Component get(int componentID) {
		return this.components.get(componentID);
	}
	
	protected int[] makeFocusCycle(int... elements) {		
		if(elements == View.NATURAL_FOCUS_CYCLE) {
			elements = new int[this.components.size()];
			
			int fillingIndex = 0;
			
			while(fillingIndex < elements.length) {
				elements[fillingIndex] = fillingIndex++;
			}
		}
		
		for(int i = 0; i< elements.length; i++) {
			elements[i] += this.focusViewUID;
		}
		
		return elements;
	}
	
	public void show() {
		super.show();
		this.components.forEach(comp -> comp.show());
	}
	
	public void hide() {
		super.hide();
		this.components.forEach(comp -> comp.hide());
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
			this.getAlphaAnimation().startAnimation(500, 1.0f);
		} else {
			this.alpha = 1.0f;
		}
	}
	
	public void hide(boolean transition) {		
		if(transition) {
			this.getAlphaAnimation().startAnimation(500, after -> {
				this.hide();
			}, 0.0f);
		} else {
			this.alpha = 0.0f;
			this.hide();
		}
	}
	
	public AppManager getAppManager() {
		return this.appManager;
	}
	
	public LayoutManager getLayoutManager() {
		return this.layoutManager;
	}
}