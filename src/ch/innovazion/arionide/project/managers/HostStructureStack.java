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
package ch.innovazion.arionide.project.managers;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class HostStructureStack {
	
	private final Set<HostStructureChangeObserver> observers = Collections.synchronizedSet(new HashSet<>());
	private final Deque<Integer> structIDs = new ArrayDeque<>();
	
	private int generation = 0;

	public synchronized void push(int structID) {
		push(structID, true);
	}
	
	public synchronized int pop() {
		return pop(true);
	}
	
	public synchronized void push(int structID, boolean notifyObservers) {
		structIDs.push(structID);
		generation++;
		
		notifyObservers();
	}
	
	public synchronized int pop(boolean notifyObservers) {
		generation--;
		int pop = structIDs.pop();
				
		notifyObservers();
		
		return pop;
	}
	
	public synchronized void reset() {
		structIDs.clear();
		generation = 0;
	}
	
	public synchronized boolean contains(int struct) {
		return structIDs.contains(struct);
	}
	
	public synchronized int getCurrent() {		
		if(this.isEmpty()) {
			return -1;
		} else {
			return structIDs.peek();
		}
	}
	
	public int getGeneration() {
		return generation;
	}
	
	public boolean isEmpty() {
		return generation == 0;
	}
	
	public Collection<Integer> getStack() {
		return Collections.unmodifiableCollection(structIDs);
	}
	
	public void registerObserver(HostStructureChangeObserver observer) {
		assert observer != null;
		
		synchronized(observers) {
			observers.add(observer);
		}
	}
	
	public void unregisterObserver(HostStructureChangeObserver observer) {
		assert observer != null;
		
		synchronized(observers) {
			observers.remove(observer);
		}
	}
	
	private void notifyObservers() {
		Set<HostStructureChangeObserver> copy;
		
		synchronized(observers) {
			copy = new HashSet<>(observers);
		}
		
		int current = getCurrent();

		for(HostStructureChangeObserver observer : copy) {
			observer.onHostStructureChanged(current);
		}
	}
	
	public String toString() {
		return structIDs.toString();
	}
}
