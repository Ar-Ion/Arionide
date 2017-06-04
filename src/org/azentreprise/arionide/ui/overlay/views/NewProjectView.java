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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'Arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.arionide.ui.overlay.views;

import java.awt.Color;

import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.arionide.ui.overlay.Views;
import org.azentreprise.arionide.ui.overlay.components.Button;
import org.azentreprise.arionide.ui.overlay.components.ClickListener;
import org.azentreprise.arionide.ui.overlay.components.Label;
import org.azentreprise.arionide.ui.overlay.components.Text;

public class NewProjectView extends View implements ClickListener {
	
	private final Text projectName = new Text(this, "Project name");
	
	public NewProjectView(AppManager appManager, LayoutManager layoutManager) {
		super(appManager, layoutManager);
		
		this.setBorderColor(new Color(0xCAFE));
				
		this.add(new Label(this, "New project"), 0, 0.05f, 1.0f, 0.3f);
		
		this.add(this.projectName, 0.1f, 0.4f, 0.9f, 0.6f);
		
		this.add(new Button(this, "Create").setHandler(this, "create"), 0.1f, 0.8f, 0.45f, 0.9f);
		this.add(new Button(this, "Cancel").setHandler(this, "cancel"), 0.55f, 0.8f, 0.9f, 0.9f);
		
		this.getAppManager().getFocusManager().setupCycle(1, 2, 3);
		this.getAppManager().getFocusManager().request(1);
	}

	public void onClick(Object... signals) {
		switch((String) signals[0]) {
			case "cancel":
				this.openView(Views.main, true);
				break;
			case "create":
				/*String[] fields = UIText.fetchUserData(this);
								
				if(fields[0].length() > 0 && fields[1].length() > 0 && fields[2].length() > 0) {
					try {
						Project project = Projects.createProject(fields[0]);
						
						project.name = fields[0];
						project.owner = fields[1];
						project.description = fields[2];
						
						project.save();
						
						UIMainView.updatePageID();
						
						//UIMain.show(new UICodeProjectView(this, this.getRootComponent()));
					} catch (Exception exception) {
						Debug.exception(exception);
					}
				}*/
				
				break;
		}
	}
}