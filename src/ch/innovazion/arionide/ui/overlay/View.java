/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.ui.overlay;

import java.util.ArrayList;
import java.util.List;

import ch.innovazion.arionide.debugging.IAm;
import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.animations.Animation;
import ch.innovazion.arionide.ui.animations.FieldModifierAnimation;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.render.PrimitiveFactory;
import ch.innovazion.arionide.ui.render.Shape;
import ch.innovazion.arionide.ui.topology.Bounds;

public abstract class View extends Container {
			
	private final Shape borders = PrimitiveFactory.instance().newRectangle();
	private final Shape mask = PrimitiveFactory.instance().newSolid(0, 127);

	
	private final AppManager appManager;
	private final Animation animation;
	
	private final int focusViewUID;
	
	private View stackParent;
	
	private int alpha;
	private boolean hasBorders;
	
	public View(AppManager appManager, LayoutManager layoutManager) {
		super(null, layoutManager);
		
		this.appManager = appManager;
		
		this.animation = new FieldModifierAnimation(appManager, "alpha", View.class, this);
		this.focusViewUID = getAppManager().getFocusManager().requestViewUID();
		
		mask.updateBounds(new Bounds(0.0f, 0.0f, 2.0f, 2.0f));
	}
	
	public View setBounds(Bounds bounds) {
		super.setBounds(bounds);
		borders.updateBounds(bounds);
		return this;
	}
	
	public void setBorderColor(int rgb) {
		borders.updateRGB(rgb);
		hasBorders = rgb != -1;
	}

	public void drawSurface(AppDrawingContext context) {
		int newAlpha = getAppManager().getAlphaLayering().push(AlphaLayer.VIEW, alpha);
		
		borders.updateAlpha(newAlpha);
		mask.updateAlpha(getMaskAlpha(newAlpha));
		
		getPreferedRenderingSystem(context).renderLater(mask);
		
		if(hasBorders) {
			getPreferedRenderingSystem(context).renderLater(borders);
		}
				
		drawComponents(context);
		
		getAppManager().getAlphaLayering().pop(AlphaLayer.VIEW);
	}
	
	protected void setupFocusCycle(int... elements) {
		List<Integer> cycle = new ArrayList<>();
		
		if(elements.length == 0) {
			int fillingIndex = 0;
			
			elements = new int[getComponents().size()];
			
			while(fillingIndex < getComponents().size()) {
				elements[fillingIndex] = fillingIndex++;
			}
		}
				
		for(int element : elements) {
			cycle.add(focusViewUID + element);
		}
				
		getAppManager().getFocusManager().setupCycle(cycle);
	}
	
	public AppManager getAppManager() {
		return appManager;
	}

	@IAm("navigating to a view")
	public void navigateTo(View target) {
		Transition.replace.show(target, target.animation);
		Transition.replace.hide(this, this.animation);
	}
	
	@IAm("navigating to a view")
	public void navigateFrom(View source) {
		Transition.replace.show(this, this.animation);
		Transition.replace.hide(source, source.animation);
	}
	
	@IAm("stacking a view") 
	public void stack(View target) {
		target.stackParent = this;
		
		Transition.fade.show(target, target.animation);
		Transition.fade.hide(this, this.animation);
	}
	
	@IAm("stacking a view") 
	public void stackOnto(View source) {
		stackParent = source;
		
		Transition.fade.show(this, this.animation);
		Transition.fade.hide(source, source.animation);
	}
	
	@IAm("discarding a view") 
	public void discard() {
		if(stackParent != null) {
			navigateTo(stackParent);
		} else {
			System.err.println("Could not discard a view that was not previously stacked");
		}
	}
	
	public int getOpacity() {
		return alpha;
	}
	
	
	protected int getMaskAlpha(int viewAlpha) {
		return viewAlpha / 2;
	}
	
	protected void viewWillAppear() {
		;
	}
	
	protected void viewWillDisappear() {
		;
	}
	
	public static void showAsInitialView(View view) {
		Transition.slowReplace.show(view, view.animation);
	}
}