/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
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
package ch.innovazion.arionide.ui.overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.overlay.views.CodeView;
import ch.innovazion.arionide.ui.overlay.views.InputPromptView;
import ch.innovazion.arionide.ui.overlay.views.MainView;
import ch.innovazion.arionide.ui.overlay.views.NewProjectView;
import ch.innovazion.arionide.ui.overlay.views.RunView;
import ch.innovazion.arionide.ui.overlay.views.TextPromptView;

public class Views {
	
	public static View main;
	public static View newProject;
	public static View code;
	public static View run;
	public static TextPromptView acknowledge;
	public static TextPromptView confirm;
	public static TextPromptView decide;
	public static InputPromptView input;

	private static List<View> all = new ArrayList<>();
	
	public static void init(AppManager appManager, LayoutManager layoutManager) {
		assert Views.all.isEmpty();
		
		Views.all.add(Views.main = new MainView(appManager, layoutManager));
		Views.all.add(Views.newProject = new NewProjectView(appManager, layoutManager));
		Views.all.add(Views.code = new CodeView(appManager, layoutManager));
		Views.all.add(Views.run = new RunView(appManager, layoutManager));
		Views.all.add(Views.acknowledge = new TextPromptView(appManager, layoutManager, 1));
		Views.all.add(Views.confirm = new TextPromptView(appManager, layoutManager, 2));
		Views.all.add(Views.decide = new TextPromptView(appManager, layoutManager, 3));
		Views.all.add(Views.input = new InputPromptView(appManager, layoutManager));
	}
	
	public static void load() {
		assert !Views.all.isEmpty();

		for(View view : all) {
			view.load();
		}
	}
	
	public static List<View> getList() {
		return Collections.unmodifiableList(all);
	}
}