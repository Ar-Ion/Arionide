package org.azentreprise.arionide.ui.menu;

import java.util.Arrays;
import java.util.List;

import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.ui.AppManager;

public class CodeEdit extends Menu {

	private static final String setup = "Setup";
	private static final String append = "Append";
	private static final String back = "Back";
	
	private int structureID;
	
	protected CodeEdit(AppManager manager) {
		super(manager);
		this.getElements().addAll(manager.getWorkspace().getCurrentProject().getCompiler().getInstructionSet().getInstructions());
		this.getElements().add(setup);
		this.getElements().add(append);
		this.getElements().add(back);
	}
	
	public void show() {
		super.show();
		
		List<Integer> list = this.getManager().getCoreRenderer().getInside();
		
		if(list.size() > 0) {
			this.structureID = list.get(list.size() - 1);
		}
	}
	
	public void onClick(String element) {
		if(this.getManager().getWorkspace().getCurrentProject().getCompiler().getInstructionSet().getInstructions().contains(element)) {
			Project project = this.getManager().getWorkspace().getCurrentProject();

			this.getManager().getEventDispatcher().fire(project.getDataManager().insertCode(0, element));
			
			this.getManager().getCoreRenderer().loadProject(project); // Reload renderers
		} else if(element == comment) {
			
		} else if(element == back) {
			
		} else assert false : "default case is not permitted";
	}
	
	public String getDescription() {
		return this.structureID > 0 ? "Code editor for " + super.getDescription() : "Editor not available";
	}
}
