/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
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
import ch.innovazion.arionide.lang.SpecificationElement;
import ch.innovazion.arionide.lang.natives.NativeDataCommunicator;

public class Print implements NativeInstruction {
	
	private final String message;
	
	public Print(Data data) {
		this.message = data.getValue();
	}

	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		String realMessage = this.message;
		
		if(realMessage.startsWith(SpecificationElement.VAR)) {
			realMessage = communicator.getVariable(realMessage.substring(4)).getValue();
		}
		
		communicator.info(realMessage, 0xFFFFFF);
		
		return true;
	}
}