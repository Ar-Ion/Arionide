/*******************************************************************************
 * This file is part of ArionIDE.
 *
 * ArionIDE is an IDE whose purpose is to build a language from assembly. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
 *
 * ArionIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArionIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with ArionIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.ui.views.project.editors;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.KeyEvent;

import org.azentreprise.Projects;
import org.azentreprise.configuration.Project;
import org.azentreprise.lang.AutoCompletionType;
import org.azentreprise.lang.ISAMapping;
import org.azentreprise.lang.InstructionInfo;
import org.azentreprise.lang.ObjectType;
import org.azentreprise.lang.isa.FuncParamType;
import org.azentreprise.ui.components.UIAutoCompletion;
import org.azentreprise.ui.components.UILabel;
import org.azentreprise.ui.components.UIText;
import org.azentreprise.ui.views.UIView;
import org.azentreprise.ui.views.project.UIProjectView;

public class UIAutoCompletionEditorProjectView extends UIEditorProjectView {
		
	private UIAutoCompletion[] params;
	
	public UIAutoCompletionEditorProjectView(UIView parent, Frame rootComponent) {
		super(parent, rootComponent);
	}
	
	public void initComponents() {
		this.add(new UILabel(this, 0.71f, 0.05f, 0.95f, 0.15f, "Instruction").alterFont(Font.BOLD));

		this.add(new UIAutoCompletion(this, 0.72f, 0.15f, 0.93f, 0.25f, "Native function", AutoCompletionType.NATIVE));
		
		this.params = new UIAutoCompletion[3];
		
		this.params[0] = new UIAutoCompletion(this, 0.72f, 0.28f, 0.93f, 0.35f, "First parameter", AutoCompletionType.NONE);
		this.params[1] = new UIAutoCompletion(this, 0.72f, 0.38f, 0.93f, 0.45f, "Second parameter", AutoCompletionType.NONE);
		this.params[2] = new UIAutoCompletion(this, 0.72f, 0.48f, 0.93f, 0.55f, "Third parameter", AutoCompletionType.NONE);
		
		for(UIAutoCompletion param : this.params) {
			this.add(param);
		}

		this.setFocus(1);
	}
	
	public void keyReleased(KeyEvent event) {
		super.keyReleased(event);
		
		if(event.getKeyCode() == KeyEvent.VK_TAB) {
			
			String id = UIText.fetchUserData(this)[0];
			
			InstructionInfo info = ISAMapping.getNative(id);

			for(org.azentreprise.lang.Object object : Projects.getCurrentProject().objects) {
				if(object.getID().startsWith("Native." + id)) {
					for(int i = 0; i < this.params.length; i++) {
						try {
							this.params[i].setPlaceholder(object.getArgs()[i]);
							this.params[i].setAutoCompletionType(info.getParamTypes()[i] == FuncParamType.FUNCNAME ? AutoCompletionType.FUNCTION : (info.getParamTypes()[i] == FuncParamType.VARNAME  ? AutoCompletionType.VARIABLE : AutoCompletionType.NONE));
							this.params[i].setDisabled(false);
						} catch(ArrayIndexOutOfBoundsException | NullPointerException e) {
							this.params[i].setPlaceholder("Undefined");
							this.params[i].setDisabled(true);
						}
					}
					
					return;
				}
			}
		}
	}

	public boolean validate() {
		
		String[] data = UIText.fetchUserData(this);
 		
		if(data.length > 0 && ISAMapping.getNative(data[0]) != null && data.length - 1 == ISAMapping.getNative(data[0]).getParamTypes().length) {
			Project project = Projects.getCurrentProject();
			
			String id = project.current + "." + String.join(":", data);
			
			org.azentreprise.lang.Object object = new org.azentreprise.lang.Object(id, Projects.getCurrentProject().theCode.getNext(), 0, ObjectType.NATIVE, true);
			
			project.objects.add(object);
			project.objectMapping.put(id, object);
			
			UIProjectView.simpleRenderer.reset(project);
			UIProjectView.complexRenderer.reset(project);
			
			return true;
		} else {
			return false;
		}
	}
}
