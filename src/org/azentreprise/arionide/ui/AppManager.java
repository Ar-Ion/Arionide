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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.azentreprise.arionide.Arionide;
import org.azentreprise.arionide.IWorkspace;
import org.azentreprise.arionide.debugging.IAm;
import org.azentreprise.arionide.events.IEventDispatcher;
import org.azentreprise.arionide.events.InvalidateLayoutEvent;
import org.azentreprise.arionide.resources.Resources;
import org.azentreprise.arionide.ui.animations.Animation;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.overlay.Views;

public class AppManager {
	
	private final AppDrawingContext drawingContext;
	private final IEventDispatcher dispatcher;
	private final FocusManager focusManager;
	
	private final List<Animation> animations = new ArrayList<>();
	
	private Arionide theInstance;
	private IWorkspace workspace;
	private Resources resources;
	private CoreRenderer renderer;
	
	private boolean initialized = false;
	
	public AppManager(AppDrawingContext drawingContext, IEventDispatcher dispatcher) {
		this.drawingContext = drawingContext;
		this.dispatcher = dispatcher;
		this.focusManager = new FocusManager(dispatcher);
	}

	@IAm("drawing the frame")
	public void draw(Graphics2D g2d) {
		
		Rectangle bounds = new Rectangle(this.getDrawingContext().getSize());
		
		if(this.initialized) {
			this.renderer.render(g2d, bounds);
			
			this.tickAnimations();
			
			for(Drawable view : Views.all) {
				view.draw(g2d, bounds);
			}
		}
	}
	
	@IAm("ticking the animations")
	public void tickAnimations() {
		for(Animation animation : this.animations) {
			animation.doTick();
		}
	}
	
	public synchronized void registerAnimation(Animation animation) {
		this.animations.add(animation);
	}
	
	public void loadUI(Arionide theInstance, IWorkspace workspace, Resources resources, CoreRenderer renderer, LayoutManager manager) {
		this.theInstance = theInstance;
		this.workspace = workspace;
		this.resources = resources;
		this.renderer = renderer;
		
		Views.init(this, manager);
		this.initialized = true;
		
		this.getEventDispatcher().fire(new InvalidateLayoutEvent());
		
		Views.main.show(true);
	}
	
	public AppDrawingContext getDrawingContext() {
		return this.drawingContext;
	}
	
	public IEventDispatcher getEventDispatcher() {
		return this.dispatcher;
	}
	
	public FocusManager getFocusManager() {
		return this.focusManager;
	}
	
	public Resources getResources() {
		return this.resources;
	}
	
	public IWorkspace getWorkspace() {
		return this.workspace;
	}
	
	public void shutdown() {
		this.theInstance.shutdown();
	}
}