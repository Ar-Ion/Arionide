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

import org.joml.Vector3f;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.events.GeometryInvalidateEvent;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.mutables.MutableStructure;
import ch.innovazion.arionide.ui.ApplicationTints;
import ch.innovazion.automaton.Inherit;

public class TintSelector extends Menu {
	
	@Inherit
	protected MutableStructure target;

	public TintSelector(MenuManager manager) {
		super(manager, ApplicationTints.getColorNames().toArray(new String[0]));
	}
	
	protected void onEnter() {				
		super.onEnter();
		updateCursor(target.getColorID());
	}
	
	public void updateCursor(int cursor) {
		super.updateCursor(cursor);
		
		Vector3f color = ApplicationTints.getColorByID(id);
		int rgb = Utils.packRGB((int) (color.x * 255), (int) (color.y * 255), (int) (color.z * 255));
		
		this.description = new MenuDescription(rgb, new String(), "Preview");
	}

	public void onAction(String action) {
		dispatch(project.getStructureManager().setColor(target.getIdentifier(), id));
		dispatch(new GeometryInvalidateEvent(2));
		go("..");
	}
}
