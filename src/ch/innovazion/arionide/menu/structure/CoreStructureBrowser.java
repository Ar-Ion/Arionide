package ch.innovazion.arionide.menu.structure;

import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.ui.AppManager;

public class CoreStructureBrowser extends StructureBrowser {

	private final Menu editor;
	
	public CoreStructureBrowser(AppManager manager) {
		super(manager);
		this.editor = new StructureEditor(this);
	}

	public void onClick(int index) {
		super.onClick(index);
		editor.show();
	}
}