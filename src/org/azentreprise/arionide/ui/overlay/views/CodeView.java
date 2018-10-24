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
package org.azentreprise.arionide.ui.overlay.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import org.azentreprise.arionide.events.ClickEvent;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.events.MenuEvent;
import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.events.ScrollEvent;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.project.Storage;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.animations.Animation;
import org.azentreprise.arionide.ui.animations.FieldModifierAnimation;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.core.RenderingScene;
import org.azentreprise.arionide.ui.core.TeleportInfo;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.menu.Menu;
import org.azentreprise.arionide.ui.overlay.Component;
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.arionide.ui.overlay.Views;
import org.azentreprise.arionide.ui.overlay.components.Button;
import org.azentreprise.arionide.ui.overlay.components.Label;
import org.azentreprise.arionide.ui.overlay.components.Scroll;
import org.azentreprise.arionide.ui.overlay.components.Tab;

public class CodeView extends View implements EventHandler {

	private final Animation currentMessageAlphaAnimation;
	
	private final Scroll menu = new Scroll(this, "<No menu loaded>");
	
	private final Label currentInfo = new Label(this, "");
	private final Label currentMessage = new Label(this, "");
	
	private Project currentProject;
	private Menu currentMenu;
	
	public CodeView(AppManager appManager, LayoutManager layoutManager) {
		super(appManager, layoutManager);
		
		this.currentMessageAlphaAnimation = new FieldModifierAnimation(appManager, "alpha", Label.class, this.currentMessage);

		layoutManager.register(this, null, 0.0f, 0.0f, 1.0f, 1.0f);
		
		this.add(new Tab(this, "Inheritance", "Hierarchy", "Call graph").setSignal("sceneChanged").setActiveComponent(1), 0.3f, 0.05f, 0.7f, 0.1f);
	
		this.add(new Button(this, "<").setSignal("back"), 0.05f, 0.05f, 0.15f, 0.1f);
		this.add(new Button(this, "Run").setSignal("run"), 0.85f, 0.05f, 0.95f, 0.1f);
		
		this.add(new Button(this, "+").setSignal("add"), 0.05f, 0.86f, 0.15f, 0.94f);
		this.add(this.menu, 0.2f, 0.85f, 0.8f, 0.95f);

		this.add(new Button(this, "...").setSignal("more"), 0.85f, 0.86f, 0.95f, 0.94f);

		this.add(this.currentMessage, 0.2f, 0.1f, 0.8f, 0.2f);
		this.add(this.currentInfo, 0.0f, 0.75f, 1.0f, 0.85f);
		
		this.getAppManager().getEventDispatcher().registerHandler(this);
	}
	
	public void show() {
		super.show();
		this.setupFocusCycle(2, 3, 5, 0);
		this.currentProject = this.getAppManager().getWorkspace().getCurrentProject();
		this.getAppManager().getEventDispatcher().fire(new MessageEvent("'" + this.currentProject.getName() + "' has been successfully loaded", MessageType.SUCCESS));
		this.getAppManager().getCoreRenderer().loadProject(this.currentProject);
	}
	
	public void drawSurface(AppDrawingContext context) {
		super.drawSurface(context);
	}

	public <T extends Event> void handleEvent(T event) {
		if(event instanceof ClickEvent) {
			ClickEvent click = (ClickEvent) event;
			AppManager manager = this.getAppManager();
			CoreRenderer renderer = manager.getCoreRenderer();
			
			if(click.isTargetting(this, "back")) {
				manager.getWorkspace().closeProject(this.currentProject);
				manager.getCoreRenderer().loadProject(null);
				this.openView(Views.main);
			} else if(click.isTargetting(this, "sceneChanged")) {
				int tabID = (int) click.getData()[0];
				
				switch(tabID) {
					case 0:
						renderer.setScene(RenderingScene.INHERITANCE);
						break;
					case 1:
						renderer.setScene(RenderingScene.HIERARCHY);
						break;
					case 2:
						renderer.setScene(RenderingScene.CALLGRAPH);
						break;
					default:
						throw new RuntimeException("Invalid scene id");
				}
			} else if(click.isTargetting(this, "run")) {
				this.openView(Views.run);
			} else if(click.isTargetting(this, "add")) {
				new Thread(() -> {
					String name = JOptionPane.showInputDialog(null, "Please enter the name of the new structure", "New structure", JOptionPane.PLAIN_MESSAGE);
					
					if(name != null) {
						MessageEvent message = this.currentProject.getDataManager().newStructure(name);
						manager.getEventDispatcher().fire(message);
						
						if(message.getMessageType() != MessageType.ERROR) {
							Storage storage = this.currentProject.getStorage();
							
							int structID = storage.getCallGraph().get(storage.getCallGraph().size() - 1).getID();
							int instructionID = storage.getCode().get(structID).get(0).getID();
							
							renderer.getStructuresGeometry().requestReconstruction();
							renderer.teleport(new TeleportInfo(structID, instructionID));
						}
					}
				}).start();
			} else if(click.isTargetting((Component) null, "menuScroll")) {
				if(!this.isHidden()) {
					assert this.currentMenu != null;
					this.currentMenu.click();
				}
			}
		} else if(event instanceof MessageEvent) {
			MessageEvent message = (MessageEvent) event;
			
			if(message.getMessageType() != MessageType.DEBUG) {
				Label ref = message.getMessageType().equals(MessageType.INFO) ? this.currentInfo : this.currentMessage;
							
				ref.setLabel(message.getMessage());
				ref.setColor(message.getMessageType().getColor());
				
				if(ref == this.currentMessage) {
					this.currentMessageAlphaAnimation.startAnimation(1000, (animation) -> animation.startAnimation(5000, 1), 0xFF);
				}
			}
		} else if(event instanceof MenuEvent) {
			if(this.currentMenu != null) {
				this.currentMenu.setActive(false);
			}
			
			this.currentMenu = ((MenuEvent) event).getMenu();
			this.menu.setActiveComponent(this.currentMenu.getMenuCursor());
			
			List<String> elements = new ArrayList<>(this.currentMenu.getMenuElements());
			
			synchronized(elements) {
				this.menu.setComponents(elements.stream().map(this.menu.getGenerator()).collect(Collectors.toList()));
			}
			
			this.currentInfo.setLabel(this.currentMenu.getDescription());
			this.currentInfo.setColor(MessageType.INFO.getColor());

			this.currentMenu.setActive(true);
		} else if(event instanceof ScrollEvent) {
			ScrollEvent scroll = (ScrollEvent) event;
			
			if(scroll.isTargetting(this)) {
				assert this.currentMenu != null;
				this.currentMenu.select(scroll.getSubComponentID());
			}
		}
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(ClickEvent.class, MessageEvent.class, MenuEvent.class, ScrollEvent.class);
	}
}