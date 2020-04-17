package ch.innovazion.arionide.lang.avr.branch;

import java.util.List;

import ch.innovazion.arionide.lang.ApplicationMemory;
import ch.innovazion.arionide.lang.Environment;
import ch.innovazion.arionide.lang.EvaluationException;
import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Skeleton;
import ch.innovazion.arionide.lang.avr.device.AVRSRAM;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.lang.symbols.Numeric;
import ch.innovazion.arionide.lang.symbols.Specification;
import ch.innovazion.arionide.project.StructureModel;
import ch.innovazion.arionide.project.StructureModelFactory;

public class ReturnFromInterrupt extends Instruction {

	public void validate(Specification spec, List<String> validationErrors) {
		
	}

	public void evaluate(Environment env, Specification spec, ApplicationMemory programMemory) throws EvaluationException {
		AVRSRAM sram = env.getPeripheral("sram");
		env.getProgramCounter().set(sram.popWord());
	}

	public Node assemble(Specification spec, Skeleton skeleton, List<String> assemblyErrors) {
		return new Numeric(0).cast(16);
	}

	public StructureModel createStructureModel() {
		return StructureModelFactory.draft("ret")
			.withColor(0.96f)
			.withComment("Returns from a subroutine")
			.beginSignature("default")
			.endSignature()
			.build();
	}

	public int getLength() {
		return 2;
	}

}
