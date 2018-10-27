package ch.innovazion.arionide.project;

import java.io.Serializable;
import java.util.List;

public interface CodeChain extends Serializable {
	public List<? extends HierarchyElement> getChain();
}