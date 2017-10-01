package org.azentreprise.arionide.lang;

import org.azentreprise.arionide.project.Project;

public class NativeCompiler extends Compiler {
	
	private NativeInstructionSet instructionSet;
	
	public NativeCompiler(Project project) {
		super(project);
	}

	public void load() {
		this.instructionSet = new NativeInstructionSet(this.getProject());
	}
	
	public InstructionSet getInstructionSet() {
		return this.instructionSet;
	}
}
