package ch.innovazion.arionide.lang.avr;

import java.util.List;

import ch.innovazion.arionide.lang.Environment;
import ch.innovazion.arionide.lang.EvaluationException;
import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Skeleton;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.lang.symbols.Numeric;
import ch.innovazion.arionide.lang.symbols.Specification;
import ch.innovazion.arionide.project.StructureModel;
import ch.innovazion.arionide.project.StructureModelFactory;
import ch.innovazion.arionide.ui.ApplicationTints;

public class NoOperation extends Instruction {

	private final StructureModel model = StructureModelFactory
			.draft("nop")
			.withColor(ApplicationTints.getColorIDByName("Red"))
			.withComment("Wait one cycle")
			.beginSignature("default")
			.endSignature()
			.build();
	
	public void validate(Specification spec, List<String> validationErrors) {
		;
	}

	public void evaluate(Environment env, Specification spec) throws EvaluationException {
		env.getProgramCounter().incrementAndGet();
		env.getClock().incrementAndGet();
	}

	public Node assemble(Specification spec, Skeleton skeleton, List<String> assemblyErrors) {
		return new Numeric(0).cast(16);
	}

	public StructureModel getStructureModel() {
		return model;
	}

	public int getLength() {
		return 16;
	}
}
