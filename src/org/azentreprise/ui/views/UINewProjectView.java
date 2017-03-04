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
package org.azentreprise.ui.views;

import java.awt.Color;
import java.awt.Frame;

import org.azentreprise.Debug;
import org.azentreprise.Projects;
import org.azentreprise.configuration.Project;
import org.azentreprise.ui.UIMain;
import org.azentreprise.ui.components.UIButton;
import org.azentreprise.ui.components.UIButton.UIButtonClickListener;
import org.azentreprise.ui.components.UILabel;
import org.azentreprise.ui.components.UIText;
import org.azentreprise.ui.views.project.UICodeProjectView;

public class UINewProjectView extends UIView implements UIButtonClickListener {
	public UINewProjectView(UIView parent, Frame rootComponent) {
		super(parent, rootComponent, 0.1f, 0.1f, 0.9f, 0.9f);
		
		this.setBackground(new Color(63, 63, 63, 127));
		this.setBorderColor(new Color(0xCAFE));
				
		this.add(new UILabel(this, 0, 0.05f, 1.0f, 0.2f, "New project"));
		
		this.add(new UIText(this, 0.1f, 0.3f, 0.9f, 0.4f, "Project name"));
		this.add(new UIText(this, 0.1f, 0.45f, 0.9f, 0.55f, "Owner"));
		this.add(new UIText(this, 0.1f, 0.60f, 0.9f, 0.70f, "Description"));
		
		this.add(new UIButton(this, 0.1f, 0.8f, 0.45f, 0.9f, "Create").setHandler(this, "create"));
		this.add(new UIButton(this, 0.55f, 0.8f, 0.9f, 0.9f, "Cancel").setHandler(this, "cancel"));
		
		this.setFocus(1);
	}

	public void onClick(Object... signals) {
		switch((String) signals[0]) {
			case "cancel":
				this.back();
				break;
			case "create":
				String[] fields = UIText.fetchUserData(this);
								
				if(fields[0].length() > 0 && fields[1].length() > 0 && fields[2].length() > 0) {
					try {
						Project project = Projects.createProject(fields[0]);
						
						project.name = fields[0];
						project.owner = fields[1];
						project.description = fields[2];
						
						project.save();
						
						UIMainView.updatePageID();
						
						UIMain.show(new UICodeProjectView(this, this.getRootComponent()));
					} catch (Exception exception) {
						Debug.exception(exception);
					}
				}
				
				break;
		}
	}
}
