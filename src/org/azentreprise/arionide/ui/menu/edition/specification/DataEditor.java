package org.azentreprise.arionide.ui.menu.edition.specification;

import org.azentreprise.arionide.lang.Data;
import org.azentreprise.arionide.lang.Specification;
import org.azentreprise.arionide.lang.TypeManager;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.SpecificMenu;
import org.azentreprise.arionide.ui.menu.code.TypeEditor;

public class DataEditor extends SpecificationElementEditor {

	private static final String setType = "Set type";
	private static final String setDefault = "Set default";

	private TypeManager type;
	private String description;
	
	protected DataEditor(AppManager manager, SpecificMenu parent) {
		super(manager, parent);
		
		this.getElements().add(setType);
		this.getElements().add(setDefault);
	}

	protected void setTarget(Specification specification, int id) {
		super.setTarget(specification, id);
		
		Data element = (Data) this.getElement();
		
		this.type = this.getAppManager().getWorkspace().getCurrentProject().getLanguage().getTypes().getTypeManager(element.getType());			
		this.description = element.getName() + " [" + this.type + "]";
		
		if(element.getValue() != null) {
			this.description += " (default: " + element.getValue() + ")";
		}
	}
	
	public void onClick(String element) {		
		switch(element) {
			case setType:
				TypeSelector selector = new TypeSelector(this.getAppManager(), this, this.getSpecification(), this.getElementID());
				selector.show();
				break;
			case setDefault:
				TypeEditor editor = new TypeEditor(this.getAppManager(), this, (Data) this.getElement());
				editor.show();
				break;
			default: 
				super.onClick(element);
		}
	}
	
	public String getDescription() {
		return this.description;
	}
}
