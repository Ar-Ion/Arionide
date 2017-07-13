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
package org.azentreprise.ui.views.project;

import java.awt.Font;
import java.awt.Frame;

import org.azentreprise.Debug;
import org.azentreprise.Projects;
import org.azentreprise.configuration.Project;
import org.azentreprise.ui.components.UIButton;
import org.azentreprise.ui.components.UIButton.UIButtonClickListener;
import org.azentreprise.ui.components.UILabel;
import org.azentreprise.ui.views.UIView;

public class UISelectionProjectView extends UIProjectView implements UIButtonClickListener {
	public UISelectionProjectView(UIView parent, Frame rootComponent) {
		super(parent, rootComponent);
				
		this.add(new UILabel(this, 0.7f, 0.05f, 0.95f, 0.15f, "Selection").alterFont(Font.BOLD));
		this.add(new UIButton(this, 0.72f, 0.15f, 0.93f, 0.2f, "Copy").setHandler(this, "copy"));
		this.add(new UIButton(this, 0.72f, 0.23f, 0.93f, 0.28f, "Cut").setHandler(this, "cut"));
		this.add(new UIButton(this, 0.72f, 0.31f, 0.93f, 0.36f, "Delete").setHandler(this, "delete"));
		this.add(new UIButton(this, 0.72f, 0.39f, 0.93f, 0.44f, "New structure").setHandler(this, "new"));
		this.add(new UIButton(this, 0.72f, 0.47f, 0.93f, 0.52f, "Back").setHandler(this, "back"));
		this.add(new UIButton(this, 0.72f, 0.55f, 0.93f, 0.6f, "<Undefined>").setHandler(this, "..."));
		this.add(new UIButton(this, 0.72f, 0.63f, 0.93f, 0.68f, "<Undefined>").setHandler(this, "..."));
	}

	public void onClick(Object... signals) {
		super.onClick(signals);
		
		switch((String) signals[0]) {
			case "copy":
				break;
			case "cut":
				break;
			case "delete":
				this.delete();
				break;
			case "new":
				break;
			case "back":
				this.back();
		}
	}
	
	private void delete() {		
		for(String id : this.getSelection().keySet()) {
			
			Debug.info("Deleting " + id);
			
			Project project = Projects.getCurrentProject();
			
			project.objects.remove(project.objectMapping.remove(id));
						
			UIProjectView.simpleRenderer.reset(project);
			UIProjectView.complexRenderer.reset(project);
		}
		
		this.getSelection().clear();
		
		this.back();
	}
}

