/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
package org.azentreprise.arionide.lang.natives.instructions;

import java.util.List;
import java.util.Stack;

import org.azentreprise.arionide.lang.natives.NativeDataCommunicator;

public class Call implements NativeInstruction {
	
	private final int reference;
	
	public Call(int reference) {
		this.reference = reference;
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		if(references.contains(this.reference)) {
			Stack<Integer> theStack = communicator.getStack();
			
			if(theStack.size() > 100) {
				communicator.exception("Stack overflow");
				return false;
			}
			
			theStack.push(this.reference);
			communicator.exec(references.indexOf(this.reference));
			theStack.pop();
			
			return true;
		} else {
			communicator.exception("Dead reference error");
			return false;
		}
	}
}