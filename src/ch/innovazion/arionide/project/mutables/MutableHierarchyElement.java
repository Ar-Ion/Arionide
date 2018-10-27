package ch.innovazion.arionide.project.mutables;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.innovazion.arionide.project.HierarchyElement;

public class MutableHierarchyElement implements HierarchyElement {
	private static final long serialVersionUID = -5901117492235888923L;

	private final int id;
	protected final List<MutableHierarchyElement> children;

	public MutableHierarchyElement(int id, List<MutableHierarchyElement> children) {
		this.id = id;
		this.children = children;
	}
	
	public List<HierarchyElement> getChildren() {
		return Collections.unmodifiableList(this.children);
	}
	
	public List<MutableHierarchyElement> getMutableChildren() {
		return this.children;
	}

	public int getID() {
		return this.id;
	}
	
	public boolean equals(Object other) {
		if(other instanceof MutableHierarchyElement) {
			return this.id == ((MutableHierarchyElement) other).id;
		}
		
		return false;
	}
	
	public int hashCode() {
		return this.id;
	}
	
	public String toString() {
		if(this.children != null) {
			return this.id + "{" + String.join(",", this.children.stream().map(HierarchyElement::toString).collect(Collectors.toList())) + "}";
		} else {
			return "null";
		}
	}
}
