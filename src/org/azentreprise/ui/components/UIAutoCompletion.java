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
package org.azentreprise.ui.components;

import java.awt.event.KeyEvent;
import java.util.List;

import org.azentreprise.lang.AutoCompletion;
import org.azentreprise.lang.AutoCompletionType;
import org.azentreprise.ui.views.UIView;

public class UIAutoCompletion extends UIText {
	
	private AutoCompletionType type;
	private List<String> autoCompletion;
	private int index = 0;
	
	public UIAutoCompletion(UIView parent, float x1, float y1, float x2, float y2, String placeholder, AutoCompletionType type) {
		super(parent, x1, y1, x2, y2, placeholder);
		this.type = type;
	}
	
	public UIAutoCompletion setAutoCompletionType(AutoCompletionType type) {
		this.type = type;
		return this;
	}
	
	protected void updateText() {			
		this.autoCompletion = AutoCompletion.autoComplete(this.getText(), this.type);
		this.index = 0;
		this.updateLabel();
	}
	
	private void updateLabel() {
		this.setLabel(this.getText().length() > 0 ? this.getText().toString() + " - " + this.autoCompletion.get(this.index) : this.getPlaceholder());
	}
	
	public void focusLost() {
		super.updateText();
		super.focusLost();
	}
	
	public boolean handleKeyboardEvent(int code, char ch, int modifiers) {
		if(this.hasFocus) {
			if(code == KeyEvent.VK_DOWN) {
				if(this.index < this.autoCompletion.size() - 1) {
					this.index++;
				}
				
				this.updateLabel();
			} else if(code == KeyEvent.VK_UP) {
				if(this.index > 0) {
					this.index--;
				}
				
				this.updateLabel();
			} else if(code == KeyEvent.VK_ENTER && !this.autoCompletion.get(this.index).equals("<No proposal>")) {
				this.setText(this.autoCompletion.get(this.index));
			} else {
				return super.handleKeyboardEvent(code, ch, modifiers);
			}
			
			return false;
		} else {
			return true;
		}
	}
}
