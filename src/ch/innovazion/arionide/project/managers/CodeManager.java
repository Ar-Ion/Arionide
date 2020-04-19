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
package ch.innovazion.arionide.project.managers;

import java.util.ArrayList;

import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Language;
import ch.innovazion.arionide.lang.LanguageManager;
import ch.innovazion.arionide.project.CodeChain;
import ch.innovazion.arionide.project.Storage;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.project.mutables.MutableCode;
import ch.innovazion.arionide.project.mutables.MutableCodeChain;
import ch.innovazion.arionide.project.mutables.MutableHierarchyElement;

public class CodeManager extends ContextualManager<Integer> {
	
	private final ResourceAllocator allocator;
	
	protected CodeManager(Storage storage, ResourceAllocator allocator) {
		super(storage);
		
		this.allocator = allocator;
		
		setContext(-1);
	}
	
	public MessageEvent resetCodeChain(int id) {
		getCode().put(id, new MutableCodeChain());
		return new MessageEvent("Code chain reset", MessageType.SUCCESS);
	}
	
	public MessageEvent insertCode(int index, Instruction instr, int signatureID) {
		Structure host = getStructures().get(getContext());
		
		if(host == null) {
			return new MessageEvent("You have to be inside a structure to add instructions to the code", MessageType.ERROR);
		}
				
		Language lang = LanguageManager.get(host.getLanguage());
		
		if(lang == null) {
			return new MessageEvent("The target language for this structure is invalid... Try updating Arionide", MessageType.ERROR);
		}
		
		Integer entryID = getLanguages().get(host.getLanguage());
		
		if(entryID == null) {
			return new MessageEvent("Specified language does not seem to be installed in the structure... Try reinstalling the target language", MessageType.ERROR);
		}
				
		int instructionIndex = lang.getOperators().indexOf(instr);
		int resolvedIndex = entryID + 1; // One for the entry point
		
		if(instructionIndex < 0) {
			instructionIndex = lang.getStandardInstructions().indexOf(instr);

			for(Instruction operator : lang.getOperators()) {
				resolvedIndex += operator.createStructureModel().getPossibleSignatures().size(); 
				// Each signature takes one structure slot
			}
			
			for(int i = 0; i < instructionIndex; i++) {
				resolvedIndex += lang.getStandardInstructions().get(i).createStructureModel().getPossibleSignatures().size(); 
				// Each signature takes one structure slot
			}
		} else {
			for(int i = 0; i < instructionIndex; i++) {
				resolvedIndex += lang.getOperators().get(i).createStructureModel().getPossibleSignatures().size(); 
				// Each signature takes one structure slot
			}
		}
		
		if(instructionIndex < 0) {
			return new MessageEvent("Invalid instruction for the target language", MessageType.ERROR);
		}
		
		resolvedIndex += signatureID; // Final offset
		
		
		Structure resolvedDefinition = getStructures().get(resolvedIndex);
		
		if(resolvedDefinition == null || !resolvedDefinition.getName().equals(instr.createStructureModel().getUniqueName())) {
			return new MessageEvent("Failed to retrieve the definition of this instruction... Try reinstalling the target language", MessageType.ERROR);
		}
						
		int structureID = allocator.allocStructure();
		MutableCode code = new MutableCode(structureID, resolvedDefinition);
		
		code.setLanguage(host.getLanguage());
		
		getStructures().put(structureID, code);
		saveStructures();
				
		getCurrentCode0().getMutableList().add(index, new MutableHierarchyElement(structureID, new ArrayList<>()));
		saveCode();
			
		return success();
	}

	public MessageEvent deleteCode(int id) {
		MutableCodeChain chain = getCurrentCode0();
		int index = chain.indexOf(id);
				
		if(index > 0) { 
			getStructures().remove(id);
			saveStructures();
			
			chain.getMutableList().remove(index);
			saveCode();
			
			return success();
		} else {
			return new MessageEvent("Cannot remove the entry point of this structure", MessageType.ERROR);
		}

	} 
	
	public boolean hasCode() {
		return getContext() != -1 && !getCurrentCode0().isAbstract();
	}
	
	public CodeChain getCurrentCode() {
		return getCurrentCode0();
	}

	private MutableCodeChain getCurrentCode0() {
		MutableCodeChain chain = getCode().get(getContext());
		
		if(chain != null) {
			return chain;
		} else {
			throw new IllegalStateException("Invalid context: Unable to fetch code for structure " + getContext());
		}
	}
}
