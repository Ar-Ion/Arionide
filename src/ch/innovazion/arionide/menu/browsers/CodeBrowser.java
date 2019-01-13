package ch.innovazion.arionide.menu.browsers;

import java.util.List;
import java.util.Map;

import ch.innovazion.arionide.lang.SpecificationElement;
import ch.innovazion.arionide.menu.MainMenus;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.project.CodeChain;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.project.StructureMeta;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.ApplicationTints;

public class CodeBrowser extends Menu {
	
	private CodeChain loadedChain;
	private MenuDescription description;
	
	public CodeBrowser(AppManager manager) {
		super(manager);
	}
	
	protected void onClick(int index) {		
		MainMenus.getCodeEditor().setTargetByIndex(index);
		MainMenus.getCodeEditor().show();
	}
	
	public void onSelect(int index) {
		if(loadedChain != null) {
			int id = loadedChain.getID(index);
			
			getAppManager().getCoreRenderer().selectInstruction(id);
			
			Project project = getAppManager().getWorkspace().getCurrentProject();
			
			StructureMeta meta = project.getStorage().getStructureMeta().get(id);
			List<SpecificationElement> elements = meta.getSpecification().getElements();
			
			description = new MenuDescription(ApplicationTints.MENU_INFO_INACTIVE_COLOR, 1.0f);
			
			for(SpecificationElement element : elements) {
				description.add(element.toString());
			}
			
			description.setHighlight(0);
		}
	}

	public void show() {
		super.show();
		
		List<String> elements = getElements();
		elements.clear();
		
		Project project = this.getAppManager().getWorkspace().getCurrentProject();
		
		if(project != null) {
			loadedChain = project.getDataManager().getCodeManager().getCurrentCode();
			Map<Integer, StructureMeta> meta = project.getStorage().getStructureMeta();
			
			for(HierarchyElement element : loadedChain) {
				elements.add(meta.get(element.getID()).getName());
			}
			
			assert !elements.isEmpty();
		} else {
			throw new IllegalStateException();
		}
	}
	
	public MenuDescription getDescription() {
		if(description != null) {
			return description;
		} else {
			return super.getDescription();
		}
	}
}
