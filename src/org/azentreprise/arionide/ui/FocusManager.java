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
package org.azentreprise.arionide.ui;

import java.util.ArrayList;
import java.util.List;

import org.azentreprise.arionide.events.FocusGainedEvent;
import org.azentreprise.arionide.events.FocusLostEvent;
import org.azentreprise.arionide.events.dispatching.IEventDispatcher;
import org.azentreprise.arionide.ui.overlay.Component;

public class FocusManager {
	
	private static final int NOT_INITIALIZED = 0xC0FFEE;
	
	private final List<Component> components = new ArrayList<>();
	private final IEventDispatcher dispatcher;
	
	private List<Integer> cycle = null; // This array represents the values of a bijective function in a modular N+/N+ euclidian space.
	
	private int focus = FocusManager.NOT_INITIALIZED;
	
	public FocusManager(IEventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
	
	public void registerComponent(Component component) {
		this.components.add(component);
	}
	
	public int requestViewUID() {
		return this.components.size();
	}
	
	public void setupCycle(List<Integer> elements) {
		assert elements.size() > 0 && elements.size() <= this.components.size();
		
		this.cycle = elements;
		
		this.request(0);
	}
	
	public void request(Component component) {
		int index = this.cycle.indexOf(this.components.indexOf(component));
		System.out.println(index);
		if(index > -1) {
			this.request(index);
		} // Else ignore because the component is hidden
	}
	
	public void request(int id) {
		if(this.focus == id) {
			return;
		}
		
		this.loseFocus();

		this.focus = id;
		
		Component current = this.accessModular();
		
		if(current.isFocusable()) {
			this.dispatcher.fire(new FocusGainedEvent(current));
		} else {
			this.focus++;
			this.tryIncrementalFocus(1);
		}
	}
	
	public void next() {
		this.loseFocus();
		
		this.focus++;
				
		this.tryIncrementalFocus(1);
	}
	
	public void prev() {
		this.loseFocus();
		
		this.focus--;
		
		this.tryIncrementalFocus(-1);
	}
	
	private void loseFocus() {
		if(this.focus != FocusManager.NOT_INITIALIZED) {
			this.dispatcher.fire(new FocusLostEvent(this.accessModular()));
		}
	}
	
	private void tryIncrementalFocus(int lambda) {
		
		int index = this.focus;
		
		while(!this.accessModular().isFocusable()) {
			this.focus += lambda;
			
			if(Math.abs(this.focus - index) >= this.cycle.size()) {
				return; // failed (no component is focusable)
			}
		}
		
		this.dispatcher.fire(new FocusGainedEvent(this.accessModular()));
	}

	private Component accessModular() {
		return this.components.get(this.cycle.get(Math.floorMod(this.focus, this.cycle.size())));
	}
}