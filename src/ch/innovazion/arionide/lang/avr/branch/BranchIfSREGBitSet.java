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
import ch.innovazion.arionide.lang.symbols.Reference;
import ch.innovazion.arionide.lang.symbols.Specification;
import ch.innovazion.arionide.project.StructureModel;
import ch.innovazion.arionide.project.StructureModelFactory;

public class BranchIfSREGBitSet extends Instruction {
	public void validate(Specification spec, List<String> validationErrors) {
		
	}

	public void evaluate(Environment env, Specification spec, ApplicationMemory programMemory) throws EvaluationException {		
		Numeric m = (Numeric) ((Enumeration) getConstant(spec, 0)).getValue();
		long offsetValue = 0;
		
		if(spec.getParameters().size() <= 2) {
			Node param = getConstant(spec, 1);

			if(param instanceof Numeric) {
				Numeric offset = (Numeric) param;
				offsetValue = Bit.toInteger(offset.cast(16).getRawStream());
			} else if(param instanceof Reference) {
				Reference ref = (Reference) param;
				Callable target = ref.getTarget();
				
				if(target != null) {
					Long address = programMemory.getSkeleton().getTextAddress(target);
					
					if(address != null) {
						offsetValue = address / 2 - env.getProgramCounter().get() - 1;
					} else {
						throw new EvaluationException("Reference address could not be retrieved");
					}				
				} else {
					throw new EvaluationException("Target is undefined");
				}
			}
		} else {
			Long virtual = programMemory.getSkeleton().getDataAddress(getVariable(spec, 1));
			String addressMask = ((Enumeration) getConstant(spec, 2)).getKey();

			if(virtual != null) {
				if(addressMask.equalsIgnoreCase("low")) {
					virtual &= 0xFF;
				} else if(addressMask.equalsIgnoreCase("high")) {
					virtual >>>= 8;
					virtual &= 0xFF;
				}
				
				offsetValue = virtual;
			} else {
				throw new EvaluationException("Unable to find data variable");
			}
		}
		
		AVRSRAM sram = env.getPeripheral("sram");
		
		int sregPtr = AVRSRAM.SREG;
		int mValue = (int) Bit.toInteger(m.getRawStream());
				
		
		if(-64 <= offsetValue && offsetValue < 64) {
			if((sram.get(sregPtr) | mValue) != 0) {
				env.getProgramCounter().addAndGet(1 + offsetValue);
				env.getClock().incrementAndGet();
				env.getClock().incrementAndGet();
			} else {
				env.getProgramCounter().incrementAndGet();
				env.getClock().incrementAndGet();
			}
		} else {
			throw new EvaluationException("Relative address is out of bounds");
		}
	}

	public Node assemble(Specification spec, Skeleton skeleton, List<String> assemblyErrors) {
		return new Numeric(0).cast(16);
	}

	public StructureModel createStructureModel() {
		return StructureModelFactory
			.draft("brbs")
			.withColor(0.74f)
			.withComment("Branches if the specified is one in SREG")
			.beginSignature("Using offset")
				.withParameter(new Parameter("Bit number").asConstant(AVREnums.AND_BIT_MASK))
				.withParameter(new Parameter("Offset").asConstant(new Numeric(0)))
			.endSignature()
			.beginSignature("Using variable offset")
				.withParameter(new Parameter("Bit number").asConstant(AVREnums.AND_BIT_MASK))
				.withParameter(new Parameter("Offset").asVariable(new Numeric(0)))
				.withParameter(new Parameter("Address mask").asConstant(AVREnums.ADDRESS_MASK))
			.endSignature()
			.beginSignature("Using reference")
				.withParameter(new Parameter("Bit number").asConstant(AVREnums.AND_BIT_MASK))
				.withParameter(new Parameter("Target").asConstant(new Reference()))
			.endSignature()
			.build();
	}

	public int getLength() {
		return 2;
	}
}