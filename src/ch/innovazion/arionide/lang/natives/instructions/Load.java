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
import ch.innovazion.arionide.lang.natives.NativeTypes;

public class Load implements NativeInstruction {

	private final Data source;
	private final Data target;
	
	public Load(Data source, Data target) {
		this.source = source;
		this.target = target;
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		if(this.target.getValue().startsWith(SpecificationElement.VAR)) {
			String refID = this.source.getValue();
			String target = this.target.getValue().substring(4);
			
			if(refID.startsWith(SpecificationElement.VAR)) {
				refID = communicator.getVariable(refID.substring(4)).getValue();
			}
			
			String identifier = communicator.load(Integer.parseInt(refID.substring(1), refID.charAt(0) != 'd' ? refID.charAt(0) != 'b' ? 16 : 2 : 10));
			
			communicator.setVariable(target, !communicator.isDefined(target) || communicator.isLocal(target), new Data(target, identifier, NativeTypes.TEXT));
		
			return true;
		} else {
			communicator.exception("You can't use direct values for an object");
			return false;
		}
	}

}
