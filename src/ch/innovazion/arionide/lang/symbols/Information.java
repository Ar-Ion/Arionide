package ch.innovazion.arionide.lang.symbols;

import java.util.List;

public class Information implements ParameterValue {
	
	private static final long serialVersionUID = 6399415184230040034L;
	
	private Node root;
	
	public Information() {
		this.root = new Node();
	}
	
	public void resetRootNode(Node newRoot) {
		if(newRoot == null) {
			throw new IllegalArgumentException();
		}
		
		this.root = newRoot;
	}
	
	public Node getRoot() {
		return root;
	}

	public List<String> getDisplayValue() {
		return root.getDisplayValue();
	}

	public Information clone() {
		Information clone = new Information();
		
		clone.root = this.root.clone();
		
		return clone;
	}
}
