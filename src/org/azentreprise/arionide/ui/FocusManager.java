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
import org.azentreprise.arionide.events.IEventDispatcher;
import org.azentreprise.arionide.ui.overlay.Component;

public class FocusManager {
	
	public static final int[] NATURAL_CYCLE = new int[0];
	
	private final List<Component> components = new ArrayList<>();
	private final IEventDispatcher dispatcher;
	
	private int[] cycle = null; // this array represents the values of a bijective function in a modular N+/N+ euclidian space.
	private int focus = 0;
	
	public FocusManager(IEventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
	
	public void registerComponent(Component component) {
		this.components.add(component);
	}
	
	public void reset() {
		this.components.clear();
	}
	
	public void setupCycle(int... elements) {
		if(elements != FocusManager.NATURAL_CYCLE) {
			assert elements.length == this.components.size();
			this.cycle = elements;
		} else {
			this.cycle = new int[this.components.size()];
			
			int fillingIndex = 0;
			
			while(fillingIndex < this.cycle.length) {
				this.cycle[fillingIndex++] = fillingIndex;
			}
		}
		
		this.request(0);
	}
	
	public void request(Component component) {
		this.request(this.components.indexOf(component));
	}
	
	public void request(int id) {
		if(this.focus == id) {
			return;
		}
		
		this.dispatcher.fire(new FocusLostEvent(this.accessModular()));
		
		Component current = this.accessModular();
		
		this.focus = id;
		
		if(current.isFocusable()) {
			this.dispatcher.fire(new FocusGainedEvent(this.accessModular()));
		} else {
			this.next();
		}
	}
	
	public void next() {
		this.focus++;
		this.tryIncrementalFocus(1);
	}
	
	public void prev() {
		this.focus--;
		this.tryIncrementalFocus(-1);
	}
	
	private void tryIncrementalFocus(int lambda) {
		
		int index = this.focus;
		
		while(!this.accessModular().isFocusable()) {
			this.focus += lambda;
			
			if(Math.abs(this.focus - index) >= this.cycle.length) {
				return; // failed (no component is focusable)
			}
		}
	}

	private Component accessModular() {
		return this.components.get(this.cycle[this.focus % this.cycle.length]);
	}
}