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
package org.azentreprise.ui.views.project;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.azentreprise.Projects;
import org.azentreprise.ui.UIEvents;
import org.azentreprise.ui.components.UIButton;
import org.azentreprise.ui.components.UIButton.UIButtonClickListener;
import org.azentreprise.ui.components.UILabel;
import org.azentreprise.ui.components.UIText;
import org.azentreprise.ui.views.UIView;

public class UICodeProjectView extends UIProjectView implements UIButtonClickListener {
	
	private CodeObjectSpawner theSpawner = null;
	
	private final UIButton[] buttons = new UIButton[8];
	private final UIText[] lines = new UIText[8];
	
	public UICodeProjectView(UIView parent, Frame rootComponent) {
		super(parent, rootComponent);
		
		String name = Projects.getCurrentProject().current;
		
		if(name.isEmpty()) {
			name = "Root";
		}
		
		this.add(new UILabel(this, 0.75f, 0.05f, 1.0f, 0.17f, name).alterFont(Font.BOLD));
		
		for(int i = 0; i < this.lines.length; i++) {
			this.lines[i] = new UIText(this, 0.8f, 0.15f + i * 0.1f, 0.98f, 0.2f + i * 0.1f, "");
			this.add(this.lines[i]);
		}
		
		for(int i = 0; i < this.lines.length; i++) {
			this.buttons[i] = new UIButton(this, 0.76f, 0.15f + i * 0.1f, 0.79f, 0.2f + i * 0.1f, Integer.toString(i));
			this.add(this.buttons[i]);
		}
	}

	public void onClick(Object... signals) {
		super.onClick(signals);
		
		this.accessHighlighting().clear();
		
		switch((String) signals[0]) {
			case "struct":
				this.theSpawner = new CodeStructureSpawner();
				break;
			case "const":
				this.theSpawner = new CodeStructureSpawner();
				break;
			case "call":
				this.theSpawner = new CodeCallSpawner();
				break;
			case "nothing":
				this.theSpawner = null;
				return;
		}
		
		if(this.theSpawner != null) {
			this.theSpawner.updateHighlighting(this.accessHighlighting());
		}
	}
	
	public void mousePressed(MouseEvent event) {
		super.mousePressed(event);
		
		Point point = event.getPoint();
		
		UIEvents.transform(point);
		
		if(this.theSpawner != null && event.getButton() == MouseEvent.BUTTON3) {
			int x = (int) Math.round((float) (point.getX() - this.project.gridPosX) / this.project.pixelsPerCell - 0.5f);
			int y = (int) Math.round((float) (point.getY() - this.project.gridPosY) / this.project.pixelsPerCell - 0.5f);
			
			this.theSpawner.loadCell(new Point2D.Float(x, y));
		}
	}
	
	public void keyReleased(KeyEvent event) {
		super.keyReleased(event);
		
		/*if(event.getKeyCode() == KeyEvent.VK_SPACE) {
			UIProjectView view = new UIAutoCompletionEditorProjectView(this, this.getRootComponent());
	
			view.keyReleased(event);
		
			UIMain.show(view);
		}*/
	}
}