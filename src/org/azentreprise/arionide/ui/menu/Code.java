package org.azentreprise.arionide.ui.menu;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.core.opengl.WorldElement;

public class Code extends SpecificMenu {

	private static final String back = "Back";
	
	private final Menu parent;
	
	private int selected = -1;
	
	protected Code(AppManager manager, Menu parent) {
		super(manager, back);
		this.parent = parent;
	}

	public void setCurrent(WorldElement element) {
		super.setCurrent(element);
		
		Project project = this.getManager().getWorkspace().getCurrentProject();
	
		if(project != null) {
			Map<Integer, StructureMeta> meta = project.getStorage().getStructureMeta();
			List<String> elements = project.getStorage().getCurrentData().stream().map(e -> meta.get(e.getID()).getName()).collect(Collectors.toList());
			List<String> strings = this.getElements();
			
			strings.clear();
			strings.addAll(elements);
			strings.add(back);
		}
	}
	
	public void onSelect(int id) {
		Project project = this.getManager().getWorkspace().getCurrentProject();
		
		if(project != null && id < this.getElements().size() - 1) {
			this.selected = project.getStorage().getCurrentData().get(id).getID();
			this.getManager().getCoreRenderer().selectInstruction(this.selected);
		}
	}
	
	public void onClick(int id) {
		Project project = this.getManager().getWorkspace().getCurrentProject();
		
		if(project != null && id < this.getElements().size() - 1) {
		
		} else {
			this.parent.show();
		}
	}
	
	public String getDescription() {
		Project project = this.getManager().getWorkspace().getCurrentProject();

		if(project != null) {
			return this.selected > 0 ? project.getStorage().getStructureMeta().get(this.selected).getSpecification().toString() : "No code is available";
		}
		
		return "Error";
	}
}
