package ch.innovazion.arionide.menu.code;

import java.util.List;

import ch.innovazion.arionide.menu.Browser;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.HierarchyElement;

public class CodeBrowser extends Browser {
	
	public CodeBrowser(MenuManager manager) {
		super(manager);
	}

	public void browse() {
		go("edit");
	}

	protected List<HierarchyElement> fetchBrowsableIDs() {
		return project.getStructureManager().getCodeManager().getCurrentCode().list();
	}
}