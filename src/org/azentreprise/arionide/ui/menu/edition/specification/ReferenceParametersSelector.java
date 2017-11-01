package org.azentreprise.arionide.ui.menu.edition.specification;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.lang.Data;
import org.azentreprise.arionide.lang.Reference;
import org.azentreprise.arionide.lang.Specification;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Menu;

public class ReferenceParametersSelector extends Menu {
	
	private final Menu parent;
	private final Reference reference;
	
	public ReferenceParametersSelector(AppManager manager, Menu parent, Specification specification, int id) {
		super(manager, "Back", "Callability", "Add");
		
		this.parent = parent;
		this.reference = (Reference) specification.getElements().get(id);
		
		this.setMenuCursor(2);
		this.load();
	}
	
	public void load() {
		this.getElements().subList(3, this.getElements().size()).clear();
		
		if(this.reference.getParameters() != null) {
			this.getElements().addAll(this.reference.getParameters().stream().map(Data::toString).collect(Collectors.toList()));
		}
	}
	
	public void onClick(int id) {
		if(id == 0) {
			this.parent.show();
		} else if(id == 1) {
			if(this.reference.getParameters() != null) {
				this.reference.setParameters(null);
				this.getAppManager().getEventDispatcher().fire(new MessageEvent("This reference is not callable anymore", MessageType.SUCCESS));
				this.load();
			} else {
				this.reference.setParameters(new ArrayList<>());
				this.getAppManager().getEventDispatcher().fire(new MessageEvent("This reference is now callable", MessageType.SUCCESS));
				this.load();
			}
		} else if(id == 2) {
			if(this.reference.getParameters() != null) {
				
			} else {
				this.getAppManager().getEventDispatcher().fire(new MessageEvent("This reference is not callable", MessageType.ERROR));
			}
		} else {
			
		}
	}
}