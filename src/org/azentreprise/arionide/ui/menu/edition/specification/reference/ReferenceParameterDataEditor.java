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
package org.azentreprise.arionide.ui.menu.edition.specification.reference;

import javax.swing.JOptionPane;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.lang.Data;
import org.azentreprise.arionide.lang.Reference;
import org.azentreprise.arionide.lang.Specification;
import org.azentreprise.arionide.lang.TypeManager;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Confirm;
import org.azentreprise.arionide.ui.menu.MainMenus;
import org.azentreprise.arionide.ui.menu.Menu;
import org.azentreprise.arionide.ui.menu.SpecificMenu;
import org.azentreprise.arionide.ui.menu.code.TypeEditor;

public class ReferenceParameterDataEditor extends SpecificMenu {

	private static final String back = "Back";
	private static final String delete = "Delete";
	private static final String setName = "Set name";
	private static final String setType = "Set type";
	private static final String setDefault = "Set default";

	private final ReferenceParameters parent;
	
	private Specification spec;
	private int id;
	private int dataID;
	private Data data;
	private TypeManager type;
	private String description;
	
	protected ReferenceParameterDataEditor(AppManager manager, ReferenceParameters parent) {
		super(manager, back, delete, setName, setType, setDefault);
		this.parent = parent;
	}

	protected void setTarget(Specification specification, int id, int dataID, Data data) {
		this.spec = specification;
		this.id = id;
		this.dataID = dataID;
		this.data = data;
		this.type = this.getAppManager().getWorkspace().getCurrentProject().getLanguage().getTypes().getTypeManager(data.getType());			
		this.description = data.getName() + " [" + this.type + "]";
		
		if(this.data.getValue() != null) {
			this.description += " (default: " + data.getValue() + ")";
		}
	}
	
	public void reload() {
		this.setTarget(this.spec, this.id, this.dataID, this.data);
	}
	
	public void onClick(String element) {		
		switch(element) {
			case back:
				this.parent.show();
				break;
			case delete:
				Menu confirm = new Confirm(this.getAppManager(), this, this::delete, "Do you really want to delete this element?");
				confirm.show();
				break;
			case setName:
				new Thread(() -> {
					String name = JOptionPane.showInputDialog(null, "Enter the reference's new name", "Reference name editor", JOptionPane.PLAIN_MESSAGE);
					
					if(name != null) {
						Project project = this.getAppManager().getWorkspace().getCurrentProject();
						
						if(project != null) {
							MessageEvent message = project.getDataManager().refactorParameterName(this.spec, this.id, this.dataID, name);
							this.getAppManager().getEventDispatcher().fire(message);
							
							this.reload();
							this.show();
						}
					}
				}).start();
				
				break;
			case setType:
				Menu selector = new ReferenceParameterDataTypeSelector(this.getAppManager(), this, this.spec, this.id, this.dataID);
				selector.show();
				break;
			case setDefault:
				TypeEditor editor = MainMenus.getTypeEditor();
				editor.setTarget(this.data);
				editor.show();
				break;
			default: 
				super.onClick(element);
		}
	}
	
	private void delete() {
		Reference ref = (Reference) this.spec.getElements().get(this.id);
		ref.getNeededParameters().remove(this.dataID);
		
		this.getAppManager().getEventDispatcher().fire(new MessageEvent("Parameter successfully removed", MessageType.SUCCESS));
		
		this.parent.load();
		this.parent.show();
	}

	public String getDescription() {
		return "Reference parameter editor for " + this.description;
	}
}
