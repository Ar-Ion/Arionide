package org.azentreprise.arionide.lang;

import org.azentreprise.arionide.project.Project;

public abstract class Compiler {
	
	private final Project project;

	public Compiler(Project project) {
		this.project = project;
	}
	
	protected Project getProject() {
		return this.project;
	}
	
	public abstract void load();
	public abstract InstructionSet getInstructionSet();
}
