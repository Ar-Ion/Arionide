package org.azentreprise.arionide.ui.menu.code;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.lang.CoreDataManager;
import org.azentreprise.arionide.lang.Reference;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Menu;

public class ReferenceEditor extends Menu {
	
	private static final String back = "Back";

	private final Menu parent;
	private final SpecificationElement element;
		
	public ReferenceEditor(AppManager manager, Menu parent, Reference element) {
		super(manager);
		
		this.parent = parent;
		this.element = element;
		
		CoreDataManager cdm = manager.getWorkspace().getCurrentProject().getLanguage().getCoreDataManager();
		
		List<String> suggestions = new ArrayList<>();
		
		for(Entry<Integer, String> entry : cdm.getReferencables().entrySet()) {
			suggestions.add(entry.getValue() + "$$$" + entry.getKey());
		}
		
		this.getElements().add(back);
		this.getElements().addAll(suggestions);
		
		if(element.getValue() != null) {
			int index = this.getElements().indexOf(element.getValue());
		
			if(index > -1) {
				this.select(index + 1);
			}
		}
	}
	
	public void onClick(String element) {
		this.element.setValue(element);
		this.parent.show();
		this.getAppManager().getEventDispatcher().fire(new MessageEvent("Value successfully updated", MessageType.SUCCESS));
		this.getAppManager().getWorkspace().getCurrentProject().getStorage().saveStructureMeta();
	}
	
	public String getDescription() {
		return "Reference editor for '" + this.element + "'";
	}
}
