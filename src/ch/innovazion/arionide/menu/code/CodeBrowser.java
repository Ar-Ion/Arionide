package ch.innovazion.arionide.menu.code;

import java.util.List;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.menu.Browser;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.ui.core.geom.WorldElement;

public abstract class CodeBrowser extends Browser {
		
	public CodeBrowser(Menu parent) {
		super(parent);
	}
	
	protected WorldElement getTarget() {
		return getAppManager().getCoreRenderer().getCodeGeometry().getElementByID(getSelectedID());
	}
	
	public List<Integer> loadCurrentElements() {
		return Utils.extract(getProject().getDataManager().getCodeManager().getCurrentCode().list(), HierarchyElement::getID);
	}
}