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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.azentreprise.arionide.lang.Bit;
import org.azentreprise.arionide.lang.Data;
import org.azentreprise.arionide.lang.Object;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.lang.natives.NativeDataCommunicator;

public class Write implements NativeInstruction {

	private final Data object;
	private final Data path;
	
	public Write(Data object, Data path) {
		this.object = object;
		this.path = path;
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		if(this.object.getValue().startsWith("var@")) {
			SpecificationElement element = communicator.getVariable(this.object.getValue().substring(4));
			
			if(element != null) {
				String file = this.path.getValue();
				
				if(file.startsWith("var@")) {
					file = communicator.getVariable(file.substring(4)).getValue();
				}
				
				if(file != null) {
					Object object = communicator.getObject(element.getValue());
					
					if(object != null) {
						Bit[] bits = object.getData();
						byte[] data = new byte[bits.length / 8 + 1];
						
						System.out.println(Arrays.toString(bits));
						
						for(int i = 0; i < data.length; i++) {
							byte b = 0x0;
							
							for(int j = 0; j < 8; j++) {
								int bitID = 8 * i + j;
								
								if(bitID < bits.length) {
									System.out.println(bits[bitID].getBit());
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
						communicator.exception("Invalid object instance: " + element.getValue());
					}
				} else {
					communicator.exception("Dead variable: " + file);
				}
			} else {
				communicator.exception("Dead variable: " + this.object.getValue());
			}
		} else {
			communicator.exception("You can't use a direct value for an object instance");
		}
		
		return false;
	}
}