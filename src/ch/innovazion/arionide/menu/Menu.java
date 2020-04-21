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
package ch.innovazion.arionide.menu;

import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.MenuEvent;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.automaton.Export;
import ch.innovazion.automaton.Inherit;
import ch.innovazion.automaton.State;

public abstract class Menu extends State {

	private final MenuManager manager;
	
	private final String[] staticElements;
	private TreeMap<String, Integer> sortedDynamicElements;
	
	private String[] sortedElements;
	private String[] naturalElements;
	
	private boolean searching;
	

	protected int cursor;
	protected int id;
	protected String selection;
	
	
	@Export
	@Inherit
	protected Project project;

	@Export
	@Inherit
	protected MenuDescription description = new MenuDescription();
	
		
	protected Menu(MenuManager manager, String... elements) {
		super(manager);
		
		this.manager = manager;
		
		this.staticElements = elements;
		this.naturalElements = elements;
		this.sortedElements = elements;
		
		ensureNonEmpty();
		updateCursor(0);
	}
	
	private void ensureNonEmpty() {
		if(naturalElements.length == 0) {
			naturalElements = new String[] { "<Empty>" };
		}
		
		if(sortedElements.length == 0) {
			sortedElements = new String[] { "<Empty>" };
		}
	}
	
	protected List<String> getActions() {
		if(searching) {
			return Arrays.asList(sortedElements);
		} else {
			return Arrays.asList(naturalElements);
		}
	}
	
	protected void setDynamicElements(String... dynamicElements) {
		this.sortedDynamicElements = new TreeMap<>((a, b) -> a.toString().toLowerCase().compareTo(b.toString().toLowerCase()));
		
		for(int i = 0; i < dynamicElements.length; i++) {
			if(dynamicElements[i] != null) {
				sortedDynamicElements.putIfAbsent(dynamicElements[i], i);
			}
		}
		
		this.naturalElements = Utils.combine(String.class, staticElements, dynamicElements);
		this.sortedElements = Utils.combine(String.class, staticElements, sortedDynamicElements.keySet().toArray(new String[0]));
		
		ensureNonEmpty();
		
		manager.refresh(this);
	}
	
	protected void onEnter() {
		manager.refresh(this);
	}
	
	protected void setSearching(boolean searching) {
		if(sortedDynamicElements != null) {
			this.searching = searching;
	
			if(id >= staticElements.length) {
				if(searching) {
					updateCursor(getSortedID(naturalElements[id]));
				} else {
					updateCursor(id);
				}
			}
			
			dispatch(new MenuEvent(this));
		}
	}
	
	protected void updateCursor(String like) {
		if(searching) {
			updateCursor(getSortedID(like));
		}
	}
	
	protected void updateCursor(int cursor) {
		this.cursor = cursor;

		if(searching) {
			computeID(sortedElements); // Computes the element index with respect to the sorted array
			id = getNaturalID(sortedElements[id]); // And remaps it to the natural array
		} else {
			computeID(naturalElements);
		}
			
		this.selection = naturalElements[id];
	}
	
	private void computeID(String[] elements) {
		if(isCyclic()) {
			id = cursor % elements.length;
		} else if(cursor < elements.length && cursor >= 0) {
			id = cursor;
		} else {
			id = 0;
		}
	}
	
	private int getSortedID(String element) {
		SortedMap<String, Integer> head = sortedDynamicElements.headMap(element);
		
		if(!head.isEmpty()) {
			return staticElements.length + head.size();
		} else {
			return staticElements.length;
		}
	}
	
	private int getNaturalID(String element) {
		return staticElements.length + sortedDynamicElements.floorEntry(element).getValue();
	}
	
	protected void dispatch(Event event) {
		manager.dispatch(event);
	}
	
	public void up() {
		
	}
	
	public void down() {
		
	}
	
	public int getCursor() {
		return cursor;
	}
	
	public MenuDescription getDescription() {
		return description;
	}
	
	public boolean isCyclic() {
		return false;
	}
}