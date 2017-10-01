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
		
	private final InheritanceElementEdition editor;
	
	private List<Integer> parents;
	
	protected Inheritance(AppManager manager) {
		super(manager);
		this.editor = new InheritanceElementEdition(manager, this);
	}

	public void setCurrent(WorldElement element) {
		super.setCurrent(element);
		
		Storage storage = this.getManager().getWorkspace().getCurrentProject().getStorage();
		
		InheritanceElement object = storage.getInheritance().get(element.getID());
		
		if(object != null) {
			this.parents = object.getParents();
			
			List<String> elements = this.getElements();
			elements.clear();
			elements.addAll(this.parents.stream().map(e -> storage.getStructureMeta().get(e).getName()).collect(Collectors.toList()));
			elements.add("Add");
			elements.add("Cancel");
		}
	}
	
	public void onClick(int id) {
		if(this.parents != null && id < this.parents.size()) {
			int element = this.parents.get(id);
			this.editor.setTarget(element);
			this.editor.show();
		} else if(id == this.parents.size()){
			Storage storage = this.getManager().getWorkspace().getCurrentProject().getStorage();
			new StructureSelection(this.getManager(), this::inherit, new AlphabeticalComparator(storage)).show();
		} else {
			MainMenus.STRUCT_EDIT.show();
		}
	}
	
	public void inherit(int parent) {
		MessageEvent message = this.getManager().getWorkspace().getCurrentProject().getDataManager().inherit(this.getCurrent().getID(), parent);
		this.getManager().getEventDispatcher().fire(message);
		this.reload();
		this.show(); // This is being called by the structure selection menu...
	}
	
	public String getDescription() {
		return "Inheritance editor for " + super.getDescription();
	}
}