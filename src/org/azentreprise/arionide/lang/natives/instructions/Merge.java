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
package org.azentreprise.arionide.lang.natives.instructions;

import java.util.List;

import org.azentreprise.arionide.lang.Data;
import org.azentreprise.arionide.lang.natives.NativeDataCommunicator;
import org.azentreprise.arionide.lang.natives.NativeTypes;

public class Merge implements NativeInstruction {
	
	private final Data source1;
	private final Data source2;
	private final Data destination;
	
	public Merge(Data source1, Data source2, Data destination) {
		this.source1 = source1;
		this.source2 = source2;
		this.destination = destination;
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		if(this.destination.getValue().startsWith("var@")) {
			String destVar = this.destination.getValue().substring(4);
			
			String value1 = this.source1.getValue();
			String value2 = this.source2.getValue();

			if(value1.startsWith("var@")) {
				value1 = communicator.getVariable(value1).getValue();
			}
			
			if(value2.startsWith("var@")) {
				value2 = communicator.getVariable(value2).getValue();
			}
			
			communicator.setVariable(destVar, communicator.isDefined(destVar) && communicator.isLocal(destVar), new Data(destVar, value1 + value2, NativeTypes.TEXT));
		} else {
			communicator.exception("You can't use a direct for a destination variable");
		}
		
		return false;
	}
}