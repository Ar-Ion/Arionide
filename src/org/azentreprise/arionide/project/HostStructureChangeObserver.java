package org.azentreprise.arionide.project;

@FunctionalInterface
public interface HostStructureChangeObserver {
	public void onHostStructureChanged(int newStruct);
}
