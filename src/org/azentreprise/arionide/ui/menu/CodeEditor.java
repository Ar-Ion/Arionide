package org.azentreprise.arionide.ui.menu;

import org.azentreprise.arionide.project.HierarchyElement;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.AppManager;

public class CodeEditor extends Menu {

	private static final String back = "Back";
	private static final String append = "Append";
	
	private final Menu parent;
	private final CodeAppender appender;
	
	private int instructionID;
	private HierarchyElement instruction;
	private StructureMeta instructionMeta;
	
	protected CodeEditor(AppManager manager, Menu parent) {
		super(manager);
		
		this.parent = parent;
		this.appender = new CodeAppender(manager, parent);
		
		this.getElements().add(back);
		this.getElements().add(append);
		// TODO specification
	}
	
	protected void setTargetInstruction(int id) {
		Project project = this.getManager().getWorkspace().getCurrentProject();
		
		if(project != null) {
			this.instructionID = id;
			this.instruction = project.getStorage().getCurrentData().get(id);
			this.instructionMeta = project.getStorage().getStructureMeta().get(this.instruction.getID());
			
			assert this.instructionMeta != null;
		}
		
		this.setMenuCursor(1);
	}
	
	public void onClick(String element) {
		if(this.getManager().getWorkspace().getCurrentProject().getCompiler().getInstructionSet().getInstructions().contains(element)) {
			Project project = this.getManager().getWorkspace().getCurrentProject();

			this.getManager().getEventDispatcher().fire(project.getDataManager().insertCode(0, element));
			
			this.getManager().getCoreRenderer().loadProject(project); // Reload renderers
		} else if(element == append) {
			this.appender.setAppenderPosition(this.instructionID);
			this.appender.show();
		} else if(element == back) {
			this.parent.show();
		} else assert false : "default case is not permitted";
	}
	
	public String getDescription() {
		return this.instructionMeta.getName() + " [" + this.instructionMeta.getSpecification() + "]";
	}
}
