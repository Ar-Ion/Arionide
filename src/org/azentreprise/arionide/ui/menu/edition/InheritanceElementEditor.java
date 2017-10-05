package org.azentreprise.arionide.ui.menu.edition;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Menu;
import org.azentreprise.arionide.ui.menu.SpecificMenu;

public class InheritanceElementEditor extends Menu {

	private static final String remove = "Remove from inheritance";
	private static final String close = "Close";
	
	private final SpecificMenu parent;
		
	private int target;
	
	protected InheritanceElementEditor(AppManager manager, SpecificMenu parent) {
		super(manager, remove, close);
		this.parent = parent;
	}
	
	protected void setTarget(int target) {
		this.target = target;
	}

	protected void onClick(String element) {		
		switch(element) {
			case remove:
				Project project = this.getAppManager().getWorkspace().getCurrentProject();
				
				MessageEvent message = null;
				
				if(project != null) {

					message = project.getDataManager().desinherit(this.target, this.parent.getCurrent().getID());
				} else {
					message = new MessageEvent("No project is currently loaded", MessageType.ERROR);
				}
				
				this.getAppManager().getEventDispatcher().fire(message);
				
				this.parent.reload();
				this.parent.show();
				
				break;
			case close:
				this.parent.show();
				break;
		}
	}
	
	public String getDescription() {
		return "Inheritance element editor";
	}
}
