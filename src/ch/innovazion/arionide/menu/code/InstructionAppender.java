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
import ch.innovazion.arionide.events.TargetUpdateEvent;
import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Language;
import ch.innovazion.arionide.lang.LanguageManager;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.project.StructureModel;
import ch.innovazion.automaton.Export;
import ch.innovazion.automaton.Inherit;

public abstract class InstructionAppender extends Menu {

	@Export
	@Inherit
	protected Structure target;
		
	private List<Instruction> instructions = new ArrayList<>();
	
	public InstructionAppender(MenuManager manager, String... staticElements) {
		super(manager, staticElements);
	}
	
	protected void onEnter() {
		super.onEnter();
		
		this.instructions = getInstructions();
		setDynamicElements(instructions.stream().map(Instruction::toString).toArray(String[]::new));
	}
	
	protected void updateCursor(int cursor) {
		super.updateCursor(cursor);
		
		Instruction instr = instructions.get(id);
		generateDescription(instr.getStructureModel());
	}
	
	private void generateDescription(StructureModel model) {
		this.description = new MenuDescription();
		
		if(model != null) {
			for(Parameter param : model.getParameters()) {
				description.add(param.toString());
			}
			
			description.spacer();
			
			for(String comment : model.getComment()) {
				description.add(comment);
			}
		}
	}
	
	protected void appendInstruction(int instructionID) {
		Instruction instr = instructions.get(instructionID);
		int index = project.getStructureManager().getCodeManager().getCurrentCode().indexOf(target.getIdentifier());
		
		dispatch(project.getStructureManager().getCodeManager().insertCode(index, instr));
		
		int newTargetID = project.getStructureManager().getCodeManager().getCurrentCode().getID(index + 1);
		this.target = project.getStorage().getStructures().get(newTargetID);
		
		dispatch(new GeometryInvalidateEvent(0));
		dispatch(new TargetUpdateEvent(target.getIdentifier()));
		
		go("../specify");
	}
	
	protected abstract List<Instruction> getInstructions();
}
