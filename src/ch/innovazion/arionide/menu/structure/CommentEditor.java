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

import java.util.ArrayList;
import java.util.List;

import ch.innovazion.arionide.events.GeometryInvalidateEvent;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.ui.ApplicationTints;
import ch.innovazion.arionide.ui.overlay.Views;
import ch.innovazion.automaton.Inherit;

public class CommentEditor extends Menu {

	@Inherit
	private Structure target;
	
	public CommentEditor(MenuManager manager) {
		super(manager, "Line 1", "Line 2", "Line 3", "Line 4");
	}
	
	protected void onEnter() {
		super.onEnter();
		
		this.description = new MenuDescription(ApplicationTints.MENU_INFO_INACTIVE_COLOR, 1.0f);

		List<String> comment = target.getComment();
		
		for(int i = 0; i < 4; i++) {
			if(i < comment.size() && !comment.get(i).isEmpty()) {
				description.add(comment.get(i));
			} else {
				description.add("<Empty line>", ApplicationTints.MENU_INFO_EMPTY_COLOR);
			}
		}
	}
	
	protected void updateCursor(int cursor) {
		super.updateCursor(cursor);
		
		if(target != null) {
			description.setHighlight(id);
		}
	}
	
	public void onAction(String action) {
		String placeholder = new String();
		
		if(id < target.getComment().size()) {
			placeholder = target.getComment().get(id);
		}
		
		Views.input.setText(action)
				   .setPlaceholder(placeholder)
				   .setResponder(this::onLineUpdate)
				   .stackOnto(Views.code);
	}
	
	private void onLineUpdate(String line) {
		List<String> copy = new ArrayList<>(target.getComment());
		
		do {
			copy.add(new String());
		} while(copy.size() <= id);
		
		copy.set(id, line);
		
		dispatch(project.getStructureManager().setComment(target.getIdentifier(), copy));
		go(".");
		dispatch(new GeometryInvalidateEvent(2));
	}
}
