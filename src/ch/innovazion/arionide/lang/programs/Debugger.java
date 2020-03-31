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

import ch.innovazion.arionide.lang.ApplicationMemory;
import ch.innovazion.arionide.lang.Environment;
import ch.innovazion.arionide.lang.EvaluationException;
import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Language;
import ch.innovazion.arionide.lang.Program;
import ch.innovazion.arionide.lang.symbols.Callable;
import ch.innovazion.arionide.project.Storage;

public class Debugger extends Program {

	private final Environment env;
	
	public Debugger(Storage storage, Environment env) {
		super(storage);
		this.env = env;
	}

	public void run(int rootStructure, ProgramIO io) {
		ApplicationMemory memory = io.in(ApplicationMemory.class);
	
		if(memory != null) {
			try {
				Language lang = env.getLanguage();
				
				while(true) {
					Callable callable = memory.textAt(env.getProgramCounter().get());
					Instruction instruction = lang.getInstructionSet().get(callable.getName());
					
					if(instruction != null) {
						instruction.evaluate(env, callable.getSpecification());
					} else {
						io.fatal("Invalid instruction: " + callable.getName());
						break;
					}
				}
			} catch (EvaluationException e) {
				io.fatal(e.getMessage());
			}
		} else {
			io.fatal("Cannot run debugger without having relocated the application skeleton");
		}
	}

	public String getName() {
		return "Built-in debugger";
	}
}
