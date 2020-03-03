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
package ch.innovazion.arionide.menu.structure.specification.reference;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import ch.innovazion.arionide.lang.Data;
import ch.innovazion.arionide.lang.symbols.Reference;
import ch.innovazion.arionide.lang.symbols.Specification;
import ch.innovazion.arionide.lang.symbols.SpecificationElement;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.ui.AppManager;

public class ReferenceParameters extends Menu {
	
	private final Menu parent;
	private final Specification spec;
	private final int id;
	private final List<SpecificationElement> parameters;
	private final ReferenceParameterDataEditor dataEditor;
	
	protected ReferenceParameters(AppManager manager, Menu parent, Specification spec, int id, List<SpecificationElement> parameters) {
		super(manager, "Back", "Add data", "Add reference");
		
		this.parent = parent;
		this.spec = spec;
		this.id = id;
		this.parameters = parameters;
		this.dataEditor = new ReferenceParameterDataEditor(manager, this);
		
		this.load();
		
		this.setMenuCursor(1);
	}
	
	protected void load() {
		this.getElements().subList(2, this.getElements().size()).clear();
		
		for(SpecificationElement data : this.parameters) {
			this.getElements().add(data.getName());
		}
	}
	
	public void onClick(int id) {
		if(id == 0) {
			this.parent.show();
		} else if(id == 1) {
			new Thread(() -> {
				String name = JOptionPane.showInputDialog(null, "Enter the name for the new parameter", "New data parameter", JOptionPane.PLAIN_MESSAGE);
				
				if(name != null) {
					Data data = new Data(name, null, -1);
					this.getAppManager().getWorkspace().getCurrentProject().getDataManager().getSpecificationManager().addParam(parameters, data);
					
					Menu selector = new ReferenceParameterDataTypeSelector(this.getAppManager(), this, this.spec, this.id, this.parameters.size() - 1);
					selector.show();
				}
			}).start();
		} else if(id == 2) {
			new Thread(() -> {
				String name = JOptionPane.showInputDialog(null, "Enter the name for the new parameter", "New reference parameter", JOptionPane.PLAIN_MESSAGE);
				
				if(name != null) {
					Reference ref = new Reference(name, null, new ArrayList<>(), new ArrayList<>());
					this.getAppManager().getWorkspace().getCurrentProject().getDataManager().getSpecificationManager().addParam(parameters, ref);

					this.load();
					this.show();
				}
			}).start();
		} else {
			id -= 2;
			
			SpecificationElement element = this.parameters.get(id);
			
			if(element instanceof Data) {
				this.dataEditor.setTarget(this.spec, this.id, id, (Data) element);
				this.dataEditor.show();
			} else if(element instanceof Reference) {
				// TODO
			}
		}
	}
}