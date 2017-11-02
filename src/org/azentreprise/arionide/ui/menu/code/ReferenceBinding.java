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
package org.azentreprise.arionide.ui.menu.code;

import java.util.List;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Menu;

public class ReferenceBinding extends Menu {
	
	private final Menu parent;
	private final SpecificationElement element;
	private final List<SpecificationElement> possible;
	
	public ReferenceBinding(AppManager manager, Menu parent, SpecificationElement element, List<SpecificationElement> possible) {
		super(manager, "Back");
		
		this.parent = parent;
		this.element = element;
		this.possible = possible;
		
		for(SpecificationElement poss : possible) {
			if(element.getClass().isInstance(poss)) {
				this.getElements().add(poss.getName());
			}
		}
	}
	
	public void onClick(int id) {
		if(id != 0) {
			this.possible.get(id - 1).setValue("var@" + this.element.getName());
			this.getAppManager().getWorkspace().getCurrentProject().getStorage().saveStructureMeta();
			this.getAppManager().getEventDispatcher().fire(new MessageEvent("Parameter successfully bound", MessageType.SUCCESS));
			this.parent.show();
		}
		
		this.parent.show();
	}
}