package ch.innovazion.arionide.lang.notek;

import java.util.List;

import ch.innovazion.arionide.lang.ApplicationMemory;
import ch.innovazion.arionide.lang.Environment;
import ch.innovazion.arionide.lang.EvaluationException;
import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Skeleton;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.lang.symbols.Specification;

public abstract class NotekInstruction extends Instruction {

	public void validate(Specification spec, List<String> validationErrors) {
		
	}

	public void evaluate(Environment env, Specification spec, ApplicationMemory programMemory)
			throws EvaluationException {
		throw new EvaluationException("Notek instructions are not meant to be evaluated");
	}

	public Node assemble(Specification spec, Skeleton skeleton, List<String> assemblyErrors) {
		return null;
	}

	public int getLength() {
		return 0;
	}
}