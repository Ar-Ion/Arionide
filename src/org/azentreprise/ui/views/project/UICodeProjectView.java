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
package org.azentreprise.ui.views.project;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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
	
	private final UILabel title = new UILabel(this, 0.75f, 0.05f, 1.0f, 0.17f, "");
	
	private final UIButton[] buttons = new UIButton[8];
	private final UIText[] lines = new UIText[this.buttons.length];
	
	public UICodeProjectView(UIView parent, Frame rootComponent) {
		super(parent, rootComponent);
		
		this.title.alterFont(Font.BOLD);
		this.title.setLabel(this.resolveName(Projects.getCurrentProject().current));
		this.add(this.title);
		
		for(int i = 0; i < this.lines.length; i++) {
			this.lines[i] = new UIText(this, 0.8f, 0.15f + i * 0.1f, 0.98f, 0.2f + i * 0.1f, "");
			this.add(this.lines[i]);
		}
		
		for(int i = 0; i < this.lines.length; i++) {
			this.buttons[i] = new UIButton(this, 0.76f, 0.15f + i * 0.1f, 0.79f, 0.2f + i * 0.1f, Integer.toString(i + 1));
			this.add(this.buttons[i]);
		}
		
		for(int i = 0; i < this.lines.length; i++) {
			if(i < this.project.sourceCode.size()) {
				this.lines[i].setText(this.project.sourceCode.get(i));
			} else {
				this.project.sourceCode.add("");
			}
		}
	}
	
	protected void open(String id) {
		super.open(id);
		this.title.setLabel(this.resolveName(id));
	}
	
	private String resolveName(String id) {
		if(id.isEmpty()) {
			return "Root";
		} else {
			return new org.azentreprise.lang.Object(id, null, 0, null, false).getName();
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
	
	public int getLineNumberCorrespondingToSlot(int id) {
		return Integer.parseInt(this.buttons[id].toString());
	}
	
	public void syncWithDataStructure() {
		for(int i = 0; i < this.lines.length; i++) {
			String line = this.lines[i].toString();
			this.project.sourceCode.set(this.getLineNumberCorrespondingToSlot(i) - 1, line);
		}
	}
	
	public void mouseWheelMoved(MouseWheelEvent event) {
		super.mouseWheelMoved(event);
		
		Point point = event.getPoint();
		
		UIEvents.transform(point);
		
		if(this.controls != null && this.controls.contains(point)) {
			
			this.syncWithDataStructure();
			
			int scalar = event.getWheelRotation();
			
			if(scalar > this.lines.length) {
				scalar = this.lines.length;
			}
			
			scalar += this.getLineNumberCorrespondingToSlot(0);
			
			if(scalar <= 0) {
				scalar = 1;
			}
			
			for(int i = 0; i < this.lines.length; i++) {								
				if(i + scalar - 1 < this.project.sourceCode.size()) {
					this.lines[i].setText(this.project.sourceCode.get(i + scalar - 1));
				} else {
					this.project.sourceCode.add("");
				}
				
				this.buttons[i].setLabel(Integer.toString(i + scalar));
			}
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