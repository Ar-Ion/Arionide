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

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.jogamp.newt.event.KeyEvent;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.events.ClickEvent;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.EventHandler;
import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.events.PressureEvent;
import ch.innovazion.arionide.events.WheelEvent;
import ch.innovazion.arionide.lang.Language;
import ch.innovazion.arionide.lang.LanguageManager;
import ch.innovazion.arionide.lang.Program;
import ch.innovazion.arionide.lang.programs.ProgramIO;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.Storage;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.core.CoreController;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.overlay.Container;
import ch.innovazion.arionide.ui.overlay.View;
import ch.innovazion.arionide.ui.overlay.Views;
import ch.innovazion.arionide.ui.overlay.components.Button;
import ch.innovazion.arionide.ui.overlay.components.Label;
import ch.innovazion.arionide.ui.overlay.components.Tab;

public class RunView extends View implements EventHandler {
	
	private final ProgramIO programIO = new ProgramIO();
	
	private final Tab sourceSelector;
	private final Tab programSelector;
	private final Label[] console = new Label[15];
	private final Container debuggerContainer;
	private final List<Entry<String, Integer>> consoleData = new ArrayList<>();
	
	private Container debugger;
	
	private Structure source;
	private Program program;
	private List<Program> programs;
	private double wheelPosition;
	
	public RunView(AppManager appManager, LayoutManager layoutManager) {
		super(appManager, layoutManager);
		
		layoutManager.register(this, null, 0.0f, 0.0f, 1.0f, 1.0f);

		this.add(this.sourceSelector = new Tab(this, "<No source available>").setSignal("setSource"), 0.2f, 0.05f, 0.8f, 0.1f);
		this.add(this.programSelector = new Tab(this, "<No program available>").setSignal("setProgram"), 0.2f, 0.12f, 0.8f, 0.17f);
		
		this.add(new Button(this, "<").setSignal("back"), 0.05f, 0.05f, 0.15f, 0.1f);
		this.add(new Button(this, "Run").setSignal("run"), 0.85f, 0.05f, 0.95f, 0.1f);
		
		for(int i = 0; i < this.console.length; i++) {
			this.add(this.console[i] = new Button(this, new String()).setSignal("console", i).setBordered(false), 0.0f, 0.17f + i * 0.05f, 0.5f, 0.22f + i * 0.05f);
		}
		
		this.debuggerContainer = new Container(this, layoutManager) {
			public void drawSurface(AppDrawingContext context) {
				if(debugger != null) {
					debugger.drawSurface(context);
				}
			}
		};
		
		this.add(this.debuggerContainer, 0.5f, 0.15f, 1.0f, 0.95f);

		this.getAppManager().getEventDispatcher().registerHandler(this, 0.6f);
		
		programIO.registerConsole(this::info);
	}

	public void viewWillAppear() {		
		Storage storage = this.getAppManager().getWorkspace().getCurrentProject().getStorage();
		
		List<HierarchyElement> elements = storage.getHierarchy();
		Map<Integer, Structure> metaData = storage.getStructures();
		String[] buffer = new String[elements.size()];
				
		int i = 0;
		for(HierarchyElement element : elements) {
			buffer[i++] = metaData.get(element.getID()).getName();
		}
				
		this.sourceSelector.setComponents(buffer);
	}
	
	public <T extends Event> void handleEvent(T event) {
		if(!isVisible()) {
			return;
		}
		
		if(event instanceof ClickEvent) {
			ClickEvent click = (ClickEvent) event;
			
			if(click.isTargetting(this, "back")) {
				this.navigateTo(Views.code);
			} else if(click.isTargetting(this, "setSource")) {
				int sourceID = (int) click.getData()[0];

				this.source = getAppManager().getWorkspace().getCurrentProject().getStorage().getStructures().get(sourceID);
				
				Language lang = LanguageManager.get(source.getLanguage());
				
				this.debugger = lang.getEnvironment().create(getAppManager(), debuggerContainer.getBounds());
				this.programs = lang.getPrograms();
				
				programSelector.setComponents(programs.stream().map(Program::getName).toArray(String[]::new));
			} else if(click.isTargetting(this, "setProgram")) {
				this.program = programs.get((int) click.getData()[0]);
			} else if(click.isTargetting(this, "run")) {
				if(source != null && program != null) {
					program.run(source.getIdentifier(), programIO);
				} else {
					getAppManager().getEventDispatcher().fire(new MessageEvent("Please first select a source structure and a program to run", MessageType.ERROR));
				}
			} else if(click.isTargetting(this, "console")) {
				int row = (int) click.getData()[0];
				
				String data = this.console[row].toString();
				
				int start = data.indexOf('(');
				
				if(start > -1) {
					int end = data.indexOf(')', start);
					
					if(end > start) {
						String[] identifiers = data.substring(start + 1, end).split(":");
						
						if(identifiers.length == 2) {
							int structID = Integer.parseInt(identifiers[0]);
							int instructionID = Integer.parseInt(identifiers[1]);
							
							navigateTo(Views.code);
							
							CoreController controller = getAppManager().getCoreOrchestrator().getController();
							
							controller.requestTeleportation(structID);
							controller.requestFocus(instructionID);
						}
					}
				}
			}
		} else if(event instanceof WheelEvent) {
			WheelEvent wheel = (WheelEvent) event;
			
			this.wheelPosition += wheel.getDelta();
			
			if(this.wheelPosition < 0.0f) {
				this.wheelPosition = 0.0f;
			} else if(this.wheelPosition > this.consoleData.size() - 15) {
				this.wheelPosition = this.consoleData.size() - 15;
			}
			
			this.updateConsole();
		} else if(event instanceof PressureEvent) {
			PressureEvent pressure = ((PressureEvent) event);
		
			event.abortDispatching();

			if(pressure.isDown()) {
				switch(pressure.getKeycode())  {
				case KeyEvent.VK_ESCAPE:
					navigateTo(Views.code);
					break;
				}
			}
		}
	}
	
	private void info(String message, int color) {
		this.consoleData.add(new SimpleEntry<String, Integer>(message, color));
		this.updateConsole();
	}
	
	private void updateConsole() {
		List<Entry<String, Integer>> toRender;
		int realID = 0;
		
		if(this.consoleData.size() < 15) {
			toRender = this.consoleData;
		} else {
			int offset = (int) this.wheelPosition;
			toRender = this.consoleData.subList(this.consoleData.size() - offset - 15, this.consoleData.size() - offset);
			realID = this.consoleData.size() - offset - 15;
		}
		
		for(int i = 0; i < this.console.length; i++) {
			if(i < toRender.size()) {
				Entry<String, Integer> entry = toRender.get(i);
				
				if(i != 0) {
					this.console[i].setColor(entry.getValue()).setLabel(entry.getKey());
				} else {
					this.console[i].setColor(entry.getValue()).setLabel((realID + i + 1) + ": " + entry.getKey());
				}
			} else {
				this.console[i].setLabel(new String());
			}
		}
	}

	public Set<Class<? extends Event>> getHandleableEvents() {
		return Utils.asSet(ClickEvent.class, WheelEvent.class, PressureEvent.class);
	}
}