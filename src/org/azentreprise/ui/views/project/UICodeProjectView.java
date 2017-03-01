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
import org.azentreprise.ui.UIMain;
import org.azentreprise.ui.components.UIButton;
import org.azentreprise.ui.components.UIButton.UIButtonClickListener;
import org.azentreprise.ui.components.UILabel;
import org.azentreprise.ui.views.UIView;
import org.azentreprise.ui.views.project.editors.UIAutoCompletionEditorProjectView;

public class UICodeProjectView extends UIProjectView implements UIButtonClickListener {
	
	private CodeObjectSpawner theSpawner = null;
	
	public UICodeProjectView(UIView parent, Frame rootComponent) {
		super(parent, rootComponent);
		
		String name = Projects.getCurrentProject().current;
		
		if(name.isEmpty()) {
			name = "Root";
		}
		
		this.add(new UILabel(this, 0.7f, 0.05f, 0.95f, 0.17f, name).alterFont(Font.BOLD));
		this.add(new UIButton(this, 0.72f, 0.17f, 0.93f, 0.27f, "New structure").setHandler(this, "struct"));
		this.add(new UIButton(this, 0.72f, 0.3f, 0.93f, 0.4f, "New constant").setHandler(this, "const"));
		this.add(new UIButton(this, 0.72f, 0.43f, 0.93f, 0.53f, "New explicit call").setHandler(this, "call"));
		this.add(new UIButton(this, 0.72f, 0.56f, 0.93f, 0.66f, "Do nothing").setHandler(this, "nothing"));
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
		
		if(event.getKeyCode() == KeyEvent.VK_SPACE) {
			UIProjectView view = new UIAutoCompletionEditorProjectView(this, this.getRootComponent());
	
			view.keyReleased(event);
		
			UIMain.show(view);
		}
	}
}