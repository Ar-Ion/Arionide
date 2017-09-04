/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
package org.azentreprise.arionide.ui.menu;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.core.opengl.WorldElement;

public class StructureList extends Menu {
	
	private static final String empty = "<Empty structure>";
	private List<WorldElement> elements;
	
	public StructureList(AppManager manager) {
		super(manager, empty);
	}
	
	public void set(List<WorldElement> elements) {
		this.elements = elements;
				
		if(elements.size() > 0) {
			this.setElements(elements.stream().map((e) -> e.toString()).collect(Collectors.toList()));
		} else {
			this.setElements(Arrays.asList(empty));
		}
	}
	
	protected void onClick(int id) {
		if(this.elements != null && id < this.elements.size()) {
			MainMenus.STRUCT_EDIT.setCurrent(this.elements.get(id));
			this.show(MainMenus.STRUCT_EDIT);
		}
	}
}