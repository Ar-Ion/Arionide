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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.innovazion.arionide.lang.ApplicationMemory;
import ch.innovazion.arionide.lang.Language;
import ch.innovazion.arionide.lang.LanguageManager;
import ch.innovazion.arionide.lang.Program;
import ch.innovazion.arionide.lang.Skeleton;
import ch.innovazion.arionide.lang.symbols.Callable;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.project.Storage;

public class Relocator extends Program {

	private final long programMemorySize;
	
	public Relocator(Storage storage, int programMemorySize) {
		super(storage);
		this.programMemorySize = programMemorySize;
	}
	
	public void run(int rootStructure, ProgramIO io) {
		io.log("Relocating application memory...");

		Skeleton skeleton = io.in(Skeleton.class);
		
		if(skeleton != null) {
			Map<Long, Callable> text = new HashMap<>();
			Map<Long, Node> data = new HashMap<>();
			
			Callable root = getCallable(rootStructure);
			Language lang = LanguageManager.get(root.getLanguage());
			
			for(Callable callable : skeleton.getText()) {
				List<Callable> instructions = getInstructions(callable.getIdentifier());
				long address = skeleton.getTextAddress(callable);
				
				for(Callable instr : instructions) {					
					long length = lang.getInstructionSet().get(instr.getName()).getLength();

					if(length > 0) {
						text.put(address, instr);
						address += length;	
					}
				}
			}
						
			for(Node info : skeleton.getRodata()) {
				data.put(skeleton.getRodataAddress(info), info);
			}
			
			for(Node info : skeleton.getBSS()) {
				data.put(skeleton.getBSSAddress(info), info);
			}
			
			for(Node info : skeleton.getData()) {
				data.put(skeleton.getDataAddress(info), info);
			}
						
			io.out(new ApplicationMemory(programMemorySize, text, data));
			io.success("Relocation succeeded.");
		} else {
			io.fatal("Cannot run relocator without having built the application skeleton");
		}
	}

	public String getName() {
		return "Built-in relocator";
	}
}
