package org.azentreprise.arionide.ui.menu;

import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.core.opengl.WorldElement;

public class SpecificMenu extends Menu {
	
	private WorldElement current;

	protected SpecificMenu(AppManager manager, String... elements) {
		super(manager, elements);
	}
	
	public void setCurrent(WorldElement current) {
		this.current = current;
	}
	
	protected WorldElement getCurrent() {
		return this.current;
	}
}
