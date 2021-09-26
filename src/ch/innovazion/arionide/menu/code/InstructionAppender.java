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
package ch.innovazion.arionide.menu.code;

import java.util.ArrayList;
import java.util.List;

import ch.innovazion.arionide.events.GeometryInvalidateEvent;
import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.events.TargetUpdateEvent;
import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.Signature;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.project.StructureModel;
import ch.innovazion.automaton.Export;
import ch.innovazion.automaton.Inherit;

public abstract class InstructionAppender extends Menu {

	private final MenuManager manager;
	private final int numStaticElements;

	@Export
	@Inherit
	protected Structure target;
	
	@Export
	protected int previousIndex;
	
	@Export
	protected Instruction instruction;
	
	private List<Instruction> instructions = new ArrayList<>();
		
	public InstructionAppender(MenuManager manager, String... staticElements) {
		super(manager, staticElements);
		this.manager = manager;
		this.numStaticElements = staticElements.length;
	}
	
	protected void onEnter() {
		super.onEnter();
				
		this.instructions = getInstructions();
		setDynamicElements(instructions.stream().map(Instruction::toString).toArray(String[]::new));
	}
	
	protected void updateCursor(int cursor) {
		super.updateCursor(cursor);
		
		if(instructions != null && id >= numStaticElements && id < numStaticElements + instructions.size()) {
			Instruction instr = instructions.get(id - numStaticElements);
			generateDescription(instr.createStructureModel());	
		} else {
			this.description = new MenuDescription();
		}
	}
	
	private void generateDescription(StructureModel model) {
		this.description = new MenuDescription();
		
		if(model != null) {
			if(model.hasUniqueSignature()) {
				for(Parameter param : model.getSignature(0)) {
					description.add(param.toString());
				}
			} else {
				for(Signature signature : model.getPossibleSignatures()) {
					description.add(signature.toString());
				}
			}
			
			description.spacer();
			
			for(String comment : model.getComment()) {
				description.add(comment);
			}
		}
	}
	
	protected void appendInstruction(int instructionID) {
		this.instruction = instructions.get(instructionID);
		this.previousIndex = project.getStructureManager().getCodeManager().getCurrentCode().indexOf(target.getIdentifier());
						
		if(instruction.createStructureModel().hasUniqueSignature()) {
			MessageEvent event = project.getStructureManager().getCodeManager().insertCode(previousIndex + 1, this.instruction, 0);
			
			if(event.getMessageType() != MessageType.ERROR) {
				int newTargetID = project.getStructureManager().getCodeManager().getCurrentCode().getID(previousIndex + 1);
				this.target = project.getStorage().getStructures().get(newTargetID);
				
				dispatch(new GeometryInvalidateEvent(0));
				dispatch(new TargetUpdateEvent(target.getIdentifier()));
				manager.selectCode(target);
				
				if(!instruction.createStructureModel().isTextOnly()) {
					go("edit");
				} else {
					go("comment");
				}
			} else {
				dispatch(event);
				return;
			}
		} else {
			go("signature");
		}
	}
	
	protected abstract List<Instruction> getInstructions();
}
