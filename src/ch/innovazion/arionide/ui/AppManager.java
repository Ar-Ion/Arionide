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
package ch.innovazion.arionide.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.innovazion.arionide.Arionide;
import ch.innovazion.arionide.Workspace;
import ch.innovazion.arionide.debugging.IAm;
import ch.innovazion.arionide.events.InvalidateLayoutEvent;
import ch.innovazion.arionide.events.dispatching.IEventDispatcher;
import ch.innovazion.arionide.resources.Resources;
import ch.innovazion.arionide.threading.Purgeable;
import ch.innovazion.arionide.threading.timing.Timer;
import ch.innovazion.arionide.ui.animations.Animation;
import ch.innovazion.arionide.ui.core.CoreOrchestrator;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.overlay.AlphaLayeringSystem;
import ch.innovazion.arionide.ui.overlay.View;
import ch.innovazion.arionide.ui.overlay.Views;

public class AppManager implements Purgeable {
		
	private final Arionide theInstance;
	private final AppDrawingContext drawingContext;
	private final IEventDispatcher dispatcher;
	private final FocusManager focusManager;
	private final Timer systemTimer;
	private final AlphaLayeringSystem alphaLayering;
	
	private final List<Animation> animations = Collections.synchronizedList(new ArrayList<>());
	
	private Workspace workspace;
	private Resources resources;
	private CoreOrchestrator orchestrator;
	private LayoutManager layout;
	
	private boolean initialized = false;
	
	public AppManager(Arionide theInstance, AppDrawingContext drawingContext, IEventDispatcher dispatcher) {
		this.theInstance = theInstance;
		this.drawingContext = drawingContext;
		this.dispatcher = dispatcher;
		this.focusManager = new FocusManager(dispatcher);
		this.systemTimer = new Timer(dispatcher);
		this.alphaLayering = new AlphaLayeringSystem();
	}

	@IAm("drawing the frame")
	public void draw() {		
		if(this.initialized) {						
			for(Drawable view : Views.getList()) {
				view.draw(this.getDrawingContext());
			}
		}
	}

	@IAm("updating the frame")
	public void update(){
		synchronized(this.animations) {
			for(Animation animation : this.animations) {
				animation.doTick();
			}
		}
		
		for(Drawable view : Views.getList()) {
			view.update();
		}
	}
	
	public void registerAnimation(Animation animation) {
		synchronized(this.animations) {
			this.animations.add(animation);
		}
	}
	
	public void initUI(Workspace workspace, Resources resources, CoreOrchestrator orchestrator, LayoutManager manager) {
		this.workspace = workspace;
		this.resources = resources;
		this.orchestrator = orchestrator;
		this.layout = manager;
		
		this.initialized = true;
	}
	
	public void loadUI() {		
		Views.init(this, this.layout);
		
		this.layout.compute();
		this.getEventDispatcher().fire(new InvalidateLayoutEvent());
		
		Views.load();
		
		View.showAsInitialView(Views.main);
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
	
	public Timer getSystemTimer() {
		return this.systemTimer;
	}
	
	public AlphaLayeringSystem getAlphaLayering() {
		return this.alphaLayering;
	}
	
	public Resources getResources() {
		return this.resources;
	}
	
	public Workspace getWorkspace() {
		return this.workspace;
	}
	
	public CoreOrchestrator getCoreOrchestrator() {
		return this.orchestrator;
	}
	
	public void shutdown() {
		this.theInstance.shutdown();
	}
	
	public void purge() {
		this.animations.clear();
	}
}