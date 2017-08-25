package org.azentreprise.arionide.ui.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Menu {
	
	private final List<String> elements = new ArrayList<>();
	
	protected Menu(String... elements) {
		this.elements.addAll(Arrays.asList(elements));
	}
	
	public List<String> getElements() {
		return this.elements;
	}
}
