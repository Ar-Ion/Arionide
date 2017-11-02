package org.azentreprise.arionide.ui.menu.edition.specification;

import javax.swing.JOptionPane;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.lang.Data;
import org.azentreprise.arionide.lang.Specification;
import org.azentreprise.arionide.lang.TypeManager;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Confirm;
import org.azentreprise.arionide.ui.menu.Menu;
import org.azentreprise.arionide.ui.menu.SpecificMenu;
import org.azentreprise.arionide.ui.menu.code.TypeEditor;

public class ReferenceParametersEditor extends SpecificMenu {

	private static final String back = "Back";
	private static final String delete = "Delete";
	private static final String setName = "Set name";
	private static final String setType = "Set type";
	private static final String setDefault = "Set default";

	private final Menu parent;
	
	private Specification spec;
	private int id;
	private int dataID;
	private Data data;
	private TypeManager type;
	private String description;
	
	protected ReferenceParametersEditor(AppManager manager, Menu parent) {
		super(manager, back, delete, setName, setType, setDefault);
		this.parent = parent;
	}

	protected void setTarget(Specification specification, int id, int dataID, Data data) {
		this.spec = specification;
		this.id = id;
		this.dataID = dataID;
		this.data = data;
		this.type = this.getAppManager().getWorkspace().getCurrentProject().getLanguage().getTypes().getTypeManager(data.getType());			
		this.description = data.getName() + " [" + this.type + "]";
		
		if(this.data.getValue() != null) {
			this.description += " (default: " + data.getValue() + ")";
		}
	}
	
	public void onClick(String element) {		
		switch(element) {
			case back:
				this.parent.show();
				break;
			case delete:
				Menu confirm = new Confirm(this.getAppManager(), this, this::delete, "Do you really want to delete this element?");
				confirm.show();
				break;
			case setName:
				new Thread(() -> {
					String name = JOptionPane.showInputDialog(null, "Enter the reference's new name", "Reference name editor", JOptionPane.PLAIN_MESSAGE);
					
					if(name != null) {
						Project project = this.getAppManager().getWorkspace().getCurrentProject();
						
						if(project != null) {
							MessageEvent message = project.getDataManager().refactorParameterName(this.spec, this.id, this.dataID, name);
							this.getAppManager().getEventDispatcher().fire(message);
						}
					}
				}).start();
				
				break;
			case setType:
				Menu selector = new ReferenceParameterTypeSelector(this.getAppManager(), this, this.spec, this.id, this.dataID);
				selector.show();
				break;
			case setDefault:
				TypeEditor editor = new TypeEditor(this.getAppManager(), this, this.data);
				editor.show();
				break;
			default: 
				super.onClick(element);
		}
	}
	
	private void delete() {
		// TODO
	}

	public String getDescription() {
		return "Reference parameter editor for " + this.description;
	}
}
