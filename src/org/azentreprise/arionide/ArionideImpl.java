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
package org.azentreprise.arionide;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.azentreprise.arionide.debugging.WatchdogState;
import org.azentreprise.arionide.events.IEventDispatcher;
import org.azentreprise.arionide.events.MainEventDispatcher;
import org.azentreprise.arionide.resources.Resources;
import org.azentreprise.arionide.threading.EventDispatchingThread;
import org.azentreprise.arionide.threading.MiscProcessingThread;
import org.azentreprise.arionide.threading.UserHelpingThread;
import org.azentreprise.arionide.threading.WorkingThread;
import org.azentreprise.arionide.ui.AWTDrawingContext;
import org.azentreprise.arionide.ui.AWTWrapperThread;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.layout.LayoutManager;

public class ArionideImpl implements Arionide {
	
	private EventDispatchingThread eventThread;
	private WorkingThread userThread;
	private WorkingThread miscThread;
	private AWTWrapperThread drawingThread;
	
	public void startThreads() {
		this.eventThread = new EventDispatchingThread();
		this.userThread = new UserHelpingThread();
		this.miscThread = new MiscProcessingThread();
		this.drawingThread = new AWTWrapperThread();
		
		this.eventThread.start();
		this.userThread.start();
		this.miscThread.start();
		this.drawingThread.start();
	}

	public IEventDispatcher setupEventDispatcher() {
		return new MainEventDispatcher(this.eventThread);
	}
	
	public IWorkspace setupWorkspace(IEventDispatcher dispatcher) {
		File path = new File(System.getProperty("user.home") + File.separator + "Arionide Workspace");
		return new Workspace(path, dispatcher);
	}

	public AppDrawingContext setupAppDrawingContext(IEventDispatcher dispatcher) {
		return new AWTDrawingContext(this.drawingThread, dispatcher, 800, 600);
	}

	public Resources loadResources(IWorkspace workspace, AppDrawingContext context) {
		return new Resources(workspace, context);
	}

	public CoreRenderer loadCoreRenderer(AppDrawingContext context, IEventDispatcher dispatcher, Resources resources) {
		return new CoreRenderer();
	}

	public LayoutManager setupLayoutManager(AppDrawingContext context, IEventDispatcher dispatcher) {
		return new LayoutManager(context, dispatcher);
	}

	public void loadUI(Arionide theInstance, AppDrawingContext context, IWorkspace workspace, Resources resources, CoreRenderer renderer, LayoutManager manager) {
		context.load(theInstance, workspace, resources, renderer, manager);
	}
	
	// note: this list is not mutable
	private List<WorkingThread> getSystemThreads() {
		return Arrays.asList(this.eventThread, this.userThread, this.miscThread, this.drawingThread);
	}

	public WatchdogState runWatchdog() {
		Stream<WorkingThread> notRespondingThreads = this.getSystemThreads().stream().filter(thread -> thread.getLagRate() > 10.0f);

		this.getSystemThreads().stream()
			.forEach(thread -> System.out.println("Lag rate for thread '" + thread.getDescriptor() + "' is " + thread.getLagRate() * 100 + "% "
				+ "and is running at " + (thread.pollTicks() / Arionide.WATCHDOG_TIMER) + " TPS."));
		
		System.out.println();
		
		Iterator<WorkingThread> iterator = notRespondingThreads.iterator();
		
		while(iterator.hasNext()) {
			WorkingThread thread = iterator.next();
			
			System.err.println("Thread '" + thread.getDescriptor() + "' is not responding... trying to respawn...");
			
			boolean crashed = true;
			
			for(int i = 0; i < Arionide.RESPAWN_MAX_ATTEMPTS; i++) {
				if(thread.respawn(i)) {
					crashed = false;
					break;
				}
			}
			
			if(crashed) {
				return WatchdogState.CRASH;
			}
		}
		
		return WatchdogState.NO_PROBLEM;
	}

	public void shutdown() {
		System.exit(0);
	}
}