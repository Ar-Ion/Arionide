package ch.innovazion.arionide.menu;

import ch.innovazion.arionide.menu.code.CodeBrowser;
import ch.innovazion.arionide.menu.code.CodeEditor;
import ch.innovazion.arionide.menu.structure.StructureBrowser;
import ch.innovazion.arionide.menu.structure.StructureEditor;
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

		register("/code", codeBrowser = new CodeBrowser((MenuManager) mgr));
		register("/code/edit", new CodeEditor((MenuManager) mgr));
	}
	
	protected Menu resolveCurrentState() {
		return (Menu) super.resolveCurrentState();
	}
}
