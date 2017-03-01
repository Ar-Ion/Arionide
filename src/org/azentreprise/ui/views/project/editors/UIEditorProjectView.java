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

import java.awt.Frame;

import org.azentreprise.ui.UIMain;
import org.azentreprise.ui.components.UIButton;
import org.azentreprise.ui.components.UIButton.UIButtonClickListener;
import org.azentreprise.ui.views.UIView;
import org.azentreprise.ui.views.project.UIProjectView;

public abstract class UIEditorProjectView extends UIProjectView implements UIButtonClickListener {
	public UIEditorProjectView(UIView parent, Frame rootComponent) {
		super(parent, rootComponent);
		
		this.setNeedValidationFlag();
		
		this.initComponents();
		
		this.add(new UIButton(this, 0.72f, 0.6f, 0.81f, 0.66f, "Validate").setHandler(this, "validate"));
		this.add(new UIButton(this, 0.84f, 0.6f, 0.93f, 0.66f, "Cancel").setHandler(this, "cancel"));
	}
	
	public void onClick(Object... signals) {
		super.onClick(signals);
		
		if(signals[0] instanceof String) {
			switch((String) signals[0]) {
				case "validate":
					if(!this.validate()) {
						break;
					}
				case "cancel":
					UIMain.back(this);
					this.clearNeedValidationFlag();
			}
		}
	}
	
	public abstract void initComponents();	
	public abstract boolean validate();
}
