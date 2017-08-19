package org.azentreprise.arionide.project;

import java.io.Serializable;
import java.util.List;

public class StructureElement implements Serializable {
	private static final long serialVersionUID = -5901117492235888923L;

	private final String name;
	private final List<StructureElement> children;
	
	public StructureElement(String name, List<StructureElement> children) {
		this.name = name;
		this.children = children;
	}
	
	public List<StructureElement> getChildren() {
		return this.children;
	}
	
	public String getName() {
		return this.name;
	}
}
