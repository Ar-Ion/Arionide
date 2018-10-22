package org.azentreprise.arionide.ui.core;

@FunctionalInterface
public interface HostStructureChangeObserver {
	public void onHostStructureChanged(int newStruct);
}
