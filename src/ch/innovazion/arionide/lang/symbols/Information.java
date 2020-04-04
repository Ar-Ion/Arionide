package ch.innovazion.arionide.lang.symbols;

import java.util.List;

public class Information implements ParameterValue {
	
	private static final long serialVersionUID = 6399415184230040034L;
	
	private Node root;
	
	public Information(String rootName) {
		this.root = new Node(rootName);
	}
	
	public Node resetRootNode() {
		Node newRoot = new Node(root.getLabel());
		
		return this.root = newRoot;
	}
	
	public Node getRoot() {
		return root;
	}

	public List<String> getDisplayValue() {
		return root.getDisplayValue();
	}

	public Information clone() {
		Information clone = new Information(root.getLabel());
		
		clone.root = this.root.clone();
		
		return clone;
	}
}
