/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
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

import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.lang.symbols.Specification;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.menu.Confirm;
import ch.innovazion.arionide.menu.Menu;

public abstract class SpecificationElementEditor extends Menu {
	
	private static final String delete = "Delete";
	private static final String setName = "Set name";

	private final Menu confirm;
	
	private int id;
	private Specification specification;
	private Parameter element;
	
	protected SpecificationElementEditor(Menu parent) {
		super(parent, delete, setName);
		this.confirm = new Confirm(parent, this::delete, "Do you really want to delete this element?");
	}

	protected void setTarget(Specification specification, int id) {
		int size = specification.getElements().size();
		
		if(id < size) {
			this.id = id;
			this.specification = specification;
			this.element = specification.getElements().get(id);
		} else if(size > 0){
			setTarget(specification, size - 1);
		} else {
			back();
		}
	}
	
	public void show() {
		setTarget(specification, id); // reload
		super.show();
	}

	protected Parameter getElement() {
		return element;
	}
	
	protected Specification getSpecification() {
		return specification;
	}
	
	protected int getElementID() {
		return id;
	}
	
	public void onClick(String element) {
		switch(element) {
			case delete:
				confirm.show();
				break;
			case setName:
				new Thread(() -> {
					String name = JOptionPane.showInputDialog(null, "Enter the new name of this specification element", "Element name", JOptionPane.PLAIN_MESSAGE);
					
					if(name != null) {
						Event message = getProject().getDataManager().getSpecificationManager().refactorName(specification, id, name);
						getAppManager().getEventDispatcher().fire(message);
						
						setTarget(specification, id);
					}
				}).start();
		}
	}
	
	protected abstract void delete();
}