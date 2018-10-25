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
package ch.innovazion.arionide.ui.menu;

import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.menu.code.CodeEditor;
import ch.innovazion.arionide.ui.menu.code.ReferenceEditor;
import ch.innovazion.arionide.ui.menu.code.TypeEditor;
import ch.innovazion.arionide.ui.menu.edition.StructureEditor;

public class MainMenus {
	
	private static StructureList structList;
	private static StructureEditor structEditor;
	private static CodeEditor codeEditor;
	private static TypeEditor typeEditor;
	private static ReferenceEditor referenceEditor;
	
	private static boolean initialized = false;
	
	public static void init(AppManager manager) {
		assert !initialized : "Already initialized";
	
		structList = new StructureList(manager);
		structEditor = new StructureEditor(manager);
		codeEditor = new CodeEditor(manager, structList);
		typeEditor = new TypeEditor(manager, structList);
		referenceEditor = new ReferenceEditor(manager, structList);
	}
	
	public static StructureList getStructureList() {
		return structList;
	}
	
	public static StructureEditor getStructureEditor() {
		return structEditor;
	}
	
	public static CodeEditor getCodeEditor() {
		return codeEditor;
	}
	
	public static TypeEditor getTypeEditor() {
		return typeEditor;
	}
	
	public static ReferenceEditor getReferenceEditor() {
		return referenceEditor;
	}
}