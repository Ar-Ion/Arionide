package org.azentreprise.arionide.ui.menu.edition.specification;

import java.util.ArrayList;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.lang.Reference;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.SpecificMenu;

public class ReferenceEditor extends SpecificationElementEditor {
	
	private static final String callability = "Callability";
	private static final String setParameters = "Parameters";
	
	protected ReferenceEditor(AppManager manager, SpecificMenu parent) {
		super(manager, parent);
		
		this.getElements().add(callability);
		this.getElements().add(setParameters);
	}
	
	public void onClick(String element) {		
		Reference reference = (Reference) this.getElement();

		switch(element) {
			case callability:				
				if(reference.getParameters() != null) {
					reference.setParameters(null);
					this.getAppManager().getEventDispatcher().fire(new MessageEvent("This reference is not callable anymore", MessageType.SUCCESS));
				} else {
					reference.setParameters(new ArrayList<>());
					this.getAppManager().getEventDispatcher().fire(new MessageEvent("This reference is now callable", MessageType.SUCCESS));
				}
				
				break;
			case setParameters:
				if(reference.getParameters() != null) {
					
				} else {
					this.getAppManager().getEventDispatcher().fire(new MessageEvent("This reference is not callable", MessageType.ERROR));
				}
				
				break;
			default: 
				super.onClick(element);
		}
	}
	
	public String getDescription() {
		return this.getElement().toString();
	}
}