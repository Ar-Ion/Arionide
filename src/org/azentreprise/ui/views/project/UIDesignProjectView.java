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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.ui.views.project;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.KeyEvent;

import org.azentreprise.Projects;
import org.azentreprise.ui.Style;
import org.azentreprise.ui.UIMain;
import org.azentreprise.ui.components.UIButton;
import org.azentreprise.ui.components.UIButton.UIButtonClickListener;
import org.azentreprise.ui.components.UILabel;
import org.azentreprise.ui.views.UIView;
import org.azentreprise.ui.views.project.editors.UIStyleEditorProjectView;

public class UIDesignProjectView extends UIProjectView implements UIButtonClickListener {
	public UIDesignProjectView(UIView parent, Frame rootComponent) {
		super(parent, rootComponent);
		
		this.add(new UILabel(this, 0.7f, 0.05f, 0.95f, 0.15f, "Design").alterFont(Font.BOLD));
		
		for(int i = 0; i < 8; i++) {
			if(Projects.getCurrentProject().styles.size() > i) {
				Style style = Projects.getCurrentProject().styles.get(i);
				this.add(new UIButton(this, 0.72f, 0.15f + i * 0.067f, 0.93f, 0.20f + i * 0.067f, style.getName()).setHandler(this, style));
			} else break;
		}
	}
	
	public void onClick(Object... signals) {
		super.onClick(signals);
		
		if(signals[0] instanceof Style) {
			UIStyleEditorProjectView.setTarget((Style) signals[0]);
			UIMain.show(new UIStyleEditorProjectView(this, this.getRootComponent()));
		}
	}
	
	public void keyPressed(KeyEvent event) {		
		if(event.getKeyCode() == KeyEvent.VK_SHIFT || event.getKeyCode() == KeyEvent.VK_ALT) {
			this.back();
		} else {
			super.keyPressed(event);
		}
	}
}
