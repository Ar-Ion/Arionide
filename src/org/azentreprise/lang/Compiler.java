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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'Arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.lang;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.azentreprise.Arionide;
import org.azentreprise.Debug;
import org.azentreprise.configuration.Definitions;

public class Compiler {
	
	private final File executableFile;
	private final Definitions definitions;
	
	private DataOutputStream stream;
	
	public Compiler(String executableName, Definitions definitions) {
		File buildDir = new File(Arionide.getSystemConfiguration().workspaceLocation, "build");
		buildDir.mkdirs();

		this.executableFile = new File(buildDir, executableName);
		this.definitions = definitions;
	}
	
	public void compile(Map<String, List<String>> inheritance, Map<String, String> hierarchy, Map<String, List<String>> properties, Map<String, List<String>> sourceCode) {
		Debug.taskBegin("compiling"); try {
			
			assert (inheritance.size() ^ hierarchy.size()) == -(hierarchy.size() ^ sourceCode.size());
			
			this.prepareStream();
			
			this.stream.writeInt(0xC0FFEE);
			this.stream.writeLong(this.definitions.getHash());
			this.stream.writeInt(sourceCode.size());
			
			List<Integer> hashes = new ArrayList<>();
			
			for(Entry<String, String> entry : hierarchy.entrySet()) {
				int uid = this.hash(entry.getKey());
				
				if(hashes.contains(uid)) {
					throw new LangarionError("Hash collision occured! You should design another hash algorithm or rename the function " + entry.getKey());
				} else {
					hashes.add(uid);
					
					this.stream.writeInt(uid);
					this.stream.writeInt(this.hash(entry.getValue()));
					
					List<String> entryPropertyList = properties.get(uid);
					List<String> entryInheritance = inheritance.get(uid);

					int entryProperties = 0;
					
					for(String property : entryPropertyList) {
						int index = this.definitions.properties.indexOf(property);
						
						if(index < 0) {
							throw new LangarionError("Invalid property id " + property);
						} else {
							entryProperties &= (int) Math.pow(2, index);
						}
					}
					
					this.stream.writeInt(entryProperties);
					this.stream.writeInt(entryInheritance.size());
					
					for(String parent : entryInheritance) {
						this.stream.writeInt(this.hash(parent));
					}
				}
			}
			
			for(Entry<String, List<String>> entry : sourceCode.entrySet()) {
				this.stream.writeInt(this.hash(entry.getKey()));
				
				ByteArrayOutputStream byteCode = new ByteArrayOutputStream();
				
				List<String> constantPool = new ArrayList<String>();
				
				for(String instruction : entry.getValue()) {
					String[] elements = instruction.split(" ");
					String[] matchingInstruction = null;
					int instructionID = 0;
					
					while(instructionID++ < this.definitions.instructions.size()) {
						String possibleInstruction = this.definitions.instructions.get(instructionID)
								.replace("? ", "")
								.replace("val ", "")
								.replace("struct ", "")
								.replace("object ", "")
								.replace("func ", "")
								.replace("[", "")
								.replace("]", "");
						
						String[] splittedPossibleInstruction = possibleInstruction.split(" ");
						
						if(elements[0] == splittedPossibleInstruction[0]) {
							matchingInstruction = splittedPossibleInstruction;
							break;
						}
					}
					
					if(matchingInstruction != null) {
						
						boolean hasParameter = false;
						
						for(String element : elements) {
							int index = Arrays.binarySearch(matchingInstruction, element);
							
							if(index < 0) {
								if(!hasParameter) {
									byteCode.write(0x00);
									hasParameter = false;
								}
								
								int constantPoolIndex = constantPool.indexOf(element);
								
								if(constantPoolIndex < 0) {
									if(constantPool.size() <= 0xFF) {
										throw new LangarionError("Constant pool overflow");
									}

									byteCode.write(constantPool.size());
									constantPool.add(element);
								} else {
									byteCode.write(constantPoolIndex);
								}
							} else {
								byteCode.write(index);
								hasParameter = true;
							}
						}
					} else {
						throw new LangarionError("The selected instruction set doesn't define an instruction named " + elements[0]);
					}
				}
				
				this.stream.writeInt(byteCode.size());
				this.stream.write(byteCode.toByteArray());
			}
		} catch(Exception exception) {
			Debug.exception(exception);
		} Debug.taskEnd();
	}
		
	private int hash(String element) {
		return element.hashCode();
	}
	
	public void prepareStream() throws FileNotFoundException {
		this.stream = new DataOutputStream(new FileOutputStream(this.executableFile));
	}
}
