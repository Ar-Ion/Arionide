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
package ch.innovazion.arionide.menu.structure.specification;

import javax.swing.JOptionPane;

import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.lang.Specification;
import ch.innovazion.arionide.lang.SpecificationElement;
import ch.innovazion.arionide.menu.Confirm;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.project.Project;

public abstract class SpecificationElementEditor extends Menu {
	
	private static final String delete = "Delete";
	private static final String setName = "Set name";

	private int id;
	private Specification specification;
	private SpecificationElement element;
	
	protected SpecificationElementEditor(Menu parent) {
		super(parent, delete, setName);
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
			case delete:
				Menu confirm = new Confirm(this, this::delete, "Do you really want to delete this element?");
				confirm.show();
				break;
			case setName:
				new Thread(() -> {
					String name = JOptionPane.showInputDialog(null, "Enter the specification element's new name", "Specification name editor", JOptionPane.PLAIN_MESSAGE);
					
					if(name != null) {
						Project project = this.getAppManager().getWorkspace().getCurrentProject();
						
						if(project != null) {
							MessageEvent message = project.getDataManager().getSpecificationManager().refactorName(this.specification, this.id, name);
							this.getAppManager().getEventDispatcher().fire(message);
						}
						
						this.setTarget(this.specification, this.id);
					}
				}).start();
		}
	}
	
	public abstract void delete();
}