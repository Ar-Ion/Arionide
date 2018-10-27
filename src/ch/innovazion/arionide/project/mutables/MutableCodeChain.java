package ch.innovazion.arionide.project.mutables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.innovazion.arionide.project.CodeChain;
import ch.innovazion.arionide.project.HierarchyElement;

public class MutableCodeChain implements CodeChain {
	private static final long serialVersionUID = -3058461639828251252L;

	private final List<MutableHierarchyElement> code = new ArrayList<>();
	
	public List<? extends HierarchyElement> getChain() {
		return Collections.unmodifiableList(this.code);
	}
	
	public List<MutableHierarchyElement> getMutableChain() {
		return this.code;
	}
}