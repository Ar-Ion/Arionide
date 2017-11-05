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
package org.azentreprise.arionide.ui.menu.edition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.project.HierarchyElement;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.core.opengl.WorldElement;
import org.azentreprise.arionide.ui.menu.MainMenus;
import org.azentreprise.arionide.ui.menu.SpecificMenu;

public class LanguageSelection extends SpecificMenu {
	
	private final List<Integer> languageIDs = new ArrayList<>();

	private int current;
	
	public LanguageSelection(AppManager manager) {
		super(manager, "<Error>");
	}
	
	public void setCurrent(WorldElement current) {
		super.setCurrent(current);
		
		Project project = this.getAppManager().getWorkspace().getCurrentProject();
		Map<Integer, StructureMeta> metaData = project.getStorage().getStructureMeta();
		
		if(project != null) {
			this.getElements().clear();
			this.languageIDs.clear();
			
			int i = 0;
			
			for(HierarchyElement element : project.getStorage().getHierarchy()) {
				if(element.getID() != current.getID()) {
					StructureMeta meta = metaData.get(element.getID());
					
					if(meta != null) {
						this.getElements().add(meta.getName());
						this.languageIDs.add(i);
					}
				} else {
					this.current = i;
				}

				i++;
			}
			
			this.getElements().add("Cancel");
		}
	}
	
	public void onClick(int element) {
		if(element < this.languageIDs.size()) {
			Project project = this.getAppManager().getWorkspace().getCurrentProject();
			
			if(project != null) {
				MessageEvent message = project.getDataManager().setLanguage(this.current, this.languageIDs.get(element));
				this.getAppManager().getEventDispatcher().fire(message);
				MainMenus.getStructureEditor().show();
			}
		} else {
			MainMenus.getStructureEditor().show();
		}
	}

	public String getDescription() {
		return "Language selection for " + super.getDescription();
	}
}