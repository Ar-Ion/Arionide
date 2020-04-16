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
package ch.innovazion.arionide.lang;

import java.util.List;

import ch.innovazion.arionide.lang.symbols.Information;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.lang.symbols.Specification;
import ch.innovazion.arionide.lang.symbols.Variable;
import ch.innovazion.arionide.project.StructureModel;

public abstract class Instruction {
	public String toString() {
		return createStructureModel().getUniqueName();
	}
	
	public Node getConstant(Specification spec, int id) {
		return ((Information) spec.getParameters().get(id).getValue()).getRoot();
	}
	
	public Variable getVariable(Specification spec, int id) {
		return (Variable) spec.getParameters().get(id).getValue();
	}
	
	public abstract void validate(Specification spec, List<String> validationErrors);
	public abstract void evaluate(Environment env, Specification spec, ApplicationMemory programMemory) throws EvaluationException;
	public abstract Node assemble(Specification spec, Skeleton skeleton, List<String> assemblyErrors);
	
	public abstract StructureModel createStructureModel();
	public abstract int getLength(); // In bits
}
