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
package org.azentreprise.arionide.ui.menu.edition.specification;

import javax.swing.JOptionPane;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.lang.Specification;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Confirm;
import org.azentreprise.arionide.ui.menu.Menu;
import org.azentreprise.arionide.ui.menu.SpecificMenu;

public abstract class SpecificationElementEditor extends Menu {
	
	private static final String back = "Back";
	private static final String delete = "Delete";
	private static final String setName = "Set name";

	private final SpecificMenu parent;
	private int id;
	private Specification specification;
	private SpecificationElement element;
	
	protected SpecificationElementEditor(AppManager manager, SpecificMenu parent) {
		super(manager, back, delete, setName);
		this.parent = parent;
	}

	protected void setTarget(Specification specification, int id) {
		this.id = id;
		this.specification = specification;
		this.element = specification.getElements().get(id);
	}
	
	public void show() {
		super.show();
		this.setTarget(this.specification, this.id);
	}
	
	public SpecificationElement getElement() {
		return this.element;
	}
	
	public Specification getSpecification() {
		return this.specification;
	}
	
	public int getElementID() {
		return this.id;
	}
	
	public void onClick(String element) {
		switch(element) {
			case back:
				this.getParent().reload();
				this.getParent().show();
				break;
			case delete:
				Menu confirm = new Confirm(this.getAppManager(), this, this::delete, "Do you really want to delete this element?");
				confirm.show();
				break;
			case setName:
				new Thread(() -> {
					String name = JOptionPane.showInputDialog(null, "Enter the specification element's new name", "Specification name editor", JOptionPane.PLAIN_MESSAGE);
					
					if(name != null) {
						Project project = this.getAppManager().getWorkspace().getCurrentProject();
						
						if(project != null) {
							MessageEvent message = project.getDataManager().refactorSpecificationName(this.specification, this.id, name);
							this.getAppManager().getEventDispatcher().fire(message);
						}
						
						this.setTarget(this.specification, this.id);
					}
				}).start();
		}
	}
	
	public SpecificMenu getParent() {
		return this.parent;
	}
	
	public abstract void delete();
	public abstract String getDescription();
}