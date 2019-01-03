package ch.innovazion.arionide.menu.browsers;

import java.util.List;
import java.util.Map;

import ch.innovazion.arionide.menu.MainMenus;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.project.CodeChain;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.project.StructureMeta;
import ch.innovazion.arionide.ui.AppManager;

public class CodeBrowser extends Menu {
	
	public CodeBrowser(AppManager manager) {
		super(manager);
	}
	
	protected void onClick(int id) {		
		MainMenus.getCodeEditor().setTargetByIndex(id);
		MainMenus.getCodeEditor().show();
	}

	public void show() {
		super.show();
		
		List<String> elements = getElements();
		elements.clear();
		
		Project project = this.getAppManager().getWorkspace().getCurrentProject();
		
		if(project != null) {
			CodeChain chain = project.getDataManager().getCodeManager().getCurrentCode();
			Map<Integer, StructureMeta> meta = project.getStorage().getStructureMeta();
			
			for(HierarchyElement element : chain) {
				elements.add(meta.get(element.getID()).getName());
			}
			
			assert !elements.isEmpty();
		} else {
			throw new IllegalStateException();
		}
	}
}
