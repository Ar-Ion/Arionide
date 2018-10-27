package ch.innovazion.arionide.project.mutables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.innovazion.arionide.project.InheritanceElement;

public class MutableInheritanceElement implements InheritanceElement {
	private static final long serialVersionUID = 3470515787748482035L;
	
	private final List<Integer> parents = new ArrayList<>();
	private final List<Integer> children = new ArrayList<>();
	
	public List<Integer> getParents() {
		return Collections.unmodifiableList(this.parents);
	}
	
	public List<Integer> getChildren() {
		return Collections.unmodifiableList(this.children);
	}
	
	public List<Integer> getMutableParents() {
		return this.parents;
	}
	
	public List<Integer> getMutableChildren() {
		return this.children;
	}
}
