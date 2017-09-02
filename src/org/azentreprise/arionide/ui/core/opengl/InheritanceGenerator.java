package org.azentreprise.arionide.ui.core.opengl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.azentreprise.arionide.project.StructureElement;

public class InheritanceGenerator {
	
	private final List<StructureElement> elements;
	private final Consumer<List<StructureElement>> completion;
	
	protected InheritanceGenerator(List<StructureElement> elements, Consumer<List<StructureElement>> completion) {
		this.elements = elements;
		this.completion = completion;
	}
	
	protected void generate(int id) {
		List<StructureElement> output = new ArrayList<StructureElement>();
		this.process(this.elements.stream().filter((element) -> element.getID() == id).findAny().orElse(this.elements.get(0)), output);
		this.completion.accept(Arrays.asList(new StructureElement(id, output)));
	}
	
	private void process(StructureElement src, List<StructureElement> dest) {		
		for(StructureElement child : src.getChildren()) {
			List<StructureElement> next = new ArrayList<>();
			dest.add(new StructureElement(child.getID(), next));
			this.process(child, next);
		}
		
		for(StructureElement parent : src.getParents()) {
			List<StructureElement> next = new ArrayList<>();
			dest.add(new StructureElement(parent.getID(), next));
			this.process(parent, next);
		}
	}
}