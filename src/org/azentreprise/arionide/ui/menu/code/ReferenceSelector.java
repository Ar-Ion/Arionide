package org.azentreprise.arionide.ui.menu.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.lang.CoreDataManager;
import org.azentreprise.arionide.lang.Reference;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Menu;

class ReferenceSelector extends Menu {
	private static final String back = "Back";
	
	private final Menu parent;
	private final Reference element;
	
	public ReferenceSelector(AppManager manager, Menu parent, Reference element) {
		super(manager, back);
		
		this.parent = parent;
		this.element = element;
		
		CoreDataManager cdm = manager.getWorkspace().getCurrentProject().getLanguage().getCoreDataManager();
		
		List<String> suggestions = new ArrayList<>();
		
		for(Entry<Integer, String> entry : cdm.getReferencables().entrySet()) {
			suggestions.add(entry.getValue() + "$$$" + entry.getKey());
		}
		
		Collections.sort(suggestions);
		this.getElements().addAll(suggestions);
				
		if(element.getValue() != null) {
			int index = this.getElements().indexOf(element.getValue());
		
			if(index > -1) {
				this.select(index + 1);
			}
		}
	}
	
	public void onClick(String element) {
		if(element.equals(back)) {
			this.parent.show();
		} else {
			this.element.setValue(element);
			
			if(this.element.getSpecificationParameters() != null) {
				
			}
			
			this.parent.show();
			this.getAppManager().getEventDispatcher().fire(new MessageEvent("Reference successfully updated", MessageType.SUCCESS));
			this.getAppManager().getWorkspace().getCurrentProject().getStorage().saveStructureMeta();
		}
	}
	
	public String getDescription() {
		return "Reference selector for '" + this.element + "'";
	}
}