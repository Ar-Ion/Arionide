package org.azentreprise.arionide.ui.menu;

import javax.swing.JOptionPane;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.project.HierarchyElement;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.AppManager;

public class CodeEditor extends Menu {

	private static final String back = "Back";
	private static final String description = "Set description";
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
		this.getElements().add(description);
		this.getElements().add(append);
	}
	
	protected void setTargetInstruction(int id) {
		Project project = this.getAppManager().getWorkspace().getCurrentProject();
		
		if(project != null) {
			this.instructionID = id;
			this.instruction = project.getStorage().getCurrentData().get(id);
			this.instructionMeta = project.getStorage().getStructureMeta().get(this.instruction.getID());
			
			assert this.instructionMeta != null;
		}
		
		this.setMenuCursor(2);
	}
	
	public void onClick(String element) {
		Project project = this.getAppManager().getWorkspace().getCurrentProject();

		if(project.getCompiler().getInstructionSet().getInstructions().contains(element)) {
			this.getAppManager().getEventDispatcher().fire(project.getDataManager().insertCode(0, element));
			
			this.getAppManager().getCoreRenderer().loadProject(project); // Reload renderers
		} else if(element == append) {
			this.appender.setAppenderPosition(this.instructionID);
			this.appender.show();
		} else if(element == description) {
			new Thread(() -> {
				String name = JOptionPane.showInputDialog(null, "Please enter the description of the instruction", "Description", JOptionPane.PLAIN_MESSAGE);
				
				if(name != null) {
					MessageEvent message = project.getDataManager().setName(this.instructionID, name);
					this.getAppManager().getEventDispatcher().fire(message);
					this.getAppManager().getCoreRenderer().loadProject(project);
				}
			}).start();
		} else if(element == back) {
			this.parent.show();
		} else assert false : "default case is not permitted";
	}
	
	public String getDescription() {
		return this.getInstructionName() + " [" + this.instructionMeta.getSpecification() + "]";
	}
	
	private String getInstructionName() {
		if(this.instructionMeta.getName().isEmpty() && this.getAppManager().getWorkspace().getCurrentProject() != null) {
			int realID = Integer.parseInt(this.instructionMeta.getComment().substring(5));
			return this.getAppManager().getWorkspace().getCurrentProject().getStorage().getStructureMeta().get(realID).getName();
		} else {
			return this.instructionMeta.getName();
		}
	}
}
