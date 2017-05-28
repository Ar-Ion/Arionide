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
import java.util.ArrayList;
import java.util.Collections;
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
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.arionide.ui.overlay.views.MainView;

public class AppManager {
	
	private final AppDrawingContext drawingContext;
	private final IEventDispatcher dispatcher;
	
	private final List<Animation> animations = Collections.synchronizedList(new ArrayList<>());
	
	private Arionide theInstance;
	private IWorkspace workspace;
	private Resources resources;
	private CoreRenderer renderer;
	private LayoutManager layoutManager;
	
	private View view;
	private View auxView;
	
	public AppManager(AppDrawingContext drawingContext, IEventDispatcher dispatcher) {
		this.drawingContext = drawingContext;
		this.dispatcher = dispatcher;
	}

	public void draw(Graphics2D g2d) {
		// g2d.drawImage(this.resources.getBackground(), 0, 0, null);
		
		this.tickAnimations();
		this.drawCurrentView(g2d);
	}
	
	@IAm("ticking the animations")
	public synchronized void tickAnimations() {
		for(Animation animation : this.animations) {
			animation.doTick();
		}
	}
	
	@IAm("drawing the current view")
	public void drawCurrentView(Graphics2D g2d) {
		if(this.view != null) {
			this.view.draw(g2d);
		}
		
		if(this.auxView != null) {
			this.auxView.draw(g2d);
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
		this.layoutManager = manager;
				
		this.showView(new MainView(null, this, this.layoutManager), true);
		
		this.layoutManager.handleEvent(new InvalidateLayoutEvent());
	}
	
	public void showView(View view, boolean transition) {
		if(this.view != view && view != null) {
			if(this.view != null) {
				this.view.getAlphaAnimation().stopAnimation();
				this.layoutManager.unregister(this.view);
			}
			
			if(this.auxView != null) {
				this.auxView.getAlphaAnimation().stopAnimation();
			}
			
			if(transition) {
				if(this.view != null) {
					this.auxView = this.view;
					this.view = view;
					
					this.view.getAlphaAnimation().startAnimation(1000, 1.0f);
					this.auxView.getAlphaAnimation().startAnimation(1000, 0.0f);
				} else {
					this.view = view;
					this.view.getAlphaAnimation().startAnimation(1000, 1.0f);
				}
			} else {
				this.view = view;
			}
		}
	}
	
	public AppDrawingContext getDrawingContext() {
		return this.drawingContext;
	}
	
	public IEventDispatcher getEventDispatcher() {
		return this.dispatcher;
	}
	
	public Resources getResources() {
		return this.resources;
	}
	
	public void shutdown() {
		this.theInstance.shutdown();
	}
}