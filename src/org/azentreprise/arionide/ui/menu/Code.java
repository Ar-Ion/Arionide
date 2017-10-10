package org.azentreprise.arionide.ui.menu;

import java.util.Map;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.project.HierarchyElement;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.AppManager;

public class Code extends SpecificMenu {
	
	private final CodeEditor editor;
	
	private int selected = -1;
	
	protected Code(AppManager manager) {
		super(manager);
		this.editor = new CodeEditor(manager, this);
	}

	public void show() {
		super.show();
		
		if(this.getCurrent() != null) {
			Project project = this.getAppManager().getWorkspace().getCurrentProject();
			
			if(project != null) {
				Map<Integer, StructureMeta> meta = project.getStorage().getStructureMeta();
				
				this.getElements().clear();
				
				for(HierarchyElement object : project.getStorage().getCurrentData()) {
					StructureMeta objectMeta = meta.get(object.getID());
					
					String name = objectMeta.getName();
					
					if(name.equals("?")) {
						this.getElements().add(meta.get(Integer.parseInt(objectMeta.getComment().substring(5))).getName());
					} else {
						this.getElements().add(name);
					}
				}
			}
		}
	}

	public void onSelect(int id) {
		Project project = this.getAppManager().getWorkspace().getCurrentProject();

		if(project != null) {
			this.selected = project.getStorage().getCurrentData().get(id).getID();
			this.getAppManager().getCoreRenderer().selectInstruction(this.selected);
			this.getAppManager().getEventDispatcher().fire(new MessageEvent(this.getDescription(), MessageType.INFO));
		}
	}
	
	public void onClick(int id) {
		Project project = this.getAppManager().getWorkspace().getCurrentProject();
		
		if(project != null && id < this.getElements().size()) {
			this.editor.setTargetInstruction(id);
			this.editor.show();
		}
	}
	
	public String getDescription() {
		Project project = this.getAppManager().getWorkspace().getCurrentProject();

		if(project != null && this.selected > -1) {
			Map<Integer, StructureMeta> metaData = project.getStorage().getStructureMeta();
			StructureMeta meta = metaData.get(this.selected);
			
			return metaData.get(Integer.parseInt(meta.getComment().substring(5))).getName() + " [" + meta.getSpecification() + "]";
		}
		
		return "No code is available";
	}
}
