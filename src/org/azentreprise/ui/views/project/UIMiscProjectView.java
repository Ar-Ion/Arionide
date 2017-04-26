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
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.azentreprise.Debug;
import org.azentreprise.ProjectFormatConverter;
import org.azentreprise.Projects;
import org.azentreprise.configuration.Project;
import org.azentreprise.lang.LangarionError;
import org.azentreprise.ui.UIMain;
import org.azentreprise.ui.components.UIButton;
import org.azentreprise.ui.components.UIButton.UIButtonClickListener;
import org.azentreprise.ui.components.UILabel;
import org.azentreprise.ui.views.UIMainView;
import org.azentreprise.ui.views.UIView;

public class UIMiscProjectView extends UIProjectView implements UIButtonClickListener {
	public UIMiscProjectView(UIView parent, Frame rootComponent) {
		super(parent, rootComponent);
				
		this.add(new UILabel(this, 0.77f, 0.05f, 0.98f, 0.15f, Projects.getCurrentProject().name).alterFont(Font.BOLD));
		this.add(new UIButton(this, 0.77f, 0.15f, 0.98f, 0.2f, "Export").setHandler(this, "export"));
		this.add(new UIButton(this, 0.77f, 0.23f, 0.98f, 0.28f, "Import").setHandler(this, "import"));
		this.add(new UIButton(this, 0.77f, 0.31f, 0.98f, 0.36f, "Rename").setHandler(this, "rename"));
		this.add(new UIButton(this, 0.77f, 0.39f, 0.98f, 0.44f, "Delete").setHandler(this, "delete"));
		this.add(new UIButton(this, 0.77f, 0.47f, 0.98f, 0.52f, "Build").setHandler(this, "build"));
		this.add(new UIButton(this, 0.77f, 0.55f, 0.98f, 0.6f, "Execute").setHandler(this, "execute"));
		this.add(new UIButton(this, 0.77f, 0.63f, 0.98f, 0.68f, "Debug").setHandler(this, "execute"));
		this.add(new UIButton(this, 0.77f, 0.71f, 0.98f, 0.76f, "Settings").setHandler(this, "settings"));
		this.add(new UIButton(this, 0.77f, 0.79f, 0.98f, 0.84f, "Quit").setHandler(this, "quit"));
	}

	public void onClick(Object... signals) {
		super.onClick(signals);
		
		switch((String) signals[0]) {
			case "export":
				this.export();
				break;
			case "rename":
				this.rename();
				break;
			case "delete":
				this.delete();
				break;
			case "build":
				this.build();
				break;
			case "quit":
				UIMain.show(new UIMainView(this.getParentView().getParentView(), this.getRootComponent()));
				break;
		}
	}
	
	public void export() {
		JFileChooser chooser = new JFileChooser();
		
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".arionide") && file.isFile();
			}

			public String getDescription() {
				return "Arionide archive files (.arionide)";
			}
		});
		
		if (chooser.showSaveDialog(this.getRootComponent()) == JFileChooser.APPROVE_OPTION) {
			ProjectFormatConverter.exportProject(chooser.getSelectedFile());
		}
	}
	
	public void rename() {
		String name = JOptionPane.showInputDialog(this.getRootComponent(), "Enter the new name of the project:", "Rename", JOptionPane.PLAIN_MESSAGE);
		if(name != null && !name.isEmpty()) {
			Projects.getCurrentProject().name = name;
			
			try {
				Projects.getCurrentProject().save();
			} catch (IllegalArgumentException | IllegalAccessException | IOException exception) {
				Debug.exception(exception);
			}
			
			UIMain.show(new UIMiscProjectView(this.getParentView(), this.getRootComponent()));
		}
	}
	
	public void delete() {
		Debug.taskBegin("deleting a project");
		if(JOptionPane.showConfirmDialog(this.getRootComponent(), "Do really want to delete this project ?") == JOptionPane.OK_OPTION) {
			
			try {
				Projects.getCurrentProject().delete();
			} catch (IOException exception) {
				Debug.exception(exception);
			}
			
			UIMainView.updatePageID();
			UIMain.show(new UIMainView(this.getParentView(), this.getRootComponent()));
		}
		Debug.taskEnd();
	}
	
	public void build() {
		Project project = Projects.getCurrentProject();
		
		try {
			project.theCode.compile(project);
			project.theCode.run();
		} catch (LangarionError exception) {
			this.error(exception.getMessage());
		}
	}
	
	public void keyPressed(KeyEvent event) {		
		if(event.getKeyCode() == KeyEvent.VK_CONTROL || event.getKeyCode() == KeyEvent.VK_ALT) {
			this.back();
		} else {
			super.keyPressed(event);
		}
	}
}
