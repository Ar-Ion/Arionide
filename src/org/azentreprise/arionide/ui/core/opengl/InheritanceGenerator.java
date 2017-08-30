package org.azentreprise.arionide.ui.core.opengl;

import java.util.List;
import java.util.function.Consumer;

import org.azentreprise.arionide.project.StructureElement;

public class InheritanceGenerator {
	
	private final List<StructureElement> elements;
	private final Consumer<Float> progress;
	private final Consumer<List<StructureElement>> completion;
	
	protected InheritanceGenerator(List<StructureElement> elements, Consumer<Float> progress, Consumer<List<StructureElement>> completion) {
		this.elements = elements;
		this.progress = progress;
		this.completion = completion;
	}
	
	protected void process() {
		
	}
}