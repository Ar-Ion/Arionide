/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
 *
 * Arionide is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Arionide is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Arionide.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package ch.innovazion.arionide.lang.avr.branch;

import java.util.List;

import ch.innovazion.arionide.lang.ApplicationMemory;
import ch.innovazion.arionide.lang.Environment;
import ch.innovazion.arionide.lang.EvaluationException;
import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Skeleton;
import ch.innovazion.arionide.lang.avr.AVREnums;
import ch.innovazion.arionide.lang.avr.device.AVRSRAM;
import ch.innovazion.arionide.lang.symbols.Bit;
import ch.innovazion.arionide.lang.symbols.Callable;
import ch.innovazion.arionide.lang.symbols.Enumeration;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.lang.symbols.Numeric;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.Specification;
import ch.innovazion.arionide.project.StructureModel;
import ch.innovazion.arionide.project.StructureModelFactory;

public class CompareSkipIfEqual extends Instruction {
	
	public void validate(Specification spec, List<String> validationErrors) {
		;
	}

	public void evaluate(Environment env, Specification spec, ApplicationMemory programMemory) throws EvaluationException {		
		Numeric d = (Numeric) ((Enumeration) getConstant(spec, 0)).getValue();
		Numeric r = (Numeric) ((Enumeration) getConstant(spec, 0)).getValue();

		AVRSRAM sram = env.getPeripheral("sram");
		
		int dPtr = (int) Bit.toInteger(d.getRawStream());
		int rPtr = (int) Bit.toInteger(r.getRawStream());

		int dValue = sram.getRegister(dPtr);
		int rValue = sram.getRegister(rPtr);
		
		if(dValue == rValue) {
			// Skip
			Callable next = programMemory.textAt(2 * (env.getProgramCounter().get() + 1));
			Instruction instr = env.getLanguage().getInstructionSet().get(next.getName());
			
			if(instr.getLength() == 2) {
				env.getProgramCounter().incrementAndGet();
				env.getClock().incrementAndGet();
			} else if(instr.getLength() == 4) {
				env.getProgramCounter().incrementAndGet();
				env.getProgramCounter().incrementAndGet();
				env.getClock().incrementAndGet();
				env.getClock().incrementAndGet();
			} else {
				throw new EvaluationException("Skip-instructions do not specify a behaviour for instructions different than one or two words long");
			}
		}

		env.getProgramCounter().incrementAndGet();
		env.getClock().incrementAndGet();
	}

	public Node assemble(Specification spec, Skeleton skeleton, List<String> assemblyErrors) {
		return new Numeric(0).cast(16);
	}

	public StructureModel createStructureModel() {
		return StructureModelFactory
			.draft("cpse")
			.withColor(0.97f)
			.withComment("Skips the next instruction if the two registers have equal content")
			.beginSignature("default")
			.withParameter(new Parameter("Register 1").asConstant(AVREnums.REGISTER))
			.withParameter(new Parameter("Register 2").asConstant(AVREnums.REGISTER))
			.endSignature()
			.build();
	}

	public int getLength() {
		return 2;
	}
}
