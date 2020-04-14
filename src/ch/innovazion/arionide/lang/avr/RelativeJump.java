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
