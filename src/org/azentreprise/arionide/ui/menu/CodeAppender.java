package org.azentreprise.arionide.ui.menu;

import org.azentreprise.arionide.ui.AppManager;

public class CodeAppender extends Menu {

	private final Menu parent;
	
	private int position = -1;
	
	protected CodeAppender(AppManager manager, Menu parent) {
		super(manager);
		this.parent = parent;
	}
	
	// "id" is the selected element.
	public void setAppenderPosition(int id) {
		this.position = id + 1;
	}
}
