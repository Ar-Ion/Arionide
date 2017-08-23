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
package org.azentreprise.arionide.ui.overlay.views;

import java.util.Arrays;
import java.util.List;

import org.azentreprise.arionide.events.ClickEvent;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.animations.Animation;
import org.azentreprise.arionide.ui.animations.FieldModifierAnimation;
import org.azentreprise.arionide.ui.core.RenderingScene;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.arionide.ui.overlay.Views;
import org.azentreprise.arionide.ui.overlay.components.Button;
import org.azentreprise.arionide.ui.overlay.components.Label;
import org.azentreprise.arionide.ui.overlay.components.Scroll;
import org.azentreprise.arionide.ui.overlay.components.Tab;

public class CodeView extends View implements EventHandler {

	private final Animation currentMessageAlphaAnimation;
	private final Label currentMessage = new Label(this, "");
	
	private Project currentProject;
	
	public CodeView(AppManager appManager, LayoutManager layoutManager) {
		super(appManager, layoutManager);
		
		this.currentMessageAlphaAnimation = new FieldModifierAnimation(appManager, "alpha", Label.class, this.currentMessage);

		layoutManager.register(this, null, 0.0f, 0.0f, 1.0f, 1.0f);
		
		this.add(new Button(this, "<").setSignal("back").setYCorrection(4), 0.05f, 0.05f, 0.15f, 0.1f);
		this.add(new Tab(this, "Inheritance", "Hierarchy", "Call graph").setSignal("sceneChanged"), 0.3f, 0.05f, 0.7f, 0.1f);
		this.add(new Button(this, "Run").setSignal("run"), 0.85f, 0.05f, 0.95f, 0.1f);
		
		this.add(new Button(this, "+").setSignal("add"), 0.05f, 0.86f, 0.15f, 0.94f);
				
		this.add(new Scroll(this, "Instr0", "Instr1", "Instr2", "Instr3", "Instr4", "Instr5", "Instr6", "Instr7", "Instr8", "Instr9"), 0.2f, 0.85f, 0.8f, 0.95f);
		this.add(new Button(this, "...").setSignal("more"), 0.85f, 0.86f, 0.95f, 0.94f);

		this.add(this.currentMessage, 0.2f, 0.75f, 0.8f, 0.85f);
		
		this.getAppManager().getEventDispatcher().registerHandler(this);
	}
	
	public void show() {
		super.show();
		this.setupFocusCycle(2, 3, 5, 0);
		this.currentProject = this.getAppManager().getWorkspace().getCurrentProject();
		this.getAppManager().getEventDispatcher().fire(new MessageEvent("Project loaded: " + this.currentProject.getName(), MessageType.INFO));
	}
	
	public void drawSurface(AppDrawingContext context) {
		super.drawSurface(context);
	}

	public <T extends Event> void handleEvent(T event) {
		if(event instanceof ClickEvent) {
			ClickEvent click = (ClickEvent) event;
			
			if(click.isTargetting(this, "back")) {
				this.getAppManager().getWorkspace().closeProject(this.currentProject);
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
				this.currentProject.save();
			}
		} else if(event instanceof MessageEvent) {
			MessageEvent message = (MessageEvent) event;
			
			this.currentMessage.setLabel(message.getMessage());
			this.currentMessage.setColor(message.getMessageType().getColor());
			
			this.currentMessageAlphaAnimation.startAnimation(1000, (animation) -> animation.startAnimation(5000, 1), 0xFF);
		}
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(ClickEvent.class, MessageEvent.class);
	}
}