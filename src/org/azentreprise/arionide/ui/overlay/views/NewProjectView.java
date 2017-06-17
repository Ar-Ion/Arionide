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
import org.azentreprise.arionide.ui.overlay.Component;
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
		
		layoutManager.register(this, null, 0.1f, 0.1f, 0.9f, 0.9f);
		
		this.setBorderColor(new Color(0xCAFE));
				
		this.add(new Label(this, "New project"), 0, 0.05f, 1.0f, 0.3f);
		
		this.add(this.projectName, 0.1f, 0.4f, 0.9f, 0.6f);
		
		this.add(new Button(this, "Create").setSignal("create"), 0.1f, 0.8f, 0.45f, 0.9f);
		this.add(new Button(this, "Cancel").setSignal("cancel"), 0.55f, 0.8f, 0.9f, 0.9f);
	}
	
	public void show() {
		super.show();
		this.setupFocusCycle(1, 2, 3);
	}

	public void onClick(Object... signals) {
		switch((String) signals[0]) {
			case "cancel":
				this.openView(Views.main);
				break;
			case "create":
				Component text = this.get(1);
				
				assert text instanceof Text;
				
				String name = ((Text) text).getText();
				
				if(!name.isEmpty()) {
					this.getAppManager().getWorkspace().createProject(((Text) this.get(1)).toString());
					this.openView(Views.code);
				}
				
				break;
		}
	}
}