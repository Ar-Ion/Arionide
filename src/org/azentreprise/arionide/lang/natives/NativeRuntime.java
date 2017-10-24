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
package org.azentreprise.arionide.lang.natives;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.azentreprise.arionide.lang.Runtime;
import org.azentreprise.arionide.lang.Specification;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.lang.Validator;
import org.azentreprise.arionide.lang.natives.instructions.Print;
import org.azentreprise.arionide.lang.natives.instructions.Init;
import org.azentreprise.arionide.lang.natives.instructions.NativeInstruction;
import org.azentreprise.arionide.project.HierarchyElement;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.project.Storage;
import org.azentreprise.arionide.project.StructureMeta;

public class NativeRuntime extends Runtime {
	
	private final NativeDataCommunicator ndc = new NativeDataCommunicator();
	private final List<List<NativeInstruction>> code = new ArrayList<>();
	private final List<String> symbols = new ArrayList<>();
	private final List<Integer> references = new ArrayList<>(); // ID in code list --> Real ID
	
	public NativeRuntime(Project project) {
		super(project);
		this.ndc.setInfoChannel(this::info);
	}

	public void run(int id) {
		this.code.clear();
		this.symbols.clear();
		
		this.info("Compiling sources...", 0xFFFF00);
		
		Map<Integer, StructureMeta> metaData = this.getProject().getStorage().getStructureMeta();
		
		if(this.compile(id, "root", metaData)) {
			this.info("Compilation succeed", 0x00FF00);
			this.info("Running program...", 0xFFAA00);
			
			if(this.code.size() > 0) {
				for(NativeInstruction instruction : this.code.get(0)) {
					instruction.execute(this.ndc, this.references);
				}
			}
			
			this.info("Program execution finished", 0xFFAA00);
		} else {
			this.info("Compilation failed", 0xFF0000);	
		}
	}
	
	private boolean compile(int id, String name, Map<Integer, StructureMeta> metaData) {
		Storage storage = this.getProject().getStorage();
		
		int realID = storage.getHierarchy().get(id).getID();
		
		storage.loadData(realID);
		
		List<HierarchyElement> elements = storage.getCurrentData();
		List<NativeInstruction> structure = new ArrayList<>();
		List<Integer> nextElements = new ArrayList<>();
		
		StructureMeta structureMeta = metaData.get(id);
		
		boolean state = true;

		if(structureMeta != null) {
			name += "." + structureMeta.getName();
						
			this.symbols.add(name);
			this.references.add(realID);
									
			for(HierarchyElement element : elements) {
				StructureMeta meta = metaData.get(element.getID());
				
				if(meta != null) {
					String comment = meta.getComment();
					Specification spec = meta.getSpecification();
					
					if(comment.contains("code@")) {
						try {
							int instructionID = Integer.parseInt(comment.replace("code@", ""));
							
							StructureMeta instructionMeta = metaData.get(instructionID);
							
							if(instructionMeta != null) {
								Specification instructionSpec = instructionMeta.getSpecification();

								if(spec.hasSameOrigin(instructionSpec) && spec.getElements().equals(instructionSpec.getElements())) {
									NativeInstruction compiled = this.compileInstruction(this.symbols.size(), instructionMeta.getName(), spec, nextElements);
									
									if(compiled != null) {
										structure.add(compiled);
									} else {
										this.info("Instruction compilation failed in " + name + " (" + realID + ":" + element.getID() + ")", 0xFF6000);
										state = false;
									}
								} else {
									this.info("Specification origin check failed in " + name +  " (" + realID + ":" + element.getID() + ")", 0xFF6000);
									state = false;
								}
							} else {
								this.info("Instruction ID " + instructionID + " was not properly installed", 0xFF6000);
								state = false;
							}
						} catch(NumberFormatException e) {
							this.info("Invalid instruction ID " + comment + " in " + name + " (" + realID + ":" + element.getID() + ")", 0xFF6000);
							state = false;
						}
					} else {
						this.info("Object in " + name + " (" + realID + ":" + element.getID() + ") is not an instruction", 0xFF6000);
						state = false;
					}
				} else {
					this.info("Invalid structure ID in " + name + " (" + realID + ":" + element.getID() + ")", 0xFF6000);
					state = false;
				}
			}
		} else {
			this.info("Invalid structure ID: (" + realID + ":?)", 0xFF6000);
			state = false;
		}
		
		this.code.add(structure);
		
		for(Integer next : nextElements) {
			if(!this.compile(next, name, metaData)) {
				state = false;
			}
		}
		
		return state;
	}
	
	private NativeInstruction compileInstruction(int symID, String instruction, Specification spec, List<Integer> nextElements) {
		for(SpecificationElement element : spec.getElements()) {			
			Validator validator = this.getProject().getLanguage().getTypes().getValidator(element.getType());
			
			if(validator == null || !validator.validate(element.getValue())) {
				this.info("Invalid type", 0xFF6000);
				return null;
			}
			
			if(element.getType() == NativeTypes.REF) {
				try {
					nextElements.add(Integer.parseInt(element.getValue()));
				} catch(NumberFormatException e) {
					this.info("Invalid reference", 0xFF6000);
					return null;
				}
			}
		}
		
		switch(instruction) {
			case "init":
				return new Init();
			case "print":
				return new Print(spec.getElements().get(0).getValue());
			default:
				this.info("Instruction " + instruction + " is not compilable", 0xFF6000);
				return null;
			
		}
	}
}