package org.azentreprise.arionide.ui.menu;

import javax.swing.JOptionPane;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.project.HierarchyElement;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.AppManager;

public class CodeEditor extends Menu {

	private static final String back = "Back";
	private static final String description = "Set description";
	private static final String delete = "Delete";
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
		this.getElements().add(delete);
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
		
		this.setMenuCursor(3);
	}
		
	public void onClick(String element) {
		Project project = this.getAppManager().getWorkspace().getCurrentProject();

		if(element == append) {
			this.appender.setAppenderPosition(this.instructionID);
			this.appender.show();
		} else if(element == delete) {
			if(this.instructionID > 0) {
				MessageEvent message = project.getDataManager().deleteCode(this.instructionID);
				this.getAppManager().getEventDispatcher().fire(message);
				this.getAppManager().getCoreRenderer().loadProject(project);
				this.parent.select(this.instructionID - 1);
				this.parent.show();
			} else {
				this.getAppManager().getEventDispatcher().fire(new MessageEvent("You can't delete the entry instruction", MessageType.ERROR));
			}
		} else if(element == description) {
			new Thread(() -> {
				String name = JOptionPane.showInputDialog(null, "Please enter the description of the instruction", "Description", JOptionPane.PLAIN_MESSAGE);
				
				if(name != null) {
					MessageEvent message = project.getDataManager().setName(this.instruction.getID(), name);
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
		if(this.instructionMeta.getName().equals("?") && this.getAppManager().getWorkspace().getCurrentProject() != null) {
			int realID = Integer.parseInt(this.instructionMeta.getComment().substring(5));
			return this.getAppManager().getWorkspace().getCurrentProject().getStorage().getStructureMeta().get(realID).getName();
		} else {
			return this.instructionMeta.getName();
		}
	}
}
