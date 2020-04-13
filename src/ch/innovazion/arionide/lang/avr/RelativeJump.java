package ch.innovazion.arionide.lang.avr;

import java.util.List;

import ch.innovazion.arionide.lang.Environment;
import ch.innovazion.arionide.lang.EvaluationException;
import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Skeleton;
import ch.innovazion.arionide.lang.symbols.Bit;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.lang.symbols.Numeric;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.Reference;
import ch.innovazion.arionide.lang.symbols.Specification;
import ch.innovazion.arionide.project.StructureModel;
import ch.innovazion.arionide.project.StructureModelFactory;

public class RelativeJump extends Instruction {
	
	public void validate(Specification spec, List<String> validationErrors) {
		
	}

	public void evaluate(Environment env, Specification spec) throws EvaluationException {
		Numeric offset = (Numeric) getConstant(spec, 0);
		short offsetValue = (short) Bit.toInteger(offset.cast(16).getRawStream());
		
		env.getProgramCounter().addAndGet(1 + offsetValue);
		env.getClock().incrementAndGet();
		env.getClock().incrementAndGet();
	}

	public Node assemble(Specification spec, Skeleton skeleton, List<String> assemblyErrors) {
		return new Numeric(0);
	}

	public StructureModel createStructureModel() {
		return StructureModelFactory
			.draft("rjmp")
			.withColor(0.2f)
			.withComment("Performs a relative jump (±2KB)")
			.beginSignature("Using offset")
			.withParameter(new Parameter("Offset").asConstant(new Numeric(0).cast(16)))
			.endSignature()
			.beginSignature("Using reference")
			.withParameter(new Parameter("Target").asConstant(new Reference()))
			.endSignature()
			.build();
	}

	public int getLength() {
		return 2;
	}
}
