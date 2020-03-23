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
package ch.innovazion.arionide.ui.overlay.views;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.jogamp.newt.event.KeyEvent;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.events.ClickEvent;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.EventHandler;
import ch.innovazion.arionide.events.GeometryInvalidateEvent;
import ch.innovazion.arionide.events.MenuEvent;
import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.events.PressureEvent;
import ch.innovazion.arionide.events.ScrollEvent;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.menu.MenuDescription.DescriptionLine;
import ch.innovazion.arionide.project.CodeChain;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.project.Storage;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.animations.Animation;
import ch.innovazion.arionide.ui.animations.FieldModifierAnimation;
import ch.innovazion.arionide.ui.core.CoreController;
import ch.innovazion.arionide.ui.core.RenderingScene;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.overlay.Component;
import ch.innovazion.arionide.ui.overlay.View;
import ch.innovazion.arionide.ui.overlay.Views;
import ch.innovazion.arionide.ui.overlay.components.Button;
import ch.innovazion.arionide.ui.overlay.components.Label;
import ch.innovazion.arionide.ui.overlay.components.Scroll;
import ch.innovazion.arionide.ui.overlay.components.Tab;

public class CodeView extends View implements EventHandler {

	private final Animation currentMessageAlphaAnimation;
		
	private final Scroll menu = new Scroll(this, "<No menu loaded>");
	
	private final Label[] menuDescription = new Label[MenuDescription.MAX_LINES];
	private final Label currentMessage = new Label(this, new String());
	
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

		this.add(new Button(this, " ... ").setSignal("more"), 0.85f, 0.86f, 0.95f, 0.94f);

		this.add(this.currentMessage, 0.2f, 0.1f, 0.8f, 0.2f);
				
		for(int i = 0; i < menuDescription.length; i++) {
			menuDescription[i] = new Label(this, new String());
			
			float height = 0.35f / menuDescription.length;
			
			this.add(menuDescription[i], 0.0f, 0.85f - height * (i + 1), 1.0f, 0.85f - height * i);
		}
		
		this.getAppManager().getEventDispatcher().registerHandler(this);
	}
	
	public void viewWillAppear() {
		setupFocusCycle(2, 3, 5, 0);
		currentProject = getAppManager().getWorkspace().getCurrentProject();
	}
	
	protected int getMaskAlpha(int viewAlpha) {
		return 0;
	}

	public <T extends Event> void handleEvent(T event) {
		if(!isVisible()) {
			return;
		}
		
		AppManager manager = this.getAppManager();
		CoreController controller = manager.getCoreOrchestrator().getController();
		
		if(event instanceof ClickEvent) {
			ClickEvent click = (ClickEvent) event;
			
			if(click.isTargetting(this, "back")) {
				manager.getWorkspace().closeProject(this.currentProject);
				this.navigateTo(Views.main);
			} else if(click.isTargetting(this, "sceneChanged")) {
				int tabID = (int) click.getData()[0];
				
				switch(tabID) {
					case 0:
						controller.setScene(RenderingScene.INHERITANCE);
						break;
					case 1:
						controller.setScene(RenderingScene.HIERARCHY);
						break;
					case 2:
						controller.setScene(RenderingScene.CALLGRAPH);
						break;
					default:
						throw new RuntimeException("Invalid scene id");
				}
			} else if(click.isTargetting(this, "run")) {
				this.navigateTo(Views.run);
			} else if(click.isTargetting(this, "add")) {
				
				Views.input.setText("Please enter the name of the new structure")
						   .setPlaceholder("Structure name")
						   .setResponder(this::createStructure)
						   .stackOnto(this);
				
			}
		} else if(event instanceof MessageEvent) {
			MessageEvent message = (MessageEvent) event;
						
			if(message.getMessageType() != MessageType.DEBUG) {		
				currentMessage.setLabel(message.getMessage());
				currentMessage.setColor(message.getMessageType().getColor());
				
				currentMessageAlphaAnimation.startAnimation(500, (animation) -> animation.startAnimation(3000, 1), 0xFF);
			}
		} else if(event instanceof MenuEvent) {
			this.currentMenu = ((MenuEvent) event).getMenu();

			List<String> elements = controller.getMenuManager().getAvailableActions();
			
			synchronized(elements) {
				List<Component> components = elements.stream().map(menu.getGenerator()).collect(Collectors.toList());
				if(currentMenu.isCyclic() && elements.size() > 1) {
					menu.setCyclicComponents(elements.toArray(new String[0]));		
				} else {
					menu.setCyclicComponents((String[]) null);
					menu.setComponents(components);
				}
			}

			menu.setActiveComponent(currentMenu.getCursor());
			menu.updateAll();
			
			syncMenuDescription();
		} else if(event instanceof ScrollEvent) {
			ScrollEvent scroll = (ScrollEvent) event;
			
			if(scroll.isTargetting(this)) {
				controller.getMenuManager().select(scroll.getSubComponentID());
				syncMenuDescription();
			}
		} else if(event instanceof PressureEvent) {
			PressureEvent pressure = ((PressureEvent) event);
			
			if(currentMenu != null && pressure.isDown()) {
				switch(pressure.getKeycode())  {
				case KeyEvent.VK_ESCAPE:
					controller.getMenuManager().back();
					event.abortDispatching();
					break;
				case KeyEvent.VK_UP:
					currentMenu.up();
					break;
				case KeyEvent.VK_DOWN:
					currentMenu.down();
				}
			}
		}
	}
	
	private void createStructure(String name) {
		AppManager manager = getAppManager();
		CoreController controller = manager.getCoreOrchestrator().getController();
		
		MessageEvent message = this.currentProject.getStructureManager().newStructure(name);
		manager.getEventDispatcher().fire(message);
		
		if(message.getMessageType() != MessageType.ERROR) {
			Storage storage = this.currentProject.getStorage();
			
			int structID = storage.getCallGraph().get(storage.getCallGraph().size() - 1).getID();
										
			CodeChain code = storage.getCode().get(structID);
			
			manager.getEventDispatcher().fire(new GeometryInvalidateEvent(2));
			controller.requestTeleportation(structID);
			
			if(!code.isAbstract()) {
				int instructionID = code.list().get(0).getID();
				controller.requestFocus(instructionID);
			}
		}
	}
	
	private void syncMenuDescription() {		
		DescriptionLine[] lines = currentMenu.getDescription().getDisplay();
		
		for(int i = 0; i < lines.length; i++) {
			Label label = menuDescription[i];
			DescriptionLine line = lines[i];
			
			if(line != null) {
				label.setLabel(line.getText());
				label.setColor(line.getColor());
			} else {
				label.setLabel(new String());
			}
		}
	}

	public Set<Class<? extends Event>> getHandleableEvents() {
		return Utils.asSet(ClickEvent.class, MessageEvent.class, MenuEvent.class, ScrollEvent.class, PressureEvent.class);
	}
}