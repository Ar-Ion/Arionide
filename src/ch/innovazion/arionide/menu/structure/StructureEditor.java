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
package ch.innovazion.arionide.menu.structure;

import javax.swing.JOptionPane;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.menu.Confirm;
import ch.innovazion.arionide.menu.DisplayUtils;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.menu.structure.inheritance.InheritanceMenu;
import ch.innovazion.arionide.menu.structure.specification.SpecificationEditor;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.StructureMeta;
import ch.innovazion.arionide.project.managers.DataManager;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.core.TeleportInfo;

public class StructureEditor extends Menu {
	
	private static final String abstractify = "Abstractify";
	private static final String language = "Language";
	private static final String inheritance = "Inherit";
	private static final String specification = "Specify";
	private static final String go = "Go";
	private static final String name = "Name";
	private static final String color = "Color";
	private static final String delete = "Delete";

	private final Coloring coloring;
	private final Confirm confirmDelete;
	private final Confirm confirmAbstractify;
	private final Menu languageSelection;
	private final Menu inheritanceMenu;
	private final Menu specificationMenu;
	
	private MenuDescription description;

	public StructureEditor(StructureBrowser parent) {
		super(parent, abstractify, language, inheritance, specification, go, name, color, delete);
		this.coloring = new Coloring(this);
		this.confirmDelete = new Confirm(this, this::delete, "Are you sure you want to delete the structure '$name'?");
		this.confirmAbstractify = new Confirm(this, this::abstractify, "Abstractifying '$name' will result in the loss of the whole code inside '$name' and its substructures. Are you sure you want to proceed?");
		this.languageSelection = new LanguageSelection(this);
		this.inheritanceMenu = new InheritanceMenu(this);
		this.specificationMenu = new SpecificationEditor(this);
	}
	
	public void show() {
		super.show();
		
		setMenuCursor(4);
		
		StructureMeta meta = getProject().getStorage().getStructureMeta().get(getTarget().getID());
		
		if(meta != null) {			
			description = DisplayUtils.fullDescription(meta);
		} else {
			// When the structure is deleted
			back();
		}
	}

	protected void onClick(String element) {
		super.onClick(element);
				
		AppManager manager = getAppManager();
		
		switch(element) {
			case abstractify:
				confirmAbstractify.show();
				break;
			case language:
				if(Utils.contains(getProject().getStorage().getHierarchy(), getTarget().getID(), HierarchyElement::getID)) {
					if(getProject().getStorage().getCode().get(getTarget().getID()).isAbstract()) {
						manager.getEventDispatcher().fire(new MessageEvent("There is no point setting a language on an abstract structure", MessageType.ERROR));
					} else {
						languageSelection.show();
					}
				} else {
					manager.getEventDispatcher().fire(new MessageEvent("Custom languages can only be set on root structures", MessageType.ERROR));
				}
					
				break;
			case inheritance:
				inheritanceMenu.show();
				break;
			case specification:
				specificationMenu.show();
				break;
			case go:
				getAppManager().getCoreRenderer().teleport(new TeleportInfo(getTarget().getID(), -1));
				break;
			case name:
				new Thread(() -> {
					String name = JOptionPane.showInputDialog(null, "Please enter the new name of the structure", "New name", JOptionPane.PLAIN_MESSAGE);
					
					if(name != null) {
						Event message = getProject().getDataManager().setName(getTarget().getID(), name);
						manager.getEventDispatcher().fire(message);
						manager.getCoreRenderer().requestFullReconstruction();
						show();
					}
				}).start();
				break;
			case color:
				coloring.show();
				break;
			case delete:
				confirmDelete.show();
		}
	}
	
	private void delete() {
		AppManager manager = getAppManager();
		DataManager dataManager = getProject().getDataManager();
		
		Event message = dataManager.deleteStructure(getTarget().getID());
		
		manager.getCoreRenderer().requestFullReconstruction();
		manager.getCoreRenderer().teleport(new TeleportInfo(dataManager.getHostStack().getCurrent(), -1));
		manager.getEventDispatcher().fire(message);
	}
	
	private void abstractify() {
		AppManager manager = getAppManager();
		DataManager dataManager = getProject().getDataManager();
		
		Event message = dataManager.abstractifyStructure(getTarget().getID());
		
		manager.getCoreRenderer().getCodeGeometry().requestReconstruction();
		manager.getCoreRenderer().teleport(new TeleportInfo(dataManager.getHostStack().getCurrent(), -1));
		manager.getEventDispatcher().fire(message);
	}
	
	public MenuDescription getDescription() {
		return description;
	}
}