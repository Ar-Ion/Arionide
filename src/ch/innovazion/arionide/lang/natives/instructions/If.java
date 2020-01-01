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
package ch.innovazion.arionide.lang.natives.instructions;

import java.util.List;

import ch.innovazion.arionide.lang.Data;
import ch.innovazion.arionide.lang.Reference;
import ch.innovazion.arionide.lang.natives.NativeDataCommunicator;
import ch.innovazion.arionide.lang.natives.NativeTypes;

public class If implements NativeInstruction {

	private final Reference trueCaseRef;
	private final Reference falseCaseRef;
	
	private final Call predicate;
	private final Call trueCase;
	private final Call falseCase;
	
	public If(Reference predicate, Reference trueCase, Reference falseCase) {
		this.trueCaseRef = trueCase;
		this.falseCaseRef = falseCase;
		
		this.predicate = new Call(predicate);
		this.trueCase = new Call(trueCase);
		this.falseCase = new Call(falseCase);
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		communicator.setVariable("condition", true, new Data("condition", "b0", NativeTypes.INTEGER));
		this.predicate.execute(communicator, references);
		boolean action = communicator.getVariable("condition").getValue().substring(1).equals("1");
		
		if(action) {
			if(this.trueCaseRef.getValue() != null) {
				this.trueCase.execute(communicator, references);
			}
		} else {
			if(this.falseCaseRef.getValue() != null) {
				this.falseCase.execute(communicator, references);
			}
		}
		
		return true;
	}
}