package org.azentreprise.arionide.ui.menu;

import java.util.List;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.ui.AppManager;

public class CodeAppender extends Menu {
	
	private final Menu parent; // This refers to the Code menu
	
	private int position = -1;
	private List<String> instructions;
	
	protected CodeAppender(AppManager manager, Menu parent) {
		super(manager);
		
		this.parent = parent;
	}
	
	// "id" is the selected element.
	public void setAppenderPosition(int id) {
		this.position = id + 1;
		
		Project project = this.getAppManager().getWorkspace().getCurrentProject();
		List<String> elements = this.getElements();

		elements.clear();
		
		if(project != null) {
			elements.addAll(this.instructions = project.getCompiler().getInstructionSet().getInstructions());
		}
		
		this.getElements().add("Back");
	}
		
	public void onClick(int id) {
		try {
			Project project = this.getAppManager().getWorkspace().getCurrentProject();
			MessageEvent event = project.getDataManager().insertCode(this.position, this.instructions.get(id));
			
			this.getAppManager().getEventDispatcher().fire(event);
			this.getAppManager().getCoreRenderer().loadProject(project);
			
			this.parent.onSelect(this.position);
			this.parent.onClick(this.position);
		} catch(IndexOutOfBoundsException e) {
			this.parent.show();
		}
	}
	
	public String getDescription() {
		return "Please select the instruction to insert";
	}
}
