package org.azentreprise.arionide.ui.menu.code;

import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Menu;

public class TypeEditor extends Menu {
	
	private final SpecificationElement element;
	
	protected TypeEditor(AppManager manager, SpecificationElement element) {
		super(manager);
		
		this.element = element;
	}
	
	public String getDescription() {
		return this.element.getName() + ": " + this.element.getValue();
	}
}
