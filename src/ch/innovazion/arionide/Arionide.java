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

import ch.innovazion.arionide.debugging.IAm;
import ch.innovazion.arionide.debugging.WatchdogState;
import ch.innovazion.arionide.events.dispatching.IEventDispatcher;
import ch.innovazion.arionide.resources.Resources;
import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.core.CoreOrchestrator;
import ch.innovazion.arionide.ui.layout.LayoutManager;

public interface Arionide {
	
	public static final int RESPAWN_MAX_ATTEMPTS = 5;
	public static final long WATCHDOG_TIMER = 2000L;

	@IAm("preparing the core threads for processing")
	public void startThreads();
	
	@IAm("setting up the event dispatcher")
	public IEventDispatcher setupEventDispatcher();
	
	@IAm("setting up the workspace")
	public Workspace setupWorkspace(IEventDispatcher dispatcher);
	
	@IAm("setting up the application drawing context")
	public AppDrawingContext setupAppDrawingContext(IEventDispatcher dispatcher);

	@IAm("loading the system resources")
	public Resources loadResources(Workspace workspace);
	
	@IAm("loading the core orchestrator")
	public CoreOrchestrator loadCoreOrchestrator(IEventDispatcher dispatcher, Resources resources);
	
	@IAm("setting up the layout manager")
	public LayoutManager setupLayoutManager(AppDrawingContext context, IEventDispatcher dispatcher);
	
	@IAm("showing up the user interface")
	public void loadUI(AppDrawingContext context, Workspace workspace, Resources resources, CoreOrchestrator renderer, LayoutManager manager);
	
	@IAm("running the watchdog")
	public WatchdogState runWatchdog();

	@IAm("shutting down")
	public void shutdown();
}