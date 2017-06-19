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
package org.azentreprise.arionide.ui.overlay.views;

import java.util.Arrays;
import java.util.List;

import org.azentreprise.arionide.IProject;
import org.azentreprise.arionide.events.ClickEvent;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.core.RenderingScene;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.arionide.ui.overlay.Views;
import org.azentreprise.arionide.ui.overlay.components.Button;
import org.azentreprise.arionide.ui.overlay.components.Scroll;
import org.azentreprise.arionide.ui.overlay.components.Tab;

public class CodeView extends View implements EventHandler {
	
	private IProject currentProject;
	
	public CodeView(AppManager appManager, LayoutManager layoutManager) {
		super(appManager, layoutManager);
		
		layoutManager.register(this, null, 0.0f, 0.0f, 1.0f, 1.0f);
		
		this.add(new Button(this, "<").setSignal("back").setYCorrection(4), 0.05f, 0.05f, 0.15f, 0.1f);
		this.add(new Tab(this, "Inheritance", "Hierarchy", "Call graph").setSignal("sceneChanged"), 0.3f, 0.05f, 0.7f, 0.1f);
		this.add(new Button(this, "Run").setSignal("run"), 0.85f, 0.05f, 0.95f, 0.1f);
		
		this.add(new Button(this, "+").setSignal("add"), 0.05f, 0.86f, 0.15f, 0.94f);
		this.add(new Scroll(this, "Instr0", "Instr1", "Instr2", "Instr3", "Instr4", "Instr5", "Instr6", "Instr7", "Instr8", "Instr9"), 0.2f, 0.85f, 0.8f, 0.95f);
		this.add(new Button(this, "...").setSignal("more"), 0.85f, 0.86f, 0.95f, 0.94f);

		this.getAppManager().getEventDispatcher().registerHandler(this);
	}
	
	public void show() {
		super.show();
		this.setupFocusCycle(2, 3, 5, 0);
		this.currentProject = this.getAppManager().getWorkspace().getCurrentProject();
	}

	public <T extends Event> void handleEvent(T event) {
		if(event instanceof ClickEvent) {
			ClickEvent click = (ClickEvent) event;
			
			if(click.isTargetting(this, "back")) {
				this.openView(Views.main);
			} else if(click.isTargetting(this, "sceneChanged")) {
				int tabID = (int) click.getData()[0];
				
				switch(tabID) {
					case 0:
						this.getAppManager().getCoreRenderer().setScene(RenderingScene.INHERITANCE);
						break;
					case 1:
						this.getAppManager().getCoreRenderer().setScene(RenderingScene.HIERARCHY);
						break;
					case 2:
						this.getAppManager().getCoreRenderer().setScene(RenderingScene.CALLGRAPH);
						break;
					default:
						throw new RuntimeException("Invalid scene id");
				}
			} else if(click.isTargetting(this, "run")) {
				
			}
		}
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(ClickEvent.class);
	}
}