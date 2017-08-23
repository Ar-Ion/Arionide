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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.arionide;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.azentreprise.arionide.debugging.WatchdogState;
import org.azentreprise.arionide.events.dispatching.DragSystem;
import org.azentreprise.arionide.events.dispatching.IEventDispatcher;
import org.azentreprise.arionide.events.dispatching.MainEventDispatcher;
import org.azentreprise.arionide.resources.Resources;
import org.azentreprise.arionide.threading.DrawingThread;
import org.azentreprise.arionide.threading.EventDispatchingThread;
import org.azentreprise.arionide.threading.UIThread;
import org.azentreprise.arionide.threading.UpdatingThread;
import org.azentreprise.arionide.threading.UserHelpingThread;
import org.azentreprise.arionide.threading.WorkingThread;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.OpenGLDrawingContext;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.core.opengl.OpenGLCoreRenderer;
import org.azentreprise.arionide.ui.layout.LayoutManager;

public class ArionideImpl implements Arionide {
	
	private EventDispatchingThread eventThread;
	private WorkingThread userThread;
	private UIThread drawingThread;
	private UIThread updatingThread;
	
	public void startThreads() {
		this.eventThread = new EventDispatchingThread();
		this.userThread = new UserHelpingThread();
		this.drawingThread = new DrawingThread();
		this.updatingThread = new UpdatingThread();
		
		this.eventThread.start();
		this.userThread.start();
		this.drawingThread.start();
		this.updatingThread.start();
	}

	public IEventDispatcher setupEventDispatcher() {
		return new MainEventDispatcher(this.eventThread);
	}
	
	public Workspace setupWorkspace(IEventDispatcher dispatcher) {
		File path = new File(System.getProperty("user.home") + File.separator + "Arionide Workspace");
		path.mkdirs();
		return new LocalWorkspace(path, dispatcher);
	}

	public AppDrawingContext setupAppDrawingContext(IEventDispatcher dispatcher) {
		DragSystem.init(dispatcher);
		return new OpenGLDrawingContext(this, dispatcher, 1080, 720);
	}

	public Resources loadResources(Workspace workspace, AppDrawingContext context) {
		return new Resources(workspace, context);
	}

	public CoreRenderer loadCoreRenderer(AppDrawingContext context, IEventDispatcher dispatcher, Resources resources) {
		return new OpenGLCoreRenderer(context, dispatcher);
	}

	public LayoutManager setupLayoutManager(AppDrawingContext context, IEventDispatcher dispatcher) {
		return new LayoutManager(context, dispatcher);
	}

	public void loadUI(AppDrawingContext context, Workspace workspace, Resources resources, CoreRenderer renderer, LayoutManager manager) {
		context.load(workspace, resources, renderer, manager);
				
		this.drawingThread.setupManager(context);
		this.updatingThread.setupManager(context);
	}
	
	// note: this list is not mutable
	private List<WorkingThread> getSystemThreads() {
		return Arrays.asList(this.eventThread, this.userThread, this.drawingThread, this.updatingThread);
	}

	public WatchdogState runWatchdog() {
		for(WorkingThread thread : this.getSystemThreads()) {
			int ticks = thread.pollTicks();
			System.out.println("Thread '" + thread.getDescriptor() + "' is running at " + (1000 * ticks / Arionide.WATCHDOG_TIMER) + " TPS.");
			
			if(ticks == 0) {
				System.err.println("Thread '" + thread.getDescriptor() + "' is not responding...");
				
				boolean crashed = true;
				
				for(int i = 0; i < Arionide.RESPAWN_MAX_ATTEMPTS; i++) {
					if(thread.respawn(i)) {
						crashed = false;
						break;
					}
					
					try {
						Thread.sleep(Arionide.WATCHDOG_TIMER);
					} catch (InterruptedException e) {
						;
					}
				}
				
				if(crashed) {
					return WatchdogState.CRASH;
				}
			}
		}
		
		System.out.println();
		
		return WatchdogState.NO_PROBLEM;
	}

	public void shutdown() {
		System.exit(0);
	}
}