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

import ch.innovazion.arionide.debugging.Debug;
import ch.innovazion.arionide.debugging.IAm;
import ch.innovazion.arionide.debugging.WatchdogState;
import ch.innovazion.arionide.events.dispatching.IEventDispatcher;
import ch.innovazion.arionide.resources.Resources;
import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.core.CoreOrchestrator;
import ch.innovazion.arionide.ui.layout.LayoutManager;

public class Main {
	
	private static final Class<? extends Arionide> implementation = ch.innovazion.arionide.ArionideImpl.class;
	
	@IAm("initializing Arionide")
	public static void main(String args[]) {
		
		Arionide theInstance = null;
		
		try {
			theInstance = Main.implementation.newInstance();
		} catch (InstantiationException | IllegalAccessException exception) {
			System.err.println("FATAL: This implementation of Arionide is invalid");
			throw new RuntimeException(exception);
		}
		
		try {
			theInstance.startThreads();
			
			IEventDispatcher dispatcher = theInstance.setupEventDispatcher();
	
			Workspace workspace = theInstance.setupWorkspace(dispatcher);
	
			Resources resources = theInstance.loadResources(workspace);
			AppDrawingContext context = theInstance.setupAppDrawingContext(dispatcher);
			CoreOrchestrator orchestrator = theInstance.loadCoreOrchestrator(dispatcher, resources);
			LayoutManager manager = theInstance.setupLayoutManager(context, dispatcher);
			
			theInstance.loadUI(context, workspace, resources, orchestrator, manager);
			
			WatchdogState state = null;
			
			while(theInstance.hashCode() != dispatcher.hashCode()) {
				
				state = theInstance.runWatchdog();
				
				if(state != WatchdogState.NO_PROBLEM) {
					break;
				}
				
				try {
					Thread.sleep(Arionide.WATCHDOG_TIMER);
				} catch (InterruptedException e) {
					break;
				}
			}
			
			if(state == WatchdogState.CRASH) {
				System.err.println("Arionide crashed");
			}
			
			theInstance.shutdown();
		} catch(Exception exception) {
			Debug.exception(exception);
		}
	}
}
