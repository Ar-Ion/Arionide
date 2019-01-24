/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018, 2019 AZEntreprise Corporation. All rights reserved.
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
import ch.innovazion.arionide.menu.code.CoreCodeBrowser;
import ch.innovazion.arionide.menu.code.ReferenceEditor;
import ch.innovazion.arionide.menu.code.TypeEditor;
import ch.innovazion.arionide.menu.structure.CoreStructureBrowser;
import ch.innovazion.arionide.menu.structure.StructureBrowser;
import ch.innovazion.arionide.ui.AppManager;

public class MainMenus {
	
	private static StructureBrowser structBrowser;
	private static CodeBrowser codeBrowser;
	
	private static TypeEditor typeEditor;
	private static ReferenceEditor referenceEditor;
	
	private static boolean initialized = false;
	
	public static void init(AppManager manager) {
		if(initialized) {
			throw new IllegalStateException("Already initialized");
		}
	
		structBrowser = new CoreStructureBrowser(manager);
		codeBrowser = new CoreCodeBrowser(structBrowser);
		//specificationBrowser = new SpecificationBrowser(manager);

		typeEditor = new TypeEditor(codeBrowser);
 		referenceEditor = new ReferenceEditor(codeBrowser);
	}
	
	public static StructureBrowser getStructureList() {
		return structBrowser;
	}
	
	public static CodeBrowser getCodeBrowser() {
		return codeBrowser;
	}

	public static TypeEditor getTypeEditor() {
		return typeEditor;
	}
	
	public static ReferenceEditor getReferenceEditor() {
		return referenceEditor;
	}
}