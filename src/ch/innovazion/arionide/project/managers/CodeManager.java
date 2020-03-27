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
import ch.innovazion.arionide.project.Manager;
import ch.innovazion.arionide.project.Storage;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.project.mutables.MutableCode;
import ch.innovazion.arionide.project.mutables.MutableCodeChain;
import ch.innovazion.arionide.project.mutables.MutableHierarchyElement;

public class CodeManager extends Manager {
	
	private final ResourceAllocator allocator;
	private final HostStructureStack hostStack;
	
	protected CodeManager(Storage storage, ResourceAllocator allocator, HostStructureStack hostStack) {
		super(storage);
		
		this.allocator = allocator;
		this.hostStack = hostStack;
	}
	
	public MessageEvent resetCodeChain(int id) {
		getCode().put(id, new MutableCodeChain());
		return new MessageEvent("Code chain reset", MessageType.SUCCESS);
	}
	
	public MessageEvent insertCode(int index, Instruction instr) {
		Structure host = getStructures().get(hostStack.getCurrent());
		
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
		
		int offset = lang.getStandardInstructions().indexOf(instr);
		
		if(offset < 0) {
			return new MessageEvent("Invalid instruction for the target language", MessageType.ERROR);
		}
		
		Structure resolvedDefinition = getStructures().get(entryID + offset + 1);
		
		if(resolvedDefinition == null || !resolvedDefinition.getName().equals(instr.getStructureModel().getUniqueName())) {
			return new MessageEvent("Failed to retrieve the definition of this instruction... Try reinstalling the target language", MessageType.ERROR);
		}
				
		int structureID = allocator.allocStructure();
		MutableCode code = new MutableCode(resolvedDefinition);
		
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
		return hostStack.getCurrent() != -1 && !getCurrentCode0().isAbstract();
	}
	
	public CodeChain getCurrentCode() {
		return getCurrentCode0();
	}

	
	private MutableCodeChain getCurrentCode0() {
		synchronized(hostStack) {
			MutableCodeChain chain = getCode().get(hostStack.getCurrent());	
			
			if(chain != null) {
				return chain;
			} else {
				throw new IllegalStateException("The hoststack is inconsistent or the code section has been corrupted.");
			}
		}
	}
}
