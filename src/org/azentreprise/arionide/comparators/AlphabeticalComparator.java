package org.azentreprise.arionide.comparators;

import java.util.Comparator;
import java.util.Map;

import org.azentreprise.arionide.project.Storage;
import org.azentreprise.arionide.project.StructureMeta;

public class AlphabeticalComparator implements Comparator<Integer> {
	
	private final Map<Integer, StructureMeta> structure;
	
	public AlphabeticalComparator(Storage storage) {
		this.structure = storage.getStructureMeta();
	}
	
	public int compare(Integer o1, Integer o2) {
		return this.structure.get(o1).getName().compareTo(this.structure.get(o2).getName());
	}
}