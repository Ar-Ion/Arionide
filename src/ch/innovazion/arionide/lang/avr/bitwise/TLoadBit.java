package ch.innovazion.arionide.lang.avr.bitwise;

import java.util.List;

import ch.innovazion.arionide.lang.ApplicationMemory;
import ch.innovazion.arionide.lang.Environment;
import ch.innovazion.arionide.lang.EvaluationException;
import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Skeleton;
import ch.innovazion.arionide.lang.avr.AVREnums;
import ch.innovazion.arionide.lang.avr.device.AVRSRAM;
import ch.innovazion.arionide.lang.symbols.Bit;
import ch.innovazion.arionide.lang.symbols.Enumeration;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.lang.symbols.Numeric;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.Specification;
import ch.innovazion.arionide.project.StructureModel;
import ch.innovazion.arionide.project.StructureModelFactory;

public class TLoadBit extends Instruction {
	
	public void validate(Specification spec, List<String> validationErrors) {
		;
	}

	public void evaluate(Environment env, Specification spec, ApplicationMemory programMemory) throws EvaluationException {		
		AVRSRAM sram = env.getPeripheral("sram");
		
		Numeric d = (Numeric) ((Enumeration) getConstant(spec, 0)).getValue();
		Numeric m = (Numeric) ((Enumeration) getConstant(spec, 1)).getValue();

		int sreg = sram.get(AVRSRAM.SREG);
		
		int dPtr = (int) Bit.toInteger(d.getRawStream());
		int dValue = sram.getRegister(dPtr);
		int mValue = (int) Bit.toInteger(m.getRawStream());
		int tFlag = (sreg >> 6) != 0 ? 1 : 0;
				
		sram.set(dPtr, (dValue & mValue) | tFlag);
		
		env.getProgramCounter().incrementAndGet();
		env.getClock().incrementAndGet();
	}

	public Node assemble(Specification spec, Skeleton skeleton, List<String> assemblyErrors) {
		return new Numeric(0).cast(16);
	}

	public StructureModel createStructureModel() {
		return StructureModelFactory
			.draft("bld")
			.withColor(0.67f)
			.withComment("Loads the T flag of SREG into the specified bit of a register")
			.beginSignature("default")
			.withParameter(new Parameter("Destination", AVREnums.REGISTER))
			.withParameter(new Parameter("Bit number", AVREnums.OR_BIT_MASK))
			.endSignature()
			.build();
	}

	public int getLength() {
		return 2;
	}
}