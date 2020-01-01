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

import java.util.Arrays;
import java.util.List;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.menu.Browser;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.Storage;
import ch.innovazion.arionide.project.StructureMeta;
import ch.innovazion.arionide.ui.AppManager;

public class CodeAppender extends Browser {
			
	private final Browser parent;
	
	protected CodeAppender(Browser parent) {
		super(parent);
		this.parent = parent;
	}

	public void onClick(int id) {
		AppManager manager = this.getAppManager();

		Event event = getProject().getDataManager().getCodeManager().insertCode(getTarget().getID(), getSelectedID());
		manager.getEventDispatcher().fire(event);
		
		manager.getCoreOrchestrator().getController().getCodeGeometry().requestReconstruction();
		
		back();

		parent.select(parent.getMenuCursor() + 1);
	}
	
	protected void onSelect(int id) {
		return;
	}

	protected List<Integer> loadCurrentElements() {
		Storage storage = getProject().getStorage();

		int parent = getProject().getDataManager().getHostStack().getCurrent();
		StructureMeta parentMeta = storage.getStructureMeta().get(parent);
		
		HierarchyElement instructionSet = null;
		
		for(HierarchyElement element : storage.getHierarchy()) {
			if(element.getID() == parentMeta.getLanguage()) {
				instructionSet = element;
				break;
			}
		}
		
		if(instructionSet != null) {
			List<Integer> IDs = Utils.extract(instructionSet.getChildren(), HierarchyElement::getID);
			int init = getProject().getDataManager().retrieveInstructionDefinition("init");
			
			IDs.remove((Integer) init);
			
			return IDs;
		}
		
		return Arrays.asList();
	}
	
	public MenuDescription getDescription() {
		return new MenuDescription("Please select the instruction to insert");
	}
}
