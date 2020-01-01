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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.jogamp.newt.event.KeyEvent;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.events.ClickEvent;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.EventHandler;
import ch.innovazion.arionide.events.PressureEvent;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.ApplicationTints;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.overlay.Component;
import ch.innovazion.arionide.ui.overlay.View;
import ch.innovazion.arionide.ui.overlay.components.Button;

public abstract class PromptView extends OverlayView implements EventHandler {
	
	private final List<Component> buttons = new ArrayList<>();
	private final Map<String, Consumer<View>> actions = new HashMap<>();
	
	public PromptView(AppManager appManager, LayoutManager layoutManager, int numButtons) {
		super(appManager, layoutManager);
		
		layoutManager.register(this, null, 0.3f, 0.35f, 0.7f, 0.65f);
		
		setBorderColor(ApplicationTints.MAIN_COLOR);
		
		float horizontalMargin = 0.05f;
		
		for(int i = 0; i < numButtons; i++) {
			float width = (1.0f - horizontalMargin) / numButtons;
			this.add(new Button(this, "<Undefined>").enclose(buttons), horizontalMargin + width*i, 0.75f, width*(i + 1), 0.9f);
		}
		
		appManager.getEventDispatcher().registerHandler(this);
	}
	
	public void viewWillAppear() {				
		setupFocusCycle();
		getAppManager().getFocusManager().request(1);
	}
	
	public PromptView setButtons(String... labels) {
		if(labels.length >= buttons.size()) {
			for(int i = 0; i < buttons.size(); i++) {
				((Button) buttons.get(i)).setSignal("prompt", labels[i]).setLabel(labels[i]);
			}
		} else {
			System.err.println("Failed to set labels for prompt view.");
		}
		
		return this;
	}
	
	public PromptView react(String signal, Consumer<View> action) {
		actions.put(signal, action);
		return this;
	}

	public <T extends Event> void handleEvent(T event) {
		if(event instanceof ClickEvent) {
			
			ClickEvent click = (ClickEvent) event;
			
			if(click.isTargetting(this, "prompt")) {
				String actionIdentifier = (String) click.getData()[0];
				actions.getOrDefault(actionIdentifier, (nil) -> {}).accept(this);
			}
		} else if(event instanceof PressureEvent) {
			
			PressureEvent pressure = (PressureEvent) event;
			
			if(pressure.isDown() && pressure.getKeycode() == KeyEvent.VK_ESCAPE) {
				discard();
			}
		}
	}

	public Set<Class<? extends Event>> getHandleableEvents() {
		return Utils.combine(super.getHandleableEvents(), ClickEvent.class, PressureEvent.class);
	}
}
