package org.azentreprise.arionide.ui.menu.edition;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.MainMenus;
import org.azentreprise.arionide.ui.menu.Menu;
import org.azentreprise.arionide.ui.menu.SpecificMenu;

public class InheritanceElementEdition extends SpecificMenu {

	private static final String edit = "Edit";
	private static final String remove = "Remove from inheritance";
	private static final String close = "Close";
	
	private final Menu parent;
	
	protected InheritanceElementEdition(AppManager manager, Menu parent) {
		super(manager, edit, remove, close);
		this.parent = parent;
	}
	
	protected void onClick(String element) {		
		switch(element) {
			case edit:
				MainMenus.STRUCT_EDIT.setCurrent(this.getCurrent());
				this.show(MainMenus.STRUCT_EDIT);
				break;
			case remove:
				Project project = this.getManager().getWorkspace().getCurrentProject();
				
				MessageEvent message = null;
				
				if(project != null) {
					message = project.getDataManager().desinherit(this.getCurrent().getID(), names.indexOf(element));
				} else {
					message = new MessageEvent("No project is currently loaded", MessageType.ERROR);
				}
				
				this.getManager().getEventDispatcher().fire(message);
				this.show(MainMenus.STRUCT_LIST);
				
				break;
			case close:
				this.show(this.parent);
				break;
		}
	}
	
	public String getDescription() {
		return "Inheritance element editor for " + super.getDescription();
	}
}
