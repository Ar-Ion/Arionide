package ch.innovazion.arionide.project.managers;

import ch.innovazion.arionide.coders.Coder;
import ch.innovazion.arionide.project.Project;

public class ResourceAllocator {
	
	private final Project project;
	
	protected ResourceAllocator(Project project) {
		this.project = project;
	}
	
	public int allocSpecification() {
		return this.alloc("specificationGen");
	}
	
	public int allocStructure() {
		return this.alloc("structureGen");
	}
	
	private int alloc(String object) {
		int id = this.project.getProperty(object, Coder.integerDecoder).intValue();
		this.project.setProperty(object, (long) id + 1, Coder.integerEncoder); // Increment generator
		this.project.save();
		
		return id;
	}
	
	public int nextSpecification() {
		return this.next("specificationGen");
	}
	
	public int nextStructure() {
		return this.next("structureGen");
	}
	
	private int next(String object) {
		return this.project.getProperty(object, Coder.integerDecoder).intValue();
	}
}
