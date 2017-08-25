package org.azentreprise.arionide.events;

import org.azentreprise.arionide.ui.menu.Menu;

public class MenuEvent extends Event {
	
	private final Menu menu;
	
	public MenuEvent(Menu menu) {
		this.menu = menu;
	}
	
	public Menu getMenu() {
		return this.menu;
	}
}