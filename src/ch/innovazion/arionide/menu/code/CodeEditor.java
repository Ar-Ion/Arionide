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
package ch.innovazion.arionide.menu.code;

import java.util.List;

import javax.swing.JOptionPane;

import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.menu.Browser;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.project.StructureMeta;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.ApplicationTints;

public class CodeEditor extends Menu {

	private static final String comment = "Comment";
	private static final String delete = "Remove";
	private static final String append = "Append";
	
	private final CodeAppender appender;
	private MenuDescription description;
	
	public CodeEditor(Browser parent) {
		super(parent, append, comment, delete);
		this.appender = new CodeAppender(parent);
	}
	
	public void show() {
		super.show();
		
		StructureMeta meta = getProject().getStorage().getStructureMeta().get(getTarget().getID());
		List<Parameter> elements = meta.getSpecification().getElements();
		
		description = new MenuDescription(ApplicationTints.MENU_INFO_INACTIVE_COLOR, 1.0f);
		
		String name = meta.getName();
		
		if(!meta.getComment().equals("?")) {
			name += " (" + meta.getComment() + ")";
		}
		
		description.add(name);
		
		for(Parameter element : elements) {
			description.add(element.toString());
		}

		description.setHighlight(0);
		description.setColor(0, ApplicationTints.MENU_INFO_DEFAULT_COLOR);
	}
	
	public void onClick(String element) {
		AppManager manager = getAppManager();
		Project project = getProject();
		
		if(element == append) {
			appender.show();
		} else if(element == delete) {
			MessageEvent message = project.getDataManager().getCodeManager().deleteCode(getTarget().getID());
			manager.getEventDispatcher().fire(message);
			manager.getCoreOrchestrator().getController().getCodeGeometry().requestReconstruction();
			back();
		} else if(element == comment) {
			new Thread(() -> {
				String name = JOptionPane.showInputDialog(null, "Please describe the purpose of this instruction", "Description", JOptionPane.PLAIN_MESSAGE);
				
				if(name != null) {
					MessageEvent message = project.getDataManager().setComment(getTarget().getID(), name);
					manager.getEventDispatcher().fire(message);
					manager.getCoreOrchestrator().getController().getCodeGeometry().requestReconstruction();
					back();
				}
			}).start();
		}
	}
	
	public MenuDescription getDescription() {
		return description;
	}
}
