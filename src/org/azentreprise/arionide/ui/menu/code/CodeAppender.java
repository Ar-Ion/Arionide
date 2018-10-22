/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.project.HierarchyElement;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Menu;

public class CodeAppender extends Menu {
	
	private final Menu parent; // This refers to the Code menu
	private final List<Integer> instructions = new ArrayList<>();
	
	private int position = -1;
	
	protected CodeAppender(AppManager manager, Menu parent) {
		super(manager);
		
		this.parent = parent;
	}
	
	// "id" is the selected element.
	public void setAppenderPosition(int id) {
		this.position = id + 1;
		
		Project project = this.getAppManager().getWorkspace().getCurrentProject();
		List<String> elements = this.getElements();

		if(project != null) {
			elements.clear();
			this.instructions.clear();
						
			int parent = project.getDataManager().getHostStack().getCurrent();
			
			StructureMeta parentMeta = project.getStorage().getStructureMeta().get(parent);
			
			if(parentMeta != null) {
				HierarchyElement instructionSet = project.getStorage().getHierarchy().get(parentMeta.getLanguage());
				
				for(HierarchyElement instruction : instructionSet.getChildren()) {
					StructureMeta instructionMeta = project.getStorage().getStructureMeta().get(instruction.getID());
					
					if(instructionMeta != null) {
						elements.add(instructionMeta.getName());
						this.instructions.add(instruction.getID());
					}
				}
			}
			
			elements.add("Back");
		}
	}
		
	public void onClick(int id) {
		if(id < this.getElements().size() - 1) {
			AppManager manager = this.getAppManager();
			
			Project project = manager.getWorkspace().getCurrentProject();
			MessageEvent event = project.getDataManager().insertCode(this.position, this.instructions.get(id));
			
			manager.getEventDispatcher().fire(event);
			manager.getCoreRenderer().getCodeGeometry().requestReconstruction();
			
			this.parent.show();
			this.parent.select(this.position);
		} else {
			this.parent.show();
		}
	}
	
	public String getDescription() {
		return "Please select the instruction to insert";
	}
}
