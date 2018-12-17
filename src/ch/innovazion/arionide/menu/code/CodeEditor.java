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
package ch.innovazion.arionide.menu.code;

import java.util.List;

import javax.swing.JOptionPane;

import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.SpecificationElement;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.project.StructureMeta;
import ch.innovazion.arionide.ui.AppManager;

public class CodeEditor extends Menu {

	private static final String back = "Back";
	private static final String description = "Set description";
	private static final String delete = "Delete";
	private static final String append = "Append";
	
	private final Menu parent;
	private final CodeAppender appender;
	
	private int instructionID;
	private HierarchyElement instruction;
	private StructureMeta instructionMeta;
	
	public CodeEditor(AppManager manager, Menu parent) {
		super(manager);
		
		this.parent = parent;
		this.appender = new CodeAppender(manager, parent);
		
		this.getElements().add(back);
		this.getElements().add(description);
		this.getElements().add(delete);
		this.getElements().add(append);
	}
	
	public void setTargetInstruction(int id) {
		Project project = this.getAppManager().getWorkspace().getCurrentProject();
		
		if(project != null) {
			int hostStructureID = project.getDataManager().getHostStack().getCurrent();
						
			this.instructionID = id;
			this.instruction = project.getStorage().getCode().get(hostStructureID).getChain().get(id);
			this.instructionMeta = project.getStorage().getStructureMeta().get(this.instruction.getID());
									
			assert this.instructionMeta != null;
			
			List<String> list = this.getElements().subList(4, this.getElements().size());
			list.clear();
			
			for(SpecificationElement element : this.instructionMeta.getSpecification().getElements()) {
				list.add(element.getName());
			}
		}
		
		this.setMenuCursor(3);
	}
		
	public void onClick(String element) {
		AppManager manager = this.getAppManager();
		Project project = manager.getWorkspace().getCurrentProject();
		
		if(element == append) {
			this.appender.setAppenderPosition(this.instructionID);
			this.appender.show();
		} else if(element == delete) {
			if(this.instructionID > 0) {
				MessageEvent message = project.getDataManager().getCodeManager().deleteCode(this.instructionID);
				manager.getEventDispatcher().fire(message);
				manager.getCoreRenderer().getCodeGeometry().requestReconstruction();
				this.parent.select(this.instructionID - 1);
				this.parent.show();
			} else {
				manager.getEventDispatcher().fire(new MessageEvent("You can't delete the entry instruction", MessageType.ERROR));
			}
		} else if(element == description) {
			new Thread(() -> {
				String name = JOptionPane.showInputDialog(null, "Please enter the description of the instruction", "Description", JOptionPane.PLAIN_MESSAGE);
				
				if(name != null) {
					MessageEvent message = project.getDataManager().setName(this.instruction.getID(), name);
					manager.getEventDispatcher().fire(message);
					manager.getCoreRenderer().getCodeGeometry().requestReconstruction();
				}
			}).start();
		} else if(element == back) {
			this.parent.show();
		}
	}
	
	public String getDescription() {
		return this.getInstructionName() + " [" + this.instructionMeta.getSpecification() + "]";
	}
	
	private String getInstructionName() {
		if(this.instructionMeta.getName().equals("?") && this.getAppManager().getWorkspace().getCurrentProject() != null) {
			int realID = Integer.parseInt(this.instructionMeta.getComment().substring(5));
			return this.getAppManager().getWorkspace().getCurrentProject().getStorage().getStructureMeta().get(realID).getName();
		} else {
			return this.instructionMeta.getName();
		}
	}
}
