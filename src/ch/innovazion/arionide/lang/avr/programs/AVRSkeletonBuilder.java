package ch.innovazion.arionide.lang.avr.programs;

import java.util.Map;

import ch.innovazion.arionide.lang.Block;
import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Skeleton;
import ch.innovazion.arionide.lang.avr.special.Organize;
import ch.innovazion.arionide.lang.programs.ProgramIO;
import ch.innovazion.arionide.lang.programs.SkeletonBuilder;
import ch.innovazion.arionide.lang.symbols.Bit;
import ch.innovazion.arionide.lang.symbols.Callable;
import ch.innovazion.arionide.lang.symbols.Numeric;
import ch.innovazion.arionide.lang.symbols.Reference;
import ch.innovazion.arionide.lang.symbols.ShadowCallable;
import ch.innovazion.arionide.project.Storage;

public class AVRSkeletonBuilder extends SkeletonBuilder {
	
	public AVRSkeletonBuilder(Storage storage) {
		super(storage);
	}

	protected void processSpecialInstruction(Callable callable, Skeleton skeleton, Map<String, Instruction> instructionSet, ProgramIO io) {
		Instruction instr = instructionSet.get(callable.getName());
		
		
		if(instr instanceof Organize) {
			Numeric org = (Numeric) instr.getConstant(callable.getSpecification(), 0);
			int address = (int) Bit.toInteger(org.getRawStream());
			
			if(skeleton.setTextCursor(address)) {
				io.log("Current structure manually inserted at address " + address);
			} else {
				io.error("Failed to insert current structure at address " + address);
			}
		} else if(instr instanceof Block) {
			Reference ref = (Reference) instr.getConstant(callable.getSpecification(), 0);
			Callable target = ref.getTarget();
			
			if(target != null) {
				build(new ShadowCallable(target), skeleton, instructionSet, io);
			}
		}
	}
	
	public String getName() {
		return "AVR skeleton builder";
	}
}
