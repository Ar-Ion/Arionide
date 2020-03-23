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
package ch.innovazion.automaton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class StateManager {
	
	private final List<Consumer<String>> notifiables = Collections.synchronizedList(new ArrayList<>());
	private final StateHierarchy hierarchy;
	private final StateSyncer syncer;
	private final boolean debug;
	
	public StateManager(StateHierarchy hierarchy) {
		this(hierarchy, false);
	}
	
	public StateManager(StateHierarchy hierarchy, boolean debug) {
		this.hierarchy = hierarchy;
		this.syncer = new StateSyncer();
		this.debug = debug;
		
		hierarchy.reset();
		hierarchy.registerStates(this);
	}
	
	
	public void go(String identifier) {
		performStateTransition(null, identifier);
	}
	
	protected void performStateTransition(State current, String identifier) {
		debug("Performing state transition from state '" + current + "' to identifier '" + identifier + "'");

		Stack<String> path = hierarchy.resolvePath(identifier);
		hierarchy.enterState(path);
		debug("State hierarchy updated with path '" + path + "'");
		
		State next = hierarchy.resolveCurrentState();
		debug("Identifier '" + identifier + "' resolved as state '" + next + "'");
		
		if(current != null) {
			current.onExit();
			debug("State '" + current + "' exited");
			
			syncer.synchronizeStates(current, next, debugableSynchonizationCallback(next::onRefresh));
			debug("States synchronized");
		}
		

		next.onEnter();
		debug("State '" + next + "' entered");
		
		
		List<Consumer<String>> ghost = new ArrayList<>();
		
		synchronized(notifiables) {
			ghost.addAll(notifiables);
		}
		
		for(Consumer<String> notifier : notifiables) {
			notifier.accept(hierarchy.composePath(path));
		}
	}
	
	private BiConsumer<String, Object> debugableSynchonizationCallback(BiConsumer<String, Object> consumer) {
		return consumer.andThen(this::debugSynchronization);
	}
	
	private void debugSynchronization(String field, Object prevValue) {
		debug("Field '" + field + "' synchronized. Previous value: " + prevValue);
	}
	
	private void debug(String msg) {
		if(debug) {
			System.out.println("[StateManager@" + hashCode() + "] " + msg);
		}
	}
	
	
	public void registerNotifiable(Consumer<String> notifiable) {
		synchronized(notifiables) {
			notifiables.add(notifiable);
		}

		debug("Registered notifiable '" + notifiable + "'");
	}
	
	public void unregisterNotifiable(Consumer<String> notifiable) {
		synchronized(notifiables) {
			notifiables.remove(notifiable);
		}
		
		debug("Unregistered notifiable '" + notifiable + "'");
	}
	
	public List<String> getAvailableActions() {
		return hierarchy.resolveCurrentState().getActions();
	}
	
	public void triggerAction(String action) {
		State state = hierarchy.resolveCurrentState();
		
		state.onAction(action);
		debug("Action '" + action + "' triggered on state '"  + state + "'");
	}
}
