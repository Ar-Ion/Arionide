package org.azentreprise.arionide.ui.menu;

import org.azentreprise.arionide.ui.AppManager;

public class Code extends Menu {

	protected Code(AppManager manager) {
		super(manager, "nop");
	}
	
	public String getDescription() {
		return "Code editor for " + super.getDescription();
	}
}
