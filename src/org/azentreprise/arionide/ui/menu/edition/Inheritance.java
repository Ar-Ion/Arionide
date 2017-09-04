package org.azentreprise.arionide.ui.menu.edition;

import java.util.List;
import java.util.stream.Collectors;

import org.azentreprise.arionide.comparators.AlphabeticalComparator;
import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.project.InheritanceElement;
import org.azentreprise.arionide.project.Storage;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.core.opengl.WorldElement;
import org.azentreprise.arionide.ui.menu.MainMenus;
import org.azentreprise.arionide.ui.menu.SpecificMenu;
import org.azentreprise.arionide.ui.menu.StructureSelection;

public class Inheritance extends SpecificMenu {
		
	private List<Integer> parents;
	
	protected Inheritance(AppManager manager) {
		super(manager);
	}

	public void setCurrent(WorldElement element) {
		super.setCurrent(element);
		
		Storage storage = this.getManager().getWorkspace().getCurrentProject().getStorage();
		
		InheritanceElement object = storage.getInheritance().get(element.getID());
		
		if(object != null) {
			this.parents = object.getParents();
			List<String> elements = this.parents.stream().map(e -> storage.getStructureMeta().get(e).getName()).collect(Collectors.toList());
			elements.add("Add");
			elements.add("Cancel");
			this.setElements(elements);
		}
	}
	
	public void onClick(int id) {
		if(this.parents != null && id < this.parents.size()) {
			
		} else if(id == this.parents.size()){
			Storage storage = this.getManager().getWorkspace().getCurrentProject().getStorage();
			this.show(new StructureSelection(this.getManager(), this::inherit, new AlphabeticalComparator(storage)));
		} else {
			this.show(MainMenus.STRUCT_EDIT);
		}
	}
	
	public void inherit(int parent) {
		MessageEvent message = this.getManager().getWorkspace().getCurrentProject().getDataManager().inherit(this.getCurrentID(), parent);
		this.getManager().getEventDispatcher().fire(message);
		this.show(this); // This is being called by the structure selection menu...
	}
}