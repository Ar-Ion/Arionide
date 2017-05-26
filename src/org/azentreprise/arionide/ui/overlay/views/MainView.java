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
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
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
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.arionide.ui.overlay.components.Button;
import org.azentreprise.arionide.ui.overlay.components.ClickListener;
import org.azentreprise.arionide.ui.overlay.components.Label;

public class MainView extends View implements ClickListener {
	
	public static int pageID = 0;
	
	public MainView(View parent, AppManager appManager, LayoutManager layoutManager) {
		super(parent, appManager, layoutManager);
		
		this.setBackgroundColor(new Color(63, 63, 63, 127));
		this.setBorderColor(new Color(0xCAFE));
		
		this.add(new Label(this, "Arionide").alterFont(Font.BOLD), 0.0f, 0.05f, 1.0f, 0.2f);
		
		Queue<Float> positions = new ArrayDeque<Float>(Arrays.asList(0.23f, 0.38f, 0.53f, 0.68f));
		
		int focus = 0;
		
		if(MainView.pageID == 0) {
			this.add(new Button(this, "AZEntreprise.org").setHandler(this, "browse", "https://azentreprise.org"), 0.1f, positions.peek(), 0.9f, positions.poll() + 0.1f);
			this.add(new Button(this, "Arionide bug report").setHandler(this, "browse", "https://azentreprise.org/Arionide/bugreport.php"), 0.1f, positions.peek(), 0.9f, positions.poll() + 0.1f);
			this.add(new Button(this, "Arionide tutorials").setHandler(this, "browse", "https://azentreprise.org/Arionide/tutorials.php"), 0.1f, positions.peek(), 0.9f, positions.poll() + 0.1f);
			this.add(new Button(this, "Arionide community").setHandler(this, "browse", "https://azentreprise.org/Arionide"), 0.1f, positions.peek(), 0.9f, positions.poll() + 0.1f);
			focus = 5;
		} else {
			/*for(int i = 0; i < 4; i++) {
				try {
					String project = Projects.getProjectsList().get((MainView.pageID - 1) * 4 + i);
					this.add(new Button(this, "Open " + project).setHandler(this, "open", project), 0.1f, positions.peek(), 0.9f, positions.poll() + 0.1f);
					focus++;
				} catch (IndexOutOfBoundsException e) {
					break;
				}
			}*/
		}

		this.add(new Button(this, "New project").setHandler(this, "new"), 0.1f, 0.83f, 0.33f, 0.92f);
		this.add(new Button(this, "Collaborate").setHandler(this, "connect"), 0.38f, 0.83f, 0.61f, 0.92f);
		this.add(new Button(this, "Import").setHandler(this, "import"), 0.66f, 0.83f, 0.90f, 0.92f);

		/*if(Projects.getProjectsList().size() > MainView.pageID * 4) {
			this.add(new Button(this, ">").setHandler(this, "next"), 6.5f / 7.0f, 0.43f, 6.8f / 7.0f, 0.58f);
		}*/
		
		this.setFocus(focus);
		
		if(MainView.pageID != 0) {
			this.add(new Button(this, "<").setHandler(this, "prev"), 0.2f / 7.0f, 0.43f, 0.5f / 7.0f, 0.58f);
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
					// new project view
					break;
				case "connect":
					break;
				case "import":
					this.importProject(signals);
					break;
				case "next":
					MainView.pageID++;
					// page id transition
					break;
				case "prev":
					MainView.pageID--;
					// page id transition
					break;
			}
		} catch(Exception exception) {
			Debug.exception(exception);
		}
		Debug.taskEnd();
	}
	
	public void open(Object[] signals) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, IOException {
		// TODO
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
		
		if(chooser.showOpenDialog(this.getAppManager().getDrawingContext() instanceof Component ? (Component) this.getAppManager().getDrawingContext() : null) == JFileChooser.APPROVE_OPTION) {
			ProjectFormatConverter.importProject(chooser.getSelectedFile());
			MainView.updatePageID();
			// show main view
		}
	}
	
	public static void updatePageID() {
		MainView.pageID = (int) Math.ceil(Projects.getProjectsList().size() / 4.0d);
	}
}