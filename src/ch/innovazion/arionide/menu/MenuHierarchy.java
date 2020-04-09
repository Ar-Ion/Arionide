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

import java.util.function.Function;

import ch.innovazion.arionide.menu.code.CodeBrowser;
import ch.innovazion.arionide.menu.code.CodeEditor;
import ch.innovazion.arionide.menu.code.DefaultInstructionAppender;
import ch.innovazion.arionide.menu.code.OperatorAppender;
import ch.innovazion.arionide.menu.code.SignatureSelector;
import ch.innovazion.arionide.menu.params.InformationUpdater;
import ch.innovazion.arionide.menu.params.ParameterCreator;
import ch.innovazion.arionide.menu.params.ParameterEditor;
import ch.innovazion.arionide.menu.params.ParameterSelector;
import ch.innovazion.arionide.menu.params.assign.ConstantAssigner;
import ch.innovazion.arionide.menu.params.assign.EnumerationAssigner;
import ch.innovazion.arionide.menu.params.assign.ReferenceAssigner;
import ch.innovazion.arionide.menu.params.assign.VariableAssigner;
import ch.innovazion.arionide.menu.params.edit.EnumerationEditor;
import ch.innovazion.arionide.menu.params.edit.EnumerationPossibilityRemover;
import ch.innovazion.arionide.menu.params.edit.InformationEditor;
import ch.innovazion.arionide.menu.params.edit.ReferenceEditor;
import ch.innovazion.arionide.menu.params.edit.ReferenceParameterRemover;
import ch.innovazion.arionide.menu.params.edit.VariableEditor;
import ch.innovazion.arionide.menu.structure.CommentEditor;
import ch.innovazion.arionide.menu.structure.LanguageSelector;
import ch.innovazion.arionide.menu.structure.StructureBrowser;
import ch.innovazion.arionide.menu.structure.StructureEditor;
import ch.innovazion.arionide.menu.structure.TintSelector;
import ch.innovazion.automaton.StateHierarchy;
import ch.innovazion.automaton.StateManager;

public class MenuHierarchy extends StateHierarchy {
	
	private static final int nestingDepth = 8; // O(3^n)
	
	protected RootMenu root;
	protected StructureBrowser structureBrowser;
	protected CodeBrowser codeBrowser;
	protected GenericUpdater genericUpdater;

	protected void registerStates(StateManager mgr) {
		assert mgr instanceof MenuManager;
		
		MenuManager manager = (MenuManager) mgr;
		
		register("/", root = new RootMenu(manager));
		
		register("/structure", structureBrowser = new StructureBrowser(manager));
		register("/structure/edit", new StructureEditor(manager));
		register("/structure/edit/specify", new ParameterSelector(manager, true));
		register("/structure/edit/specify/create", new ParameterCreator(manager));
		register("/structure/edit/specify/edit", new ParameterEditor(manager));
		registerInformationUpdater("/structure/edit/specify/constant", manager, InformationEditor::new, nestingDepth);
		register("/structure/edit/specify/variable", new VariableEditor(manager));
		register("/structure/edit/specify/variable/assign", new VariableAssigner(manager));
		registerInformationUpdater("/structure/edit/specify/variable/edit", manager, InformationUpdater::new, nestingDepth);
		register("/structure/edit/comment", new CommentEditor(manager));
		register("/structure/edit/language", new LanguageSelector(manager));
		register("/structure/edit/tint", new TintSelector(manager));
		
		register("/code", codeBrowser = new CodeBrowser(manager));
		register("/code/edit", new CodeEditor(manager));
		register("/code/edit/append", new DefaultInstructionAppender(manager));
		register("/code/edit/append/signature", new SignatureSelector(manager));
		register("/code/edit/append/operator", new OperatorAppender(manager));
		register("/code/edit/append/operator/signature", new SignatureSelector(manager));
		register("/code/edit/specify", new ParameterSelector(manager, false));
		registerInformationUpdater("/code/edit/specify/constant", manager, InformationEditor::new, nestingDepth);
		register("/code/edit/specify/variable", new VariableEditor(manager));
		register("/code/edit/specify/variable/assign", new VariableAssigner(manager));
		registerInformationUpdater("/code/edit/specify/variable/edit", manager, InformationUpdater::new, nestingDepth);
		
		register("/generic", genericUpdater = new GenericUpdater(manager));
		registerInformationUpdater("/generic/information", manager, InformationUpdater::new, nestingDepth);
	}
	
	private void registerInformationUpdater(String source, MenuManager manager, Function<MenuManager, InformationUpdater> supplier, int nesting) {
		if(nesting > 0) {
			register(source, supplier.apply(manager));
			register(source + "/enum", new EnumerationEditor(manager));
			register(source + "/enum/assign", new EnumerationAssigner(manager));
			register(source + "/enum/remove", new EnumerationPossibilityRemover(manager));
			register(source + "/const", new ConstantAssigner(manager));
			register(source + "/var", new VariableEditor(manager));
			register(source + "/var/assign", new VariableAssigner(manager));
			register(source + "/ref", new ReferenceEditor(manager));
			register(source + "/ref/assign", new ReferenceAssigner(manager));
			register(source + "/ref/remove", new ReferenceParameterRemover(manager));
			
			registerInformationUpdater(source + "/enum/edit", manager, supplier, nesting - 1);
			registerInformationUpdater(source + "/var/edit", manager, supplier, nesting - 1);
			registerInformationUpdater(source + "/ref/edit", manager, supplier, nesting - 1);
		} else {
			register(source, new ErrorMenu(manager, "Nesting limit reached"));
			return;
		}
	}
	
	protected Menu resolveCurrentState() {
		return (Menu) super.resolveCurrentState();
	}
}
