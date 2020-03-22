package ch.innovazion.arionide.menu.structure;

import java.util.List;

import ch.innovazion.arionide.menu.Browser;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.HierarchyElement;

public class StructureBrowser extends Browser {

	public StructureBrowser(MenuManager manager) {
		super(manager);
	}

	public void browse() {
		go("edit");
	}

	protected List<HierarchyElement> fetchBrowsableIDs() {
		return project.getStructureManager().getCurrentGeneration(project.getStorage().getHierarchy());
	}
}
