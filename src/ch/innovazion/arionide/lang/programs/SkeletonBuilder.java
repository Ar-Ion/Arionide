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
package ch.innovazion.arionide.lang.programs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Language;
import ch.innovazion.arionide.lang.LanguageManager;
import ch.innovazion.arionide.lang.Program;
import ch.innovazion.arionide.lang.Skeleton;
import ch.innovazion.arionide.lang.symbols.Callable;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.ParameterValue;
import ch.innovazion.arionide.lang.symbols.Reference;
import ch.innovazion.arionide.project.Storage;

public class SkeletonBuilder extends Program {
	
	public SkeletonBuilder(Storage storage) {
		super(storage);
	}

	public void run(int rootStructure, ProgramIO io) {
		Skeleton skeleton = new Skeleton();
		
		Callable root = getCallable(rootStructure);
		
		Language lang = LanguageManager.get(root.getLanguage());
		
		if(lang != null) {
			build(root, skeleton, lang.getInstructionSet(), io);
			io.out(skeleton);
		} else {
			io.fatal("No language set for target structure");
		}
	}
	
	private void build(Callable target, Skeleton skeleton, Map<String, Instruction> instructionSet, ProgramIO io) {
		io.log("Building skeleton for structure '" + target.getName() + "'");

		skeleton.registerData(getState(target.getIdentifier()));
		skeleton.registerRodata(getConstants(target.getIdentifier()));
		skeleton.registerBSS(getProperties(target.getIdentifier()));
		
		List<Callable> code = getInstructions(target.getIdentifier());
				
		int length = 0;
		
		List<Callable> next = new ArrayList<>();
		
		boolean failure = false;
		
		for(Callable codeElement : code) {
			Instruction instr = instructionSet.get(codeElement.getName());
			
			if(instr != null) {
				length += instr.getLength();
				
				for(Parameter param : codeElement.getSpecification().getParameters()) {
					ParameterValue value = param.getValue();
					
					if(value instanceof Reference) {
						Reference ref = (Reference) value;
						Callable nextTarget = ref.getTarget();
						
						if(nextTarget != null) {
							next.add(nextTarget);
						} else {
							io.error("Invalid reference: " + ref.toString() + " (" + target.getIdentifier() + ":" + codeElement.getIdentifier() + ")");
							failure = true;
						}
					}
				}
			} else {
				io.error("Invalid instruction: " + codeElement.toString() + " (" + target.getIdentifier() + ":" + codeElement.getIdentifier() + ")");
				failure = true;
			}
		}
				
		skeleton.registerText(target, length);
		
		for(Callable callable : next) {
			build(callable, skeleton, instructionSet, io);
		}
		
		System.out.println(skeleton.getText());
		
		if(failure) {
			io.error("Failed to build skeleton for structure '" + target.getName() + "' (" + target.getIdentifier() + ":?)");
		} else {
			io.success("Skeleton built for structure '" + target.getName() + "'");
		}
	}
	
	public String getName() {
		return "Skeleton builder";
	}
}
