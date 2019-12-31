/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2019 Innovazion. All rights reserved.
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
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.lang.Reference;
import ch.innovazion.arionide.lang.Specification;
import ch.innovazion.arionide.lang.UserHelper;
import ch.innovazion.arionide.menu.Menu;

class ReferenceSelector extends Menu {	
	private Reference element;
	
	public ReferenceSelector(Menu parent) {
		super(parent);
	}
	
	protected void setTarget(Reference element) {
		this.element = element;
	}
	
	public void show() {
		assert element != null;
		
		super.show();
		
		UserHelper cdm = getProject().getLanguage().getUserHelper();
		
		List<String> suggestions = new ArrayList<>();
		
		for(Entry<Integer, String> entry : cdm.getReferencables().entrySet()) {
			suggestions.add(entry.getValue() + "$$$" + entry.getKey());
		}
		
		Collections.sort(suggestions);
		getElements().addAll(suggestions);
				
		if(element.getValue() != null) {
			int index = getElements().indexOf(element.getValue());
		
			if(index > -1) {
				this.select(index);
			}
		}
	}
	
	public void onClick(String element) {
		int index = element.indexOf("$$$");
		
		if(index > -1) {
			int id = Integer.parseInt(element.substring(index + 3));
			Specification spec = getProject().getStorage().getStructureMeta().get(id).getSpecification();
			this.element.setSpecificationParameters(spec.getElements());
		}
		
		Event event = getProject().getDataManager().getSpecificationManager().setValue(this.element, element);
		getAppManager().getEventDispatcher().fire(event);
		
		getAppManager().getCoreRenderer().getCodeGeometry().requestReconstruction();
		
		back();
	}
}