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

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Menu;
import org.azentreprise.arionide.ui.menu.SpecificMenu;

public class InheritanceElementEditor extends Menu {

	private static final String remove = "Remove from inheritance";
	private static final String close = "Close";
	
	private final SpecificMenu parent;
		
	private int target;
	
	protected InheritanceElementEditor(AppManager manager, SpecificMenu parent) {
		super(manager, remove, close);
		this.parent = parent;
	}
	
	protected void setTarget(int target) {
		this.target = target;
	}

	protected void onClick(String element) {		
		switch(element) {
			case remove:
				Project project = this.getAppManager().getWorkspace().getCurrentProject();
				
				MessageEvent message = null;
				
				if(project != null) {

					message = project.getDataManager().desinherit(this.target, this.parent.getCurrent().getID());
				} else {
					message = new MessageEvent("No project is currently loaded", MessageType.ERROR);
				}
				
				this.getAppManager().getEventDispatcher().fire(message);
				
				this.parent.reload();
				this.parent.show();
				
				break;
			case close:
				this.parent.show();
				break;
		}
	}
	
	public String getDescription() {
		return "Inheritance element editor";
	}
}
