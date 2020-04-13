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

import java.util.List;

import ch.innovazion.arionide.events.GeometryInvalidateEvent;
import ch.innovazion.arionide.events.TargetUpdateEvent;
import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.Signature;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.automaton.Export;
import ch.innovazion.automaton.Inherit;

public class SignatureSelector extends Menu {
	
	@Export
	@Inherit
	protected Structure target;
	
	@Inherit
	protected int previousIndex;
	
	@Inherit
	protected Instruction instruction;
	
	public SignatureSelector(MenuManager manager) {
		super(manager);
	}
	
	protected void onEnter() {
		List<Signature> signatures = instruction.createStructureModel().getPossibleSignatures();
		
		setDynamicElements(signatures.stream().map(Signature::getName).toArray(String[]::new));
		
		super.onEnter();
	}
	
	protected void updateCursor(int cursor) {
		super.updateCursor(cursor);
			
		if(target != null) {
			List<Signature> signatures = instruction.createStructureModel().getPossibleSignatures();
			this.description = new MenuDescription(signatures.get(id).getParameters().stream().map(Parameter::toString).toArray(String[]::new));	
		}
	}
	
	public void onAction(String action) {
		dispatch(project.getStructureManager().getCodeManager().insertCode(this.previousIndex, this.instruction, id));
		
		int newTargetID = project.getStructureManager().getCodeManager().getCurrentCode().getID(previousIndex + 1);
		this.target = project.getStorage().getStructures().get(newTargetID);
		
		dispatch(new GeometryInvalidateEvent(0));
		dispatch(new TargetUpdateEvent(target.getIdentifier()));
		
		go("/code/edit/specify");
	}
}