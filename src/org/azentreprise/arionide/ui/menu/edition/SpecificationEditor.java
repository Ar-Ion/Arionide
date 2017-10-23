package org.azentreprise.arionide.ui.menu.edition;

import javax.swing.JOptionPane;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.lang.Specification;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.lang.TypeManager;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Confirm;
import org.azentreprise.arionide.ui.menu.Menu;
import org.azentreprise.arionide.ui.menu.SpecificMenu;
import org.azentreprise.arionide.ui.menu.code.TypeEditor;

public class SpecificationEditor extends Menu {

	private static final String setType = "Set type";
	private static final String setDefault = "Set default";
	private static final String setName = "Set name";
	private static final String delete = "Delete";
	private static final String back = "Back";

	private final SpecificMenu parent;
	private int id;
	private Specification specification;
	private SpecificationElement element;
	private TypeManager type;
	private String description;
	
	protected SpecificationEditor(AppManager manager, SpecificMenu parent) {
		super(manager, setType, setDefault, setName, delete, back);
		this.parent = parent;
	}

	protected void setTarget(Specification specification, int id) {
		this.id = id;
		this.specification = specification;
		this.element = specification.getElements().get(id);
		this.type = this.getAppManager().getWorkspace().getCurrentProject().getLanguage().getTypes().getTypeManager(this.element.getType());			
		this.description = this.element.getName() + " [" + this.type + "]";
		
		if(this.element.getValue() != null) {
			this.description += " (default: " + this.element.getValue() + ")";
		}
	}
	
	public void show() {
		super.show();
		this.setTarget(this.specification, this.id);
	}
	
	public void onClick(String element) {
		switch(element) {
			case setType:
				TypeSelector selector = new TypeSelector(this.getAppManager(), this, this.specification, this.id);
				selector.show();
				break;
			case setDefault:
				TypeEditor editor = new TypeEditor(this.getAppManager(), this, this.element);
				editor.show();
				break;
			case setName:
				new Thread(() -> {
					String name = JOptionPane.showInputDialog(null, "Enter the specification element's new name", "Specification name editor", JOptionPane.PLAIN_MESSAGE);
					
					if(name != null) {
						Project project = this.getAppManager().getWorkspace().getCurrentProject();
						
						if(project != null) {
							MessageEvent message = project.getDataManager().refactorSpecificationName(this.specification, this.id, name);
							this.getAppManager().getEventDispatcher().fire(message);
						}
					}
				}).start();
				
				this.setTarget(this.specification, this.id);
				
				break;
			case delete:
				Menu confirm = new Confirm(this.getAppManager(), this, this::delete, "Do you really want to delete this element?");
				confirm.show();
				break;
			case back:
				this.parent.show();
		}
	}
	
	public void delete() {
		Project project = this.getAppManager().getWorkspace().getCurrentProject();
		
		if(project != null) {
			MessageEvent message = project.getDataManager().deleteSpecificationElement(this.specification, this.id);
			this.getAppManager().getEventDispatcher().fire(message);
			
			this.parent.reload();
			this.parent.show();
		}
	}
	
	public String getDescription() {
		return this.description;
	}
}
