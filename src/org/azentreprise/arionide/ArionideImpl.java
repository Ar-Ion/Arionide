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

import org.azentreprise.arionide.events.IEventDispatcher;
import org.azentreprise.arionide.events.MainEventDispatcher;
import org.azentreprise.arionide.threading.EventDispatchingThread;
import org.azentreprise.arionide.threading.MiscProcessingThread;
import org.azentreprise.arionide.threading.UIDrawingThread;
import org.azentreprise.arionide.threading.UserHelpingThread;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.primitives.Resources;

public class ArionideImpl implements Arionide {
	
	private EventDispatchingThread eventThread;
	private MiscProcessingThread miscThread;
	private UIDrawingThread uiThread;
	private UserHelpingThread userThread;
	
	
	public void startThreads() {
		this.eventThread = new EventDispatchingThread();
		this.miscThread = new MiscProcessingThread();
		this.uiThread = new UIDrawingThread();
		this.userThread = new UserHelpingThread();
		
		this.eventThread.start();
	}

	public IEventDispatcher setupEventDispatcher() {
		return new MainEventDispatcher(this.eventThread);
	}
	
	public IWorkspace setupWorkspace(IEventDispatcher dispatcher) {
		File path = new File(System.getProperty("user.home") + File.separator + "Arionide Workspace");
		return new Workspace(path, dispatcher);
	}

	public AppDrawingContext setupAppDrawingContext(IEventDispatcher dispatcher) {
		return null;
	}

	public Resources loadResources(IWorkspace workspace, AppDrawingContext context) {
		return null;
	}

	public CoreRenderer loadCoreRenderer(AppDrawingContext context, IEventDispatcher dispatcher, Resources resources) {
		return null;
	}

	public LayoutManager setupLayoutManager(AppDrawingContext context, IEventDispatcher dispatcher) {
		return null;
	}

	public void loadUI(Arionide theInstance, IWorkspace workspace, AppDrawingContext context,
			IEventDispatcher dispatcher, Resources resources, CoreRenderer renderer, LayoutManager manager) {
		
	}

	public WatchdogState runWatchdog() {
		return WatchdogState.NO_PROBLEM;
	}

	public void shutdown() {
		
	}
}