/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.menu;

import ch.innovazion.arionide.menu.code.CodeBrowser;
import ch.innovazion.arionide.menu.code.CodeEditor;
import ch.innovazion.arionide.menu.structure.CommentEditor;
import ch.innovazion.arionide.menu.structure.StructureBrowser;
import ch.innovazion.arionide.menu.structure.StructureEditor;
import ch.innovazion.arionide.menu.structure.TintSelector;
import ch.innovazion.automaton.StateHierarchy;
import ch.innovazion.automaton.StateManager;

public class MenuHierarchy extends StateHierarchy {
	
	protected RootMenu root;
	protected StructureBrowser structureBrowser;
	protected CodeBrowser codeBrowser;

	protected void registerStates(StateManager mgr) {
		assert mgr instanceof MenuManager;
		
		register("/", root = new RootMenu((MenuManager) mgr));
		
		register("/structure", structureBrowser = new StructureBrowser((MenuManager) mgr));
		register("/structure/edit", new StructureEditor((MenuManager) mgr));
		register("/structure/edit/comment", new CommentEditor((MenuManager) mgr));
		register("/structure/edit/tint", new TintSelector((MenuManager) mgr));

		register("/code", codeBrowser = new CodeBrowser((MenuManager) mgr));
		register("/code/edit", new CodeEditor((MenuManager) mgr));
	}
	
	protected Menu resolveCurrentState() {
		return (Menu) super.resolveCurrentState();
	}
}
