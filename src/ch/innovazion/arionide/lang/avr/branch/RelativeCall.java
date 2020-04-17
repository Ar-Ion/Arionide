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
import ch.innovazion.arionide.lang.Skeleton;
import ch.innovazion.arionide.lang.avr.device.AVRSRAM;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.lang.symbols.Numeric;
import ch.innovazion.arionide.lang.symbols.Specification;
import ch.innovazion.arionide.project.StructureModelFactory;
import ch.innovazion.arionide.project.StructureModelFactory.IncompleteModel;

public class RelativeCall extends AbstractBranch {
	
	public Node assemble(Specification spec, Skeleton skeleton, List<String> assemblyErrors) {
		return new Numeric(0);
	}
	
	public void evaluate(Environment env, Specification spec, ApplicationMemory programMemory) throws EvaluationException {		
		AVRSRAM sram = env.getPeripheral("sram");
		sram.pushWord((int) env.getProgramCounter().get() + 1);
		super.evaluate(env, spec, programMemory);
	}

	protected boolean isOffsetInBounds(long offset) {
		return -2048 <= offset && offset < 2048;
	}

	protected boolean isConditionMet(Environment env) {
		return true;
	}

	protected IncompleteModel getModelDraft() {
		return StructureModelFactory.draft("rcall").withColor(0.95f).withComment("Performs a relative call (±2KB)");
	}
}