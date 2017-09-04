package org.azentreprise.arionide.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InheritanceElement implements Serializable {
	private static final long serialVersionUID = -5015584300296770902L;

	protected final List<Integer> parents = new ArrayList<>();
	protected final List<Integer> children = new ArrayList<>();
	
	public List<Integer> getParents() {
		return Collections.unmodifiableList(this.parents);
	}
	
	public List<Integer> getChildren() {
		return Collections.unmodifiableList(this.children);
	}
}