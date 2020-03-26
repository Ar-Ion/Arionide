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
package ch.innovazion.arionide.menu.structure;

import ch.innovazion.arionide.events.GeometryInvalidateEvent;
import ch.innovazion.arionide.events.TeleportEvent;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.ui.overlay.View;
import ch.innovazion.arionide.ui.overlay.Views;
import ch.innovazion.automaton.Export;
import ch.innovazion.automaton.Inherit;

public class StructureEditor extends Menu {
	
	@Export
	@Inherit
	protected Structure target;

	public StructureEditor(MenuManager manager) {
		super(manager, "Go", "Specify", "Comment", "Rename", "Language", "Tint", "Delete");
	}

	public void onAction(String action) {		
		switch(action) {
		case "Go":
			dispatch(new TeleportEvent(target.getIdentifier()));
			break;
		case "Rename":
			Views.input.setText("Please enter the new name of the new structure")
					   .setPlaceholder("Structure name")
					   .setResponder(this::rename)
					   .stackOnto(Views.code);
			
			break;
		case "Delete":
			Views.confirm.setPrimaryText("Are you sure you want to delete this structure?")
						 .setSecondaryText("Everything inside the structure will be deleted")
					     .setButtons("Yes", "Cancel")
					     .react("Yes", this::delete)
					     .react("Cancel", View::discard)
					     .stackOnto(Views.code);
			
			break;
		default:
			go(action.toLowerCase());
		}
	}
	
	private void rename(String name) {
		dispatch(project.getStructureManager().rename(target.getIdentifier(), name));
		dispatch(new GeometryInvalidateEvent(2));
	}
	
	private void delete(View view) {
		dispatch(project.getStructureManager().deleteStructure(target.getIdentifier()));
		dispatch(new GeometryInvalidateEvent(2));
		view.discard();
		go("/structure");
	}
}
