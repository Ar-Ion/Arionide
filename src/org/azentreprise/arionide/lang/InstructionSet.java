package org.azentreprise.arionide.lang;

import java.util.List;

import org.azentreprise.arionide.project.Project;

public abstract class InstructionSet {
	
	private final Project project;
	
	public InstructionSet(Project project) {
		this.project = project;
	}
	
	protected int installInstruction(String name, int color, List<Integer> parents) {
		return this.project.getDataManager().installInstruction(name, color, parents);
	}
	
	protected int retrieveInstruction(String name) {
		return this.project.getDataManager().retrieveInstruction(name);
	}
	
	public abstract int getStructureEntry();
	public abstract int getInstructionID(String name);
	public abstract List<String> getInstructions();
}
