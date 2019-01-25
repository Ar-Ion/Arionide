/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018, 2019 AZEntreprise Corporation. All rights reserved.
 *
 * Arionide is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Arionide is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more dretails.
 * You should have received a copy of the GNU General Public License
 * along with Arionide.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package ch.innovazion.arionide.menu.structure.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.lang.Data;
import ch.innovazion.arionide.lang.Reference;
import ch.innovazion.arionide.lang.Specification;
import ch.innovazion.arionide.lang.SpecificationElement;
import ch.innovazion.arionide.menu.DisplayUtils;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.menu.structure.specification.data.DataEditor;
import ch.innovazion.arionide.menu.structure.specification.data.TypeSelector;
import ch.innovazion.arionide.menu.structure.specification.reference.ReferenceEditor;
import ch.innovazion.arionide.project.Storage;
import ch.innovazion.arionide.project.StructureMeta;

public class SpecificationEditor extends Menu {
		
	private final DataEditor dataEditor;
	private final ReferenceEditor referenceEditor;
	private final TypeSelector typeSelector;
	
	private Specification specification;
	private MenuDescription description;
	
	public SpecificationEditor(Menu parent) {
		super(parent);
		
		this.dataEditor = new DataEditor(this);
		this.referenceEditor = new ReferenceEditor(this);
		this.typeSelector = new TypeSelector(this);
	}

	public void show() {		
		Storage storage = this.getAppManager().getWorkspace().getCurrentProject().getStorage();
		
		specification = storage.getStructureMeta().get(getTarget().getID()).getSpecification();
		
		if(specification != null) {
			List<String> elements = getElements();
			
			elements.clear();
			elements.add("Add data");
			elements.add("Add reference");
			elements.addAll(specification.getElements().stream().map(SpecificationElement::getName).collect(Collectors.toList()));
		}
		
		StructureMeta meta = getProject().getStorage().getStructureMeta().get(getTarget().getID());
		
		if(meta != null) {			
			description = DisplayUtils.fullDescription(meta);
		} else {
			// When the structure is deleted
			back();
		}
		
		super.show();
	}
	
	public void onClick(int id) {
		if(id == 0) {
			new Thread(() -> {
				String name = JOptionPane.showInputDialog(null, "Enter the name for the new data", "New data", JOptionPane.PLAIN_MESSAGE);
				
				if(name != null) {
					SpecificationElement element = new Data(name, null, -1);
					Event event = getProject().getDataManager().getSpecificationManager().addElement(specification, element);
					getAppManager().getEventDispatcher().fire(event);
					
					typeSelector.setTarget(specification, specification.getElements().size() - 1);
					typeSelector.show();
				}
			}).start();
		} else if(id == 1) {
			new Thread(() -> {
				String name = JOptionPane.showInputDialog(null, "Enter the name for the new reference", "New reference", JOptionPane.PLAIN_MESSAGE);
				
				if(name != null) {
					SpecificationElement element = new Reference(name, null, new ArrayList<>(), new ArrayList<>());
					Event event = getProject().getDataManager().getSpecificationManager().addElement(specification, element);
					getAppManager().getEventDispatcher().fire(event);
					
					show();
				}
			}).start();
		} else {
			SpecificationElement element = specification.getElements().get(id - 2);
			
			if(element instanceof Data) {
				dataEditor.setTarget(specification, id - 2);
				dataEditor.show();	
			} else if(element instanceof Reference) {
				referenceEditor.setTarget(specification, id - 2);
				referenceEditor.show();
			} else {
				throw new RuntimeException("Strange object found");
			}
		}
	}
	
	public MenuDescription getMenuDescription() {
		return description;
	}
}