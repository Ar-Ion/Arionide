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
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.azentreprise.Debug;
import org.azentreprise.ProjectFormatConverter;
import org.azentreprise.Projects;
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.ui.UIMain;
import org.azentreprise.ui.components.UIButton;
import org.azentreprise.ui.components.UILabel;
import org.azentreprise.ui.views.UINewProjectView;
import org.azentreprise.ui.views.UIView;
import org.azentreprise.ui.views.project.UICodeProjectView;

public class MainView extends View {
	
	public static int pageID = 0;
	
	public MainView(UIView parent, Frame rootComponent) {
		super(parent, rootComponent, 0.1f, 0.1f, 0.9f, 0.9f);
		
		this.setBackground(new Color(63, 63, 63, 127));
		this.setBorderColor(new Color(0xCAFE));
		
		this.add(new UILabel(this, 0, 0.05f, 1.0f, 0.2f, "Arionide").alterFont(Font.BOLD));
		
		Queue<Float> yMatrix = new ArrayDeque<Float>(Arrays.asList(0.23f, 0.38f, 0.53f, 0.68f));
		
		int focus = 0;
		
		if(MainView.pageID == 0) {
			this.add(new UIButton(this, 0.1f, yMatrix.peek(), 0.9f, yMatrix.poll() + 0.1f, "AZEntreprise.org").setHandler(this, "browse", "https://azentreprise.org"));
			this.add(new UIButton(this, 0.1f, yMatrix.peek(), 0.9f, yMatrix.poll() + 0.1f, "Arionide bug report").setHandler(this, "browse", "https://azentreprise.org/Arionide/bugreport.php"));
			this.add(new UIButton(this, 0.1f, yMatrix.peek(), 0.9f, yMatrix.poll() + 0.1f, "Arionide tutorials").setHandler(this, "browse", "https://azentreprise.org/Arionide/tutorials.php"));
			this.add(new UIButton(this, 0.1f, yMatrix.peek(), 0.9f, yMatrix.poll() + 0.1f, "Arionide community").setHandler(this, "browse", "https://azentreprise.org/Arionide"));
			focus = 5;
		} else {
			for(int i = 0; i < 4; i++) {
				try {
					String project = Projects.getProjectsList().get((MainView.pageID - 1) * 4 + i);
					this.add(new UIButton(this, 0.1f, yMatrix.peek(), 0.9f, yMatrix.poll() + 0.1f, "Open " + project).setHandler(this, "open", project));
					focus++;
				} catch (IndexOutOfBoundsException e) {
					break;
				}
			}	
		}

		this.add(new UIButton(this, 0.1f, 0.83f, 0.33f, 0.92f, "New project").setHandler(this, "new"));
		this.add(new UIButton(this, 0.38f, 0.83f, 0.61f, 0.92f, "Collaborate").setHandler(this, "connect"));
		this.add(new UIButton(this, 0.66f, 0.83f, 0.90f, 0.92f, "Import").setHandler(this, "import"));

		if(Projects.getProjectsList().size() > MainView.pageID * 4) {
			this.add(new UIButton(this, 6.5f / 7.0f, 0.43f, 6.8f / 7.0f, 0.58f, ">").setHandler(this, "next"));
		}
		
		this.setFocus(focus);
		
		if(MainView.pageID != 0) {
			this.add(new UIButton(this, 0.2f / 7.0f, 0.43f, 0.5f / 7.0f, 0.58f, "<").setHandler(this, "prev"));
		}		
	}

	public void onClick(Object... signals) {
		Debug.taskBegin("clicking on a button");
		try {
			switch((String) signals[0]) {
				case "open":
					this.open(signals);
					break;
				case "browse":
					Desktop.getDesktop().browse(new URL((String) signals[1]).toURI()); 
					break;
				case "new":
					UIMain.show(new UINewProjectView(this, this.getRootComponent()));
					break;
				case "connect":
					break;
				case "import":
					this.importProject(signals);
					break;
				case "next":
					MainView.pageID++;
					UIMain.show(new MainView(this.getParentView(), this.getRootComponent()));
					break;
				case "prev":
					MainView.pageID--;
					UIMain.show(new MainView(this.getParentView(), this.getRootComponent()));
					break;
			}
		} catch(Exception exception) {
			Debug.exception(exception);
		}
		Debug.taskEnd();
	}
	
	public void open(Object[] signals) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, IOException {
		if(UIMain.repairRequested) {
			UIMain.repairRequested = false;
			Projects.repairProject((String) signals[1]);
		}
		
		Projects.loadProject((String) signals[1]);
		
		UIMain.show(new UICodeProjectView(this, this.getRootComponent()));
	}
	
	public void importProject(Object[] signals) {
		JFileChooser chooser = new JFileChooser();
		
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".Arionide") && file.isFile();
			}

			public String getDescription() {
				return "Arionide archive files (.Arionide)";
			}
		});
		
		if(chooser.showOpenDialog(this.getRootComponent()) == JFileChooser.APPROVE_OPTION) {
			ProjectFormatConverter.importProject(chooser.getSelectedFile());
			MainView.updatePageID();
			UIMain.show(new MainView(this.getParentView(), this.getRootComponent()));
		}
	}
	
	public static void updatePageID() {
		MainView.pageID = (int) Math.ceil(Projects.getProjectsList().size() / 4.0d);
	}
}