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
package ch.innovazion.arionide.menu.edition;

import javax.swing.JOptionPane;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.menu.Confirm;
import ch.innovazion.arionide.menu.MainMenus;
import ch.innovazion.arionide.menu.SpecificMenu;
import ch.innovazion.arionide.menu.edition.specification.SpecificationEditor;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.core.TeleportInfo;

public class StructureEditor extends SpecificMenu {
	
	private static final String language = "Set language";
	private static final String inheritance = "Inherit";
	private static final String specification = "Specify";
	private static final String go = "Go";
	private static final String name = "Name";
	private static final String color = "Color";
	private static final String delete = "Delete";
	private static final String close = "Close";

	private final SpecificMenu languageSelection;
	private final SpecificMenu inheritanceMenu;
	private final SpecificMenu specificationMenu;
	private final Coloring coloring;
	private final Confirm confirmDelete;

	public StructureEditor(AppManager manager) {
		super(manager, language, inheritance, specification, go, name, color, delete, close);
		this.coloring = new Coloring(manager);
		this.confirmDelete = new Confirm(manager, this, this::delete, "Are you sure you want to delete the structure '$name'?");
		this.languageSelection = new LanguageSelection(manager);
		this.inheritanceMenu = new InheritanceMenu(manager);
		this.specificationMenu = new SpecificationEditor(manager);
		
		this.setMenuCursor(3);
	}
	
	public Coloring getColoring() {
		return this.coloring;
	}

	protected void onClick(String element) {
		assert this.getCurrent() != null;
		
		this.setMenuCursor(3);
		
		AppManager manager = this.getAppManager();
		
		switch(element) {
			case language:
				Project theProject = manager.getWorkspace().getCurrentProject();
				
				if(theProject != null) {
					if(Utils.contains(theProject.getStorage().getHierarchy(), this.getCurrent().getID(), HierarchyElement::getID)) {
						this.languageSelection.setCurrent(this.getCurrent());
						this.languageSelection.show();
					} else {
						manager.getEventDispatcher().fire(new MessageEvent("This structure isn't directly in the space", MessageType.ERROR));
					}
				}
				break;
			case inheritance:
				this.inheritanceMenu.setCurrent(this.getCurrent());
				this.inheritanceMenu.show();
				break;
			case specification:
				this.specificationMenu.setCurrent(this.getCurrent());
				this.specificationMenu.show();
				break;
			case go:
				this.go();
				break;
			case name:
				new Thread(() -> {
					String name = JOptionPane.showInputDialog(null, "Please enter the new name of the structure", "New name", JOptionPane.PLAIN_MESSAGE);
					
					if(name != null) {
						Project project = manager.getWorkspace().getCurrentProject();
						MessageEvent message = project.getDataManager().setName(this.getCurrent().getID(), name);
						manager.getEventDispatcher().fire(message);
						manager.getCoreRenderer().getStructuresGeometry().requestReconstruction();
					}
				}).start();
				break;
			case color:
				this.coloring.setCurrent(this.getCurrent());
				this.coloring.show();
				break;
			case delete:
				this.confirmDelete.setCurrent(this.getCurrent());
				this.confirmDelete.show();
				break;
			case close:
				MainMenus.getStructureList().show();
		}
	}
	
	private void delete() {
		AppManager manager = this.getAppManager();
		Project project = manager.getWorkspace().getCurrentProject();
		
		MessageEvent message = project.getDataManager().deleteStructure(this.getCurrent().getID());
		
		manager.getCoreRenderer().getStructuresGeometry().requestReconstruction();
		manager.getEventDispatcher().fire(message);
	}
	
	private void go() {
		this.getAppManager().getCoreRenderer().teleport(new TeleportInfo(this.getCurrent().getID(), -1));

	}
	
	public String getDescription() {
		return "Structure edition for " + super.getDescription();
	}
}