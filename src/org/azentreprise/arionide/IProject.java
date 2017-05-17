package org.azentreprise.arionide;

public interface IProject extends Resource, IMappedStructure, ISaveable {
	public void checkVersionCompatibility();
}
