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
package ch.innovazion.arionide.menu;

import java.util.List;

import ch.innovazion.arionide.lang.symbols.SpecificationElement;
import ch.innovazion.arionide.project.StructureMeta;
import ch.innovazion.arionide.ui.ApplicationTints;

public class DisplayUtils {
	public static MenuDescription fullDescription(StructureMeta meta) {
		MenuDescription description = new MenuDescription(ApplicationTints.MENU_INFO_INACTIVE_COLOR, 1.0f);
		List<SpecificationElement> elements = meta.getSpecification().getElements();

		String name = meta.getName();
		
		if(!meta.getComment().equals("?")) {
			name += " (" + meta.getComment() + ")";
		}
		
		description.add(name);
		
		for(SpecificationElement element : elements) {
			description.add(element.toString());
		}

		description.setHighlight(0);
		description.setColor(0, ApplicationTints.MENU_INFO_DEFAULT_COLOR);
		
		return description;
	}
}
