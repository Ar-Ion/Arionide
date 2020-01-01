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
package ch.innovazion.arionide;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import ch.innovazion.arionide.debugging.WatchdogState;
import ch.innovazion.arionide.events.dispatching.DragSystem;
import ch.innovazion.arionide.events.dispatching.IEventDispatcher;
import ch.innovazion.arionide.events.dispatching.MainEventDispatcher;
import ch.innovazion.arionide.resources.Resources;
import ch.innovazion.arionide.threading.DrawingThread;
import ch.innovazion.arionide.threading.EventDispatchingThread;
import ch.innovazion.arionide.threading.UIThread;
import ch.innovazion.arionide.threading.UpdatingThread;
import ch.innovazion.arionide.threading.UserHelpingThread;
import ch.innovazion.arionide.threading.WorkingThread;
import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.OpenGLContext;
import ch.innovazion.arionide.ui.core.CoreController;
import ch.innovazion.arionide.ui.core.CoreOrchestrator;
import ch.innovazion.arionide.ui.core.gl.GLRenderer;
import ch.innovazion.arionide.ui.layout.LayoutManager;

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
		
		return new OpenGLContext(this, dispatcher, 1920, 1080);
	}

	public Resources loadResources(Workspace workspace) {
		return new Resources(workspace);
	}

	public CoreOrchestrator loadCoreOrchestrator(IEventDispatcher dispatcher, Resources resources) {
		CoreController controller = new CoreController();
		GLRenderer renderer = new GLRenderer(controller);
		
		return new CoreOrchestrator(dispatcher, controller, renderer);
	}

	public LayoutManager setupLayoutManager(AppDrawingContext context, IEventDispatcher dispatcher) {
		return new LayoutManager(context, dispatcher);
	}

	public void loadUI(AppDrawingContext context, Workspace workspace, Resources resources, CoreOrchestrator orchestrator, LayoutManager manager) {		
		context.load(workspace, resources, orchestrator, manager);
		
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