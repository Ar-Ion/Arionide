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
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.azentreprise.Debug;
import org.azentreprise.lang.Data;
import org.azentreprise.ui.components.UILabel;
import org.azentreprise.ui.components.UIText;
import org.azentreprise.ui.views.UIView;

public abstract class UIVariablesEditorProjectView extends UIEditorProjectView {

	private UILabel[] labels;
	private UIText[] texts;
	
	private List<String> names = new ArrayList<String>();
	private List<Data> values = new ArrayList<Data>();
	
	public UIVariablesEditorProjectView(UIView parent, Frame rootComponent) {
		super(parent, rootComponent);
	}

	public void initComponents() {
		this.labels = new UILabel[8];
		this.texts = new UIText[8];
		
		for(int i = 0; i < 8; i++) {
			this.labels[i] = new UILabel(this, 0.72f, 0.15f + i * 0.055f, 0.82f, 0.20f + i * 0.055f, "Empty");
			this.texts[i] = (UIText) new UIText(this, 0.83f, 0.15f + i * 0.055f, 0.93f, 0.19f + i * 0.055f, "Undefined").setDisabled(true);
		
			this.add(this.labels[i]);
			this.add(this.texts[i]);
		}
		
		this.updateVars();
	}
	
	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_SPACE) {
			this.newVariable();
		} else if(event.getKeyCode() == KeyEvent.VK_UP) {
			
		} else if(event.getKeyCode() == KeyEvent.VK_DOWN) {
			
		} else {
			super.keyPressed(event);
		}
	}
	
	public boolean validate() {
		return false;
	}
	
	private void updateVars() {
		this.names.clear();
		this.values.clear();
		
		for(String name : this.names) {
			this.values.add(this.getVarmap().get(name));
		}
	}
	
	public void newVariable() {
		Debug.taskBegin("creating a new variable");
		
		String name = JOptionPane.showInputDialog("Enter variable name:");
		 
		if(name.isEmpty()) {
			throw new RuntimeException("Empty variable name");
		} else {
			this.createVariable(name);
		}
		
		Debug.taskEnd();
	}
	
	public abstract void createVariable(String name);
	public abstract Map<String, Data> getVarmap();
}
