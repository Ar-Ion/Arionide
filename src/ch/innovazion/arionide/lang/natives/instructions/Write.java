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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import ch.innovazion.arionide.lang.Data;
import ch.innovazion.arionide.lang.natives.NativeDataCommunicator;
import ch.innovazion.arionide.lang.symbols.Bit;
import ch.innovazion.arionide.lang.symbols.Information;
import ch.innovazion.arionide.lang.symbols.Parameter;

public class Write implements NativeInstruction {

	private final Data object;
	private final Data path;
	
	public Write(Data object, Data path) {
		this.object = object;
		this.path = path;
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		if(this.object.getDisplayValue().startsWith(Parameter.VAR)) {
			Parameter element = communicator.getVariable(this.object.getDisplayValue().substring(4));
			
			if(element != null) {
				String file = this.path.getDisplayValue();
				
				if(file.startsWith(Parameter.VAR)) {
					file = communicator.getVariable(file.substring(4)).getDisplayValue();
				}
				
				if(file != null) {
					Information object = communicator.getObject(element.getDisplayValue());
					
					if(object != null) {
						Bit[] bits = object.getData();
						byte[] data = new byte[bits.length / 8 + 1];
												
						for(int i = 0; i < data.length; i++) {
							byte b = 0x0;
							
							for(int j = 0; j < 8; j++) {
								int bitID = 8 * i + j;
								
								if(bitID < bits.length) {
									b |= bits[bitID].getBit() << (7 - j);
								}
							}
							
							data[i] = b;
						}
						
						try {
							Files.write(new File(file).toPath(), data);
							return true;
						} catch (IOException e) {
							e.printStackTrace();
							communicator.exception("Failed to write data to: " + file);
						}
					} else {
						communicator.exception("Invalid object instance: " + element.getDisplayValue());
					}
				} else {
					communicator.exception("Dead variable: " + file);
				}
			} else {
				communicator.exception("Dead variable: " + this.object.getDisplayValue());
			}
		} else {
			communicator.exception("You can't use a direct value for an object instance");
		}
		
		return false;
	}
}