package org.azentreprise.arionide.project;

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
