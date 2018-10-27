package ch.innovazion.arionide.ui.core.geom;

import java.util.Collections;
import java.util.List;

import ch.innovazion.arionide.project.HierarchyElement;

public class SymbolicHierarchyElement implements HierarchyElement {
	private static final long serialVersionUID = -6458098431772597703L;
	
	private final int id;
	private final List<HierarchyElement> children;
	
	public SymbolicHierarchyElement(int id, List<HierarchyElement> children) {
		this.id = id;
		this.children = Collections.unmodifiableList(children);
	}
	
	public List<HierarchyElement> getChildren() {
		return this.children;
	}
	
	public int getID() {
		return this.id;
	}
}
