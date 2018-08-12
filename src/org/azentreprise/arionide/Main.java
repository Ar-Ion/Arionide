/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
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

import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.debugging.IAm;
import org.azentreprise.arionide.debugging.WatchdogState;
import org.azentreprise.arionide.events.dispatching.IEventDispatcher;
import org.azentreprise.arionide.resources.Resources;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.layout.LayoutManager;

public class Main {
	
	private static final Class<? extends Arionide> implementation = org.azentreprise.arionide.ArionideImpl.class;
	
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
			CoreRenderer renderer = theInstance.loadCoreRenderer(context, dispatcher, resources);
			LayoutManager manager = theInstance.setupLayoutManager(context, dispatcher);
			
			theInstance.loadUI(context, workspace, resources, renderer, manager);
			
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
