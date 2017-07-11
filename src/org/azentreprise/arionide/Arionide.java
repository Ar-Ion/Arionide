/*******************************************************************************
 * This file is part of ArionIDE.
 *
 * ArionIDE is an IDE whose purpose is to build a language from assembly. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
 *
 * ArionIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArionIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with ArionIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.arionide;

import org.azentreprise.arionide.debugging.IAm;
import org.azentreprise.arionide.debugging.WatchdogState;
import org.azentreprise.arionide.events.dispatching.IEventDispatcher;
import org.azentreprise.arionide.resources.Resources;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.layout.LayoutManager;

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
	public Resources loadResources(Workspace workspace, AppDrawingContext context);
	
	@IAm("loading the core renderer")
	public CoreRenderer loadCoreRenderer(AppDrawingContext context, IEventDispatcher dispatcher, Resources resources);
	
	@IAm("setting up the layout manager")
	public LayoutManager setupLayoutManager(AppDrawingContext context, IEventDispatcher dispatcher);
	
	@IAm("showing up the user interface")
	public void loadUI(AppDrawingContext context, Workspace workspace, Resources resources, CoreRenderer renderer, LayoutManager manager);
	
	@IAm("running the watchdog")
	public WatchdogState runWatchdog();

	@IAm("shutting down")
	public void shutdown();
}