package org.azentreprise.arionide.ui.menu;

import java.util.Map;

import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.core.opengl.WorldElement;

public class Code extends SpecificMenu {
	
	private final CodeEditor editor;
	
	private int selected = -1;
	
	protected Code(AppManager manager) {
		super(manager);
		this.editor = new CodeEditor(manager, this);
	}

	public void setCurrent(WorldElement element) {
		super.setCurrent(element);
		
		Project project = this.getManager().getWorkspace().getCurrentProject();
	
		if(project != null) {
			Map<Integer, StructureMeta> meta = project.getStorage().getStructureMeta();
			this.getElements().clear();
			project.getStorage().getCurrentData().stream()
					.map(e -> meta.get(Integer.parseInt(meta.get(e.getID()).getComment().substring(5))).getName())
					.forEach(this.getElements()::add);
		}
	}
	
	public void onSelect(int id) {
		Project project = this.getManager().getWorkspace().getCurrentProject();
		
		if(project != null) {
			this.selected = project.getStorage().getCurrentData().get(id).getID();
			this.getManager().getCoreRenderer().selectInstruction(this.selected);
		}
	}
	
	public void onClick(int id) {
		Project project = this.getManager().getWorkspace().getCurrentProject();
		
		if(project != null && id < this.getElements().size()) {
			this.editor.setTargetInstruction(project.getStorage().getCurrentData().get(id));
			this.editor.show();
		}
	}
	
	public String getDescription() {
		Project project = this.getManager().getWorkspace().getCurrentProject();

		if(project != null) {
			return this.selected >= 0 ? project.getStorage().getStructureMeta().get(this.selected).getSpecification().toString() : "No code is available";
		}
		
		return "Error";
	}
}
