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
package ch.innovazion.arionide.project.managers;

import java.util.ArrayList;
import java.util.List;

import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.Specification;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.Manager;
import ch.innovazion.arionide.project.Storage;
import ch.innovazion.arionide.project.mutables.MutableHierarchyElement;
import ch.innovazion.arionide.project.mutables.MutableStructureMeta;

public class CodeManager extends Manager {
	
	private final Storage storage;
	private final ResourceAllocator allocator;
	private final HostStructureStack hostStack;
	
	protected CodeManager(Storage storage, ResourceAllocator allocator, HostStructureStack hostStack) {
		super(storage);
		
		this.storage = storage;
		this.allocator = allocator;
		this.hostStack = hostStack;
	}
	
	public MessageEvent insertCode(int index, int instructionID) {
		int structureID = this.allocator.allocStructure();
		
		MutableStructureMeta meta = new MutableStructureMeta(-1);
		meta.setComment("code@" + instructionID);
		meta.setSpecification(new Specification(this.storage.getStructureMeta().get(instructionID).getSpecification()));
		meta.setAccessAllowed(false);
		
		this.getMeta().put(structureID, meta);
		this.saveMeta();
		
		this.getMutableCurrentCode().add(index, new MutableHierarchyElement(structureID, new ArrayList<>()));
		this.saveCode();
			
		return new MessageEvent("Added an instruction to the code", MessageType.SUCCESS);
	}

	public MessageEvent deleteCode(int id) {
		HierarchyElement element = this.getMutableCurrentCode().remove(id);
		this.saveCode();
		
		if(element != null) {
			this.getMeta().remove(element.getID());
		}
		
		return new MessageEvent("Removed an instruction from the code", MessageType.SUCCESS);
	}
	
	public List<? extends HierarchyElement> getCurrentCode() {
		return this.getCode().get(this.hostStack.getCurrent()).getChain();
	}
	
	private List<MutableHierarchyElement> getMutableCurrentCode() {
		return this.getCode().get(this.hostStack.getCurrent()).getMutableChain();
	}
}
