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
package ch.innovazion.arionide.project;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

// Pointer to the identifier of the structure the user is in.
public class HostStructureStack {
	
	private final Set<HostStructureChangeObserver> observers = Collections.synchronizedSet(new HashSet<>());
	private final Deque<Integer> structIDs = new ConcurrentLinkedDeque<Integer>();
	
	private int generation = 0;

	public synchronized void push(int structID) {
		this.structIDs.push(structID);
		this.generation++;
		
		this.notifyObservers();
	}
	
	public synchronized void pop() {
		this.structIDs.pop();
		this.generation--;
				
		this.notifyObservers();
	}
	
	public int getCurrent() {		
		if(this.isEmpty()) {
			return -1;
		} else {
			return this.structIDs.peek();
		}
	}
	
	public int getGeneration() {
		return this.generation;
	}
	
	public boolean isEmpty() {
		return this.generation == 0;
	}
	
	public Collection<Integer> getStack() {
		return Collections.unmodifiableCollection(structIDs);
	}
	
	public void registerObserver(HostStructureChangeObserver observer) {
		assert observer != null;
		
		synchronized(this.observers) {
			this.observers.add(observer);
		}
	}
	
	public void unregisterObserver(HostStructureChangeObserver observer) {
		assert observer != null;
		
		synchronized(this.observers) {
			this.observers.remove(observer);
		}
	}
	
	private void notifyObservers() {
		Set<HostStructureChangeObserver> copy;
		
		synchronized(this.observers) {
			copy = new HashSet<>(this.observers);
		}

		for(HostStructureChangeObserver observer : copy) {
			observer.onHostStructureChanged(this.getCurrent());
		}
	}
}
